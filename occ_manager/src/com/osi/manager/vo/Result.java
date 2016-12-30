package com.osi.manager.vo;

import javax.xml.bind.annotation.XmlRootElement;


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
	public void setResultId(int resultId) {
		this.resultId = resultId;
	}

	public boolean isISINCIDENT() {
		return ISINCIDENT;
	}
	public void setISINCIDENT(boolean iSINCIDENT) {
		ISINCIDENT = iSINCIDENT;
	}

	public String getMSG() {
		return MSG;
	}
	public void setMSG(String mSG) {
		MSG = mSG;
	}

	public String getFORMATTEDVALUE() {
		return FORMATTEDVALUE;
	}
	public void setFORMATTEDVALUE(String fORMATTEDVALUE) {
		FORMATTEDVALUE = fORMATTEDVALUE;
	}
	
}
