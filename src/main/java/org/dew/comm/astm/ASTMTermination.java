package org.dew.comm.astm;

import org.dew.comm.IRecord;

public 
class ASTMTermination implements IRecord
{
  private int sequenceNumber = 1;
  private String code = "F";
  
  public ASTMTermination()
  {
  }
  
  public
  void setData(String[] asData)
  {
    if(asData == null) return;
    if(asData.length > 0 && asData[0] != null) code = asData[0];
  }
  
  public 
  String[] getData() 
  {
    String[] asResult = new String[1];
    asResult[0] = code;
    return asResult;
  }
  
  public 
  Type getType() 
  {
    return Type.TERMINATION;
  }
  
  public int getSequenceNumber() {
    return sequenceNumber;
  }

  public void setSequenceNumber(int sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }
  
  public
  String toString()
  {
    StringBuffer sbResult = new StringBuffer();
    sbResult.append('L');
    sbResult.append('|');
    sbResult.append(sequenceNumber);
    sbResult.append('|');
    sbResult.append(code);
    sbResult.append((char) 13); // <CR>
    return sbResult.toString();
  }
  
  public
  byte[] getBytes()
  {
    return toString().getBytes();
  }
}

