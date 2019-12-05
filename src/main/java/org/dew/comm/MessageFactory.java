package org.dew.comm;

import java.util.ArrayList;
import java.util.List;

import org.dew.comm.astm.ASTMMessage;
import org.dew.comm.astm.ASTMMessageParser;
import org.dew.comm.csv.CSVMessage;
import org.dew.comm.csv.CSVMessageParser;
import org.dew.comm.xml.XmlMessage;
import org.dew.comm.xml.XmlMessageParser;

/**
 * Classe factory dei messaggi.<br />
 * I tipi supportati sono i seguenti:<br />
 * <br />
 * - ASTM<br />
 * - XML<br />
 * - CSV<br />
 */
public 
class MessageFactory 
{
  /**
   * Restituisce l'istanza di un messaggio secondo il tipo specificato.
   * 
   * @param sType Tipo messaggio
   * @return
   */
  public static
  IMessage getMessage(String sType)
  {
    if(sType == null || sType.length() == 0) sType = "a";
    String sTypeLC = sType.toLowerCase();
    char c0 = sTypeLC.charAt(0);
    if(c0 == 'a') {
      return new ASTMMessage();
    }
    else
    if(c0 == 'x') {
      return new XmlMessage();
    }
    return new CSVMessage();
  }
  
  /**
   * Restituisce l'istanza di un messaggio secondo il tipo del messaggio specificato.
   * 
   * @param IMessage messaggio
   * @return
   */
  public static
  IMessage newMessage(IMessage message)
  {
    if(message instanceof ASTMMessage) {
      return new ASTMMessage();
    }
    else
    if(message instanceof XmlMessage) {
      return new XmlMessage();
    }
    else
    if(message instanceof CSVMessage) {
      return new CSVMessage();
    }
    return null;
  }
  
  public static
  IMessage parse(byte[] abMessage)
    throws Exception
  {
    if(abMessage == null || abMessage.length < 6) return null;
    boolean boETX = false;
    boolean boETB = false;
    StringBuffer sbMessage = new StringBuffer();
    for(int i = 0; i < abMessage.length; i++) {
      byte b = abMessage[i];
      if(b == 2) { // <STX>
        // Dopo <STX> si ha il frame sequence number (1 byte da scartare)
        i++;
        continue;
      }
      else
      if(b == 3) { // <ETX> 
        // Dopo <ETX> si ha il checksum seguito da <CR><LF>
        boETX = true;
      }
      else
      if(b == 23) { // <ETB> 
        // Dopo <ETB> si ha il checksum seguito da <CR><LF>
        boETB = true;
      }
      
      if(boETX || boETB) {
        if(b == 10) { // <LF>
          boETX = false;
          boETB = false;
        }
      }
      
      if(!boETX && !boETB) {
        if(b == 13 || b == 9 || b == 10 || b > 31) { // <CR> || <TAB> || <LF> || > 31
          sbMessage.append((char) b);
        }
      }
    }
    String sMessage = sbMessage.toString();
    if(sMessage.startsWith("<?xml")) {
      // XML
      XmlMessageParser xmlMessageParser = new XmlMessageParser();
      return xmlMessageParser.parse(sMessage);
    }
    else
    if(sMessage.startsWith("H|")) {
      // ASTM 
      return ASTMMessageParser.parse(sMessage);
    }
    return CSVMessageParser.parse(sMessage);
  }
  
  /**
   * Restituisce i tipi di messaggio disponibili.
   * 
   * @return Vector
   */
  public static
  List<List<String>> getAvailableTypes()
  {
    List<List<String>> listResult = new ArrayList<List<String>>();
    
    List<String> vRecord = new ArrayList<String>();
    vRecord.add("ASTM");
    vRecord.add("Tracciato ASTM");
    listResult.add(vRecord);
    
    vRecord = new ArrayList<String>();
    vRecord.add("CSV");
    vRecord.add("Comma Separated Value");
    listResult.add(vRecord);
    
    vRecord = new ArrayList<String>();
    vRecord.add("XML");
    vRecord.add("Tracciato XML");
    listResult.add(vRecord);
    
    return listResult;
  }
}
