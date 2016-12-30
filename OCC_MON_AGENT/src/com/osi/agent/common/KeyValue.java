package com.osi.agent.common;

public class KeyValue {
	
	public static String MS_PARSE_TEST_CONNECTION_JOB_INTERVAL = CommonUtilities.getProperty("MS_PARSE_TEST_CONNECTION_JOB_INTERVAL");
	public static String MS_PARSE_UNSCHEDULEXML_JOB_INTERVAL = CommonUtilities.getProperty("MS_PARSE_UNSCHEDULEXML_JOB_INTERVAL");
	public static String MS_PARSE_ALERTCONFIGXML_JOB_INTERVAL = CommonUtilities.getProperty("MS_PARSE_ALERTCONFIGXML_JOB_INTERVAL");
	public static String MS_PARSE_SENDRESULT_TO_MANAGER_JOB_INTERVAL = CommonUtilities.getProperty("MS_PARSE_SENDRESULT_TO_MANAGER_JOB_INTERVAL");
	public static String MS_PARSE_DEVICEXML_JOB_INTERVAL = CommonUtilities.getProperty("MS_PARSE_DEVICEXML_JOB_INTERVAL");
	public static String GETDEVICEXML_FROM_MANAGER_URL_OF_MANAGER = CommonUtilities.getProperty("GETDEVICEXML_FROM_MANAGER_URL_OF_MANAGER");
	public static String SENDRESULT_TOMANAGER_URL_OF_MANAGER = CommonUtilities.getProperty("SENDRESULT_TOMANAGER_URL_OF_MANAGER");
	public static String MS_PARSE_HEARTBEAT_JOB_INTERVAL = CommonUtilities.getProperty("MS_PARSE_HEARTBEAT_JOB_INTERVAL");
	public static String MS_PARSE_CONFIG_JOB_INTERVAL = CommonUtilities.getProperty("MS_PARSE_CONFIG_JOB_INTERVAL");
	public static String MS_ERROR_MONITORING_JOB_INTERVAL = CommonUtilities.getProperty("MS_ERROR_MONITORING_JOB_INTERVAL");
	public static String MS_RESULT_SERIALIZE_JOB_INTERVAL = CommonUtilities.getProperty("MS_RESULT_SERIALIZE_JOB_INTERVAL");
	public static String MS_MANAGER_RESULT_POLLING_INTERVAL = CommonUtilities.getProperty("MS_MANAGER_RESULT_POLLING_INTERVAL");
	public static String MS_FILE_COUNT = CommonUtilities.getProperty("MS_FILE_COUNT");
	public static String MS_CONFIG_LOCATION = CommonUtilities.getProperty("MS_CONFIG_LOCATION");
	public static String MS_CONFIG_UPDATE_LOCATION = CommonUtilities.getProperty("MS_CONFIG_UPDATE_LOCATION");
	public static String MS_MON_ERROR_LOG_LOCATION = CommonUtilities.getProperty("MS_MON_ERROR_LOG_LOCATION");
	public static String MS_MON_RESULT_LOCATION = CommonUtilities.getProperty("MS_MON_RESULT_LOCATION");
	public static String MS_CONFIG_LOCATION_WINDOWS = CommonUtilities.getProperty("MS_CONFIG_LOCATION_WINDOWS");
	public static String MS_CONFIG_UPDATE_LOCATION_WINDOWS = CommonUtilities.getProperty("MS_CONFIG_UPDATE_LOCATION_WINDOWS");
	public static String MS_MON_ERROR_LOG_LOCATION_WINDOWS = CommonUtilities.getProperty("MS_MON_ERROR_LOG_LOCATION_WINDOWS");
	public static String MS_MON_RESULT_LOCATION_WINDOWS = CommonUtilities.getProperty("MS_MON_RESULT_LOCATION_WINDOWS");
	public static String MS_CONFIG_LOCATION_LINUX = CommonUtilities.getProperty("MS_CONFIG_LOCATION_LINUX");
	public static String MS_CONFIG_UPDATE_LOCATION_LINUX = CommonUtilities.getProperty("MS_CONFIG_UPDATE_LOCATION_LINUX");
	public static String MS_MON_ERROR_LOG_LOCATION_LINUX = CommonUtilities.getProperty("MS_MON_ERROR_LOG_LOCATION_LINUX");
	public static String MS_MON_RESULT_LOCATION_LINUX = CommonUtilities.getProperty("MS_MON_RESULT_LOCATION_LINUX");
	public static String CHECKPOINT_MONITORING_JOB_BASE_NAME = CommonUtilities.getProperty("CHECKPOINT_MONITORING_JOB_BASE_NAME");
	public static String CHECKPOINT_MONITORING_TRIGGER_BASE_NAME = CommonUtilities.getProperty("CHECKPOINT_MONITORING_TRIGGER_BASE_NAME");
	public static String CHECKPOINT_MONITORING_JOB_GROUP_BASE_NAME = CommonUtilities.getProperty("CHECKPOINT_MONITORING_JOB_GROUP_BASE_NAME");
	public static String CHECKPOINT_MONITORING_TRIGGER_GROUP_BASE_NAME = CommonUtilities.getProperty("CHECKPOINT_MONITORING_TRIGGER_GROUP_BASE_NAME");
	public static String THRESHOLD_CHECKPOINT_MONITORING_JOB_BASE_NAME = CommonUtilities.getProperty("THRESHOLD_CHECKPOINT_MONITORING_JOB_BASE_NAME");
	public static String THRESHOLD_CHECKPOINT_MONITORING_TRIGGER_BASE_NAME = CommonUtilities.getProperty("THRESHOLD_CHECKPOINT_MONITORING_TRIGGER_BASE_NAME");
	public static String THRESHOLD_CHECKPOINT_MONITORING_JOB_GROUP_BASE_NAME = CommonUtilities.getProperty("THRESHOLD_CHECKPOINT_MONITORING_JOB_GROUP_BASE_NAME");
	public static String THRESHOLD_CHECKPOINT_MONITORING_TRIGGER_GROUP_BASE_NAME = CommonUtilities.getProperty("THRESHOLD_CHECKPOINT_MONITORING_TRIGGER_GROUP_BASE_NAME");
	public static String CHECKPOINT_DETAILS_MEATADATA = CommonUtilities.getProperty("CHECKPOINT_DETAILS_MEATADATA");
	public static String CACHE_MANAGER = CommonUtilities.getProperty("CACHE_MANAGER");
	public static String QUARTZ_THREAD_COUNT = CommonUtilities.getProperty("QUARTZ_THREAD_COUNT");
	public static String ERROR_MESSAGE = CommonUtilities.getProperty("ERROR_MESSAGE");
	public static String AGENT_HOST_NAME = CommonUtilities.getProperty("AGENT_HOST_NAME");
	public static String AGENT_PORT = CommonUtilities.getProperty("AGENT_PORT");
	public static String AGENT_COMMUNITY_STRING = CommonUtilities.getProperty("AGENT_COMMUNITY_STRING");
	public static String AGENT_RESULT_OID = CommonUtilities.getProperty("AGENT_RESULT_OID");
	public static String AGENT_ERROR_OID = CommonUtilities.getProperty("AGENT_ERROR_OID");
	public static String MANAGER_HOST_NAME = CommonUtilities.getProperty("MANAGER_HOST_NAME");
	public static String MANAGER_PORT = CommonUtilities.getProperty("MANAGER_PORT");
	public static String MANAGER_COMMUNITY_STRING = CommonUtilities.getProperty("MANAGER_COMMUNITY_STRING");
	public static String MANAGER_RETRY_ATTEMPTS = CommonUtilities.getProperty("MANAGER_RETRY_ATTEMPTS");
	public static String MANAGER_TIME_OUT = CommonUtilities.getProperty("MANAGER_TIME_OUT");
	public static String DEVICE_CONFIG_OID = CommonUtilities.getProperty("DEVICE_CONFIG_OID");
	public static String DEVICE_THRESHOLD_CONFIG_OID = CommonUtilities.getProperty("DEVICE_THRESHOLD_CONFIG_OID");
	public static String AGENT_ID = CommonUtilities.getProperty("AGENT_ID");
	public static String SNMP_VERSION = CommonUtilities.getProperty("SNMP_VERSION");
	public static String SNMP_TIMEOUT = CommonUtilities.getProperty("SNMP_TIMEOUT");
	public static String SNMP_RETRIES = CommonUtilities.getProperty("SNMP_RETRIES");
	public static String JMX_CONNECT_TYPE = CommonUtilities.getProperty("JMX_CONNECT_TYPE");
	public static String SNMP_CONNECT_TYPE = CommonUtilities.getProperty("SNMP_CONNECT_TYPE");
	public static String SNMP_WALK_CONNECT_TYPE = CommonUtilities.getProperty("SNMP_WALK_CONNECT_TYPE");
	public static String JDBC_CONNECT_TYPE = CommonUtilities.getProperty("JDBC_CONNECT_TYPE");
	public static String LOGMONITOR_CONNECT_TYPE = CommonUtilities.getProperty("LOGMONITOR_CONNECT_TYPE");
	public static String SSHWRAPPER_CONNECT_TYPE = CommonUtilities.getProperty("SSHWRAPPER_CONNECT_TYPE");
	//Added For SnmpTable 
	public static String SNMPTABLE_CONNECT_TYPE = CommonUtilities.getProperty("SNMPTABLE_CONNECT_TYPE");
	public static String SNMPTABLE_ROWID = CommonUtilities.getProperty("SNMPTABLE_ROWID");
	public static String HRSTORAGE_ALLOCATION_UNITS = CommonUtilities.getProperty("HRSTORAGE_ALLOCATION_UNITS");
	public static String HRSTORAGE_MESUREMENT = CommonUtilities.getProperty("HRSTORAGE_MESUREMENT");
	public static String HRSTORAGE_MESURE_PERCENT = CommonUtilities.getProperty("HRSTORAGE_MESURE_PERCENT");
	public static String HRSTORAGE_SIZE = CommonUtilities.getProperty("HRSTORAGE_SIZE");
	public static String HRSTORAGE_USED = CommonUtilities.getProperty("HRSTORAGE_USED");
	public static String HRSTORAGE_DESCR = CommonUtilities.getProperty("HRSTORAGE_DESCR");
	public static String MESUREMENT_BYTE = CommonUtilities.getProperty("MESUREMENT_BYTE");
	//Query Appenders
	//public static String JDBC_QUERY_APPENDER_1 = CommonUtilities.getProperty("JDBC_QUERY_APPENDER_1");
	//public static String JDBC_QUERY_APPENDER_2 = CommonUtilities.getProperty("JDBC_QUERY_APPENDER_2");
	//Message Appenders
	public static String JDBC_MSG_APPENDER_1 = CommonUtilities.getProperty("JDBC_MSG_APPENDER_1");
	public static String JDBC_MSG_APPENDER_2 = CommonUtilities.getProperty("JDBC_MSG_APPENDER_2");
	
	public static String No_OF_LINES_TO_READ = CommonUtilities.getProperty("No_OF_LINES_TO_READ");
	public static String No_OF_LOOPS = CommonUtilities.getProperty("No_OF_LOOPS");
	public static String MAX_CHARS_FOR_INCIDENT = CommonUtilities.getProperty("MAX_CHARS_FOR_INCIDENT");
	private KeyValue(){
		
	}
}
