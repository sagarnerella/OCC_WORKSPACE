package com.osi.agent.jdbc;

import java.io.StringWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import oracle.jdbc.OracleTypes;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.osi.agent.common.Base64;
import com.osi.agent.common.CommonUtilities;
import com.osi.agent.common.OCCRegularExpressions;
import com.osi.agent.exception.OCCAgentJDBCException;
import com.osi.agent.monitor.IMonitor;
import com.osi.agent.vo.CheckPointDetails;
import com.osi.agent.vo.CheckpointResult;

public class JDBCMonitor implements IMonitor {
	private static final Logger LOGGER = Logger.getLogger(JDBCMonitor.class);
	private String resultDescription="";
	private String result= null;
	@Override
	public CheckpointResult performCheck(CheckPointDetails checkPointDetails)throws  OCCAgentJDBCException {
		LOGGER.info("CheckID in JDBC :: "+checkPointDetails.getCheckpointID());
		CheckpointResult checkpointResult = null;
		boolean resultFlag = false;
		Connection conn = null;
		try{
			conn = getConnection(checkPointDetails);
			if (null != conn) {
				if(null != checkPointDetails.getExecutionType()){
					if(checkPointDetails.getExecutionType().equalsIgnoreCase("sql"))
						result = getResponseStringForSQL(conn, checkPointDetails);
					else
						result = getResponseStringForPLSQL(conn, checkPointDetails);
				}
				resultFlag = CommonUtilities.isNotNullAndEmpty(result);
				
				
				if(resultFlag){
					resultDescription=result;
					checkpointResult = new CheckpointResult();
					checkpointResult.setCheckPointresult(result);
					checkpointResult.setConnectType("JDBC");
					checkpointResult.setResultDesc(resultDescription);
					checkpointResult.setCheckPointDetails(checkPointDetails);
				} else {
					result = "<RESULT><ISINCIDENT>true</ISINCIDENT><MSG>"+Base64.encode("<ROWSET><ROW><Decription>Result not found, please check the query provided while configuration of JDBC check</Decription></ROW></ROWSET>")+"</MSG><FORMATTEDVALUE>-1</FORMATTEDVALUE></RESULT>";
					resultDescription=result;
					checkpointResult = new CheckpointResult();
					checkpointResult.setCheckPointresult(result);
					checkpointResult.setConnectType("JDBC");
					checkpointResult.setCheckPointDetails(checkPointDetails);
					resultFlag = true;
				}
		    }
		} catch(Exception e){
			throw new OCCAgentJDBCException("", "Error while getting the result", e.getMessage());
		} finally{
			if(!resultFlag){
				//result = "<RESULT><ISINCIDENT>true</ISINCIDENT><MSG>"+Base64.encode("<ROWSET><ROW><Decription>Failed to connect Database please verify DB connectivity, username and password</Decription></ROW></ROWSET>")+"</MSG><FORMATTEDVALUE>-1</FORMATTEDVALUE></RESULT>";
				checkpointResult = new CheckpointResult();
				checkpointResult.setCheckPointresult(result);
				checkpointResult.setConnectType("JDBC");
				checkpointResult.setCheckPointDetails(checkPointDetails);
			}
			checkpointResult.setResultDesc(resultDescription);
			try {
				if(null != conn) {
					conn.close();
				}
			} catch (SQLException e) {
				throw new OCCAgentJDBCException("", "Error while closing the connection", e.getMessage());
			}
		}
		return checkpointResult;
	}
	
	public Connection getConnection(CheckPointDetails checkPointDetails) {
		Connection conn = null;
		String errorMessage="";
		try {
			Class.forName(checkPointDetails.getDriverClass());
			conn = DriverManager.getConnection(checkPointDetails.getDbURL(), checkPointDetails.getUserName(), checkPointDetails.getPassword());
		}catch(ClassNotFoundException e){
			result=" java.lang.ClassNotFoundException ";
			resultDescription="There is no Driver with provided name,Please check the Driver Class name valid or not.";
			LOGGER.error("",e);
		}catch(SQLException e){
			errorMessage=e.getMessage();
			if(errorMessage.contains("Communications link failure")){
				result="<RESULT><ISINCIDENT>true</ISINCIDENT><MSG>"+Base64.encode("<ROWSET><ROW><Decription>Communications link failure, Please check the given url, ip address and port are valid or not</Decription></ROW></ROWSET>")+"</MSG><FORMATTEDVALUE>-1</FORMATTEDVALUE></RESULT>";
				resultDescription="Communications link failure, Please check the given url, ip address and port are valid or not . ";
			}
			if(errorMessage.contains("java.sql.SQLException")){
				result="java.sql.SQLException";
				result="<RESULT><ISINCIDENT>true</ISINCIDENT><MSG>"+Base64.encode("<ROWSET><ROW><Decription>java.sql.SQLException, Failed to connect Database , Please check the given username and password are valid or not</Decription></ROW></ROWSET>")+"</MSG><FORMATTEDVALUE>-1</FORMATTEDVALUE></RESULT>";
				resultDescription="Unable to connect to database , Please check the given username and password are valid or not. ";
			}
			LOGGER.error("",e);
		} catch (Exception e) {
			result="<RESULT><ISINCIDENT>true</ISINCIDENT><MSG>"+Base64.encode("<ROWSET><ROW><Decription>"+e.getMessage()+", Failed to connect Database , Please check the given url, username and password are valid or not.</Decription></ROW></ROWSET>")+"</MSG><FORMATTEDVALUE>-1</FORMATTEDVALUE></RESULT>";
			resultDescription="Failed to connect Database , Please check the given url, username and password are valid or not. ";
			LOGGER.error("",e);
		}
		return conn; 
	}
	
	public String getResponseStringForSQL(Connection conn, CheckPointDetails checkPointDetails) {
		PreparedStatement preparedStatement = null;
		String result = null;
		Document doc = null;
		try {
			preparedStatement = conn.prepareStatement(Base64.decode(checkPointDetails.getCheckPoint()));
			ResultSet rs = preparedStatement.executeQuery();
			ResultSetMetaData rsmd=rs.getMetaData();
			double formatedValue = 0.0;
			boolean incidentFlag = false;
			if (rsmd.getColumnCount() == 1) {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				doc = builder.newDocument();
				Element resultElement = doc.createElement("RESULT");
				doc.appendChild(resultElement);
				Element msgHeader=doc.createElement("MSG");
				resultElement.appendChild(msgHeader);
				
				if (rs.next()) {
					try {
						formatedValue = Double.parseDouble(rs.getString(1));
						incidentFlag = OCCRegularExpressions.verifyThreshold(formatedValue, checkPointDetails.getAlternateThreshold());
						if (checkPointDetails.getSupressIncident().equalsIgnoreCase("Yes")) {
							incidentFlag = false;
						}
					} catch (Exception e) {
						incidentFlag = true;
						LOGGER.error("Non Numeric JDBC Reslt while executing check with checkpoint ID "+checkPointDetails.getCheckpointID()+" : "+e.getMessage());
					}
					
					Element rowset=doc.createElement("ROWSET");
					msgHeader.appendChild(rowset);
					
					Element row = doc.createElement("ROW");
					rowset.appendChild(row);
					Element node = doc.createElement(rsmd.getColumnName(1));
					node.appendChild(doc.createTextNode(rs.getString(1)));
					row.appendChild(node);
				}
				
				
				Element incidentHeaders=doc.createElement("ISINCIDENT");
				incidentHeaders.appendChild(doc.createTextNode(""+incidentFlag));
				resultElement.appendChild(incidentHeaders);
				   
				Element formatedHeader=doc.createElement("FORMATTEDVALUE");
				formatedHeader.appendChild(doc.createTextNode(""+formatedValue));
				resultElement.appendChild(formatedHeader);
				    
			} else {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			    DocumentBuilder builder = factory.newDocumentBuilder();
			    doc = builder.newDocument();
			    Element resultElement = doc.createElement("RESULT");
			    doc.appendChild(resultElement);
			    
			    Element msgHeader=doc.createElement("MSG");
			    resultElement.appendChild(msgHeader);
			    
			    
				boolean rowsetFlag = true;
				Element rowset = null;
			    while (rs.next()) {
			    	
			    	if(rowsetFlag) {
			    		incidentFlag = true;
			    		rowset=doc.createElement("ROWSET");
					    msgHeader.appendChild(rowset);
					    rowsetFlag = false;
			    	}
			    	
					Element row = doc.createElement("ROW");
					rowset.appendChild(row);
				    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				        Element node = doc.createElement(rsmd.getColumnName(i));
				        node.appendChild(doc.createTextNode(rs.getString(i)));
				        row.appendChild(node);
				        if("VARIABLE_VALUE".equals(rsmd.getColumnName(i))){
				        formatedValue=Double.parseDouble(rs.getString(i));
						incidentFlag = OCCRegularExpressions.verifyThreshold(formatedValue, checkPointDetails.getAlternateThreshold());
						if (checkPointDetails.getSupressIncident().equalsIgnoreCase("Yes")) {
							incidentFlag = false;
						}
				        }
				    }
				}
			    
			    if (checkPointDetails.getSupressIncident().equalsIgnoreCase("Yes")) {
					incidentFlag = false;
			    }
			    
			    Element incidentHeaders=doc.createElement("ISINCIDENT");
			    incidentHeaders.appendChild(doc.createTextNode(""+incidentFlag));
			    resultElement.appendChild(incidentHeaders);
			    
			    Element formatedHeader=doc.createElement("FORMATTEDVALUE");
			    formatedHeader.appendChild(doc.createTextNode(""+formatedValue));
			    resultElement.appendChild(formatedHeader);
			    
			}
			DOMSource domSource = new DOMSource(doc);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
			StringWriter sw = new StringWriter();
			StreamResult sr = new StreamResult(sw);
			transformer.transform(domSource, sr);
			result = sw.toString();
			if (result.indexOf("<ROWSET>") != -1) {
				String resultMsg = result.substring(result.indexOf("<ROWSET>"), result.lastIndexOf("</ROWSET>")+9);
				result = result.replace(resultMsg, Base64.encode(resultMsg));
			}
		} catch (SQLException e) {
			String errorMessage=e.getMessage();
			if(errorMessage.contains("Query was empty")){
				result = "<RESULT><ISINCIDENT>true</ISINCIDENT><MSG>"+Base64.encode("<ROWSET><ROW><Decription>Unable to execute query , Exception is ::"+e+"</Decription></ROW></ROWSET>")+"</MSG><FORMATTEDVALUE>-1</FORMATTEDVALUE></RESULT>";
				resultDescription="Query is empty , Please provide the valid query.";
			}else{
				result = "<RESULT><ISINCIDENT>true</ISINCIDENT><MSG>"+Base64.encode("<ROWSET><ROW><Decription>Unable to execute query , Exception is ::"+e+"</Decription></ROW></ROWSET>")+"</MSG><FORMATTEDVALUE>-1</FORMATTEDVALUE></RESULT>";
				resultDescription="You have an error in your SQL query, Please check the query properly.";
			}
			LOGGER.error("Exception occured while executing check with checkpoint ID "+checkPointDetails.getCheckpointID(),e);
			
		} catch (Exception e) {
			resultDescription="You have an error in your SQL query, Please check the query properly.";
			LOGGER.error("while executing check with checkpoint ID "+checkPointDetails.getCheckpointID(),e);
			result = "<RESULT><ISINCIDENT>true</ISINCIDENT><MSG>"+Base64.encode("<ROWSET><ROW><Decription>Error Occured while executing JDBC Query , Exception is ::"+e+"</Decription></ROW></ROWSET>")+"</MSG><FORMATTEDVALUE>-1</FORMATTEDVALUE></RESULT>";
		}
		return result;
	}
	
	public String getResponseStringForPLSQL(Connection conn, CheckPointDetails checkPointDetails) {
		CallableStatement callableStatement = null;
		String result = null;
		try {
			callableStatement = conn.prepareCall(Base64.decode(checkPointDetails.getCheckPoint()));
			if(checkPointDetails.getExecutionType().equalsIgnoreCase("plsql"))
				callableStatement.registerOutParameter(1, OracleTypes.CURSOR);
			else 
				callableStatement.registerOutParameter(1, Types.VARCHAR);
			callableStatement.execute();
			if(callableStatement.getObject(1).toString().contains("oracle.jdbc.driver.OracleResultSetImpl")) {
				ResultSet cursorResultSet = (ResultSet) callableStatement.getObject(1);
				if(cursorResultSet.next ()) {
					result = (String) cursorResultSet.getString(1);
				}
			} else {
				result = (String) callableStatement.getObject(1);
			}
				
		} catch (SQLException e) {
			LOGGER.error("",e);
			resultDescription=e.getMessage();
			result = "<RESULT><ISINCIDENT>true</ISINCIDENT><MSG>"+Base64.encode("<ROWSET><ROW><Decription>Unable to execute PLSQL Block , Exception is ::"+e+"</Decription></ROW></ROWSET>")+"</MSG><FORMATTEDVALUE>-1</FORMATTEDVALUE></RESULT>";
		} catch (Exception e) {
			resultDescription=e.getMessage();
			LOGGER.error("",e);
			result = "<RESULT><ISINCIDENT>true</ISINCIDENT><MSG>"+Base64.encode("<ROWSET><ROW><Decription>Error Occured while executing JDBC Query , Exception is ::"+e+"</Decription></ROW></ROWSET>")+"</MSG><FORMATTEDVALUE>-1</FORMATTEDVALUE></RESULT>";
		}
		return result;
	}
	public static void main(String[] args) throws Exception {
		//System.out.println(Base64.decode("PFJPV1NFVD48Uk9XPjxyX2NvdW50Pjk8L3JfY291bnQ+PC9ST1c+PC9ST1dTRVQ+"));
		//System.out.println(Base64.encode("SELECT * FROM ms_agent_errors"));
		//DriverManager.registerDriver (new oracle.jdbc.OracleDriver());
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection con = DriverManager.getConnection("jdbc:oracle:thin:@192.168.174.170:1521:PROD1", "osidemo", "osidemo");
		if (null != con) {
			System.out.println("Connection Established successfully...");
		} else {
			System.out.println("Unable to connect to the database");
		}
		//Class.forName("oracle.jdbc.driver.OracleDriver");
		/*DriverManager.registerDriver (new oracle.jdbc.OracleDriver());
		Connection con = DriverManager.getConnection("jdbc:oracle:thin:@clesdevebsdb.advanstar.com:1546:finenk", "apps", "appsenk");
		if (null != con) {
			System.out.println("Connection Established successfully...");
		} else {
			System.out.println("Unable to connect to the database");
		}*/
		
		/*Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://192.168.174.151:3306/occ_v2", "root", "occp@ss");
		if (null != con) {
			System.out.println("Connection Established successfully...");
		} else {
			System.out.println("Unable to connect to the database");
		}*/
	}
}
