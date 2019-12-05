package org.dew.comm.astm;

import org.dew.comm.IRecord;

import java.util.*;

public 
class ASTMOrder implements IRecord
{
  private int sequenceNumber = 1;
  private String specimenId;
  private List<UniversalTestId> listOfTest;
  private String priority   = "R";
  private String actionCode = "Q";
  private String reportType = "O";
  
  public ASTMOrder()
  {
  }
  
  public ASTMOrder(String sSpecimenId, String... asManufacturersCode)
  {
    this.specimenId = sSpecimenId;
    this.listOfTest = new ArrayList<UniversalTestId>();
    if(asManufacturersCode != null) {
      for(String sManufacturersCode : asManufacturersCode) {
        listOfTest.add(new UniversalTestId(sManufacturersCode));
      }
    }
  }
  
  public
  void setData(String[] asData)
  {
    this.listOfTest = new ArrayList<UniversalTestId>();
    if(asData == null) return;
    if(asData.length > 0 && asData[0] != null) specimenId = asData[0];
    if(asData.length > 1 && asData[1] != null) priority   = asData[1];
    if(asData.length > 2 && asData[2] != null) actionCode = asData[2];
    if(asData.length > 3 && asData[3] != null) reportType = asData[3];
    if(asData.length > 4) {
      for(int i = 4; i < asData.length; i++) {
        listOfTest.add(new UniversalTestId(asData[i]));
      }
    }
  }
  
  public 
  String[] getData() 
  {
    int iCountTest = listOfTest != null ? listOfTest.size() : 0;
    String[] asResult = new String[4 + iCountTest];
    asResult[0] = specimenId;
    asResult[1] = priority;
    asResult[2] = actionCode;
    asResult[3] = reportType;
    if(listOfTest != null) {
      for(int i = 0; i < listOfTest.size(); i++) {
        UniversalTestId universalTestId = listOfTest.get(i);
        asResult[i + 4] = universalTestId.getManufacturersCode();
      }
    }
    return asResult;
  }
  
  public 
  Type getType() 
  {
    return Type.ORDER;
  }
  
  public int getSequenceNumber() {
    return sequenceNumber;
  }

  public void setSequenceNumber(int sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
  }

  public String getSpecimenId() {
    return specimenId;
  }

  public void setSpecimenId(String specimenId) {
    this.specimenId = specimenId;
  }

  public List<UniversalTestId> getListOfTest() {
    return listOfTest;
  }

  public void setListOfTest(List<UniversalTestId> listOfTest) {
    this.listOfTest = listOfTest;
  }

  public String getPriority() {
    return priority;
  }

  public void setPriority(String priority) {
    this.priority = priority;
  }

  public String getActionCode() {
    return actionCode;
  }

  public void setActionCode(String actionCode) {
    this.actionCode = actionCode;
  }

  public String getReportType() {
    return reportType;
  }

  public void setReportType(String reportType) {
    this.reportType = reportType;
  }
  
  public
  String toString()
  {
    if(listOfTest == null) listOfTest = new ArrayList<UniversalTestId>();
    StringBuffer sbResult = new StringBuffer();
    sbResult.append('O');
    sbResult.append('|');
    sbResult.append(sequenceNumber);
    sbResult.append('|');
    sbResult.append(specimenId);
    sbResult.append("||");
    String sListOfTest = "";
    for(UniversalTestId test : listOfTest) {
      sListOfTest += "\\" + test;
    }
    if(sListOfTest.length() > 0) sListOfTest = sListOfTest.substring(1);
    sbResult.append(sListOfTest);
    sbResult.append('|');
    sbResult.append(priority);
    sbResult.append("||||||");
    sbResult.append(actionCode);
    sbResult.append("||||||||||||||");
    sbResult.append(reportType);
    sbResult.append((char) 13); // <CR>
    return sbResult.toString();
  }
  
  public
  byte[] getBytes()
  {
    return toString().getBytes();
  }
}
