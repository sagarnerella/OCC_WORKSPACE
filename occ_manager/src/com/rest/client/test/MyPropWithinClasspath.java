package com.rest.client.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.osi.manager.common.CommonUtilities;
 
public class MyPropWithinClasspath {
	private static final Logger LOGGER = Logger.getLogger(MyPropWithinClasspath.class);
    private Properties prop = null;
     
    public MyPropWithinClasspath(){
         
        InputStream is = null;
        try {
            this.prop = new Properties();
            LOGGER.info("this.getClass() "+this.getClass());
            is = this.getClass().getResourceAsStream("/MSManager.properties");
            prop.load(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
     
    public String getPropertyValue(String key){
        return this.prop.getProperty(key);
    }
     
    public static void main(String a[]){
         
        MyPropWithinClasspath mpc = new MyPropWithinClasspath();
        LOGGER.info("db.host: "+mpc.getPropertyValue("db.mysql_driver"));
        LOGGER.info("db.user: "+mpc.getPropertyValue("mysql_url"));
        LOGGER.info(" CommonUtilities.getPropertymysql_min_poolsize "+mpc.getPropertyValue("mysql_min_poolsize"));
    }
}
