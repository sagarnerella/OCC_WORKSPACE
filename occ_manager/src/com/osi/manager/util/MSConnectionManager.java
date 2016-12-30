package com.osi.manager.util;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class MSConnectionManager {
	
	private static final Logger LOGGER = Logger.getLogger(MSConnectionManager.class);
	
	public static Connection getConnection() throws SQLException {
		Connection connection=null;
		try {
			MSDataSource dataSource=MSDataSource.getInstance();
			connection=dataSource.getPooledDataSource().getConnection(); 
	    	connection.setAutoCommit(false);
		} catch (Exception e) {
			LOGGER.error("Unable to load datasource",e);
			throw new SQLException("Unable to load datasource");
		}
        return connection;
    }

}
