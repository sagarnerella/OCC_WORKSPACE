package com.osi.agent.snmptable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.log4j.Logger;

import com.adventnet.snmp.beans.DataException;
import com.adventnet.snmp.beans.SnmpTable;
import com.adventnet.snmp.mibs.MibException;
import com.osi.agent.common.CommonUtilities;
import com.osi.agent.common.KeyValue;
import com.osi.agent.exception.OCCAgentSnmpTableException;
import com.osi.agent.exception.OCCSnmpTableDataException;
import com.osi.agent.monitor.IMonitor;
import com.osi.agent.vo.CheckPointDetails;
import com.osi.agent.vo.CheckpointResult;

public class SnmpTableMonitor implements IMonitor{
	private static final Logger LOGGER = Logger.getLogger(SnmpTableMonitor.class);

	@Override
	public CheckpointResult performCheck(CheckPointDetails checkPointDetails)
			throws OCCAgentSnmpTableException {

		CheckpointResult checkpointResult = null;
		try {
			if(checkPointDetails.isValid(checkPointDetails)){	
				boolean checkPointDetailFlag = CommonUtilities.isNotNull(checkPointDetails);
				if(checkPointDetailFlag){
					checkpointResult=getResult(checkPointDetails);
				}
			}
		}
		catch (OCCSnmpTableDataException e) {
			LOGGER.error("",e);
			throw new OCCAgentSnmpTableException("", e.getUserMessage(), e.getMessage());
		}
		catch (Exception e) {
			LOGGER.error("",e);
			throw new OCCAgentSnmpTableException("", "Exception while getting the result from SnmpTable in SNMPTABLE performCheck", e.getMessage());
		} 
		return checkpointResult;
	}
		
	public CheckpointResult getResult(CheckPointDetails resCheckPointDetails) throws OCCSnmpTableDataException{
		
		synchronized(SnmpTableMonitor.class){
		String resultMsg="";
		CheckpointResult checkpointResult = new CheckpointResult();
		String checkResult="";
		boolean resultFlag = true;
		List<String> columnHeadings=new ArrayList<String>();
		List<SnmpTableRow> data=new ArrayList<SnmpTableRow>();

		String remoteHost = resCheckPointDetails.getSnmpTableIpAddress().trim(); 
		String tableOid =resCheckPointDetails.getCheckPoint().trim();
		String community = resCheckPointDetails.getCommunityString().trim();
		String mib=resCheckPointDetails.getSnmpTableMibName().trim();

		SnmpTable snmpTable = new SnmpTable();
		snmpTable.setTargetHost(remoteHost);
		snmpTable.setCommunity(community);

		try{
			snmpTable.loadMibs(mib);
			snmpTable.setTableOID(tableOid);

			try { Thread.sleep(5000); }  // allow some time to get all rows
			catch (InterruptedException ex) {}  

			//START BELOW LINES ARE TO BE REMOVED - used for printing
			StringBuffer columns = new StringBuffer(); 

			for (int i=0;i<snmpTable.getColumnCount();i++){  // print column names
				columns.append(snmpTable.getColumnName(i)+" \t");
				columnHeadings.add(snmpTable.getColumnName(i).toString());
			}
			//END BELOW LINES ARE TO BE REMOVED

			SnmpTableRow firstRow=new SnmpTableRow();
			firstRow.setValue(columnHeadings);
			data.add(firstRow);

			for (int j=0;j<snmpTable.getRowCount();j++) {
				List<String> rowdata=new ArrayList<String>();
				SnmpTableRow snmpTableRow = new SnmpTableRow();
				for (int i=0;i<snmpTable.getColumnCount();i++){
					rowdata.add(snmpTable.getValueAt(j,i)!=null?snmpTable.getValueAt(j,i).toString().trim():"");
				}
				snmpTableRow.setValue(rowdata);
				data.add(snmpTableRow);
			}

			 List<String> selectedColumns=null;
			 String snmpTableColumns = resCheckPointDetails.getSnmpTableColumnNames().trim();
			 if(null!=snmpTableColumns)
				 selectedColumns=new ArrayList<String>(Arrays.asList(snmpTableColumns.split(",")));

			 if(!selectedColumns.contains(KeyValue.HRSTORAGE_ALLOCATION_UNITS))
				 selectedColumns.add(KeyValue.HRSTORAGE_ALLOCATION_UNITS);
			 
			 if(!selectedColumns.contains(KeyValue.HRSTORAGE_USED))
				 selectedColumns.add(KeyValue.HRSTORAGE_USED);

			 List<String> colHeadings = new ArrayList<String>();
			 for(int i=0 ; i<data.get(0).getValue().size();i++){
				 colHeadings.add(data.get(0).getValue().get(i));
			 }
			 
			 String kvPair="";
			 Map<String,String> keyValuePair=new HashMap<String,String>();
			 int snmpTableRowId = resCheckPointDetails.getSnmpTableRowId();
			 
			 for(int k=0 ; k<data.get(0).getValue().size();k++){
				 if(selectedColumns.contains(colHeadings.get(k))){
					 keyValuePair.put(colHeadings.get(k), data.get(snmpTableRowId).getValue().get(k));
				 }
				 if(columnHeadings.contains(colHeadings.get(k))){
					 kvPair=kvPair+colHeadings.get(k)+"="+data.get(snmpTableRowId).getValue().get(k)+"<br>";
				 }
			 }

			 int loopCount=0;
			 String unitOfMeasure = resCheckPointDetails.getUnitOfMeasure().trim();
			 String MesureMentArray[]=KeyValue.HRSTORAGE_MESUREMENT.split(",");
			 for(int i=0;i<MesureMentArray.length;i++){
				 if(MesureMentArray[i].equalsIgnoreCase(unitOfMeasure)){
					 loopCount=i+1;
					 break;
				 }
			 }
			 String hrStorageAllocationUnits=KeyValue.HRSTORAGE_ALLOCATION_UNITS;

			 String hrStorageSize="";
			 String hrStorageUsed=""; 
			 if(unitOfMeasure!=null && (unitOfMeasure.equalsIgnoreCase(KeyValue.HRSTORAGE_MESURE_PERCENT) || unitOfMeasure.equals("%") || unitOfMeasure.equalsIgnoreCase(KeyValue.MESUREMENT_BYTE))){
				 hrStorageSize=KeyValue.HRSTORAGE_SIZE;
				 hrStorageUsed=KeyValue.HRSTORAGE_USED;
			 }
			 else{
			 hrStorageSize=convertUnitOfMeasure(KeyValue.HRSTORAGE_ALLOCATION_UNITS,KeyValue.HRSTORAGE_SIZE,loopCount);
			 hrStorageUsed=convertUnitOfMeasure(KeyValue.HRSTORAGE_ALLOCATION_UNITS,KeyValue.HRSTORAGE_USED,loopCount);
			 }
			 
			 String resultDescription="";
			 Iterator<Entry<String, String>> it = keyValuePair.entrySet().iterator();
			 while (it.hasNext()) {
				 Map.Entry<String, String> pair = (Entry<String, String>)it.next();
				 if(pair.getKey().toString().equals(KeyValue.HRSTORAGE_DESCR))
					 resultDescription= pair.getValue().toString();
				 hrStorageSize=hrStorageSize.replaceAll(pair.getKey().toString(), pair.getValue().toString());
				 hrStorageUsed=hrStorageUsed.replaceAll(pair.getKey().toString(), pair.getValue().toString());
				 hrStorageAllocationUnits=hrStorageAllocationUnits.replace(pair.getKey().toString(), pair.getValue().toString());
				 it.remove();
			 }
			 
			 ScriptEngine engine = null;
			 ScriptEngineManager mgr = new ScriptEngineManager();
			 engine = mgr.getEngineByName("JavaScript");
			 
			 if(unitOfMeasure!=null && (!unitOfMeasure.equals(KeyValue.HRSTORAGE_MESURE_PERCENT) && !unitOfMeasure.equals("%") || !unitOfMeasure.equalsIgnoreCase(KeyValue.MESUREMENT_BYTE))){
				 hrStorageSize = engine.eval(hrStorageSize).toString();
				 hrStorageUsed = engine.eval(hrStorageUsed).toString();
			 }
			 String rowAlgorithm=resCheckPointDetails.getSnmpTableAlgorithm().trim();

			 Map<String,String> l_map=new HashMap<String,String>();
			 l_map.put(KeyValue.HRSTORAGE_SIZE,hrStorageSize);
			 l_map.put(KeyValue.HRSTORAGE_USED,hrStorageUsed);
			 l_map.put(KeyValue.HRSTORAGE_ALLOCATION_UNITS,hrStorageAllocationUnits);
			 Iterator<String> l_itr=l_map.keySet().iterator();
			 while(l_itr.hasNext()){
				 String key=l_itr.next();
				 rowAlgorithm=rowAlgorithm.replaceAll(key,l_map.get(key));
			 }
			 LOGGER.info("After Converting "+KeyValue.HRSTORAGE_SIZE+" = "+hrStorageSize);
			 LOGGER.info("After Converting "+KeyValue.HRSTORAGE_USED+" = "+hrStorageUsed);
			 LOGGER.info("After applying values rowAlgorithm = "+rowAlgorithm);
			 checkResult=engine.eval(rowAlgorithm).toString();
			 resultFlag = CommonUtilities.isNotNullAndEmpty(checkResult);
			 if(resultFlag){
				 //resultDescription="<br>Device Name: "+resCheckPointDetails.getSnmpTableHost()+"<br>IpAddress: "+resCheckPointDetails.getSnmpTableIpAddress()+"<br>Table OID: "+resCheckPointDetails.getCheckPoint()+"<br>checkResult: "+checkResult+"<br>unitOfMeasure: "+unitOfMeasure+"<br>Raw Data: "+kvPair;
				 resultDescription="<br>unitOfMeasure: "+unitOfMeasure+"<br>IpAddress: "+resCheckPointDetails.getSnmpTableIpAddress()+"<br>Table OID: "+resCheckPointDetails.getCheckPoint()+"<br> <br>Raw Data:<br>"+kvPair;
				 checkpointResult.setResultDesc(resultDescription);
				 double chkDoubleVal = Double.parseDouble(checkResult);
				 checkResult = String.valueOf(Math.round(chkDoubleVal*100)/100.00);
				 checkpointResult.setCheckPointresult(checkResult);
				 checkpointResult.setCheckPointDetails(resCheckPointDetails);
			 }
			 LOGGER.info(" checkpoint id  "+resCheckPointDetails.getCheckpointID()+" Algorithm "+resCheckPointDetails.getSnmpTableAlgorithm()+" snmpTableRowId "+snmpTableRowId+" unitOfMeasure "+unitOfMeasure+" Result "+checkResult+" for "+resultDescription);
		}
		catch (MibException e) {
			LOGGER.error("MibException while parsing MIB in SNMPTABLE performCheck",e);
			resultFlag = false;
			//throw new OCCSnmpTableDataException("", "MibException while parsing MIB in SNMPTABLE performCheck", e.getMessage());
		}
		catch (FileNotFoundException  e) {
			LOGGER.error("FileNotFoundException while reading MIB file in SNMPTABLE performCheck",e);
			resultFlag = false;
			resultMsg = "Incorrect Data. Please check MIB";
			//throw new OCCSnmpTableDataException("", "FileNotFoundException while reading MIB file in SNMPTABLE performCheck", e.getMessage());
		}
		catch (IOException e) {
			LOGGER.error("IOException while performing IO in SNMPTABLE performCheck",e);
			resultFlag = false;
			//throw new OCCSnmpTableDataException("", "IOException while performing IO in SNMPTABLE performCheck", e.getMessage());
		}catch (DataException e) {
			LOGGER.error("DataException while reading data from MIB in SNMPTABLE performCheck",e);
			resultFlag = false;
			resultMsg = "Incorrect Data. Please check OID";
			//throw new OCCSnmpTableDataException("", "DataException while reading data from MIB in SNMPTABLE performCheck", e.getMessage());
		}
		catch (IndexOutOfBoundsException e) {
			LOGGER.error("IOBException while getting the result from SnmpTable in SNMPTABLE performCheck",e);
			resultFlag = false;
			resultMsg = "Incorrect Data. Please check Host IP Address and Community String.";
			//throw new OCCSnmpTableDataException("", "IOBException while getting the result from SnmpTable in SNMPTABLE performCheck", e.getMessage());
		}
		catch (ScriptException e) {
			LOGGER.error("ScriptException while executing Script in the SNMPTABLE performCheck",e);
			resultFlag = false;
			//throw new OCCSnmpTableDataException("", "ScriptException while executing Script in the SNMPTABLE performCheck", e.getMessage());
		}
		catch (Exception e) {
			LOGGER.error("Exception while getting the result from SnmpTable in  SNMPTABLE performCheck",e);
			resultFlag = false;
			//throw new OCCSnmpTableDataException("", "Exception while getting the result from SnmpTable in  SNMPTABLE performCheck", e.getMessage());
		}finally {
			if(!resultFlag){
				checkpointResult.setCheckPointresult(resultMsg);
				resCheckPointDetails.setCheckType('E');
				checkpointResult.setCheckPointDetails(resCheckPointDetails);
			}
		}	
		return checkpointResult;
		}
	}


	public String convertUnitOfMeasure(String hrStorageAllocationUnits,String hrStorageSizeOrUsed,int loopCount){
		String createUnitOfMsr="";
		String OneKbToByte="/1024";
		String openBr="(";
		String closeBr=")";
		for(int i=1;i<=loopCount;i++){
			if(i==1)
				createUnitOfMsr=openBr+hrStorageAllocationUnits+"*"+hrStorageSizeOrUsed+OneKbToByte+closeBr;
			else
				createUnitOfMsr=openBr+createUnitOfMsr+OneKbToByte+closeBr;
		}
		return createUnitOfMsr;
	}
}
