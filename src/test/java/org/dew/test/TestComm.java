package org.dew.test;

import org.dew.comm.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestComm extends TestCase {
  
  public TestComm(String testName) {
    super(testName);
  }
  
  public static Test suite() {
    return new TestSuite(TestComm.class);
  }
  
  public void testApp() {
    IMessage message = MessageFactory.getMessage("csv");
    message.addRecord(IRecord.Type.HEADER,      new String[]{"LIS", "Host", "P", "1"});
    message.addRecord(IRecord.Type.QUERY,       new String[]{"ALL", null, null, null, "ALL", null, null, "R"});
    message.addRecord(IRecord.Type.TERMINATION, null);
    
    System.out.println(message);
    
    System.out.println("parse:\n");
    IMessage message2 = null;
    try {
      message2 = MessageFactory.parse(message.toString().getBytes());
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
    System.out.println(message2);
  }
  
}
