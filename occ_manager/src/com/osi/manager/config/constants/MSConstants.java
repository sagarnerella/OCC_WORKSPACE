/**
 * 
 */
package com.osi.manager.config.constants;

import com.osi.manager.common.CommonUtilities;

/**
 * @author jkorada1
 *
 */
public class MSConstants {
	
	public static final String ERROR_CODE_7000="7000";
	public static final String ERROR_CODE_7001="7001";
	public static final String ERROR_CODE_7002="7002";
	public static final String ERROR_CODE_7003="7003";
	public static final String SNMP=CommonUtilities.getProperty("SNMP");
	public static final String SNMP_WALK=CommonUtilities.getProperty("SNMP_WALK");
	public static final String JMX=CommonUtilities.getProperty("JMX");
	public static final String JDBC=CommonUtilities.getProperty("JDBC");
	public static final String SSH=CommonUtilities.getProperty("SSH");
	public static final String FILE_MONITORING=CommonUtilities.getProperty("FILE_MONITORING");
	//Added for SNMPTable
	public static final String SNMP_TABLE=CommonUtilities.getProperty("SNMP_TABLE");
	
}
