# Comm

Library for manage data (ASTM, XML, CVS,...) communication (RS232, TCP, HTTP, FILE,...).

## Example

```java
IMessage msg = MessageFactory.getMessage("ASTM");
msg.addRecord(IRecord.Type.HEADER,      new String[]{"NG_LIS", "Host", "P", "1"});
msg.addRecord(IRecord.Type.QUERY,       new String[]{"ALL", null, null, null, "ALL", null, null, "R"});
msg.addRecord(IRecord.Type.TERMINATION, null);

IDriver driver = null;
try {
  driver = DriverFactory.getDriver("rs232", "COM5");
  
  IMessage res = driver.sendMessage(msg);
  
  System.out.println(res);
}
catch(Exception ex) {
  ex.printStackTrace();
}
finally {
  if(driver != null) driver.destroy();
}
```

## Build

- `git clone https://github.com/giosil/comm.git`
- `mvn clean install`

## Contributors

* [Giorgio Silvestris](https://github.com/giosil)
