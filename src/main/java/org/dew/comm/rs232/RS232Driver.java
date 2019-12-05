package org.dew.comm.rs232;

import java.io.*;
import java.util.*;

import javax.comm.*;

import org.dew.comm.*;
import org.dew.comm.astm.ASTMMessage;

public 
class RS232Driver implements IDriver
{
  protected CommPortIdentifier commPortIdentifier;
  protected SerialPort serialPort;
  protected InputStream inputStream;
  protected OutputStream outputStream;
  protected PrintStream psLog = new PrintStream(new NullOutputStream());
  protected Object port;
  
  public 
  RS232Driver()
  {
  }
  
  public 
  RS232Driver(String sPort)
    throws Exception
  {
    Properties properties = new Properties();
    properties.setProperty(sPORT, sPort);
    init(properties);
  }
  
  public 
  void setPort(Object port)
  {
    this.port = port;
  }
  
  public
  void setLogPrintStream(PrintStream ps)
  {
    this.psLog = ps;
    if(psLog == null) psLog = new PrintStream(new NullOutputStream());
  }
  
  @SuppressWarnings({ "rawtypes" })
  public 
  void init(Properties properties) 
    throws Exception 
  {
    psLog.println("[RS232Driver] init " + properties);
    String sPort = properties != null ? properties.getProperty(sPORT) : null;
    if(sPort == null || sPort.length() == 0)  {
      sPort = port != null ? port.toString() : null;
    }
    port = sPort;
    if(port == null) throw new Exception("Not specified port");
    System.out.println("CommPortIdentifier.getPortIdentifiers()...");
    Enumeration portList = CommPortIdentifier.getPortIdentifiers();
    while(portList.hasMoreElements()) {
      CommPortIdentifier portIdentifier = (CommPortIdentifier) portList.nextElement();
      if (portIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL) {
        String sPortIdentifierName = portIdentifier.getName();
        System.out.println("check " + sPortIdentifierName + "...");
        if (sPortIdentifierName != null && sPortIdentifierName.equals(sPort)) {
          commPortIdentifier = portIdentifier;
          System.out.println("commPortIdentifier.open...");
          serialPort = (SerialPort) commPortIdentifier.open("ASTMRS232InstrumentEmulator", 2000);
          serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
          inputStream  = serialPort.getInputStream();
          outputStream = serialPort.getOutputStream();
        }
      }
    }
  }
  
  public 
  IMessage sendMessage(IMessage message) 
    throws Exception 
  {
    return sendMessage(message, true, true);
  }
  
  public 
  IMessage waitForHostMessages(IMessage message) 
    throws Exception
  {
    psLog.println("[RS232Driver] waitForHostMessages");
    if(inputStream == null) {
      psLog.println("[RS232Driver] Driver not initialized or unrecognized port");
      return null;
    }
    IMessage messageReceived = null;
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      while(true) {
        
        byte[] buffer = new byte[1024];
        int b = 0;
        while(true) {
          byte[] abRead = new byte[1024];
          inputStream.read(abRead);
          CommUtils.append(baos, abRead);
          byte bLast = 0;
          for(int i = 0; i < abRead.length; i++) {
            byte byteAti = abRead[i];
            if(byteAti == 0) break;
            bLast = byteAti;
            buffer[b++]  = byteAti;
          }
          if(bLast == 10 || bLast == 13 || bLast == 5 || bLast == 4) break;
        }
        
        psLog.println("[RS232Driver] " + CommUtils.getString(buffer));
        byte byte0 = buffer[0];
        if(byte0 != 4) { // byte0 != <EOT>
          psLog.println("[RS232Driver] -> <ACK>");
          outputStream.write((byte) 6); // <ACK>
        }
        else {
          psLog.println("[RS232Driver] EOT received");
          boolean boSendQueryResult = false;
          byte[] abMessageReceived = baos.toByteArray();
          messageReceived = MessageFactory.parse(abMessageReceived);
          if(messageReceived != null) {
            int iRecordsCount = messageReceived.getRecordsCount();
            for(int i = 0; i < iRecordsCount; i++) {
              IRecord record = messageReceived.getRecord(i);
              if(record.getType() == IRecord.Type.QUERY) {
                boSendQueryResult = true;
                break;
              }
            }
          }
          baos = new ByteArrayOutputStream();
          if(boSendQueryResult) {
            sendResult(message);
          }
          else {
            psLog.println("[RS232Driver] -> <ACK>");
            outputStream.write((byte) 6); // <ACK>
          }
          if(messageReceived != null) break;
        }
      }
    }
    catch(Exception ex) {
      ex.printStackTrace(psLog);
    }
    return messageReceived;
  }
  
  public 
  void destroy() 
  {
    psLog.println("[RS232Driver] destroy");
    if(inputStream  != null) try{ inputStream.close();  } catch(Exception ex) {}
    if(outputStream != null) try{ outputStream.close(); } catch(Exception ex) {}
    if(serialPort   != null) try{ serialPort.close();   } catch(Exception ex) {}
    commPortIdentifier = null;
    serialPort   = null;
    inputStream  = null;
    outputStream = null;
  }
  
  protected
  void finalize()
  {
    destroy();
  }
  
  protected 
  IMessage sendMessage(IMessage message, boolean boSendEnquiry, boolean boSendEndOfTransmission)
    throws Exception 
  {
    psLog.println("[RS232Driver] sendMessage");
    if(message == null) throw new Exception("Message is null.");
    if(outputStream == null) throw new Exception("RS232Driver not initialized.");
    boolean boASTMMessage = message instanceof ASTMMessage;
    boolean boExpectedResponse = false; 
    if(!boSendEnquiry || sendEnquiry()) {
      int iRecordsCount = message.getRecordsCount();
      int iFrameSequenceNumber = 0;
      for(int i = 0; i < iRecordsCount; i++) {
        IRecord record = message.getRecord(i);
        if(record.getType() == IRecord.Type.QUERY) {
          boExpectedResponse = true;
        }
        byte[] abRecord = record.getBytes();
        psLog.println("[RS232Driver] send record " + record.getType() + "...");
        if(abRecord != null && abRecord.length > 0) {
          if(boASTMMessage) {
            int iASTMMaxBlockSize = 240;
            int iLength = abRecord.length;
            int iBlocks = iLength / iASTMMaxBlockSize;
            int iMod    = iLength % iASTMMaxBlockSize;
            if(iMod > 0) iBlocks++;
            int iLenOfLastBlock = iMod > 0 ? iMod : iASTMMaxBlockSize;
            for(int j = 0; j < iBlocks; j++) {
              boolean boLast = (j == iBlocks - 1);
              int iOffset = j * iASTMMaxBlockSize;
              int iLen    = boLast ? iLenOfLastBlock : iASTMMaxBlockSize;
              byte bET    = boLast ? (byte) 3 : (byte) 23;
              iFrameSequenceNumber++;
              
              byte[] abASTMFrame = CommUtils.getASTMFrame(iFrameSequenceNumber, bET, abRecord, iOffset, iLen);
              psLog.println("[RS232Driver] send: " + CommUtils.getString(abASTMFrame));
              outputStream.write(abASTMFrame);
              outputStream.flush();
              int iByteRead = inputStream.read();
              if(iByteRead != 6) {
                psLog.println("[RS232Driver] Frame " + iFrameSequenceNumber + " not accepted.");
                break;
              }
            }
          }
          else {
            outputStream.write(abRecord);
            outputStream.flush();
            int iByteRead = inputStream.read();
            if(iByteRead != 6) {
              psLog.println("[RS232Driver] Record " + i + " not accepted.");
              break;
            }
          }
        }
      }
      if(boSendEndOfTransmission) sendEndOfTransmission();
      psLog.println("[RS232Driver] message sent");
      if(boExpectedResponse) {
        psLog.println("[RS232Driver] read results");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while(true) {
          byte[] buffer = new byte[255];
          inputStream.read(buffer);
          psLog.println("[RS232Driver] " + CommUtils.getString(buffer));
          CommUtils.append(baos, buffer);
          byte byte0 = buffer[0];
          if(byte0 != 4) { // EOT
            outputStream.write((byte) 6); // <ACK>
            psLog.println("[RS232Driver] -> <ACK>");
          }
          else {
            psLog.println("[RS232Driver] EOT received");
            break;
          }
        }
        byte[] abMessage = baos.toByteArray();
        return MessageFactory.parse(abMessage);
      }
    }
    else {
      psLog.println("[RS232Driver] Enquiry not accepted.");
      throw new Exception("Enquiry not accepted.");
    }
    return null;
  }
  
  public 
  void sendResult(IMessage message) 
    throws Exception 
  {
    psLog.println("[RS232Driver] sendResult");
    boolean boASTMMessage = message instanceof ASTMMessage;
    if(sendEnquiry()) {
      int iRecordsCount = message.getRecordsCount();
      int iFrameSequenceNumber = 0;
      for(int i = 0; i < iRecordsCount; i++) {
        IRecord record = message.getRecord(i);
        byte[] abRecord = record.getBytes();
        psLog.println("[RS232Driver] send record " + i);
        if(abRecord != null && abRecord.length > 0) {
          if(boASTMMessage) {
            int iASTMMaxBlockSize = 240;
            int iLength = abRecord.length;
            int iBlocks = iLength / iASTMMaxBlockSize;
            int iMod    = iLength % iASTMMaxBlockSize;
            if(iMod > 0) iBlocks++;
            int iLenOfLastBlock = iMod > 0 ? iMod : iASTMMaxBlockSize;
            for(int j = 0; j < iBlocks; j++) {
              boolean boLast = (j == iBlocks - 1);
              int iOffset = j * iASTMMaxBlockSize;
              int iLen    = boLast ? iLenOfLastBlock : iASTMMaxBlockSize;
              byte bET    = boLast ? (byte) 3 : (byte) 23;
              iFrameSequenceNumber++;
              
              byte[] abASTMFrame = CommUtils.getASTMFrame(iFrameSequenceNumber, bET, abRecord, iOffset, iLen);
              psLog.println("[RS232Driver] -> " + CommUtils.getString(abASTMFrame));
              outputStream.write(abASTMFrame);
              outputStream.flush();
              int iByteRead = inputStream.read();
              if(iByteRead != 6) {
                psLog.println("[RS232Driver] Frame " + iFrameSequenceNumber + " not accepted.");
                break;
              }
            }
          }
          else {
            outputStream.write(abRecord);
            outputStream.flush();
            int iByteRead = inputStream.read();
            if(iByteRead != 6) {
              psLog.println("[RS232Driver] Record " + i + " not accepted.");
              break;
            }
          }
        }
      }
      sendEndOfTransmission();
      psLog.println("[RS232Driver] message sent");
    }
    else {
      psLog.println("[RS232Driver] Enquiry not accepted.");
    }
  }
  
  protected
  boolean sendEnquiry()
    throws Exception
  {
    psLog.println("[RS232Driver] sendEnquiry");
    outputStream.write((byte) 5); // ENQ
    outputStream.flush();
    int iByteRead = inputStream.read();
    return iByteRead == 6; // ACK
  }
  
  protected
  void sendEndOfTransmission()
    throws Exception
  {
    psLog.println("[RS232Driver] sendEndOfTransmission");
    outputStream.write((byte) 4); // EOT
    outputStream.flush();
  }
}
