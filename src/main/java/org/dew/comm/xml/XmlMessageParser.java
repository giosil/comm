package org.dew.comm.xml;

import org.dew.comm.IMessage;
import org.dew.comm.IRecord;

import java.io.*;
import java.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

public 
class XmlMessageParser implements ContentHandler 
{
  private String sCurrentTag = null;
  private String sCurrentValue = null;
  private Stack<String> stackElements = null;
  
  private IMessage message;
  private IRecord record;
  private XmlPatient xmlPatient;
  private XmlOrder xmlOrder;
  private String[] asData;
  private int iTest = 0;
  
  private static Map<String, Integer> mapFieldIndex = new HashMap<String, Integer>();
  static {
    // Header
    mapFieldIndex.put("header|senderid",                    new Integer(0));
    mapFieldIndex.put("header|receiverid",                  new Integer(1));
    mapFieldIndex.put("header|processingid",                new Integer(2));
    mapFieldIndex.put("header|versionnumber",               new Integer(3));
    // Patient
    mapFieldIndex.put("patient|patientid",                  new Integer(0));
    mapFieldIndex.put("patient|lastname",                   new Integer(1));
    mapFieldIndex.put("patient|firstname",                  new Integer(2));
    mapFieldIndex.put("patient|dateofbirth",                new Integer(3));
    mapFieldIndex.put("patient|sex",                        new Integer(4));
    mapFieldIndex.put("patient|attendingphysician",         new Integer(5));
    mapFieldIndex.put("patient|location",                   new Integer(6));
    mapFieldIndex.put("patient|middleinitial",              new Integer(7));
    // Order
    mapFieldIndex.put("order|specimenid",                   new Integer(0));
    mapFieldIndex.put("order|priority",                     new Integer(1));
    mapFieldIndex.put("order|actioncode",                   new Integer(2));
    mapFieldIndex.put("order|reporttype",                   new Integer(3));
    // Query
    mapFieldIndex.put("query|startingpatientid",            new Integer(0));
    mapFieldIndex.put("query|startingsampleid",             new Integer(1));
    mapFieldIndex.put("query|endingpatientid",              new Integer(2));
    mapFieldIndex.put("query|endingsampleid",               new Integer(3));
    mapFieldIndex.put("query|test",                         new Integer(4));
    mapFieldIndex.put("query|natureofrequesttimelimits",    new Integer(5));
    mapFieldIndex.put("query|beginningrequestresultsdate",  new Integer(6));
    mapFieldIndex.put("query|endingrequestresultsdate",     new Integer(7));
    mapFieldIndex.put("query|requestinformationstatuscode", new Integer(8));
    // Result
    mapFieldIndex.put("result|test",                        new Integer(0));
    mapFieldIndex.put("result|resultaspects",               new Integer(1));
    mapFieldIndex.put("result|datavalue",                   new Integer(2));
    mapFieldIndex.put("result|units",                       new Integer(3));
    mapFieldIndex.put("result|datetestcompleted",           new Integer(4));
  }
  
  public 
  IMessage parse(String sMessage) 
    throws Exception 
  {
    InputSource inputSource = new InputSource(new ByteArrayInputStream(sMessage.getBytes()));
    XMLReader xmlReader = XMLReaderFactory.createXMLReader();
    xmlReader.setContentHandler(this);
    xmlReader.parse(inputSource);
    return message;
  }
  
  public void startDocument() throws SAXException {
    stackElements = new Stack<String>();
  }
  
  public void endDocument() throws SAXException {
  }
  
  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    // Costruzione sCurrentTag
    stackElements.push(localName);
    sCurrentTag = "";
    for (int i = 0; i < stackElements.size(); i++) {
      sCurrentTag += "|" + stackElements.get(i);
    }
    sCurrentTag = sCurrentTag.substring(1);
    sCurrentValue = "";
    // Inizializzazione strutture
    if (localName.equalsIgnoreCase("Message")) {
      message = new XmlMessage();
    }
    else if (localName.equalsIgnoreCase("Header")) {
      record = new XmlHeader();
      asData = new String[4];
    }
    else if (localName.equalsIgnoreCase("Patient")) {
      xmlPatient = new XmlPatient();
      record = xmlPatient;
      asData = new String[8];
    }
    else if (localName.equalsIgnoreCase("PatientComment")) {
      record = new XmlPatientComment();
      asData = new String[3];
    }
    else if (localName.equalsIgnoreCase("Order")) {
      if(xmlPatient != null) {
        xmlPatient.setPartOfOrders(true);
      }
      xmlOrder = new XmlOrder();
      record = xmlOrder;
      asData = new String[20];
      iTest  = 0;
    }
    else if (localName.equalsIgnoreCase("Query")) {
      record = new XmlQuery();
      asData = new String[9];
    }
    else if (localName.equalsIgnoreCase("Result")) {
      record = new XmlResult();
      asData = new String[5];
    }
    else if (localName.equalsIgnoreCase("ResultComment")) {
      record = new XmlResultComment();
      asData = new String[3];
    }
  }
  
  public void endElement(String uri, String localName, String qName) throws SAXException {
    // Impostazione valori
    int iIndex = getIndexData();
    if(iIndex >= 0 && asData != null && iIndex < asData.length) {
      asData[iIndex] = sCurrentValue;
    }
    
    String sCurrentTagLC = sCurrentTag.toLowerCase();
    if (sCurrentTagLC.endsWith("|header")) {
      record.setData(asData);
      message.addRecord(record);
    }
    else if (sCurrentTagLC.endsWith("|patient")) {
      record.setData(asData);
      message.addRecord(record);
    }
    else if (sCurrentTagLC.endsWith("|patientcomment")) {
      record.setData(asData);
      message.addRecord(record);
    }
    else if (sCurrentTagLC.endsWith("|order")) {
      record.setData(asData);
      message.addRecord(record);
    }
    else if (sCurrentTagLC.endsWith("|test")) {
      if(record instanceof XmlOrder) {
        if(4 + iTest < asData.length) {
          asData[4 + iTest] = sCurrentValue;
          iTest++;
        }
      }
    }
    else if (sCurrentTagLC.endsWith("|query")) {
      record.setData(asData);
      message.addRecord(record);
    }
    else if (sCurrentTagLC.endsWith("|result")) {
      record.setData(asData);
      message.addRecord(record);
    }
    else if (sCurrentTagLC.endsWith("|resultcomment")) {
      record.setData(asData);
      message.addRecord(record);
    }
    else if (sCurrentTagLC.endsWith("orders")) {
      if(xmlOrder != null) {
        xmlOrder.setLastOfOrders(true);
      }
    }
    else if (sCurrentTagLC.endsWith("message")) {
      if(xmlOrder != null) {
        xmlOrder.setLastOfOrders(true);
      }
      record = new XmlTermination();
      message.addRecord(record);
    }
    
    // Costruzione sCurrentTag
    if (!stackElements.isEmpty()) {
      stackElements.pop();
    }
    sCurrentTag = "";
    for (int i = 0; i < stackElements.size(); i++) {
      sCurrentTag += "|" + stackElements.get(i);
    }
    sCurrentTag = sCurrentTag.length() > 0 ? sCurrentTag.substring(1) : "";
  }
  
  public void characters(char[] ch, int start, int length) throws SAXException {
    sCurrentValue += new String(ch, start, length);
  }
  
  public void endPrefixMapping(String prefix) throws SAXException {
  }
  
  public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
  }
  
  public void processingInstruction(String target, String data) throws SAXException {
  }
  
  public void setDocumentLocator(Locator locator) {
  }
  
  public void skippedEntity(String name) throws SAXException {
  }
  
  public void startPrefixMapping(String prefix, String uri) throws SAXException {
  }
  
  private int getIndexData() {
    int iSep = sCurrentTag.lastIndexOf('|');
    if(iSep < 0 || iSep == sCurrentTag.length() - 1) return -1;
    if(iSep > 0) {
      int iStart = sCurrentTag.lastIndexOf('|', iSep - 1);
      if(iStart < 0) return -1;
      String sKey = sCurrentTag.substring(iStart + 1).toLowerCase();
      Integer oIndex = mapFieldIndex.get(sKey);
      if(oIndex == null) return -1;
      return oIndex.intValue();
    }
    return -1;
  }
}