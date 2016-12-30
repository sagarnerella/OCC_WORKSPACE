package com.osi.agent.webservice;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.adventnet.snmp.beans.DataException;
import com.adventnet.snmp.beans.SnmpTable;
import com.adventnet.snmp.mibs.MibException;
import org.apache.log4j.Logger;
import com.osi.agent.AgentActivator;
import com.osi.agent.webservice.CheckConnectionIntf;
import com.osi.agent.webservice.SnmpTableRow;
public class CheckConnectionImpl implements CheckConnectionIntf {
	private static final Logger LOGGER = Logger.getLogger(CheckConnectionImpl.class);
	public String executeSnmpTable(String address, String tableOid, String mib,
			String communityStrg) {
		String result="";
		List<String> columnHeadings=new ArrayList<String>();
		List<SnmpTableRow> snmpTableRowData=new ArrayList<SnmpTableRow>();
		String remoteHost = address; 
		String tableoid =tableOid;
		ObjectMapper mapper=new ObjectMapper();
		
		try{
			SnmpTable table = new SnmpTable();
			table.setTargetHost(remoteHost);
			table.setCommunity(communityStrg);
			table.loadMibs(mib);
			table.setTableOID(tableoid);

			LOGGER.info("Getting table.  Table items in Webservice :");

			StringBuffer sb = new StringBuffer();

			try { Thread.sleep(1000); }  // allow some time to get all rows
			catch (InterruptedException ex) {}  

			for (int i=0;i<table.getColumnCount();i++){  // print column names
				sb.append(table.getColumnName(i)+" \t");
				columnHeadings.add(table.getColumnName(i).toString());
			}
			SnmpTableRow firstRow=new SnmpTableRow();
			firstRow.setValue(columnHeadings);
			//snmpTableRowData.add(firstRow);

			if(table.getRowCount()>0)
				snmpTableRowData.add(firstRow);
			
			for (int j=0;j<table.getRowCount();j++) {
				List<String> rowdata=new ArrayList<String>();
				SnmpTableRow snmpTablRowData = new SnmpTableRow();
				for (int i=0;i<table.getColumnCount();i++){
					rowdata.add(table.getValueAt(j,i)!=null?table.getValueAt(j,i).toString().trim():"");
				}
				snmpTablRowData.setValue(rowdata);
				snmpTableRowData.add(snmpTablRowData);
				rowdata=null;
			}
			
			if(table.getRowCount()>0)
				result=mapper.writeValueAsString(snmpTableRowData);
			else
				result=mapper.writeValueAsString("Incorrect Data.Please check Host IP Address and Community String.");
			LOGGER.info("json data " + result);
		} catch (MibException e) {
			LOGGER.info("MibException while parsing MIB in SNMPTABLE Webservice"
							+ e);
		} catch (FileNotFoundException e) {
			LOGGER.info("FileNotFoundException while reading MIB file in SNMPTABLE Webservice"
							+ e);
			result = "Incorrect Data.Please check MIB";
		} catch (DataException e) {
			LOGGER.info("DataException while reading data from MIB in SNMPTABLE Webservice"
							+ e);
			result = "Incorrect Data.Please check OID";
		} catch (IndexOutOfBoundsException e) {
			LOGGER.info("IOBException while getting the result from SnmpTable in SNMPTABLE Webservice"
							+ e);
		} catch (JsonGenerationException e) {
			LOGGER.info("JsonGenerationException while Json Generating "
					+ e);
		} catch (JsonMappingException e) {
			LOGGER.info("JsonMappingException while Json Mapping " + e);
		} catch (IOException e) {
			LOGGER.info("IOException while converting Data into ObjectMapper's writeValueAsString "
							+ e);
		} catch (Exception e) {
			LOGGER.info("Exception " + e);
		}

		return result;
	}
}
	
	
