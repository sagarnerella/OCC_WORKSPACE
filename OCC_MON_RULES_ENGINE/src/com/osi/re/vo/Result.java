package com.osi.re.vo;

import javax.xml.bind.annotation.XmlRootElement;

import com.sun.xml.internal.txw2.annotation.XmlValue;

@XmlRootElement(name = "RESULT")
public class Result {
	private int resultId;
	private boolean ISINCIDENT;
	private String MSG;
	private String FORMATTEDVALUE;
	
	public Result(){}

	public int getResultId() {
		return resultId;
	}
	@XmlValue
	public void setResultId(int resultId) {
		this.resultId = resultId;
	}

	public boolean isISINCIDENT() {
		return ISINCIDENT;
	}
	@XmlValue
	public void setISINCIDENT(boolean iSINCIDENT) {
		ISINCIDENT = iSINCIDENT;
	}

	public String getMSG() {
		return MSG;
	}
	@XmlValue
	public void setMSG(String mSG) {
		MSG = mSG;
	}

	public String getFORMATTEDVALUE() {
		return FORMATTEDVALUE;
	}
	@XmlValue
	public void setFORMATTEDVALUE(String fORMATTEDVALUE) {
		FORMATTEDVALUE = fORMATTEDVALUE;
	}
	
}
