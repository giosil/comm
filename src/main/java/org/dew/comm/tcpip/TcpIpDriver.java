package org.dew.comm.tcpip;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import org.dew.comm.*;
import org.dew.comm.astm.ASTMMessage;

public 
class TcpIpDriver implements IDriver
{
	protected Socket socket;
	protected InputStream inputStream;
	protected OutputStream outputStream;
	protected PrintStream psLog = new PrintStream(new NullOutputStream());
	protected Object port;
	protected int iPort = 8888;
	protected String sHost = "localhost";
	
	public 
	TcpIpDriver()
	{
	}
	
	public 
	TcpIpDriver(String sHostPort)
		throws Exception
	{
		Properties properties = new Properties();
		properties.setProperty(sPORT, sHostPort);
		init(properties);
	}
	
	public 
	TcpIpDriver(String sHost, int iPort)
			throws Exception
	{
		Properties properties = new Properties();
		properties.setProperty(sPORT, sHost + ":" + iPort);
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
	
	public 
	void init(Properties properties) 
		throws Exception 
	{
		psLog.println("[TcpIpDriver] init " + properties);
		String sPort = properties != null ? properties.getProperty(sPORT) : null;
		if(sPort == null || sPort.length() == 0)  {
			sPort = port != null ? port.toString() : null;
		}
		port = sPort;
		if(port == null) throw new Exception("Not specified port");
		
		int iSep = sPort.indexOf(':');
		if(iSep > 0) {
			sHost = sPort.substring(0, iSep);
			iPort = Integer.parseInt(sPort.substring(iSep + 1));
		}
		else {
			sHost = "localhost";
			iPort = Integer.parseInt(sPort);
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
		psLog.println("[TcpIpDriver] waitForHostMessages");
		psLog.println("[TcpIpDriver] listen on " + iPort + "...");
		ServerSocket serverSocket = null;
		IMessage messageReceived  = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			serverSocket = new ServerSocket(iPort);
			socket = serverSocket.accept();
			psLog.println("[TcpIpDriver] begin session");
			
			inputStream  = socket.getInputStream();
			outputStream = socket.getOutputStream();
			while(true) {
				byte[] buffer = new byte[255];
				inputStream.read(buffer);
				CommUtils.append(baos, buffer);
				psLog.println("[TcpIpDriver] " + CommUtils.getString(buffer));
				byte byte0 = buffer[0];
				if(byte0 != 4) { // EOT
					outputStream.write((byte) 6); // <ACK>
					psLog.println("[TcpIpDriver] -> <ACK>");
				}
				else {
					psLog.println("[TcpIpDriver] EOT received");
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
						if(message != null) {
							sendMessage(message, false, true);
							break;
						}
						else {
							psLog.println("[TcpIpDriver] -> <NAK>");
							outputStream.write((byte) 21); // <NAK>
						}
					}
					else {
						psLog.println("[TcpIpDriver] -> <ACK>");
						outputStream.write((byte) 6); // <ACK>
					}
				}
			}
		}
		catch(Exception ex) {
			ex.printStackTrace(psLog);
		}
		finally {
			psLog.println("[TcpIpDriver] end session");
			if(outputStream != null) try{ outputStream.close(); } catch(Exception ex) {}
			if(inputStream != null) try{ inputStream.close(); } catch(Exception ex) {}
			try{ socket.close();  } catch(Exception ex) {}
		}
		return messageReceived;
	}
	
	public 
	void destroy() 
	{
		psLog.println("[TcpIpDriver] destroy");
		if(inputStream  != null) try{ inputStream.close();  } catch(Exception ex) {}
		if(outputStream != null) try{ outputStream.close(); } catch(Exception ex) {}
		if(socket       != null) try{ socket.close();       } catch(Exception ex) {}
		inputStream  = null;
		outputStream = null;
		socket       = null;
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
		psLog.println("[TcpIpDriver] sendMessage");
		if(socket == null) {
			socket = new Socket(sHost, iPort);
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
		}
		if(message == null) throw new Exception("Message is null.");
		if(outputStream == null) throw new Exception("TcpIpDriver not initialized.");
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
				psLog.println("[TcpIpDriver] send record " + record.getType() + "...");
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
							outputStream.write(CommUtils.getASTMFrame(iFrameSequenceNumber, bET, abRecord, iOffset, iLen));
							outputStream.flush();
							int iByteRead = inputStream.read();
							if(iByteRead != 6) throw new Exception("Frame " + iFrameSequenceNumber + " not accepted.");
						}
					}
					else {
						outputStream.write(abRecord);
						outputStream.flush();
						int iByteRead = inputStream.read();
						if(iByteRead != 6) throw new Exception("Record " + i + " not accepted.");
					}
				}
			}
			if(boSendEndOfTransmission) sendEndOfTransmission();
			psLog.println("[TcpIpDriver] message sent");
			if(boExpectedResponse) {
				psLog.println("[TcpIpDriver] read results");
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				while(true) {
					byte[] buffer = new byte[255];
					inputStream.read(buffer);
					psLog.println("[TcpIpDriver] " + CommUtils.getString(buffer));
					CommUtils.append(baos, buffer);
					byte byte0 = buffer[0];
					if(byte0 != 4) { // EOT
						outputStream.write((byte) 6); // <ACK>
						psLog.println("[TcpIpDriver] -> <ACK>");
					}
					else {
						psLog.println("[TcpIpDriver] EOT received");
						break;
					}
				}
				byte[] abMessage = baos.toByteArray();
				return MessageFactory.parse(abMessage);
			}
		}
		else {
			psLog.println("[TcpIpDriver] Enquiry not accepted.");
			throw new Exception("Enquiry not accepted.");
		}
		return null;
	}
	
	protected
	boolean sendEnquiry()
		throws Exception
	{
		psLog.println("[TcpIpDriver] sendEnquiry");
		outputStream.write((byte) 5); // ENQ
		outputStream.flush();
		int iByteRead = inputStream.read();
		return iByteRead == 6; // ACK
	}
	
	protected
	void sendEndOfTransmission()
		throws Exception
	{
		psLog.println("[TcpIpDriver] sendEndOfTransmission");
		outputStream.write((byte) 4); // EOT
		outputStream.flush();
	}
}
