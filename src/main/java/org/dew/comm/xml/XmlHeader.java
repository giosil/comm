package org.dew.comm.xml;

import org.dew.comm.IRecord;

public 
class XmlHeader implements IRecord 
{
  protected String[] asData;
  
  public byte[] getBytes() {
    return toString().getBytes();
  }
  
  public Type getType() {
    return Type.HEADER;
  }
  
  public void setData(String[] asData) {
    this.asData = asData;
  }
  
  public String[] getData() {
    return asData;
  }
  
  public String toString() {
    String sResult = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    sResult += "<Message>";
    sResult += "<Header>";
    if(asData.length > 0 && asData[0] != null) sResult += "<SenderId>"      + asData[0] + "</SenderId>";
    if(asData.length > 1 && asData[1] != null) sResult += "<ReceiverId>"    + asData[1] + "</ReceiverId>";
    if(asData.length > 2 && asData[2] != null) sResult += "<ProcessingId>"  + asData[2] + "</ProcessingId>";
    if(asData.length > 3 && asData[3] != null) sResult += "<VersionNumber>" + asData[3] + "</VersionNumber>";
    sResult += "</Header>";
    return sResult;
  }
}
