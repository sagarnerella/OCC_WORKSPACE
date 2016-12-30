package com.osi.manager.domain;

import java.io.Serializable;

public class UMObject implements Serializable{
	 
	private String unitOfMeasure;
	private String connectType;
	
	public String getUnitOfMeasure() {
		return unitOfMeasure;
	}
	public void setUnitOfMeasure(String unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}
	public String getConnectType() {
		return connectType;
	}
	public void setConnectType(String connectType) {
		this.connectType = connectType;
	}
	
}
