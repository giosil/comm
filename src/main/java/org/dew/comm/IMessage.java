package org.dew.comm;

/**
 * Interfaccia che definisce un messaggio.
 */
public 
interface IMessage 
{
	/**
	 * Aggiunge un record al messaggio.
	 * 
	 * @param type Tipo del messaggio
	 * @param asData Dati
	 */
	public void addRecord(IRecord.Type type, String[] asData);
	
	/**
	 * Aggiunge un record al messaggio.
	 * 
	 * @param record Record da aggiungere
	 */
	public void addRecord(IRecord record);
	
	/**
	 * Rimuove un record dal messaggio.
	 * 
	 * @param iIndex indice del messaggio.
	 * @return IRecord
	 */
	public IRecord removeRecord(int iIndex);
	
	/**
	 * Restituice un record del messaggio.
	 * 
	 * @param iIndex indice del messaggio.
	 * @return IRecord
	 */
	public IRecord getRecord(int iIndex);
	
	/**
	 * Restituice il numero di record presenti nel messaggio.
	 * 
	 * @return int
	 */
	public int getRecordsCount();
}
