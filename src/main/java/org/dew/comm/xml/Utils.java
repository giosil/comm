package org.dew.comm.xml;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public 
class Utils 
{
  public static
  String formatDateTime(Date date)
  {
    if(date == null) return "";
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    int iYear    = cal.get(Calendar.YEAR);
    int iMonth   = cal.get(Calendar.MONTH) + 1;
    int iDay     = cal.get(Calendar.DAY_OF_MONTH);
    int iHour    = cal.get(Calendar.HOUR_OF_DAY);
    int iMinute  = cal.get(Calendar.MINUTE);
    int iSecond  = cal.get(Calendar.SECOND);
    String sMonth  = iMonth  < 10 ? "0" + iMonth  : String.valueOf(iMonth);
    String sDay    = iDay    < 10 ? "0" + iDay    : String.valueOf(iDay);
    String sHour   = iHour   < 10 ? "0" + iHour   : String.valueOf(iHour);
    String sMinute = iMinute < 10 ? "0" + iMinute : String.valueOf(iMinute);
    String sSecond = iSecond < 10 ? "0" + iSecond : String.valueOf(iSecond);
    return iYear + "-" + sMonth + "-" + sDay + " " + sHour + ":" + sMinute + ":" + sSecond;
  }
  
  public static
  String formatDate(Date date)
  {
    if(date == null) return "";
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    int iYear    = cal.get(Calendar.YEAR);
    int iMonth   = cal.get(Calendar.MONTH) + 1;
    int iDay     = cal.get(Calendar.DAY_OF_MONTH);
    String sMonth  = iMonth  < 10 ? "0" + iMonth  : String.valueOf(iMonth);
    String sDay    = iDay    < 10 ? "0" + iDay    : String.valueOf(iDay);
    return iYear + "-" + sMonth + "-" + sDay;
  }
  
  public static 
  Date stringToDateTime(String sDateTime) 
  {
    if(sDateTime == null || sDateTime.length() < 4) return null;
    int iSepHHMM = sDateTime.lastIndexOf(':');
    if(iSepHHMM <= 0) {
      return stringToDate(sDateTime);
    }
    int iSepDateTime = sDateTime.lastIndexOf(' ', iSepHHMM);
    if(iSepDateTime < 0) {
      iSepDateTime = sDateTime.lastIndexOf('T', iSepHHMM);
      if(iSepDateTime < 0) {
        iSepDateTime = sDateTime.lastIndexOf(',', iSepHHMM);
      }
    }
    if(iSepDateTime <= 0 || iSepDateTime > iSepHHMM) {
      return stringToDate(sDateTime);
    }
    String sDate = sDateTime.substring(0, iSepDateTime);
    Calendar cal = intToCalendar(stringToInt(sDate));
    if(cal == null) return null;
    
    int iHH = Integer.parseInt(sDateTime.substring(iSepDateTime + 1, iSepHHMM).trim());;
    int iMM = 0;
    if(iSepHHMM < sDateTime.length() - 1) {
      iMM = Integer.parseInt(sDateTime.substring(iSepHHMM + 1).trim());
    }
    cal.set(Calendar.HOUR_OF_DAY, iHH);
    cal.set(Calendar.MINUTE, iMM);
    return cal.getTime();
  }
  
  public static 
  Date stringToDate(String sDate) 
  {
    Calendar cal = intToCalendar(stringToInt(sDate));
    if(cal == null) return null;
    return cal.getTime();
  }
  
  public static 
  Calendar intToCalendar(int iDate) 
  {
    if (iDate == 0) return null;
    int iYear = iDate / 10000;
    int iMonth = (iDate % 10000) / 100;
    int iDay = (iDate % 10000) % 100;
    return new GregorianCalendar(iYear, iMonth-1, iDay, 0, 0, 0);
  }
  
  public static
  int stringToInt(String sValue)
  {
    if(sValue == null || sValue.length() < 6) return 0;
    int iFirstSep = sValue.indexOf('/');
    if(iFirstSep < 0) {
      iFirstSep = sValue.indexOf('-');
      if(iFirstSep <= 0) {
        return Integer.parseInt(sValue);
      }
    }
    int iSecondSep = sValue.indexOf('/', iFirstSep + 1);
    if(iSecondSep < 0) {
      iSecondSep = sValue.indexOf('-', iFirstSep + 1);
      if(iSecondSep < 0) {
        return 0;
      }
    }
    String sDay   = null;
    String sMonth = null;
    String sYear  = null;
    if(iFirstSep >= 4) {
      // year - month - day
      sYear  = sValue.substring(0, iFirstSep).trim();
      sMonth = sValue.substring(iFirstSep + 1, iSecondSep).trim();
      sDay   = sValue.substring(iSecondSep + 1).trim();
    }
    else {
      // day - month - year
      sDay   = sValue.substring(0, iFirstSep).trim();
      sMonth = sValue.substring(iFirstSep + 1, iSecondSep).trim();
      sYear  = sValue.substring(iSecondSep + 1).trim();
    }
    if(sDay.length() == 0) {
      sDay = "01";
    }
    else
      if(sDay.length() == 1) {
        sDay = "0" + sDay;
      }
    if(sMonth.length() == 0) {
      sMonth = "01";
    }
    else if(sMonth.length() == 1) {
      sMonth = "0" + sMonth;
    }
    return Integer.parseInt(sYear + sMonth + sDay);
  }
}
