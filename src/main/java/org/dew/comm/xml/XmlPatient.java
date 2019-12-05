package org.dew.comm.xml;

import org.dew.comm.IRecord;

import java.util.Date;

public 
class XmlPatient implements IRecord 
{
  protected String[] asData;
  protected boolean partOfOrders = false;

  public void setPartOfOrders(boolean partOfOrders) {
    this.partOfOrders = partOfOrders;
  }
  public boolean isPartOfOrders() {
    return partOfOrders;
  }
  
  public byte[] getBytes() {
    return toString().getBytes();
  }
  
  public Type getType() {
    return Type.PATIENT;
  }
  
  public void setData(String[] asData) {
    this.asData = asData;
  }
  
  public String[] getData() {
    return asData;
  }
  
  public String toString() {
    if(asData == null) return "";
    String sResult = "";
    if(partOfOrders) {
      sResult += "<Orders><Patient>";
    }
    else {
      sResult += "<Patient>";
    }
    if(asData.length > 0 && asData[0] != null) sResult += "<PatientId>" + asData[0] + "</PatientId>";
    if(asData.length > 1 && asData[1] != null) sResult += "<LastName>"  + asData[1] + "</LastName>";
    if(asData.length > 2 && asData[2] != null) sResult += "<FirstName>" + asData[2] + "</FirstName>";
    if(asData.length > 3 && asData[3] != null) {
      Date dateOfBirth = Utils.stringToDate(asData[3]);
      sResult += "<DateOfBirth>" + Utils.formatDate(dateOfBirth) + "</DateOfBirth>";
    }
    if(asData.length > 4 && asData[4] != null) sResult += "<Sex>" + asData[4] + "</Sex>";
    if(asData.length > 5 && asData[5] != null) sResult += "<AttendingPhysician>" + asData[5] + "</AttendingPhysician>";
    if(asData.length > 6 && asData[6] != null) sResult += "<Location>"           + asData[6] + "</Location>";
    if(asData.length > 7 && asData[7] != null) sResult += "<MiddleInitial>"      + asData[7] + "</MiddleInitial>";
    sResult += "</Patient>";
    return sResult;
  }
}

