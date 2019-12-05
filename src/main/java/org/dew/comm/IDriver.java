package org.dew.comm;

import java.io.*;
import java.util.*;

/**
 * Interfaccia che definisce un driver di comunicazione per l'invio dei messaggi.
 */
public 
interface IDriver 
{
  /**
   * Voce di configurazione dedicata alla porta.
   */
  public final static String sPORT = "port";
  
  /**
   * Imposta la porta del driver.<br />
   * Esempio: "COM1", "localhost:1234", "/folder/file.msg", "http://localhost", ecc. 
   * 
   * @param oPort Porta del driver
   */
  public void setPort(Object oPort);
  
  /**
   * Imposta l'oggetto PrintStream dove viene riportato il log del driver. 
   * 
   * @param ps PrintStream
   */
  public void setLogPrintStream(PrintStream ps);
  
  /**
   * Inizializza il driver con l'oggetto Properties di configurazione.
   * 
   * @param properties Properties di configurazione
   * @throws Exception
   */
  public void init(Properties properties) throws Exception;
  
  /**
   * Invia il messaggio e restituisce l'eventuale messaggio di risposta.
   * 
   * @param message IMessage
   * @return IMessage
   * @throws Exception
   */
  public IMessage sendMessage(IMessage message) throws Exception;

  /**
   * Resta in ascolto di messaggi da parte dell'host.
   * 
   * @param message Messaggio da restituire in caso di query
   * @return messaggio ricevuto
   * @throws Exception
   */
  public IMessage waitForHostMessages(IMessage message) throws Exception;
  
  /**
   * Invoca le operazioni di rilascio.  
   */
  public void destroy();
}
