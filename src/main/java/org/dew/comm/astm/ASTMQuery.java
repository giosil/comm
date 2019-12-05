package org.dew.comm.astm;

import org.dew.comm.IRecord;

import java.util.*;

public 
class ASTMQuery implements IRecord
{
	private int sequenceNumber = 1;
	private String startingPatientId;
	private String startingSampleId;
	private String endingPatientId;
	private String endingSampleId;
	private UniversalTestId test;
	private String natureOfRequestTimeLimits;
	private Date beginningRequestResultsDate;
	private Date endingRequestResultsDate;
	private String requestInformationStatusCode = "O";
	
	public ASTMQuery()
	{
	}
	
	public
	void setData(String[] asData)
	{
		if(asData == null) return;
		if(asData.length > 0 && asData[0] != null) startingPatientId = asData[0];
		if(asData.length > 1 && asData[1] != null) startingSampleId  = asData[1];
		if(asData.length > 2 && asData[2] != null) endingPatientId   = asData[2];
		if(asData.length > 3 && asData[3] != null) endingSampleId    = asData[3];
		if(asData.length > 4 && asData[4] != null) {
			if(asData[4].equalsIgnoreCase("ALL")) {
				test = new UniversalTestId();
				test.setAll(true);
			}
			else {
				test = new UniversalTestId(asData[4]);
			}
		}
		if(asData.length > 5 && asData[5] != null) natureOfRequestTimeLimits    = asData[5];
		if(asData.length > 6 && asData[6] != null) beginningRequestResultsDate  = Utils.stringToDate(asData[6]);
		if(asData.length > 7 && asData[7] != null) endingRequestResultsDate     = Utils.stringToDate(asData[7]);
		if(asData.length > 8 && asData[8] != null) requestInformationStatusCode = asData[8];
	}
	
	public 
	String[] getData() 
	{
		String[] asResult = new String[9];
		asResult[0] = startingPatientId;
		asResult[1] = startingSampleId;
		asResult[2] = endingPatientId;
		asResult[3] = endingSampleId;
		asResult[4] = test != null ? test.getManufacturersCode() : null;
		asResult[5] = natureOfRequestTimeLimits;
		asResult[6] = Utils.formatDate(beginningRequestResultsDate);
		asResult[7] = Utils.formatDate(endingRequestResultsDate);;
		asResult[8] = requestInformationStatusCode;
		return asResult;
	}
	
	public 
	Type getType() 
	{
		return Type.QUERY;
	}
	
	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public String getStartingPatientId() {
		return startingPatientId;
	}

	public void setStartingPatientId(String startingPatientId) {
		this.startingPatientId = startingPatientId;
	}

	public String getStartingSampleId() {
		return startingSampleId;
	}

	public void setStartingSampleId(String startingSampleId) {
		this.startingSampleId = startingSampleId;
	}

	public String getEndingPatientId() {
		return endingPatientId;
	}

	public void setEndingPatientId(String endingPatientId) {
		this.endingPatientId = endingPatientId;
	}

	public String getEndingSampleId() {
		return endingSampleId;
	}

	public void setEndingSampleId(String endingSampleId) {
		this.endingSampleId = endingSampleId;
	}

	public UniversalTestId getTest() {
		return test;
	}

	public void setTest(UniversalTestId test) {
		this.test = test;
	}

	public String getNatureOfRequestTimeLimits() {
		return natureOfRequestTimeLimits;
	}

	public void setNatureOfRequestTimeLimits(String natureOfRequestTimeLimits) {
		this.natureOfRequestTimeLimits = natureOfRequestTimeLimits;
	}

	public Date getBeginningRequestResultsDate() {
		return beginningRequestResultsDate;
	}

	public void setBeginningRequestResultsDate(Date beginningRequestResultsDate) {
		this.beginningRequestResultsDate = beginningRequestResultsDate;
	}

	public Date getEndingRequestResultsDate() {
		return endingRequestResultsDate;
	}

	public void setEndingRequestResultsDate(Date endingRequestResultsDate) {
		this.endingRequestResultsDate = endingRequestResultsDate;
	}

	public String getRequestInformationStatusCode() {
		return requestInformationStatusCode;
	}

	public void setRequestInformationStatusCode(String requestInformationStatusCode) {
		this.requestInformationStatusCode = requestInformationStatusCode;
	}
	
	public
	String toString()
	{
		StringBuffer sbResult = new StringBuffer();
		sbResult.append('Q');
		sbResult.append('|');
		sbResult.append(sequenceNumber);
		sbResult.append('|');
		if(startingPatientId == null || startingPatientId.length() == 0) {
			if(startingSampleId == null || startingSampleId.length() == 0) {
				sbResult.append("ALL");
			}
			else {
				sbResult.append('^' + startingSampleId);
			}
		}
		else {
			if(startingSampleId == null || startingSampleId.length() == 0) {
				sbResult.append(startingPatientId);
			}
			else {
				sbResult.append(startingPatientId + '^' + startingSampleId);
			}			
		}
		sbResult.append('|');
		if(endingPatientId == null || endingPatientId.length() == 0) {
			if(endingSampleId == null || endingSampleId.length() == 0) {
				sbResult.append("");
			}
			else {
				sbResult.append('^' + endingSampleId);
			}
		}
		else {
			if(endingSampleId == null || endingSampleId.length() == 0) {
				sbResult.append(endingPatientId);
			}
			else {
				sbResult.append(endingPatientId + '^' + endingSampleId);
			}			
		}
		sbResult.append('|');
		sbResult.append(test != null ? test.toString() : "");
		sbResult.append("||");
		sbResult.append(formatDate(beginningRequestResultsDate));
		sbResult.append('|');
		sbResult.append(formatDate(endingRequestResultsDate));
		sbResult.append("|||||");
		sbResult.append(requestInformationStatusCode);
		sbResult.append((char) 13); // <CR>
		return sbResult.toString();
	}
	
	public
	byte[] getBytes()
	{
		return toString().getBytes();
	}
	
	private static
	String formatDate(Date date)
	{
		if(date == null) return "";
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int iDate = cal.get(Calendar.YEAR)*10000 + (cal.get(Calendar.MONTH)+1)*100 + cal.get(Calendar.DAY_OF_MONTH);
		return String.valueOf(iDate);
	}
}
