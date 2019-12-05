package org.dew.comm.http;

import java.io.*;
import java.net.*;

import java.util.Properties;

import org.dew.comm.*;

public 
class HttpEmulator implements IEmulator, Runnable
{
	protected int iPort = 0;
	protected PrintStream psLog = new PrintStream(new NullOutputStream());
	protected Object port;
	
	public 
	HttpEmulator()
	{
	}

	public 
	HttpEmulator(int iPort)
		throws Exception
	{
		Properties properties = new Properties();
		properties.setProperty(sPORT, String.valueOf(iPort));
		init(properties);
	}
	
	public 
	static void main(String[] args) 
	{
		String sPort = args.length > 0 ? args[0] : "8080";
		try {
			IEmulator emulator = new HttpEmulator();
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
		psLog.println("[HttpEmulator] init " + properties);
		String sPort = properties != null ? properties.getProperty(sPORT) : null;
		if(sPort == null || sPort.length() == 0)  {
			sPort = port != null ? port.toString() : null;
		}
		port = sPort;
		if(port == null) port = "8080";
		iPort = Integer.parseInt(port.toString());
	}

	public 
	void start() 
		throws Exception
	{
		psLog.println("[HttpEmulator] start (port = " + port + ")");
		if(iPort < 1) throw new Exception("HttpEmulator not initialized.");
		new Thread(this).start();
	}
	
	public
	void run() 
	{
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(iPort);
			do {
				Socket socket = serverSocket.accept();
				HttpSessionEmulator session = new HttpSessionEmulator(socket, psLog);
				session.start();
			}
			while(true);
		}
		catch(Exception ex) {
			ex.printStackTrace(psLog);
		}
		finally {
			if(serverSocket != null) try { serverSocket.close(); } catch(Exception ex) {}
		}
	}

	static class HttpSessionEmulator extends Thread
	{
		Socket socket;
		PrintStream psLog;
		
		public HttpSessionEmulator(Socket socket, PrintStream psLog)
		{
			this.socket = socket;
			this.psLog = psLog;
		}
		
		public 
		void run()
		{
			psLog.println("[HttpEmulator] begin session");
			String sDataPosted = null;
			int iRequestContentLength = -1;
			boolean boSendQueryResult = false;
			IMessage messageReceived = null;
			BufferedReader br  = null;
			PrintWriter pw = null;
			try {
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				pw = new PrintWriter(socket.getOutputStream());
				String sLine = null;
				while((sLine = br.readLine()) != null) {
					psLog.println("[HttpEmulator] " + sLine);
					String sLineLC = sLine.toLowerCase();
					if(iRequestContentLength < 0 && sLineLC.startsWith("content-length:")) {
						iRequestContentLength = Integer.parseInt(sLineLC.substring(15).trim());
					}
					if(sLineLC.length() == 0) {
						if(iRequestContentLength <= 0) break;
						char[] acDataPosted = new char[iRequestContentLength];
						psLog.println("[HttpEmulator] Data posted:");
						if(br.read(acDataPosted, 0, iRequestContentLength) != -1) {
							sDataPosted = String.valueOf(acDataPosted);
							psLog.println(sDataPosted);
							messageReceived = MessageFactory.parse(sDataPosted.getBytes());
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
						}
						break;
					}
				}
				String sResponse = null;
				if(boSendQueryResult) {
					IMessage resultMessage = getResultMessage(messageReceived);
					sResponse = resultMessage != null ? resultMessage.toString() : "";
				}
				else {
					sResponse = "OK";
				}
				// Invio risposta HTTP
				pw.println("HTTP/1.1 200 OK");
				pw.println("Content-Type: text/plain");
				pw.println("Content-Length: " + sResponse.length());
				pw.println("Server: 127.0.0.1");
				pw.println("");
				pw.println(sResponse);
			}
			catch(Exception ex) {
				ex.printStackTrace(psLog);
			}
			finally {
				psLog.println("[HttpEmulator] end session");
				if(pw != null) try{ pw.close(); } catch(Exception ex) {}
				if(br != null) try{ br.close(); } catch(Exception ex) {}
				try{ socket.close();  } catch(Exception ex) {}
			}
		}
		
		protected 
		IMessage getResultMessage(IMessage messageReceived) 
				throws Exception 
		{
			psLog.println("[HttpEmulator] getResultMessage");
			IMessage message = MessageFactory.newMessage(messageReceived);
			if(message == null) {
				message = MessageFactory.getMessage("ASTM"); 
			}
			message.addRecord(IRecord.Type.HEADER,      new String[]{"Host", "LIS", "P", "1"});
			message.addRecord(IRecord.Type.PATIENT,     new String[]{"1", "ROSSI", "MARIO", "19/11/1974", "M"});
			message.addRecord(IRecord.Type.ORDER,       new String[]{"7", "R", "Q", "O", "EMO"});
			message.addRecord(IRecord.Type.RESULT,      new String[]{"EMO", "DOSE", "0.18", "uIU/mL"});
			message.addRecord(IRecord.Type.TERMINATION, null);
			return message;
		}
	}
}

