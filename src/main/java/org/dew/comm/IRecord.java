package org.dew.comm;

/**
 * Interfaccia che definisce il record di un messaggio.
 */
public 
interface IRecord 
{
  /**
   *  Enumerazione delle tipologie di record. 
   */
  public enum Type {HEADER, PATIENT, PATIENT_COMMENT, ORDER, QUERY, RESULT, RESULT_COMMENT, TERMINATION};
  
  /**
   * Restituisce il tipo del messaggio.
   * 
   * @return Type
   */
  public Type getType();
  
  /**
   * Imposta i dati del record.
   * 
   * @param asData dati del record
   */
  public void setData(String[] asData);
  
  /**
   * Restituisce i dati del record.
   * 
   * @return String[]
   */
  public String[] getData();
  
  /**
   * Restituisce la rappresentazione binaria del record.
   * 
   * @return byte[]
   */
  public byte[] getBytes();
}
