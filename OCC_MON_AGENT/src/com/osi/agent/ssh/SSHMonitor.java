package com.osi.agent.ssh;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.osi.agent.common.Base64;
import com.osi.agent.common.CommonUtilities;
import com.osi.agent.exception.OCCAgentSSHException;
import com.osi.agent.monitor.IMonitor;
import com.osi.agent.vo.CheckPointDetails;
import com.osi.agent.vo.CheckpointResult;

public class SSHMonitor implements IMonitor  {

	private static final Logger LOGGER = Logger.getLogger(SSHMonitor.class);
	private Session     session     = null;
	private Channel     channel     = null;
	private ChannelSftp channelSftp = null;
	private String sshFilePath = null;
	String resultXML = null;
	String result= null;
	String resultDesc="";
	public CheckpointResult performCheck(CheckPointDetails checkPointDetails) throws OCCAgentSSHException {
		
		CheckpointResult checkpointResult = null;
		boolean resultFlag = false;
		try {
			if(checkPointDetails.isValid(checkPointDetails)){	
				boolean checkPointDetailFlag = CommonUtilities.isNotNull(checkPointDetails);
				if(checkPointDetailFlag){
					result = getSshMonitoringResult(checkPointDetails);
				resultFlag = CommonUtilities.isNotNullAndEmpty(result);
				resultDesc=result;
			}
			}
		} catch (Exception e) {
			//result = "<RESULT><ISINCIDENT>false</ISINCIDENT><MSG>"+Base64.encode("<ROWSET><ROW><Description>"+e+"</Description></ROW></ROWSET>")+"</MSG><FORMATTEDVALUE>1</FORMATTEDVALUE></RESULT>";
		}finally {
			/*if(resultFlag){
				checkpointResult = new CheckpointResult();
				checkpointResult.setConnectType(CommonUtilities.getProperty("SSHWRAPPER_CONNECT_TYPE"));
				checkpointResult.setCheckPointresult(result);
				checkpointResult.setCheckPointDetails(checkPointDetails);
			} else {
				checkpointResult=new CheckpointResult();
				checkpointResult.setConnectType(CommonUtilities.getProperty("SSHWRAPPER_CONNECT_TYPE"));
				checkpointResult.setCheckPointresult("Device Not Connected");
				checkpointResult.setCheckPointDetails(checkPointDetails);
			}*/
			checkpointResult = new CheckpointResult();
			checkpointResult.setConnectType(CommonUtilities.getProperty("SSHWRAPPER_CONNECT_TYPE"));
			checkpointResult.setCheckPointresult(result);
			checkpointResult.setCheckPointDetails(checkPointDetails);
			checkpointResult.setResultDesc(resultDesc);
		}
		return checkpointResult;
	}
	private void connectSshSessionAndChanel(CheckPointDetails checkPointDetails) throws OCCAgentSSHException{
		 try {
			JSch jsch = new JSch();
			 session = jsch.getSession(checkPointDetails.getSshUserName(),checkPointDetails.getSshIpAddress(),checkPointDetails.getSshPort());
			 session.setPassword(checkPointDetails.getSshPassword());
			 java.util.Properties config = new java.util.Properties();
			 config.put("StrictHostKeyChecking", "no");
			 session.setConfig(config);
			 session.connect();
			 channel = session.openChannel("sftp");
			 channel.connect();
			 LOGGER.info("SFTP session connected");
			// createSshFile(checkPointDetails);
		} catch (JSchException e) {
			String exceptionMessage=e.getMessage();
			if(exceptionMessage.contains("UnknownHostException")){
			result = "<RESULT><ISINCIDENT>true</ISINCIDENT><MSG>"+Base64.encode("<ROWSET><ROW><Description>"+e+"</Description></ROW></ROWSET>")+"</MSG><FORMATTEDVALUE>-1</FORMATTEDVALUE></RESULT>";
			resultDesc="Please check the Ip address is valid or not in checkpoint configuration ";
			}
			if(exceptionMessage.contains("ConnectException")){
				result = "<RESULT><ISINCIDENT>true</ISINCIDENT><MSG>"+Base64.encode("<ROWSET><ROW><Description>"+e+"</Description></ROW></ROWSET>")+"</MSG><FORMATTEDVALUE>-1</FORMATTEDVALUE></RESULT>";
				resultDesc="Please check for valid Ip Address / Port in Device configuration";
				}
			if(exceptionMessage.contains("Auth fail")){
				result = "<RESULT><ISINCIDENT>true</ISINCIDENT><MSG>"+Base64.encode("<ROWSET><ROW><Description>"+e+"</Description></ROW></ROWSET>")+"</MSG><FORMATTEDVALUE>-1</FORMATTEDVALUE></RESULT>";
				resultDesc="Please check for valid Username / Password in Device configuration";
				}else{
					result = "<RESULT><ISINCIDENT>true</ISINCIDENT><MSG>"+Base64.encode("<ROWSET><ROW><Description>"+e+"</Description></ROW></ROWSET>")+"</MSG><FORMATTEDVALUE>-1</FORMATTEDVALUE></RESULT>";
					resultDesc="Please check for valid Ip Adress,Port, Username and Password in Device configuration";
					
				}
			LOGGER.error("",e);
			disconnectSshChanelAndSession();
			throw new OCCAgentSSHException("", "Error occurred while connecting to the requested server", e.getMessage());
		}finally{
			
		}
	}
	
	private void createSshFile(CheckPointDetails checkPointDetails) throws OCCAgentSSHException{
		String sshFileName = null;
		String workingDir = null;
		 try {
			 sshFileName = checkPointDetails.getCheckpointID()+".sh";
			channelSftp = (ChannelSftp)channel;
			 workingDir = channelSftp.pwd();
			 sshFilePath = workingDir+"/"+sshFileName;
			 channelSftp.cd(workingDir);
			 InputStream obj_InputStream = new ByteArrayInputStream(Base64.decode(checkPointDetails.getCheckPoint()).getBytes());
			 channelSftp.put(obj_InputStream, sshFileName);
			 int chmodInt = Integer.parseInt("777", 8);
			 channelSftp.chmod(1, sshFilePath);
			 LOGGER.info(sshFileName+":: file created.");
		} catch (NumberFormatException e) {
			LOGGER.error("",e);
			disconnectSshChanelAndSession();
			throw new OCCAgentSSHException("", "Unable to execute the requested script/ssh file", e.getMessage());
		} catch (SftpException e) {
			LOGGER.error("",e);
			disconnectSshChanelAndSession();
			throw new OCCAgentSSHException("", "Unable to execute the requested script/ssh file", e.getMessage());
		}finally{
			
		}
	}
	private String executeCreatedSshFile(String sshFileName) throws OCCAgentSSHException{
		String cmdForExeSSH = null;
		String result = "";
		 try {
			 cmdForExeSSH = "./";
			 channel=session.openChannel("exec");
			 ((ChannelExec)channel).setCommand(cmdForExeSSH+sshFileName);
			 channel.setInputStream(null);
			 ((ChannelExec)channel).setErrStream(System.err);
			 InputStream in=channel.getInputStream();
			 channel.connect();
			 byte[] tmp=new byte[1024];
			 while(true){
			   while(in.available()>0){
			     int i=in.read(tmp, 0, 1024);
			     if(i<0)break;
			     result=result+new String(tmp, 0, i);
			   }
			   if(channel.isClosed()){
			     break;
			   }
			   try{Thread.sleep(1000);}catch(Exception ee){}
			 }
			 LOGGER.info(sshFileName+":: file executed.");
			 LOGGER.info("SSH Monitoring result ::"+result);
		} catch (JSchException e) {
			LOGGER.error("",e);
			disconnectSshChanelAndSession();
			throw new OCCAgentSSHException("", "Unable to execute the requested script/ssh file", e.getMessage());
		} catch (IOException e) {
			LOGGER.error("",e);
			disconnectSshChanelAndSession();
			throw new OCCAgentSSHException("", "Unable to execute the requested script/ssh file", e.getMessage());
		}finally{
			
		}
		return result;
	}
	private void deleteCreatedSshFile() throws OCCAgentSSHException{
		try {
			channelSftp.rm(sshFilePath);
			LOGGER.info("SH file deleted");
		} catch (SftpException e) {
			LOGGER.error("",e);
			throw new OCCAgentSSHException("", "Unable to delete the created ssh file", e.getMessage());
		}finally{
			disconnectSshChanelAndSession();
		}
	}
	private String getSshMonitoringResult(CheckPointDetails checkPointDetails) throws OCCAgentSSHException{
		String monitoringResults = null;
		try {
			connectSshSessionAndChanel(checkPointDetails);
			createSshFile(checkPointDetails);
			monitoringResults = executeCreatedSshFile(checkPointDetails.getCheckpointID()+".sh");
			deleteCreatedSshFile();
		} catch (Exception e) {
			LOGGER.error("",e);
			throw new OCCAgentSSHException("", "Unable to execute the requested script/ssh file", e.getMessage());
		}finally{
			disconnectSshChanelAndSession();
		}
		return monitoringResults;
	}
	
	private void disconnectSshChanelAndSession(){
		if(channel!=null && channel.isClosed()) {
			channel.disconnect();
			LOGGER.info("SFTP Channel disconnected");
		}
		if(session!=null && session.isConnected()){
			session.disconnect();
			LOGGER.info("SFTP session disconnected");
		}
	}
}
