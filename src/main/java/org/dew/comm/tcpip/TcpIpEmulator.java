package org.dew.comm.tcpip;

import java.io.*;
import java.net.*;

import java.util.Properties;

import org.dew.comm.*;
import org.dew.comm.astm.ASTMMessage;

public 
class TcpIpEmulator implements IEmulator, Runnable
{
	protected int iPort = 0;
	protected PrintStream psLog = new PrintStream(new NullOutputStream());
	protected Object port;
	
	public 
	TcpIpEmulator()
	{
	}
	
	public 
	TcpIpEmulator(int iPort)
		throws Exception
	{
		Properties properties = new Properties();
		properties.setProperty(sPORT, String.valueOf(iPort));
		init(properties);
	}
	
	public 
	static void main(String[] args) 
	{
		String sPort = args.length > 0 ? args[0] : "8888";
		try {
			IEmulator emulator = new TcpIpEmulator();
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
		this.psLog = ps;
		if(psLog == null) psLog = new PrintStream(new NullOutputStream());
	}
	
	public 
	void init(Properties properties) 
		throws Exception
	{
		psLog.println("[TcpIpEmulator] init " + properties);
		String sPort = properties != null ? properties.getProperty(sPORT) : null;
		if(sPort == null || sPort.length() == 0)  {
			sPort = port != null ? port.toString() : null;
		}
		port = sPort;
		if(port == null) port = "8888";
		iPort = Integer.parseInt(port.toString());
	}
	
	public 
	void start() 
		throws Exception
	{
		psLog.println("[TcpIpEmulator] start (port = " + port + ")");
		if(iPort < 1) throw new Exception("TcpIpEmulator not initialized.");
		new Thread(this).start();
	}
	
	public
	void run() 
	{
		ServerSocket serverSocket = null;
		Socket socket = null;
		try {
			serverSocket = new ServerSocket(iPort);
			do {
				socket = serverSocket.accept();
				SessionEmulator session = new SessionEmulator(socket, psLog);
				session.start();
			}
			while(true);
		}
		catch(Exception ex) {
			ex.printStackTrace(psLog);
		}
		finally {
			if(socket != null) try { socket.close(); } catch(Exception ex) {}
			if(serverSocket != null) try { serverSocket.close(); } catch(Exception ex) {}
		}
	}
	
	static class SessionEmulator extends Thread
	{
		Socket socket;
		PrintStream psLog;
		InputStream  inputStream  = null;
		OutputStream outputStream = null;
		
		public SessionEmulator(Socket socket, PrintStream psLog)
		{
			this.socket = socket;
			this.psLog = psLog;
		}
		
		public 
		void run()
		{
			psLog.println("[TcpIpEmulator] begin session");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				inputStream  = socket.getInputStream();
				outputStream = socket.getOutputStream();
				while(true) {
					byte[] buffer = new byte[255];
					inputStream.read(buffer);
					CommUtils.append(baos, buffer);
					psLog.println("[TcpIpEmulator] " + CommUtils.getString(buffer));
					byte byte0 = buffer[0];
					if(byte0 != 4) { // EOT
						outputStream.write((byte) 6); // <ACK>
						psLog.println("[TcpIpEmulator] -> <ACK>");
					}
					else {
						psLog.println("[TcpIpEmulator] EOT received");
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
							sendResult(messageReceived);
							break;
						}
						else {
							psLog.println("[TcpIpEmulator] -> <ACK>");
							outputStream.write((byte) 6); // <ACK>
						}
					}
				}
			}
			catch(Exception ex) {
				ex.printStackTrace(psLog);
			}
			finally {
				psLog.println("[TcpIpEmulator] end session");
				if(outputStream != null) try{ outputStream.close(); } catch(Exception ex) {}
				if(inputStream != null) try{ inputStream.close(); } catch(Exception ex) {}
				try{ socket.close();  } catch(Exception ex) {}
			}
		}
		
		public 
		void sendResult(IMessage messageReceived) 
			throws Exception 
		{
			psLog.println("[TcpIpEmulator] sendResult");
			IMessage message = MessageFactory.newMessage(messageReceived);
			if(message == null) {
				message = MessageFactory.getMessage("ASTM"); 
			}
			message.addRecord(IRecord.Type.HEADER,      new String[]{"Host", "LIS", "P", "1"});
			message.addRecord(IRecord.Type.PATIENT,     new String[]{"1", "ROSSI", "MARIO", "19/11/1974", "M"});
			message.addRecord(IRecord.Type.ORDER,       new String[]{"7", "R", "Q", "O", "EMO"});
			message.addRecord(IRecord.Type.RESULT,      new String[]{"EMO", "DOSE", "0.18", "uIU/mL"});
			message.addRecord(IRecord.Type.TERMINATION, null);
			
			boolean boASTMMessage = message instanceof ASTMMessage;
			if(sendEnquiry()) {
				int iRecordsCount = message.getRecordsCount();
				int iFrameSequenceNumber = 0;
				for(int i = 0; i < iRecordsCount; i++) {
					IRecord record = message.getRecord(i);
					byte[] abRecord = record.getBytes();
					psLog.println("[TcpIpEmulator] send record " + i);
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
								if(iByteRead != 6) {
									psLog.println("[TcpIpEmulator] Frame " + iFrameSequenceNumber + " not accepted.");
									break;
								}
							}
						}
						else {
							outputStream.write(abRecord);
							outputStream.flush();
							int iByteRead = inputStream.read();
							if(iByteRead != 6) {
								psLog.println("[TcpIpEmulator] Record " + i + " not accepted.");
								break;
							}
						}
					}
				}
				sendEndOfTransmission();
				psLog.println("[TcpIpEmulator] message sent");
			}
			else {
				psLog.println("[TcpIpEmulator] Enquiry not accepted.");
			}
		}
		
		private
		boolean sendEnquiry()
			throws Exception
		{
			psLog.println("[TcpIpEmulator] sendEnquiry");
			outputStream.write((byte) 5); // ENQ
			outputStream.flush();
			int iByteRead = inputStream.read();
			return iByteRead == 6; // ACK
		}
		
		private
		void sendEndOfTransmission()
			throws Exception
		{
			psLog.println("[TcpIpEmulator] sendEndOfTransmission");
			outputStream.write((byte) 4); // EOT
			outputStream.flush();
		}
	}
}
