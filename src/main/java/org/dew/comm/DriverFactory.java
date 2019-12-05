package org.dew.comm;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.dew.comm.file.*;
import org.dew.comm.http.*;
import org.dew.comm.rs232.*;
import org.dew.comm.tcpip.*;

/**
 * Classe factory dei driver.<br />
 * I tipi supportati sono i seguenti:<br />
 * <br />
 * - RS232 (porta seriale)<br />
 * - TCP/IP<br />
 * - HTTP<br />
 * - File<br />
 * - Console<br />
 * - Err (Console 2)<br />
 */
public 
class DriverFactory 
{  
  /**
   * Restituisce l'istanza del driver secondo il tipo specificato.
   * 
   * @param sType Tipo del driver
   * @param oPort Porta del driver
   * @return IDriver
   * @throws Exception
   */
  public static
  IDriver getDriver(String sType, Object oPort)
    throws Exception
  {
    if(sType == null || sType.length() == 0) sType = "c";
    String sTypeLC = sType.toLowerCase();
    char c0 = sTypeLC.charAt(0);
    IDriver driver = null;
    if(c0 == 't') {
      driver = new TcpIpDriver();
      driver.setPort(oPort);
    }
    else if(c0 == 'r' || c0 == 's') {
      driver = new RS232Driver();
      driver.setPort(oPort);
    }
    else if(c0 == 'h') {
      driver = new HttpDriver();
      driver.setPort(oPort);
    }
    else if(c0 == 'f') {
      driver = new FileDriver();
      driver.setPort(oPort);
    }
    else if(c0 == 'c') {
      driver = new FileDriver();
      driver.setPort(System.out);
    }
    else if(c0 == 'e') {
      driver = new FileDriver();
      driver.setPort(System.err);
    }
    else {
      driver = new FileDriver();
      driver.setPort(System.out);
    }
    driver.init(null);
    return driver;
  }
  
  /**
   * Restituisce l'istanza del driver secondo il tipo specificato.
   * 
   * @param sType Tipo del driver
   * @param properties Properties di configurazione
   * @return IDriver
   * @throws Exception
   */  
  public static
  IDriver getDriver(String sType, Properties properties)
    throws Exception
  {
    if(sType == null || sType.length() == 0) sType = "c";
    String sTypeLC = sType.toLowerCase();
    char c0 = sTypeLC.charAt(0);
    IDriver driver = null;
    if(c0 == 't') {
      driver = new TcpIpDriver();
      driver.init(properties);
    }
    else if(c0 == 'r' || c0 == 's') {
      driver = new RS232Driver();
      driver.init(properties);
    }
    else if(c0 == 'h') {
      driver = new HttpDriver();
      driver.init(properties);
    }
    else if(c0 == 'f') {
      driver = new FileDriver();
      driver.init(properties);
    }
    else if(c0 == 'c') {
      driver = new FileDriver();
      driver.setPort(System.out);
      driver.init(properties);
    }
    else if(c0 == 'e') {
      driver = new FileDriver();
      driver.setPort(System.err);
      driver.init(properties);
    }
    else {
      driver = new FileDriver();
      driver.setPort(System.out);
      driver.init(properties);
    }
    return driver;
  }
  
  /**
   * Restituisce i driver disponibili (sono esclusi quelli dedicati principalmente allo sviluppo).
   * 
   * @return List<List<String>>
   */
  public static
  List<List<String>> getAvailableDrivers()
  {
    List<List<String>> listResult = new ArrayList<List<String>>();
    
    List<String> vRecord = new ArrayList<String>();
    vRecord.add("RS232");
    vRecord.add("Porta Seriale (RS232)");
    listResult.add(vRecord);
    
    vRecord = new ArrayList<String>();
    vRecord.add("TCP/IP");
    vRecord.add("Protocollo TCP/IP");
    listResult.add(vRecord);
    
    vRecord = new ArrayList<String>();
    vRecord.add("FILE");
    vRecord.add("File dati");
    listResult.add(vRecord);
    
    vRecord = new ArrayList<String>();
    vRecord.add("HTTP");
    vRecord.add("Protocollo HTTP");
    listResult.add(vRecord);
    
    return listResult;
  }
}
