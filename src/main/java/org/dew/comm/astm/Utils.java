package org.dew.comm.astm;

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
		int iDate = cal.get(Calendar.YEAR)*10000 + (cal.get(Calendar.MONTH)+1)*100 + cal.get(Calendar.DAY_OF_MONTH);
		int iTime = cal.get(Calendar.HOUR_OF_DAY)*100 + cal.get(Calendar.MINUTE);
		return String.valueOf(iDate) + String.valueOf(iTime);
	}
	
	public static
	String formatDate(Date date)
	{
		if(date == null) return "";
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int iDate = cal.get(Calendar.YEAR)*10000 + (cal.get(Calendar.MONTH)+1)*100 + cal.get(Calendar.DAY_OF_MONTH);
		return String.valueOf(iDate);
	}
	
	public static 
	Date stringToDateTime(String sDateTime) 
	{
		if(sDateTime == null || sDateTime.length() < 4) return null;
		int iSepHHMM = sDateTime.lastIndexOf(':');
		if(iSepHHMM <= 0 && sDateTime.length() < 9) {
			return stringToDate(sDateTime);
		}
		int iSepDateTime = sDateTime.lastIndexOf(' ', iSepHHMM);
		if(iSepDateTime < 0) {
			iSepDateTime = sDateTime.lastIndexOf('T', iSepHHMM);
			if(iSepDateTime < 0) {
				iSepDateTime = sDateTime.lastIndexOf(',', iSepHHMM);
			}
		}
		if(iSepDateTime < 0) {
			iSepDateTime = 8;
			iSepHHMM = iSepDateTime + 2;
		}
		String sDate = sDateTime.substring(0, iSepDateTime);
		Calendar cal = intToCalendar(stringToIntDate(sDate));
		if(cal == null) return null;
		try {
			String sTime = numeric(sDateTime.substring(iSepDateTime + 1), 6);
			int iHH = Integer.parseInt(sTime.substring(0, 2));
			int iMM = Integer.parseInt(sTime.substring(2, 4));
			int iSS = Integer.parseInt(sTime.substring(4, 6));
			cal.set(Calendar.HOUR_OF_DAY, iHH);
			cal.set(Calendar.MINUTE,      iMM);
			cal.set(Calendar.SECOND,      iSS);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return cal.getTime();
	}
	
	public static 
	Date stringToDate(String sDate) 
	{
		Calendar cal = intToCalendar(stringToIntDate(sDate));
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
	int stringToIntDate(String sValue)
	{
		if(sValue == null || sValue.length() < 6) return 0;
		int iFirstSep = sValue.indexOf('/');
		if(iFirstSep < 0) {
			iFirstSep = sValue.indexOf('-');
			if(iFirstSep <= 0) {
				return Integer.parseInt(numeric(sValue, 1));
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
		else
			if(sMonth.length() == 1) {
				sMonth = "0" + sMonth;
			}
		return Integer.parseInt(numeric(sYear + sMonth + sDay, 1));
	}
	
	public static
	String numeric(String sText, int iLength)
	{
		StringBuffer sbResult = new StringBuffer();
		if(sText != null && sText.length() > 0) {
			for(int i = 0; i < sText.length(); i++) {
				char c = sText.charAt(i);
				if(c >= '0' && c <= '9') {
					sbResult.append(c);
				}
			}
		}
		int diff = iLength - sbResult.length();
		for(int i = 0; i < diff; i++) sbResult.append('0');
		return sbResult.toString();
	}
}
