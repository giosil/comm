package org.dew.comm.csv;

import java.util.ArrayList;
import java.util.List;

import org.dew.comm.*;

public 
class CSVMessage implements IMessage
{
  private List<IRecord> listOfRecord;
  
  public CSVMessage()
  {
    listOfRecord = new ArrayList<IRecord>();
  }
  
  public 
  void addRecord(IRecord.Type type, String[] asData)
  {
    if(type == null) return;
    IRecord record = null;
    switch (type) {
      case HEADER:
        record = new CSVHeader();
        break;
      case TERMINATION:
        record = new CSVTermination();
        break;
      default:
        record = new CSVRecord(type);
    }
    record.setData(asData);
    addRecord(record);
  }
  
  public 
  void addRecord(IRecord record)
  {
    if(record == null) return;
    if(record instanceof CSVHeader) {
      if(listOfRecord.size() == 0) {
        listOfRecord.add(record);
      }
      else {
        IRecord record0 = listOfRecord.get(0);
        if(record0 instanceof CSVHeader) {
          listOfRecord.set(0, record);
        }
        else {
          listOfRecord.add(0, record);
        }
      }
    }
    else
    if(record instanceof CSVTermination) {
      int iIndexOfTermination = -1;
      for(int i = 0; i < listOfRecord.size(); i++) {
        IRecord record_i = listOfRecord.get(i);
        if(record_i instanceof CSVTermination) {
          iIndexOfTermination = i;
        }
      }
      if(iIndexOfTermination >= 0) {
        listOfRecord.remove(iIndexOfTermination);
      }
      listOfRecord.add(record);
    }
    else {
      listOfRecord.add(record);
    }
  }

  public 
  IRecord removeRecord(int iIndex)
  {
    if(iIndex < 0 || iIndex >= listOfRecord.size()) return null;
    return listOfRecord.remove(iIndex);
  }
  
  public 
  IRecord getRecord(int iIndex)
  {
    if(iIndex < 0 || iIndex >= listOfRecord.size()) return null;
    return listOfRecord.get(iIndex);
  }
  
  public 
  int getRecordsCount()
  {
    return listOfRecord.size();
  }
  
  public
  String toString()
  {
    StringBuffer sb = new StringBuffer();
    for(int i = 0; i < listOfRecord.size(); i++) {
      IRecord astmRecord = listOfRecord.get(i);
      if(astmRecord != null) {
        sb.append(astmRecord.toString());
      }
    }
    return sb.toString();
  }
}
