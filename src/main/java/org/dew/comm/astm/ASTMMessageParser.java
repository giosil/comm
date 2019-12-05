package org.dew.comm.astm;

import java.util.*;

import org.dew.comm.IMessage;
import org.dew.comm.IRecord;

public 
class ASTMMessageParser 
{
	public static
	IMessage parse(String sMessage)
	{
		if(sMessage == null || sMessage.length() < 3) return null;
		
		ASTMMessage astmMessage = new ASTMMessage();
		IRecord lastRecord = null;
		for(int i = 0; i < sMessage.length(); i++) {
			char c = sMessage.charAt(i);
			if(c < ' ') continue;
			int iIndexOfCR = sMessage.indexOf((char) 13, i);
			if(iIndexOfCR < 0) {
				iIndexOfCR = sMessage.indexOf('\n', i);
			}
			if(iIndexOfCR < 0 || (iIndexOfCR - i) < 2) break;
			String sFields = sMessage.substring(i + 2, iIndexOfCR);
			i = iIndexOfCR;
			
			IRecord record = null;
			if(c == 'H') {
				record = new ASTMHeader();
				record.setData(getDataHeader(sFields));
			}
			else
			if(c == 'P') {
				record = new ASTMPatient();
				record.setData(getDataPatient(sFields));
			}
			else
			if(c == 'C') {
				if(lastRecord instanceof ASTMPatient) {
					record = new ASTMPatientComment();
					record.setData(getDataComment(sFields));
				}
				else {
					record = new ASTMResultComment();
					record.setData(getDataComment(sFields));
				}
			}
			else
			if(c == 'O') {
				record = new ASTMOrder();
				record.setData(getDataOrder(sFields));
			}
			else
			if(c == 'Q') {
				record = new ASTMQuery();
				record.setData(getDataQuery(sFields));
			}
			else
			if(c == 'R') {
				record = new ASTMResult();
				record.setData(getDataResult(sFields));
			}
			else
			if(c == 'L') {
				record = new ASTMTermination();
				record.setData(getDataTermination(sFields));
			}
			if(record != null) {
				astmMessage.addRecord(record);
				lastRecord = record;
			}
		}
		return astmMessage;
	}
	
	private static
	List<String> getRecord(String sText)
	{
		List<String> listResult = new ArrayList<String>();
		if(sText == null || sText.length() == 0) return listResult;
		int iIndexOf = 0;
		int iBegin = 0;
		iIndexOf = sText.indexOf('|');
		while(iIndexOf >= 0) {
			String sToken = sText.substring(iBegin, iIndexOf);
			listResult.add(sToken);
			iBegin = iIndexOf + 1;
			iIndexOf = sText.indexOf('|', iBegin);
		}
		listResult.add(sText.substring(iBegin));
		return listResult;
	}
	
	private static
	List<String> getComponents(String sText)
	{
		List<String> listResult = new ArrayList<String>();
		if(sText == null || sText.length() == 0) return listResult;
		int iIndexOf = 0;
		int iBegin = 0;
		iIndexOf = sText.indexOf('^');
		while(iIndexOf >= 0) {
			String sToken = sText.substring(iBegin, iIndexOf);
			listResult.add(sToken);
			iBegin = iIndexOf + 1;
			iIndexOf = sText.indexOf('^', iBegin);
		}
		listResult.add(sText.substring(iBegin));
		return listResult;
	}
	
	private static
	List<String> getRepetition(String sText)
	{
		List<String> listResult = new ArrayList<String>();
		if(sText == null || sText.length() == 0) return listResult;
		int iIndexOf = 0;
		int iBegin = 0;
		iIndexOf = sText.indexOf('\\');
		while(iIndexOf >= 0) {
			String sToken = sText.substring(iBegin, iIndexOf);
			listResult.add(sToken);
			iBegin = iIndexOf + 1;
			iIndexOf = sText.indexOf('\\', iBegin);
		}
		listResult.add(sText.substring(iBegin));
		return listResult;
	}
	
	private static
	String getString(List<String> listRecord, int iPosition)
	{
		int iIndex = iPosition - 2;
		if(iIndex < 0 || iIndex > listRecord.size() - 1) return null;
		return listRecord.get(iIndex);
	}
	
	private static
	String[] getDataHeader(String sText)
	{
		List<String> listRecord = getRecord(sText);
		
		String[] asResult = new String[4];
		asResult[0] = getString(listRecord, 5);  // senderId
		asResult[1] = getString(listRecord, 10); // receiverId
		asResult[2] = getString(listRecord, 12); // processingId
		asResult[3] = getString(listRecord, 13); // versionNumber
		return asResult;
	}
	
	private static
	String[] getDataPatient(String sText)
	{
		List<String> listRecord = getRecord(sText);
		
		String sPatientName = getString(listRecord, 6);
		List<String> listPatientName = getComponents(sPatientName);
		
		String[] asResult = new String[8];
		asResult[0] = getString(listRecord, 3);  // patientId
		asResult[1] = listPatientName.size() > 0 ? listPatientName.get(0) : null; // lastName
		asResult[2] = listPatientName.size() > 1 ? listPatientName.get(1) : null; // firstName
		asResult[3] = getString(listRecord, 8);  // dateOfBirth
		asResult[4] = getString(listRecord, 9);  // sex
		asResult[5] = getString(listRecord, 14); // attendingPhysician
		asResult[6] = getString(listRecord, 26); // location
		asResult[7] = listPatientName.size() > 2 ? listPatientName.get(2) : null; // middleInitial
		
		return asResult;
	}
	
	private static
	String[] getDataComment(String sText)
	{
		List<String> listRecord = getRecord(sText);
		
		String sComment = getString(listRecord, 4);
		List<String> listComment = getComponents(sComment);
		
		String[] asResult = new String[4];
		asResult[0] = listComment.size() > 0 ? listComment.get(0) : null; // code
		asResult[1] = listComment.size() > 1 ? listComment.get(1) : null; // text
		asResult[2] = getString(listRecord, 5); // type
		return asResult;
	}
	
	private static
	String[] getDataOrder(String sText)
	{
		List<String> listRecord = getRecord(sText);
		
		String sSpecimenId = getString(listRecord, 3);
		List<String> listSpecimenId = getComponents(sSpecimenId);
		
		String sSetOfTest = getString(listRecord, 5);
		List<String> listSetOfTest = getRepetition(sSetOfTest);
		
		String[] asResult = new String[4 + listSetOfTest.size()];
		asResult[0] = listSpecimenId.size() > 0 ? listSpecimenId.get(0) : null; // Sample ID
		asResult[1] = getString(listRecord, 6);  // priority
		asResult[2] = getString(listRecord, 12); // actionCode
		asResult[3] = getString(listRecord, 26); // reportType
		for(int i = 0; i < listSetOfTest.size(); i++) {
			String sTest = listSetOfTest.get(i);
			List<String> listTest = getComponents(sTest);
			String sManufacturersCode = listTest.size() > 3 ? listTest.get(3) : null;
			if(sManufacturersCode != null && sManufacturersCode.length() > 0) {
				asResult[4 + i] = sManufacturersCode;
			}
		}
		return asResult;
	}
	
	private static
	String[] getDataQuery(String sText)
	{
		List<String> listRecord = getRecord(sText);
		
		String sStartingRangeIdNumber = getString(listRecord, 3);
		String sEndingRangeIdNumber   = getString(listRecord, 4);
		String sUniversalTestId       = getString(listRecord, 5);
		
		List<String> listStartingRange = getComponents(sStartingRangeIdNumber);
		List<String> listEndingRange   = getComponents(sEndingRangeIdNumber);
		List<String> listTest          = getComponents(sUniversalTestId);
				
		String[] asResult = new String[9];
		asResult[0] = listStartingRange.size() > 0 ? listStartingRange.get(0) : null; // startingPatientId
		asResult[1] = listStartingRange.size() > 1 ? listStartingRange.get(1) : null; // startingSampleId
		asResult[2] = listEndingRange.size() > 0   ? listEndingRange.get(0)   : null; // endingPatientId
		asResult[3] = listEndingRange.size() > 1   ? listEndingRange.get(1)   : null; // endingSampleId
		if(sUniversalTestId != null && sUniversalTestId.equalsIgnoreCase("ALL")) {
			asResult[4] = "ALL";
		}
		else {
			asResult[4] = listTest.size() > 3 ? listTest.get(3) : null; // test (Manufacturer's Code)
		}
		asResult[5] = getString(listRecord, 6);  // natureOfRequestTimeLimits
		asResult[6] = getString(listRecord, 7);  // beginningRequestResultsDate
		asResult[7] = getString(listRecord, 8);  // endingRequestResultsDate
		asResult[8] = getString(listRecord, 13); // requestInformationStatusCode
		
		return asResult;
	}
	
	private static
	String[] getDataResult(String sText)
	{
		List<String> listRecord = getRecord(sText);
		
		String sUniversalTestId = getString(listRecord, 3);
		List<String> listTest   = getComponents(sUniversalTestId);
		
		String[] asResult = new String[5];
		asResult[0] = listTest.size() > 3 ? listTest.get(3)   : null; // test (Manufacturer's Code)
		asResult[1] = listTest.size() > 7 ? listTest.get(7)   : null; // test (Result Aspects)
		asResult[2] = getString(listRecord, 4);  // dataValue
		asResult[3] = getString(listRecord, 5);  // units
		asResult[4] = getString(listRecord, 13); // dateTestCompleted
		return asResult;
	}
	
	private static
	String[] getDataTermination(String sText)
	{
		List<String> listRecord = getRecord(sText);
		
		String[] asResult = new String[1];
		asResult[0] = getString(listRecord, 3); // code
		return asResult;
	}
}
