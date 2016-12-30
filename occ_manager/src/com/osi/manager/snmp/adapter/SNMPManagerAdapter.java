/**
 * 
 */
package com.osi.manager.snmp.adapter;

import com.osi.manager.exception.OCCManagerSNMPRecieverException;
import com.osi.manager.exception.OCCManagerSNMPSenderException;

/**
 * @author jkorada1
 *
 */
public abstract class SNMPManagerAdapter {
	protected  boolean send(String oid, String value) throws OCCManagerSNMPSenderException{
		return true;
	}
	protected  Object recieve() throws OCCManagerSNMPRecieverException{
		return null;
	}
}
