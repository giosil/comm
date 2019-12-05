package org.dew.comm.xml;

import org.dew.comm.IRecord;

public 
class XmlTermination implements IRecord 
{
  public byte[] getBytes() {
    return toString().getBytes();
  }
  
  public Type getType() {
    return Type.TERMINATION;
  }
  
  public void setData(String[] asData) {
  }
  
  public String[] getData() {
    return new String[0];
  }
  
  public String toString() {
    return "</Message>";
  }
}

