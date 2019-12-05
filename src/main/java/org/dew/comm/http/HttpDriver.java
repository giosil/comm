package org.dew.comm.http;

import java.io.*;
import java.net.*;
import java.util.Properties;

import org.dew.comm.*;

public 
class HttpDriver implements IDriver
{
  protected URL url;
  protected PrintStream psLog = new PrintStream(new NullOutputStream());
  protected Object port;
  
  public 
  HttpDriver()
  {
  }
  
  public 
  HttpDriver(String sURL)
    throws Exception
  {
    Properties properties = new Properties();
    properties.setProperty(sPORT, sURL);
    init(properties);
  }
  
  public 
  HttpDriver(URL url)
    throws Exception
  {
    Properties properties = new Properties();
    properties.put(sPORT, url);
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
    psLog.println("[HttpDriver] init " + properties);
    Object oPort = properties != null ? properties.get(sPORT) : null;
    if(oPort == null) oPort = port;
    port = oPort;
    if(port == null) throw new Exception("Not specified port");
    
    if(oPort instanceof URL) {
      this.url = (URL) oPort;
    }
    else {
      this.url = new URL(oPort.toString());
    }
  }
  
  public 
  IMessage sendMessage(IMessage message) 
    throws Exception 
  {
    psLog.println("[HttpDriver] sendMessage");
    if(message == null) throw new Exception("Message is null.");
    if(url == null) throw new Exception("HttpDriver not initialized.");
    
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    int iRecordsCount = message.getRecordsCount();
    for(int i = 0; i < iRecordsCount; i++) {
      IRecord record = message.getRecord(i);
      byte[] abRecord = record.getBytes();
      if(abRecord != null && abRecord.length > 0) {
        baos.write(abRecord);
      }
    }
    byte[] abDataToPost = baos.toByteArray();
    
    URLConnection urlConnection = url.openConnection();
    urlConnection.setDoOutput(true);
    urlConnection.setRequestProperty("Content-Type",   "text; charset=utf-8");
    urlConnection.setRequestProperty("Content-Length", String.valueOf(abDataToPost.length));
    
    psLog.println("[HttpDriver] post message");
    OutputStream outputStream = urlConnection.getOutputStream();
    outputStream.write(abDataToPost);
    
    psLog.println("[HttpDriver] get response");
    String sResponse = "";
    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
    String sLine = null;
    while ((sLine = br.readLine()) != null) {
      sResponse += sLine + "\n";
    }
    br.close();
    
    psLog.println("[HttpDriver] message sent (response: " + sResponse + ")");
    return MessageFactory.parse(sResponse.getBytes());
  }
  
  public 
  IMessage waitForHostMessages(IMessage message) 
    throws Exception
  {
    psLog.println("[HttpDriver] waitForHostMessages");
    int iPort = url.getPort();
    if(iPort < 0) iPort = 80;
    
    ServerSocket serverSocket = null;
    Socket socket = null;
    String sDataPosted = null;
    int iRequestContentLength = -1;
    boolean boSendQueryResult = false;
    IMessage messageReceived = null;
    BufferedReader br  = null;
    PrintWriter pw = null;
    try {
      psLog.println("[HttpDriver] listen on " + iPort + "...");
      serverSocket = new ServerSocket(iPort);
      socket = serverSocket.accept();
      psLog.println("[HttpDriver] begin session");
      
      br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      pw = new PrintWriter(socket.getOutputStream());
      String sLine = null;
      while((sLine = br.readLine()) != null) {
        psLog.println("[HttpDriver] " + sLine);
        String sLineLC = sLine.toLowerCase();
        if(iRequestContentLength < 0 && sLineLC.startsWith("content-length:")) {
          iRequestContentLength = Integer.parseInt(sLineLC.substring(15).trim());
        }
        if(sLineLC.length() == 0) {
          if(iRequestContentLength <= 0) break;
          char[] acDataPosted = new char[iRequestContentLength];
          psLog.println("[HttpDriver] Data posted:");
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
        sResponse = message != null ? message.toString() : "";
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
      psLog.println("[HttpDriver] end session");
      if(pw != null) try{ pw.close(); } catch(Exception ex) {}
      if(br != null) try{ br.close(); } catch(Exception ex) {}
      try{ socket.close();  } catch(Exception ex) {}
    }
    return messageReceived;
  }
  
  public 
  void destroy() 
  {
    psLog.println("[HttpDriver] destroy");
    url = null;
  }
  
  protected
  void finalize()
  {
    destroy();
  }  
}
