package org.dew.comm.astm;

import org.dew.comm.IRecord;

import java.util.Date;

public 
class ASTMResult implements IRecord
{
	private int sequenceNumber = 1;
	private UniversalTestId universalTestId;
	private String dataValue;
	private String units;
	private String abnormalFlags;
	private String status;
	private Date dateTestCompleted;
	
	public ASTMResult()
	{
	}
	
	public ASTMResult(String sManufacturersCode, String sResultAspects, String sDataValue, String sUnits)
	{
		this.universalTestId = new UniversalTestId(sManufacturersCode, sResultAspects);
		this.dataValue = sDataValue;
		this.units = sUnits;
		this.dateTestCompleted = new Date();
	}
	
	public
	void setData(String[] asData)
	{
		if(asData == null) return;
		if(asData.length > 1 && asData[0] != null && asData[1] != null) {
			this.universalTestId = new UniversalTestId(asData[0], asData[1]);
		}
		if(asData.length > 2 && asData[2] != null) dataValue = asData[2];
		if(asData.length > 3 && asData[3] != null) units     = asData[3];
		if(asData.length > 4 && asData[4] != null) {
			dateTestCompleted = Utils.stringToDateTime(asData[4]);
			if(dateTestCompleted == null) dateTestCompleted = new Date();
		}
	}
	
	public 
	String[] getData() 
	{
		String[] asResult = new String[5];
		if(universalTestId != null) {
			asResult[0] = universalTestId.getManufacturersCode();
			asResult[1] = universalTestId.getResultAspects();
		}
		asResult[2] = dataValue;
		asResult[3] = units;
		asResult[4] = Utils.formatDateTime(dateTestCompleted);
		return asResult;
	}
	
	public 
	Type getType() 
	{
		return Type.RESULT;
	}
	
	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public UniversalTestId getTest() {
		return universalTestId;
	}

	public void setTest(UniversalTestId test) {
		this.universalTestId = test;
	}

	public String getDataValue() {
		return dataValue;
	}

	public void setDataValue(String dataValue) {
		this.dataValue = dataValue;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public String getAbnormalFlags() {
		return abnormalFlags;
	}

	public void setAbnormalFlags(String abnormalFlags) {
		this.abnormalFlags = abnormalFlags;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getDateTestCompleted() {
		return dateTestCompleted;
	}

	public void setDateTestCompleted(Date dateTestCompleted) {
		this.dateTestCompleted = dateTestCompleted;
	}
	
	public
	String toString()
	{
		StringBuffer sbResult = new StringBuffer();
		sbResult.append('R');
		sbResult.append('|');
		sbResult.append(sequenceNumber);
		sbResult.append('|');
		sbResult.append(universalTestId != null ? universalTestId.toString() : "");
		sbResult.append('|');
		sbResult.append(dataValue != null ? dataValue : "");
		sbResult.append('|');
		sbResult.append(units != null ? units : "");
		sbResult.append("||");
		sbResult.append(abnormalFlags != null ? abnormalFlags : "");
		sbResult.append("||");
		sbResult.append(status != null ? status : "");
		sbResult.append("||||");
		sbResult.append(Utils.formatDateTime(dateTestCompleted));
		sbResult.append((char) 13); // <CR>
		return sbResult.toString();
	}
	
	public
	byte[] getBytes()
	{
		return toString().getBytes();
	}
}
