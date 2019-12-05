package org.dew.comm;

import java.io.*;

/**
 * Classe di utilita' utilizzata per annullare il log nei Driver ed Emulator.
 */
public 
class NullOutputStream extends OutputStream 
{
  /**
   * Implementazione vuota del metodo write
   * 
   * @param iByte byte
   */
  public void write(int iByte) throws java.io.IOException {}
}
