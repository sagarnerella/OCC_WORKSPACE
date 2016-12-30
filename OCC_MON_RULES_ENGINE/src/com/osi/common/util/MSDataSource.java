package com.osi.common.util;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.osi.re.common.CommonUtilities;

public class MSDataSource {

    private static MSDataSource datasource;
    private ComboPooledDataSource cpds;

    private MSDataSource() throws IOException, SQLException, PropertyVetoException {
        cpds = new ComboPooledDataSource();
        cpds.setDriverClass(CommonUtilities.getProperty("mysql_driver")); //loads the jdbc driver
        cpds.setJdbcUrl(CommonUtilities.getProperty("mysql_url"));
        cpds.setUser(CommonUtilities.getProperty("mysql_user"));
        cpds.setPassword(CommonUtilities.getProperty("mysql_password"));

        // the settings below are optional -- c3p0 can work with defaults
        cpds.setMinPoolSize(Integer.parseInt(CommonUtilities.getProperty("mysql_min_poolsize")));
        cpds.setAcquireIncrement(Integer.parseInt(CommonUtilities.getProperty("mysql_acquire_increment")));
        cpds.setMaxPoolSize(Integer.parseInt(CommonUtilities.getProperty("mysql_max_poolsize")));
        cpds.setMaxStatements(Integer.parseInt(CommonUtilities.getProperty("mysql_max_statements")));

    }

    public static MSDataSource getInstance() throws IOException, SQLException, PropertyVetoException {
        if (datasource == null) {
            datasource = new MSDataSource();
            return datasource;
        } else {
            return datasource;
        }
    }

    public ComboPooledDataSource getPooledDataSource(){
    	return cpds;
    }
    
}

