package org.dew.comm.file;

import java.io.*;
import java.util.Properties;

import org.dew.comm.*;
import org.dew.comm.astm.ASTMMessage;

public 
class FileDriver implements IDriver
{
	protected OutputStream outputStream;
	protected PrintStream psLog = new PrintStream(new NullOutputStream());
	protected Object port;
	
	public 
	FileDriver()
	{
	}
	
	public 
	FileDriver(File file)
		throws Exception
	{
		Properties properties = new Properties();
		properties.setProperty(sPORT, file.getAbsolutePath());
		init(properties);
	}
	
	public 
	FileDriver(String sFilePath)
		throws Exception
	{
		Properties properties = new Properties();
		if(sFilePath == null || sFilePath.length() == 0) {
			properties.put(sPORT, System.out);
		}
		else {
			properties.setProperty(sPORT, sFilePath);
		}
		init(properties);
	}
	
	public 
	FileDriver(OutputStream outputStream)
		throws Exception
	{
		Properties properties = new Properties();
		properties.put(sPORT, outputStream);
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
		psLog.println("[FileDriver] init " + properties);
		Object oPort = properties != null ? properties.get(sPORT) : null;
		if(oPort == null) oPort = port;
		port = oPort;
		if(port == null) throw new Exception("Not specified port");
	}

	public 
	IMessage sendMessage(IMessage message) 
		throws Exception 
	{
		if(outputStream == null) {
			if(port instanceof OutputStream) {
				outputStream = (OutputStream) port;
			}
			else
				if(port instanceof File) {
					outputStream = new FileOutputStream((File) port);
				}
				else {
					if(port == null) throw new Exception("Not specified port");
					String sFilePath = port.toString();
					int iSepInOut = sFilePath.indexOf('|');
					if(iSepInOut > 0) {
						sFilePath = sFilePath.substring(0, iSepInOut);
					}
					File file = new File(sFilePath);
					outputStream = new FileOutputStream(file);
				}
		}
		
		psLog.println("[FileDriver] sendMessage");
		if(message == null) throw new Exception("Message is null.");
		int iRecordsCount = message.getRecordsCount();
		for(int i = 0; i < iRecordsCount; i++) {
			IRecord record = message.getRecord(i);
			byte[] abRecord = record.getBytes();
			psLog.println("[FileDriver] write record " + record.getType() + "...");
			if(abRecord != null && abRecord.length > 0) {
				outputStream.write(abRecord);
				if(message instanceof ASTMMessage) {
					outputStream.write(13);
					outputStream.write(10);
				}
				outputStream.flush();
			}
		}
		psLog.println("[FileDriver] message writed");
		return null;
	}
	
	public 
	IMessage waitForHostMessages(IMessage message) 
		throws Exception
	{
		psLog.println("[FileDriver] waitForHostMessages");
		InputStream is = null;
		if(port instanceof OutputStream) {
			is = (InputStream) port;
		}
		else
			if(port instanceof File) {
				File file = (File) port;
				if(!file.exists()) {
					return null;
				}
				is = new FileInputStream((File) port);
			}
			else {
				if(port == null) throw new Exception("Not specified port");
				String sFilePath = port.toString();
				String sFolder   = getFolder(sFilePath);
				String sFileOut  = "out.txt";
				int iSepInOut = sFilePath.indexOf('|');
				if(iSepInOut > 0) {
					sFileOut = sFilePath.substring(iSepInOut + 1);
				}
				String sFileOutput = sFolder + File.separator + sFileOut;
				File file = new File(sFileOutput);
				if(!file.exists()) {
					psLog.println("[FileDriver] file " + sFileOutput + " not found.");
					return null;
				}
				is = new FileInputStream(file);
			}
		if(is == null) throw new Exception("Can't create InputStream");
		int iAvailable = is.available();
		if(iAvailable < 3) return null;
		byte[] abMessage = new byte[iAvailable];
		is.read(abMessage);
		return MessageFactory.parse(abMessage);
	}
	
	public 
	void destroy() 
	{
		psLog.println("[FileDriver] destroy");
		if(outputStream != null && outputStream != System.out) {
			try{ outputStream.close(); } catch(Exception ex) {}
		}
		outputStream = null;
	}
	
	protected
	void finalize()
	{
		destroy();
	}
	
	protected 
	String getFolder(String sFilePath) 
	{
		int iLength = sFilePath.length();
		for (int i = 1; i <= iLength; i++) {
			int iIndex = iLength - i;
			char c = sFilePath.charAt(iLength - i);
			if (c == '/' || c == '\\') {
				return sFilePath.substring(0, iIndex);
			}
		}
		return "";
	}
}
