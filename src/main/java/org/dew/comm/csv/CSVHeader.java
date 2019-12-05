package org.dew.comm.csv;

public 
class CSVHeader extends CSVRecord 
{
  public CSVHeader()
  {
    super(Type.HEADER);
  }
  
  public String toString() {
    String sResult = "";
    if(asData != null && asData.length > 0) {
      sResult += type;
      for(int i = 0; i < asData.length; i++) {
        String sValue = asData[i];
        if(sValue == null) sValue = "";
        sResult += ";" + sValue;
      }
      sResult += "\n";
    }
    return sResult;
  }
}
