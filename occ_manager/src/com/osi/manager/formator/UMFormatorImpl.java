package com.osi.manager.formator;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import com.osi.manager.config.constants.MSConstants;
import com.osi.manager.domain.UMObject;
import com.osi.manager.exception.FinderException;


public class UMFormatorImpl implements IFormator{
	private static final Logger LOGGER = Logger.getLogger(UMFormatorImpl.class);
	@Override
	public String formatValueWithUM(String value, UMObject umObject) throws Exception {
		try {
			
			String returnData=null;
			if(umObject.getUnitOfMeasure().equalsIgnoreCase("MB")){
				returnData = convertBytesToMB(umObject, value);
				return returnData;
			} else if(umObject.getUnitOfMeasure().equalsIgnoreCase("GB")){
				returnData = convertBytesToGB(umObject, value);
				return returnData;
			} else if(umObject.getUnitOfMeasure().equalsIgnoreCase("COUNT")){
				return value;
			} else if(umObject.getUnitOfMeasure().equalsIgnoreCase("PERCENT")){
				return value;
			} else if(umObject.getUnitOfMeasure().equalsIgnoreCase("MS")){
				return value;
			} else if(umObject.getUnitOfMeasure().equalsIgnoreCase("MATCH")){
				return value;
			} else if(umObject.getUnitOfMeasure().equalsIgnoreCase("GAUAGE")){ 
				return value;
			} else if(umObject.getUnitOfMeasure().equalsIgnoreCase("BINARY")){
				java.util.Date date = null;
		    	try {
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
					date = simpleDateFormat.parse(value);
					return "1";
				} catch (ParseException pe) {
					return "0";
				} catch (Exception e) {
					return "0";
				}
			} else if(umObject.getUnitOfMeasure().equalsIgnoreCase("WBINARY")){
				try {
					Integer.parseInt(value);
					return "1";
				} catch (Exception e) {
					return "0";
				}
			} else if(umObject.getUnitOfMeasure().equalsIgnoreCase("PS")){
				try {
					Integer.parseInt(value);
					return "1";
				} catch (Exception e) {
					return "0";
				}
			} else if(umObject.getUnitOfMeasure().equalsIgnoreCase("PO")){
				if (value.equalsIgnoreCase("ready to print")) {
					return "1";
				} else {
					return "0";
				}
			} else{
				//value = 0.toString();Fri Oct 24 18:08:38 2014
				return value;
			}
			
		}catch (FinderException e) {
			LOGGER.error("",e);
			return "0";
			//throw new FinderException(MSConstants.ERROR_CODE_7001, "Error occured while parsing the result", e.getMessage());
		}catch (Exception e) {
			LOGGER.error("",e);
			return "0";
			//throw new FinderException(MSConstants.ERROR_CODE_7001, "Error occured while parsing the result", e.getMessage());
		}finally{
		}
	}
	private String convertBytesToMB(UMObject umObject, String value) throws FinderException {
		final long  MEGABYTE = 1024L * 1024L;
		String mbValue = null;
		Long calValue = null;
		try{
			if (value.equalsIgnoreCase("Null")) {
				value = "0";
			}
			if (umObject.getConnectType().equalsIgnoreCase("SNMP")) {
				calValue = Long.parseLong(value)*1024 / MEGABYTE ;
				mbValue = calValue.toString();
			} else if (umObject.getConnectType().equalsIgnoreCase("JMX")) {
				calValue = Long.parseLong(value) / MEGABYTE ;
				mbValue = calValue.toString();
			} else {
				calValue = Long.parseLong(value) / MEGABYTE ;
				mbValue = calValue.toString();
			}
		}catch (Exception e) {
			LOGGER.error("",e);
			throw new FinderException(MSConstants.ERROR_CODE_7001, "Error occured while parsing the result", e.getMessage());
		}finally{
		}
		return mbValue;
	}
	
	private String convertBytesToGB(UMObject umObject, String value) throws FinderException {
		final long  GIGABYTE = 1024L * 1024L * 1024L;
		String gbValue = null;
		Long calValue = null;
		try{	
			if (value.equalsIgnoreCase("Null")) {
				value = "0";
			}
			if (umObject.getConnectType().equalsIgnoreCase("SNMP")) {
				calValue = Long.parseLong(value)*1024 / GIGABYTE ;
				gbValue = calValue.toString();
			} else if (umObject.getConnectType().equalsIgnoreCase("JMX")) {
				calValue = Long.parseLong(value) / GIGABYTE ;
				gbValue = calValue.toString();
			} else {
				calValue = Long.parseLong(value) / GIGABYTE ;
				gbValue = calValue.toString();
			}
		}catch (Exception e) {
			LOGGER.error("",e);
			throw new FinderException(MSConstants.ERROR_CODE_7001, "Error occured while parsing the result", e.getMessage());
		}finally{
		}
		return gbValue;
	}
	
}
