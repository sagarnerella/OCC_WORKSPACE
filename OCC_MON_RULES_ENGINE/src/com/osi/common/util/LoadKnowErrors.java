package com.osi.common.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.apache.log4j.Logger;

import com.osi.re.dao.REDao;
/*
 * snerella
 * */
public class LoadKnowErrors {
	private static final Logger LOGGER = Logger.getLogger(LoadKnowErrors.class);
	private static Map<String,String> loadKnowErrorMap=null;
	static{
		try {
			if(loadKnowErrorMap==null){
			Connection connection = MSConnectionManager.getConnection();
			loadKnowErrorMap=REDao.getKnownErrors(connection);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			LOGGER.error("SQLException in LoadKnowErrors",e);
		}
		
		
	}
	
	public static Map<String,String> getKnowErrors(){
		return loadKnowErrorMap;
	}
}
