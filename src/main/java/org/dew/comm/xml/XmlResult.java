package org.dew.comm.xml;

import java.util.*;

import org.dew.comm.IRecord;

public 
class XmlResult implements IRecord 
{
  protected String[] asData;
  
  public byte[] getBytes() {
    return toString().getBytes();
  }
  
  public Type getType() {
    return Type.RESULT;
  }
  
  public void setData(String[] asData) {
    this.asData = asData;
  }
  
  public String[] getData() {
    return asData;
  }
  
  public String toString() {
    if(asData == null) return "";
    String sResult = "<Result>";
    if(asData.length > 0 && asData[0] != null) sResult += "<Test>"          + asData[0] + "</Test>";
    if(asData.length > 1 && asData[1] != null) sResult += "<ResultAspects>" + asData[1] + "</ResultAspects>";
    if(asData.length > 2 && asData[2] != null) sResult += "<DataValue>"     + asData[2] + "</DataValue>";
    if(asData.length > 3 && asData[3] != null) sResult += "<Units>"         + asData[3] + "</Units>";
    if(asData.length > 4 && asData[4] != null) {
      Date dateTime = Utils.stringToDateTime(asData[4]);
      sResult += "<DateTestCompleted>" + Utils.formatDateTime(dateTime) + "</DateTestCompleted>";
    }
    sResult += "</Result>";
    return sResult;
  }
}
