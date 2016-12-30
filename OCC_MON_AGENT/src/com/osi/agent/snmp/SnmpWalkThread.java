package com.osi.agent.snmp;

import java.util.List;

import org.snmp4j.CommunityTarget;
import org.snmp4j.smi.OID;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

public class SnmpWalkThread extends Thread{
	List<TreeEvent> events;
	CommunityTarget target;
	OID rootOID;
	TreeUtils treeUtils;
	SnmpWalkThread(TreeUtils treeUtils,List<TreeEvent> events,CommunityTarget target,OID rootOID){
		this.treeUtils=treeUtils;
		this.events=events;
		this.target=target;
		this.rootOID=rootOID;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		events=treeUtils.getSubtree(target, rootOID);
	}
	
	public List<TreeEvent> getEventList(){
		return this.events;
	}

}
