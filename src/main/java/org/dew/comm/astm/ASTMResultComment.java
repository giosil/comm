package org.dew.comm.astm;

import org.dew.comm.IRecord;

public 
class ASTMResultComment implements IRecord
{
  private int sequenceNumber = 1;
  private String source = "";
  private String code;
  private String text;
  private String type = "G";
  
  public ASTMResultComment()
  {
  }
  
  public ASTMResultComment(String sCode)
  {
    this.code = sCode;
  }
  
  public ASTMResultComment(String sCode, String sText)
  {
    this.code = sCode;
    this.text = sText;
  }
  
  public
  void setData(String[] asData)
  {
    if(asData == null) return;
    if(asData.length > 0 && asData[0] != null) code = asData[0];
    if(asData.length > 1 && asData[1] != null) text = asData[1];
    if(asData.length > 2 && asData[2] != null) type = asData[2];
  }
  
  public 
  String[] getData() 
  {
    String[] asResult = new String[3];
    asResult[0] = code;
    asResult[1] = text;
    asResult[2] = type;
    return asResult;
  }
  
  public 
  Type getType() 
  {
    return Type.RESULT_COMMENT;
  }
  
  public int getSequenceNumber() {
    return sequenceNumber;
  }

  public void setSequenceNumber(int sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getTypeComment() {
    return type;
  }

  public void setTypeComment(String type) {
    this.type = type;
  }
  
  public
  String toString()
  {
    StringBuffer sbResult = new StringBuffer();
    sbResult.append('C');
    sbResult.append('|');
    sbResult.append(sequenceNumber);
    sbResult.append('|');
    sbResult.append(source != null ? source : "");
    sbResult.append('|');
    sbResult.append(code);
    sbResult.append(text != null && text.length() > 0 ? "^" + text : "");
    sbResult.append('|');
    sbResult.append(type);
    sbResult.append((char) 13); // <CR>
    return sbResult.toString();
  }
  
  public
  byte[] getBytes()
  {
    return toString().getBytes();
  }
}
