/**
 * 
 */
package com.osi.agent.snmp.adapter;

import com.osi.agent.exception.OCCAgentSNMPRecieverException;
import com.osi.agent.exception.OCCAgentSNMPSenderException;

/**
 * @author jkorada1
 *
 */
public abstract class SNMPAgentAdapter {
	protected  boolean send(String oid, String value) throws OCCAgentSNMPSenderException{
		return true;
	}
	protected  Object recieve() throws OCCAgentSNMPRecieverException{
		return null;
	}
}
