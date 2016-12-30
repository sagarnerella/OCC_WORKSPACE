package com.osi.common.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;

import com.osi.common.config.constants.MSConstants;
import com.osi.re.exception.PersistException;

public class DBUtil {

	private static final Logger LOGGER = Logger.getLogger(DBUtil.class);
	
	public boolean persistData(List<String> sqlQuery, Connection  connection) throws PersistException {
		boolean persistStatus=false;
		try {
			Statement statement = connection.createStatement();
			for (String sql : sqlQuery) {
				statement.addBatch(sql);
			}
			statement.executeBatch();
			persistStatus=true;
		} catch (SQLException e) {
			LOGGER.error("",e);
			throw new PersistException(MSConstants.ERROR_CODE_7002, "Unable to execute batch", e.getMessage());
		} catch (Exception e) {
			LOGGER.error("",e);
			throw new PersistException(MSConstants.ERROR_CODE_7002, "Unable to execute batch", e.getMessage());
		}
		return persistStatus;
	}
	

	public int saveOrUpdateOrDelete(String sqlQuery,Connection connection) {
		int rowCount = -1;
		try {
			connection.setAutoCommit(false);
			Statement statement = connection.createStatement();
			rowCount = statement.executeUpdate(sqlQuery);
			connection.commit();
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				LOGGER.error("",e1);
			}
			LOGGER.error("",e);
		}
		return rowCount;
	}
}
