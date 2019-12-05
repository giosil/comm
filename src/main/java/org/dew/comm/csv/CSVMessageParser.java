package org.dew.comm.csv;

import java.util.ArrayList;
import java.util.List;

import org.dew.comm.IMessage;
import org.dew.comm.IRecord;

public 
class CSVMessageParser 
{
  public static
  IMessage parse(String sMessage)
  {
    if(sMessage == null || sMessage.length() < 3) return null;
    CSVMessage csvMessage = new CSVMessage();
    for(int i = 0; i < sMessage.length(); i++) {
      int iIndexOfCR = sMessage.indexOf((char) 13, i);
      if(iIndexOfCR < 0) {
        iIndexOfCR = sMessage.indexOf('\n', i);
      }
      if(iIndexOfCR < 0 || (iIndexOfCR - i) < 2) break;
      String sRecord = sMessage.substring(i, iIndexOfCR).trim();
      i = iIndexOfCR;
      IRecord record = new CSVRecord(getRecordType(sRecord));
      record.setData(getDataRecord(getRecordValues(sRecord)));
      csvMessage.addRecord(record);
    }
    return csvMessage;
  }
  
  private static
  IRecord.Type getRecordType(String sRecord)
  {
    int iSep = sRecord.indexOf(';');
    if(iSep > 0) {
      String sType = sRecord.substring(0, iSep);
      return IRecord.Type.valueOf(sType.toUpperCase());
    }
    return null;
  }
  
  private static
  String getRecordValues(String sRecord)
  {
    int iSep = sRecord.indexOf(';');
    if(iSep > 0 && iSep < sRecord.length() - 1) {
      return sRecord.substring(iSep + 1);
    }
    return "";
  }
  
  private static
  List<String> getRecord(String sText)
  {
    List<String> listResult = new ArrayList<String>();
    if(sText == null || sText.length() == 0) return listResult;
    int iIndexOf = 0;
    int iBegin = 0;
    iIndexOf = sText.indexOf(';');
    while(iIndexOf >= 0) {
      String sToken = sText.substring(iBegin, iIndexOf);
      listResult.add(sToken);
      iBegin = iIndexOf + 1;
      iIndexOf = sText.indexOf(';', iBegin);
    }
    listResult.add(sText.substring(iBegin));
    return listResult;
  }
  
  private static
  String[] getDataRecord(String sText)
  {
    List<String> listRecord = getRecord(sText);
    String[] asResult = new String[listRecord.size()];
    for(int i = 0; i < listRecord.size(); i++) {
      asResult[i] = listRecord.get(i);
    }
    return asResult;
  }
}
