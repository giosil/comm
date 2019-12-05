package org.dew.comm.xml;

import java.util.ArrayList;
import java.util.List;

import org.dew.comm.*;

public 
class XmlMessage implements IMessage
{
  protected List<IRecord> listOfRecord;
  protected XmlPatient lastPatient;
  
  public XmlMessage()
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
        record = new XmlHeader();
        break;
      case PATIENT:
        XmlPatient xmlPatient = new XmlPatient();
        record = xmlPatient;
        lastPatient = xmlPatient;
        break;
      case PATIENT_COMMENT:
        record = new XmlPatientComment();
        break;
      case ORDER:
        record = new XmlOrder();
        if(lastPatient != null) {
          lastPatient.setPartOfOrders(true);
        }
        break;
      case QUERY:
        record = new XmlQuery();
        break;
      case RESULT:
        record = new XmlResult();
        break;
      case RESULT_COMMENT:
        record = new XmlResultComment();
        break;
      case TERMINATION:
        record = new XmlTermination();
        break;
    }
    record.setData(asData);
    addRecord(record);
  }
  
  public 
  void addRecord(IRecord record)
  {
    if(record == null) return;
    if(record instanceof XmlHeader) {
      if(listOfRecord.size() == 0) {
        listOfRecord.add(record);
      }
      else {
        IRecord record0 = listOfRecord.get(0);
        if(record0 instanceof XmlHeader) {
          listOfRecord.set(0, record);
        }
        else {
          listOfRecord.add(0, record);
        }
      }
    }
    else
    if(record instanceof XmlTermination) {
      int iIndexOfTermination = -1;
      for(int i = 0; i < listOfRecord.size(); i++) {
        IRecord record_i = listOfRecord.get(i);
        if(record_i instanceof XmlTermination) {
          iIndexOfTermination = i;
        }
      }
      if(iIndexOfTermination >= 0) {
        listOfRecord.remove(iIndexOfTermination);
      }
      listOfRecord.add(record);
    }
    else
    if(record instanceof XmlOrder) {
      if(listOfRecord.size() > 0) {
        IRecord lastRecord = listOfRecord.get(listOfRecord.size() - 1);
        if(lastRecord instanceof XmlOrder) {
          ((XmlOrder) lastRecord).setLastOfOrders(false);
        }
      }
      ((XmlOrder) record).setLastOfOrders(true);
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
        sb.append('\n');
      }
    }
    return sb.toString();
  }
}

