package com.osi.manager.webservice;


import org.apache.log4j.Logger;

import com.osi.agent.webservicestub.CheckConnectionImplService;
import com.osi.agent.webservicestub.CheckConnectionIntf;
public class CallAgentWebService implements CallAgentWebServiceInterface{
	private static final Logger LOGGER = Logger.getLogger(CallAgentWebService.class);
private static String ipAddress;
public static String getIpAddress() {
 return ipAddress;
}
public static void setIpAddress(String ipAddress) {
 CallAgentWebService.ipAddress = ipAddress;
}
@Override
public String callSnmpTableWebService(String address, String tableOid,
  String mib, String communityStrg) {
  String result="";
 try {
  // TODO Auto-generated method stub
  CallAgentWebService.setIpAddress(address);
  CheckConnectionImplService.setIpAddress(address);
  LOGGER.info("CheckConnectionImplService.ipAddres "+address);
  CheckConnectionImplService snmpTableService=new CheckConnectionImplService();
  CheckConnectionIntf snmpTableInterface=snmpTableService.getCheckConnectionImplPort();
  LOGGER.info("SnmpTable Webservice Result at Agent "+snmpTableInterface.executeSnmpTable(address,tableOid,mib,communityStrg));
  result= snmpTableInterface.executeSnmpTable(address,tableOid,mib,communityStrg);
  LOGGER.info("SnmpTable Webservice Result at Agent "+result);
 } catch (Exception e) {
  // TODO Auto-generated catch block
  //e.printStackTrace();
  LOGGER.error("Exception ",e);
 }
 return result;
}}
