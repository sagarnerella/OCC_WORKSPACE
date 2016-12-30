package com.osi.re.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.log4j.Logger;
public class CommonUtilities {
	private static final Logger LOGGER = Logger.getLogger(CommonUtilities.class);
	private static Properties msAgentProperties = new Properties();
	static {
		try {
			/*
			 * load the properties file onto Properties object. The properties
			 * file should be in the same location where jar is placed
			 */
			msAgentProperties.load(new FileInputStream(System.getProperty("user.dir")+ "/MSRulesEngine.properties"));
		} catch (IOException e) {
			LOGGER.error("CommonUtilities::readProperties:IOException", e);
		} catch (Exception e) {
			LOGGER.error("CommonUtilities::readProperties:IOException", e);
		}
	}

	public static String getProperty(String key) {
		return msAgentProperties.getProperty(key);
	}
	
	public static long getExecTimeinUTC() {
	     Date utcDate = null;
	  try {
	   SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	   utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	   SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	   utcDate = dateformat.parse(utcFormat.format(System.currentTimeMillis()));
	  } catch (ParseException e) {
	   LOGGER.error("",e);
	  }
	    return utcDate.getTime();
	 }
	
	public static String formatDate(long executionTime){
		Date date=new Date(executionTime);
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df2.format(date);
	}
}
