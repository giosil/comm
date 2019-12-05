package org.dew.comm.csv;

import org.dew.comm.IRecord;

public 
class CSVRecord implements IRecord 
{
  protected Type type;
  protected String[] asData;
  
  public CSVRecord()
  {
  }
  
  public CSVRecord(Type type)
  {
    this.type = type;
  }
  
  public void setType(Type type) {
    this.type = type;
  }
  
  public void setData(String[] asData) {
    this.asData = asData;
  }
  
  public String[] getData() {
    return asData;
  }
  
  public Type getType() {
    return type;
  }
  
  public byte[] getBytes() {
    return toString().getBytes();
  }
  
  public String toString() {
    String sResult = "";
    if(asData != null && asData.length > 0) {
      sResult += type;
      for(int i = 0; i < asData.length; i++) {
        String sValue = asData[i];
        if(sValue == null) sValue = "";
        sResult += ";" + sValue;
      }
      sResult += "\n";
    }
    return sResult;
  }
}
