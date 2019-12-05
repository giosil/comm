package org.dew.comm.xml;

import org.dew.comm.IRecord;

public 
class XmlResultComment implements IRecord 
{
  protected String[] asData;
  
  public byte[] getBytes() {
    return toString().getBytes();
  }
  
  public Type getType() {
    return Type.RESULT_COMMENT;
  }
  
  public void setData(String[] asData) {
    this.asData = asData;
  }
  
  public String[] getData() {
    return asData;
  }
  
  public String toString() {
    if(asData == null) return "";
    String sResult = "<ResultComment>";
    if(asData.length > 0 && asData[0] != null) sResult += "<Code>" + asData[0] + "</Code>";
    if(asData.length > 1 && asData[1] != null) sResult += "<Text>" + asData[1] + "</Text>";
    if(asData.length > 2 && asData[2] != null) sResult += "<Type>" + asData[2] + "</Type>";
    sResult += "</ResultComment>";
    return sResult;
  }
}

