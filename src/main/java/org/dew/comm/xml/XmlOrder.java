package org.dew.comm.xml;

import org.dew.comm.IRecord;

public 
class XmlOrder implements IRecord 
{
	protected String[] asData;
	protected boolean lastOfOrders = false;

	public void setLastOfOrders(boolean lastOfOrders) {
		this.lastOfOrders = lastOfOrders;
	}
	public boolean isLastOfOrders() {
		return lastOfOrders;
	}
	
	public byte[] getBytes() {
		return toString().getBytes();
	}

	public Type getType() {
		return Type.ORDER;
	}

	public void setData(String[] asData) {
		this.asData = asData;
	}
	
	public String[] getData() {
		return asData;
	}
	
	public String toString() {
		if(asData == null) return "";
		String sResult = "<Order>";
		if(asData.length > 0 && asData[0] != null) sResult += "<SpecimenId>" + asData[0] + "</SpecimenId>";
		if(asData.length > 1 && asData[1] != null) sResult += "<Priority>"   + asData[1] + "</Priority>";
		if(asData.length > 2 && asData[2] != null) sResult += "<ActionCode>" + asData[2] + "</ActionCode>";
		if(asData.length > 3 && asData[3] != null) sResult += "<ReportType>" + asData[3] + "</ReportType>";
		if(asData.length > 4) {
			sResult += "<ListOfTest>";
			for(int i = 4; i < asData.length; i++) {
				String sTest = asData[i];
				if(sTest != null && sTest.length() > 0) {
					sResult += "<Test>" + asData[i] + "</Test>";
				}
			}
			sResult += "</ListOfTest>";
		}
		sResult += "</Order>";
		if(lastOfOrders) sResult += "</Orders>";
		return sResult;
	}
}
