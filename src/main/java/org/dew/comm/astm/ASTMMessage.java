package org.dew.comm.astm;

import org.dew.comm.*;

import java.util.*;

public 
class ASTMMessage implements IMessage
{
	protected List<IRecord> listOfRecord;
	
	protected int iPatientSequenceNumber = 0;
	protected int iPatientCommentSequenceNumber = 0;
	protected int iOrderSequenceNumber = 0;
	protected int iResultSequenceNumber = 0;
	protected int iResultCommentSequenceNumber = 0;
	protected int iQuerySequenceNumber = 0;
	
	public ASTMMessage()
	{
		listOfRecord = new ArrayList<IRecord>();
	}
	
	public 
	void addRecord(IRecord.Type type, String[] asData)
	{
		switch (type) {
			case HEADER:
				ASTMHeader header = new ASTMHeader();
				header.setData(asData);
				addHeader(header);
				break;
			case PATIENT:
				ASTMPatient patient = new ASTMPatient();
				patient.setData(asData);
				addPatient(patient);
				break;
			case PATIENT_COMMENT:
				ASTMPatientComment patientComment = new ASTMPatientComment();
				patientComment.setData(asData);
				addPatientComment(patientComment);
				break;
			case ORDER:
				ASTMOrder order = new ASTMOrder();
				order.setData(asData);
				addOrder(order);
				break;
			case QUERY:
				ASTMQuery query = new ASTMQuery();
				query.setData(asData);
				addQuery(query);
				break;
			case RESULT:
				ASTMResult result = new ASTMResult();
				result.setData(asData);
				addResult(result);
				break;
			case RESULT_COMMENT:
				ASTMResultComment resultComment = new ASTMResultComment();
				resultComment.setData(asData);
				addResultComment(resultComment);
				break;
			case TERMINATION:
				ASTMTermination termination = new ASTMTermination();
				termination.setData(asData);
				addTermination(termination);
				break;
		}
	}
	
	public 
	void addRecord(IRecord record)
	{
		if(record instanceof ASTMHeader) {
			addHeader((ASTMHeader) record);
		}
		else
		if(record instanceof ASTMTermination) {
			addTermination((ASTMTermination) record);
		}
		else
		if(record instanceof ASTMPatient) {
			addPatient((ASTMPatient) record);
		}
		else
		if(record instanceof ASTMPatientComment) {
			addPatientComment((ASTMPatientComment) record);
		}
		else
		if(record instanceof ASTMOrder) {
			addOrder((ASTMOrder) record);
		}
		else
		if(record instanceof ASTMResult) {
			addResult((ASTMResult) record);
		}
		else
		if(record instanceof ASTMResultComment) {
			addResultComment((ASTMResultComment) record);
		}
		else
		if(record instanceof ASTMQuery) {
			addQuery((ASTMQuery) record);
		}
	}
	
	public 
	IRecord removeRecord(int iIndex)
	{
		if(iIndex < 0 || iIndex >= listOfRecord.size()) {
			return null;
		}
		return listOfRecord.remove(iIndex);
	}
	
	public 
	IRecord getRecord(int iIndex)
	{
		if(iIndex < 0 || iIndex >= listOfRecord.size()) {
			return null;
		}
		return listOfRecord.get(iIndex);
	}
	
	public 
	int getRecordsCount()
	{
		return listOfRecord.size();
	}
	
	public void addHeader(ASTMHeader header) {
		if(header == null) return;
		if(listOfRecord.size() == 0) {
			listOfRecord.add(header);
		}
		else {
			IRecord record0 = listOfRecord.get(0);
			if(record0 instanceof ASTMHeader) {
				listOfRecord.set(0, header);
			}
			else {
				listOfRecord.add(0, header);
			}
		}
	}
	
	public void addTermination(ASTMTermination termination) {
		if(termination == null) return;
		int iIndexOfTermination = -1;
		for(int i = 0; i < listOfRecord.size(); i++) {
			IRecord record = listOfRecord.get(i);
			if(record instanceof ASTMTermination) {
				iIndexOfTermination = i;
				break;
			}
		}
		if(iIndexOfTermination >= 0) {
			listOfRecord.remove(iIndexOfTermination);
		}
		listOfRecord.add(termination);
	}
	
	public void addPatient(ASTMPatient patient) {
		if(patient == null) return;
		iPatientSequenceNumber++;
		iPatientCommentSequenceNumber = 0;
		iOrderSequenceNumber = 0;
		iResultSequenceNumber = 0;
		iResultCommentSequenceNumber = 0;
		patient.setSequenceNumber(iPatientSequenceNumber);
		listOfRecord.add(patient);
	}

	public void addPatientComment(ASTMPatientComment patientComment) {
		if(patientComment == null) return;
		iPatientCommentSequenceNumber++;
		patientComment.setSequenceNumber(iPatientSequenceNumber);
		listOfRecord.add(patientComment);
	}
	
	public void addOrder(ASTMOrder order) {
		if(order == null) return;
		iOrderSequenceNumber++;
		order.setSequenceNumber(iOrderSequenceNumber);
		listOfRecord.add(order);
	}

	public void addResult(ASTMResult result) {
		if(result == null) return;
		iResultSequenceNumber++;
		result.setSequenceNumber(iResultSequenceNumber);
		listOfRecord.add(result);
	}

	public void addResultComment(ASTMResultComment resultComment) {
		if(resultComment == null) return;
		iResultCommentSequenceNumber++;
		resultComment.setSequenceNumber(iResultCommentSequenceNumber);
		listOfRecord.add(resultComment);
	}
	
	public void addQuery(ASTMQuery query) {
		if(query == null) return;
		iQuerySequenceNumber++;
		query.setSequenceNumber(iQuerySequenceNumber);
		listOfRecord.add(query);
	}

	public String toString() {
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
