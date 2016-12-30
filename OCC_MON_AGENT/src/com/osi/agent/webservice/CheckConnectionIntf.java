package com.osi.agent.webservice;

import javax.jws.WebMethod;
import javax.jws.WebService;

public interface CheckConnectionIntf {
	public String executeSnmpTable(String address,String tableOid,String mib,String communityStrg);
	
}