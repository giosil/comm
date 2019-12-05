package org.dew.comm;

import java.io.PrintStream;
import java.util.Properties;

/**
 * Interfaccia che definisce un emulatore.
 */
public 
interface IEmulator 
{
	/**
	 * Voce di configurazione dedicata alla porta.
	 */	
	public final static String sPORT = "port";
	
	/**
	 * Imposta la porta dell'emulatore.<br />
	 * Esempio: "COM1", "localhost:1234", "/folder/file.msg", "http://localhost", ecc. 
	 * 
	 * @param oPort Porta del driver
	 */
	public void setPort(Object oPort);
	
	/**
	 * Imposta l'oggetto PrintStream dove viene riportato il log dell'emulatore. 
	 * 
	 * @param ps PrintStream
	 */
	public void setLogPrintStream(PrintStream ps);
	
	/**
	 * Inizializza l'emulatore con l'oggetto Properties di configurazione.
	 * 
	 * @param properties Properties di configurazione
	 * @throws Exception
	 */
	public void init(Properties properties) throws Exception;
	
	/**
	 * Avvia l'emulatore.
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception;
}
