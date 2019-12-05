package org.dew.comm.astm;

import org.dew.comm.IRecord;

import java.util.*;

public 
class ASTMPatient implements IRecord
{
  private int sequenceNumber = 1;
  private String patientId;
  private String lastName;
  private String firstName;
  private String middleInitial;
  private Date dateOfBirth;
  private String sex;
  private String attendingPhysician;
  private String location;
  
  public ASTMPatient()
  {
  }
  
  public ASTMPatient(String sPatientId)
  {
    this.patientId   = sPatientId;
  }
  
  public ASTMPatient(String sPatientId, String sLastName, String sFirstName, String sDateOfBirth, String sSex)
  {
    this.patientId   = sPatientId;
    this.lastName    = sLastName;
    this.firstName   = sFirstName;
    this.dateOfBirth = Utils.stringToDate(sDateOfBirth);
    this.sex         = sSex;
  }
  
  public ASTMPatient(String sLastName, String sFirstName, String sDateOfBirth, String sSex)
  {
    this.lastName    = sLastName;
    this.firstName   = sFirstName;
    this.dateOfBirth = Utils.stringToDate(sDateOfBirth);
    this.sex         = sSex;
  }
  
  public
  void setData(String[] asData)
  {
    if(asData == null) return;
    if(asData.length > 0 && asData[0] != null) patientId     = asData[0];
    if(asData.length > 1 && asData[1] != null) lastName      = asData[1];
    if(asData.length > 2 && asData[2] != null) firstName     = asData[2];
    if(asData.length > 3 && asData[3] != null) dateOfBirth   = Utils.stringToDate(asData[3]);
    if(asData.length > 4 && asData[4] != null) sex           = asData[4];
    if(asData.length > 5 && asData[5] != null) attendingPhysician = asData[5];
    if(asData.length > 6 && asData[6] != null) location      = asData[6];
    if(asData.length > 7 && asData[7] != null) middleInitial = asData[7];
  }
  
  public 
  String[] getData() 
  {
    String[] asResult = new String[8];
    asResult[0] = patientId;
    asResult[1] = lastName;
    asResult[2] = firstName;
    asResult[3] = Utils.formatDate(dateOfBirth);
    asResult[4] = sex;
    asResult[5] = attendingPhysician;
    asResult[6] = location;
    asResult[7] = middleInitial;
    return asResult;
  }
  
  public 
  Type getType() 
  {
    return Type.PATIENT;
  }
  
  public int getSequenceNumber() {
    return sequenceNumber;
  }

  public void setSequenceNumber(int sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
  }

  public String getPatientId() {
    return patientId;
  }

  public void setPatientId(String patientId) {
    this.patientId = patientId;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getMiddleInitial() {
    return middleInitial;
  }

  public void setMiddleInitial(String middleInitial) {
    this.middleInitial = middleInitial;
  }

  public Date getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(Date dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }

  public String getAttendingPhysician() {
    return attendingPhysician;
  }

  public void setAttendingPhysician(String attendingPhysician) {
    this.attendingPhysician = attendingPhysician;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }
  
  public
  String toString()
  {
    StringBuffer sbResult = new StringBuffer();
    sbResult.append('P');
    sbResult.append('|');
    sbResult.append(sequenceNumber);
    sbResult.append('|');
    sbResult.append(patientId != null ? patientId : "");
    sbResult.append("|||");
    sbResult.append(lastName + '^' + firstName);
    if(middleInitial != null && middleInitial.length() > 0) {
      sbResult.append('^' + middleInitial);
    }
    sbResult.append("||");
    sbResult.append(Utils.formatDate(dateOfBirth));
    sbResult.append('|');
    sbResult.append(sex != null ? sex : "");
    sbResult.append("|||||");
    sbResult.append(attendingPhysician != null ? attendingPhysician : "");
    sbResult.append("||||||||||||");
    sbResult.append(location != null ? location : "");
    sbResult.append((char) 13); // <CR>
    return sbResult.toString();
  }
    
  public
  byte[] getBytes()
  {
    return toString().getBytes();
  }
}
