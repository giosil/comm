package org.dew.comm.astm;

public 
class UniversalTestId 
{
  private String universalTestId = "";
  private String name;
  private String type;
  private String manufacturersCode;
  private String dilutionProtocol;
  private String dilutionRatio;
  private String replicateNumber;
  private String resultAspects;
  private boolean all;
  
  public UniversalTestId()
  {
  }
  
  public UniversalTestId(String sManufacturersCode)
  {
    this.manufacturersCode = sManufacturersCode;
  }
  
  public UniversalTestId(String sManufacturersCode, String sResultAspects)
  {
    this.manufacturersCode = sManufacturersCode;
    this.resultAspects = sResultAspects;
  }
  
  public String getUniversalTestId() {
    return universalTestId;
  }

  public void setUniversalTestId(String universalTestId) {
    this.universalTestId = universalTestId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getManufacturersCode() {
    return manufacturersCode;
  }

  public void setManufacturersCode(String manufacturersCode) {
    this.manufacturersCode = manufacturersCode;
  }

  public String getDilutionProtocol() {
    return dilutionProtocol;
  }

  public void setDilutionProtocol(String dilutionProtocol) {
    this.dilutionProtocol = dilutionProtocol;
  }

  public String getDilutionRatio() {
    return dilutionRatio;
  }

  public void setDilutionRatio(String dilutionRatio) {
    this.dilutionRatio = dilutionRatio;
  }

  public String getResultAspects() {
    return resultAspects;
  }

  public void setResultAspects(String resultAspects) {
    this.resultAspects = resultAspects;
  }
  
  public boolean isAll() {
    return all;
  }

  public void setAll(boolean all) {
    this.all = all;
  }
  
  public
  String toString()
  {
    if(all) return "ALL";
    boolean boDilProt  = dilutionProtocol != null && dilutionProtocol.length() > 0;
    boolean boDilRadio = dilutionRatio    != null && dilutionRatio.length()    > 0;
    boolean boRepNumb  = replicateNumber  != null && replicateNumber.length()  > 0;
    boolean boResAspec = resultAspects    != null && resultAspects.length()    > 0;
    StringBuffer sbResult = new StringBuffer();
    sbResult.append(universalTestId != null ? universalTestId : "");
    sbResult.append('^');
    sbResult.append(name != null ? name : "");
    sbResult.append('^');
    sbResult.append(type != null ? type : "");
    sbResult.append('^');
    sbResult.append(manufacturersCode != null ? manufacturersCode : "");
    sbResult.append(boDilProt ? '^' + dilutionProtocol : "");
    if(boDilRadio) {
      if(boDilProt) {
        sbResult.append('^' + dilutionRatio);
      }
      else {
        sbResult.append("^^" + dilutionRatio);
      }
    }
    if(boRepNumb) {
      if(boDilRadio) {
        sbResult.append('^' + replicateNumber);
      }
      else {
        if(boDilProt) {
          sbResult.append("^^" + replicateNumber);
        }
        else {
          sbResult.append("^^^" + replicateNumber);
        }
      }
    }
    if(boResAspec) {
      if(boRepNumb) {
        sbResult.append('^' + resultAspects);
      }
      else {
        if(boDilRadio) {
          sbResult.append("^^" + resultAspects);
        }
        else {
          if(boDilProt) {
            sbResult.append("^^^" + resultAspects);
          }
          else {
            sbResult.append("^^^^" + resultAspects);
          }
        }
      }
    }
    return sbResult.toString();
  }
}
