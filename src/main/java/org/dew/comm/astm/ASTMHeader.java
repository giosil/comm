package org.dew.comm.astm;

import org.dew.comm.IRecord;

public 
class ASTMHeader implements IRecord
{
  private String delimiterCharacters = "|\\^&";
  private String senderId      = "LIS";
  private String receiverId    = "Host";
  private String processingId  = "P";
  private String versionNumber = "1";
  
  public ASTMHeader()
  {
  }
  
  public
  void setData(String[] asData)
  {
    if(asData == null) return;
    if(asData.length > 0 && asData[0] != null) senderId      = asData[0];
    if(asData.length > 1 && asData[1] != null) receiverId    = asData[1];
    if(asData.length > 2 && asData[2] != null) processingId  = asData[2];
    if(asData.length > 3 && asData[3] != null) versionNumber = asData[3];
  }
  
  public 
  String[] getData() 
  {
    String[] asResult = new String[4];
    asResult[0] = senderId;
    asResult[1] = receiverId;
    asResult[2] = processingId;
    asResult[3] = versionNumber;
    return asResult;
  }
  
  public 
  Type getType() 
  {
    return Type.HEADER;
  }

  public String getDelimiterCharacters() {
    return delimiterCharacters;
  }

  public void setDelimiterCharacters(String delimiterCharacters) {
    this.delimiterCharacters = delimiterCharacters;
  }

  public String getSenderId() {
    return senderId;
  }

  public void setSenderId(String senderId) {
    this.senderId = senderId;
  }

  public String getReceiverId() {
    return receiverId;
  }

  public void setReceiverId(String receiverId) {
    this.receiverId = receiverId;
  }

  public String getProcessingId() {
    return processingId;
  }

  public void setProcessingId(String processingId) {
    this.processingId = processingId;
  }

  public String getVersionNumber() {
    return versionNumber;
  }

  public void setVersionNumber(String versionNumber) {
    this.versionNumber = versionNumber;
  }
  
  public
  String toString()
  {
    StringBuffer sbResult = new StringBuffer();
    sbResult.append('H');
    sbResult.append(delimiterCharacters);
    sbResult.append("|||");
    sbResult.append(senderId);
    sbResult.append("|||||");
    sbResult.append(receiverId);
    sbResult.append("||");
    sbResult.append(processingId);
    sbResult.append('|');
    sbResult.append(versionNumber);
    sbResult.append((char) 13); // <CR>
    return sbResult.toString();
  }
  
  public
  byte[] getBytes()
  {
    return toString().getBytes();
  }
}
