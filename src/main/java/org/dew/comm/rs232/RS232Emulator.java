package org.dew.comm.rs232;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Properties;

import javax.comm.CommDriver;
import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.dew.comm.CommUtils;
import org.dew.comm.IEmulator;
import org.dew.comm.IMessage;
import org.dew.comm.IRecord;
import org.dew.comm.MessageFactory;

import org.dew.comm.astm.ASTMMessage;

public 
class RS232Emulator extends JFrame implements IEmulator, Runnable 
{
  private static final long serialVersionUID = 5202089152923218023L;
  
  protected CommPortIdentifier commPortIdentifier;
  protected SerialPort serialPort;
  protected InputStream inputStream;
  protected OutputStream outputStream;
  protected Object port;
  protected JTextArea jTextArea;
  
  public 
  RS232Emulator()
  {
    setTitle("RS232 1.3");
    this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    this.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        dispose();
        System.exit(0);
      }
    });
    JPanel contentPane = (JPanel) this.getContentPane();
    contentPane.setLayout(new BorderLayout());
    jTextArea = new JTextArea();
    contentPane.add(new JScrollPane(jTextArea), BorderLayout.CENTER);
    jTextArea.append("RS232\n");    
  }
  
  public 
  RS232Emulator(String sPort)
    throws Exception
  {
    this();
    Properties properties = new Properties();
    properties.setProperty(sPORT, sPort);
    init(properties);
  }
  
  public 
  static void main(String[] args) 
  {
    String sPort = args.length > 0 ? args[0] : "COM5";
    try {
      RS232Emulator emulator = new RS232Emulator();
      emulator.setSize(800, 800);
      emulator.setVisible(true);
      emulator.setLogPrintStream(System.out);
      emulator.setPort(sPort);
      emulator.init(null);
      emulator.start();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  
  public 
  void setPort(Object port)
  {
    this.port = port;
  }
  
  public
  void setLogPrintStream(PrintStream ps)
  {
  }
  
  @SuppressWarnings("rawtypes")
  public 
  void init(Properties properties) 
    throws Exception
  {
    try {
      jTextArea.append("[RS232Emulator] init System.setSecurityManager(null)...\n");
      System.setSecurityManager(null);
      
      String driverName = "com.sun.comm.Win32Driver";
      jTextArea.append("[RS232Emulator] init CommDriver commDriver = (CommDriver) Class.forName( " + driverName + ").newInstance()...\n");
      CommDriver commDriver = (CommDriver) Class.forName( driverName ).newInstance();
      
      jTextArea.append("[RS232Emulator] init commDriver.initialize()...\n");
      commDriver.initialize();
    }
    catch(Throwable th) {
      jTextArea.append("[RS232Emulator] init exception: " + th + "\n");
    }
    
    jTextArea.append("[RS232Emulator] init...\n");
    try {
      String sPort = properties != null ? properties.getProperty(sPORT) : null;
      if(sPort == null || sPort.length() == 0)  {
        sPort = port != null ? port.toString() : null;
      }
      port = sPort;
      if(port == null) port = "COM5";
      sPort = port.toString();
      jTextArea.append("[RS232Emulator] init port = " + port + "\n");      
      jTextArea.append("[RS232Emulator] init CommPortIdentifier.getPortIdentifiers()...\n");
      boolean boFound = false;
      Enumeration portList = CommPortIdentifier.getPortIdentifiers();
      while(portList.hasMoreElements()) {
        CommPortIdentifier portIdentifier = (CommPortIdentifier) portList.nextElement();
        String sName = portIdentifier.getName();
        int iType    = portIdentifier.getPortType();
        jTextArea.append("[RS232Emulator] init portIdentifier = " + sName + ", " + iType + " \n");
        if (portIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL) {
          if (portIdentifier.getName().equals(sPort)) {
            commPortIdentifier = portIdentifier;
            serialPort = (SerialPort) commPortIdentifier.open("ASTMRS232InstrumentEmulator", 2000);
            serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            inputStream  = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
            boFound = true;
          }
        }
      }
      if(!boFound) {
        jTextArea.append("[RS232Emulator] init porta " + port + " non riconosciuta.\n");
      }
    }
    catch(Throwable th) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      th.printStackTrace(new PrintStream(baos));
      String sException = new String(baos.toByteArray());
      jTextArea.append(sException + "\n");
    }
  }
  
  public 
  void start() 
      throws Exception
  {
    jTextArea.append("[RS232Emulator] start (port = " + port + ")\n");
    if(commPortIdentifier == null) {
      jTextArea.append("RS232Emulator not initialized.\n");
      return;
    }
    new Thread(this).start();
  }
  
  public
  void run() 
  {
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
        
        jTextArea.append("[RS232Emulator] " + CommUtils.getString(buffer));
        byte byte0 = buffer[0];
        if(byte0 != 4) { // byte0 != <EOT>
          jTextArea.append("[RS232Emulator] -> <ACK>\n");
          outputStream.write((byte) 6); // <ACK>
        }
        else {
          jTextArea.append("[RS232Emulator] EOT received\n");
          boolean boSendQueryResult = false;
          byte[] abMessageReceived = baos.toByteArray();
          IMessage messageReceived = MessageFactory.parse(abMessageReceived);
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
            sendResult(messageReceived, "7", "1 - 13/07", "ROSSI",   "MARIO",   "FT3", "FT4");
            sendResult(messageReceived, "8", "2 - 12/07", "BIANCHI", "ANTONIO", "PSA", "CA5");
          }
          else {
            jTextArea.append("[RS232Emulator] -> <ACK>\n");
            outputStream.write((byte) 6); // <ACK>
          }
        }
      }
    }
    catch(Exception ex) {
      jTextArea.append("[RS232Emulator] exception: " + ex);
      ex.printStackTrace();
    }
  }
  
  public 
  void sendResult(IMessage messageReceived, String sSampleId, String sPazId, String sCognome, String sNome, String sEsame1, String sEsame2) 
    throws Exception 
  {
    jTextArea.append("[RS232Emulator] sendResult\n");
    IMessage message = MessageFactory.newMessage(messageReceived);
    if(message == null) {
      message = MessageFactory.getMessage("ASTM"); 
    }
    message.addRecord(IRecord.Type.HEADER,      new String[]{"Host", "NG_LIS", "P", "1"});
    message.addRecord(IRecord.Type.PATIENT,     new String[]{sPazId, sCognome, sNome, "19/11/1974", "M"});
    message.addRecord(IRecord.Type.ORDER,       new String[]{sSampleId, "R", "Q", "O", sEsame1, sEsame2});
//    message.addRecord(IRecord.Type.RESULT,      new String[]{"EMO", "DOSE", "0.18", "uIU/mL"});
    message.addRecord(IRecord.Type.TERMINATION, null);
    
    boolean boASTMMessage = message instanceof ASTMMessage;
    if(sendEnquiry()) {
      int iRecordsCount = message.getRecordsCount();
      int iFrameSequenceNumber = 0;
      for(int i = 0; i < iRecordsCount; i++) {
        IRecord record = message.getRecord(i);
        byte[] abRecord = record.getBytes();
        jTextArea.append("[RS232Emulator] send record " + i);
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
              jTextArea.append("[RS232Emulator] send: " + CommUtils.getString(abASTMFrame));
              outputStream.write(abASTMFrame);
              outputStream.flush();
              int iByteRead = inputStream.read();
              if(iByteRead != 6) {
                jTextArea.append("[RS232Emulator] Frame " + iFrameSequenceNumber + " not accepted.\n");
                break;
              }
            }
          }
          else {
            outputStream.write(abRecord);
            outputStream.flush();
            int iByteRead = inputStream.read();
            if(iByteRead != 6) {
              jTextArea.append("[RS232Emulator] Record " + i + " not accepted.\n");
              break;
            }
          }
        }
      }
      sendEndOfTransmission();
      jTextArea.append("[RS232Emulator] message sent\n");
    }
    else {
      jTextArea.append("[RS232Emulator] Enquiry not accepted.\n");
    }
  }
  
  protected
  boolean sendEnquiry()
    throws Exception
  {
    jTextArea.append("[RS232Emulator] sendEnquiry\n");
    outputStream.write((byte) 5); // ENQ
    outputStream.flush();
    int iByteRead = inputStream.read();
    return iByteRead == 6; // ACK
  }
  
  protected
  void sendEndOfTransmission()
    throws Exception
  {
    jTextArea.append("[RS232Emulator] sendEndOfTransmission\n");
    outputStream.write((byte) 4); // EOT
    outputStream.flush();
  }
}
