package org.dew.comm.xml;

import java.util.*;

import org.dew.comm.IRecord;

public 
class XmlQuery implements IRecord 
{
  protected String[] asData;
  
  public byte[] getBytes() {
    return toString().getBytes();
  }
  
  public Type getType() {
    return Type.QUERY;
  }
  
  public void setData(String[] asData) {
    this.asData = asData;
  }
  
  public String[] getData() {
    return asData;
  }
  
  public String toString() {
    if(asData == null) return "";
    String sResult = "<Query>";
    if(asData.length > 0 && asData[0] != null) sResult += "<StartingPatientId>" + asData[0] + "</StartingPatientId>";
    if(asData.length > 1 && asData[1] != null) sResult += "<StartingSampleId>" + asData[1] + "</StartingSampleId>";
    if(asData.length > 2 && asData[2] != null) sResult += "<EndingPatientId>" + asData[2] + "</EndingPatientId>";
    if(asData.length > 3 && asData[3] != null) sResult += "<EndingSampleId>" + asData[3] + "</EndingSampleId>";
    if(asData.length > 4 && asData[4] != null) sResult += "<Test>" + asData[4] + "</Test>";
    if(asData.length > 5 && asData[5] != null) sResult += "<NatureOfRequestTimeLimits>" + asData[5] + "</NatureOfRequestTimeLimits>";
    if(asData.length > 6 && asData[6] != null) {
      Date date = Utils.stringToDate(asData[6]);
      sResult += "<BeginningRequestResultsDate>" + Utils.formatDate(date) + "</BeginningRequestResultsDate>";
    }
    if(asData.length > 7 && asData[7] != null) {
      Date date = Utils.stringToDate(asData[7]);
      sResult += "<EndingRequestResultsDate>" + Utils.formatDate(date) + "</EndingRequestResultsDate>";
    }
    if(asData.length > 8 && asData[8] != null) sResult += "<RequestInformationStatusCode>" + asData[8] + "</RequestInformationStatusCode>";
    sResult += "</Query>";
    return sResult;
  }
}
