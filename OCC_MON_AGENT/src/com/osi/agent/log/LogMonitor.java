package com.osi.agent.log;

import com.osi.agent.common.Base64;
import com.osi.agent.common.CommonUtilities;
import com.osi.agent.common.KeyValue;
import com.osi.agent.exception.OCCAgentLogException;
import com.osi.agent.monitor.IMonitor;
import com.osi.agent.snmp.SNMPMonitor;
import com.osi.agent.vo.CheckPointDetails;
import com.osi.agent.vo.CheckpointResult;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.grep4j.core.Grep4j;
import org.grep4j.core.fluent.Dictionary;
import org.grep4j.core.model.Profile;
import org.grep4j.core.model.ProfileBuilder;
import org.grep4j.core.options.Option;
import org.grep4j.core.request.GrepExpression;
import org.grep4j.core.result.GrepResult;
import org.grep4j.core.result.GrepResults;

public class LogMonitor
implements IMonitor {
    private static final Logger LOGGER = Logger.getLogger((Class)SNMPMonitor.class);
      String resultXML = "";
      String resultDesc="";
      String errorMsg="";
    public CheckpointResult performCheck(CheckPointDetails checkPointDetails) throws OCCAgentLogException {
        CheckpointResult checkpointResult;
  
            checkpointResult = null;
            String lastRunTime = null;
            GrepResults results = null;
            StringBuffer fresult = new StringBuffer();
           
            String lrLineNum = "";
            try {
                try {
                    Profile logProfile = this.getBaseRemoteProfile(checkPointDetails);
                    String includeParams = this.getIncludeParams(checkPointDetails.getIncludeParams());
                    String excludeParams = this.getExcludeParams(checkPointDetails.getExcludeParams());
                    int i = 1;
                    while (i <= Integer.parseInt(KeyValue.No_OF_LOOPS)) {
                        lastRunTime = this.readLastRunTime(checkPointDetails.getCheckpointID());
                        if (lastRunTime == null || lastRunTime.isEmpty()) {
                            lastRunTime = "";
                            GrepResults getFirstLineData = Grep4j.grep((GrepExpression)new GrepExpression(".*\\b(.)\\b.*", true), (Profile)((Profile)Dictionary.on((Object)logProfile)), (Option[])new Option[]{Option.onlyFirstLines((int)1)});
                            String[] patrenResult = getFirstLineData.toString().split("\\r?\\n");
                            Pattern pattern = Pattern.compile(checkPointDetails.getFileRegExpression());
                            Matcher matcher = pattern.matcher(patrenResult[patrenResult.length - 1]);
                            if (matcher.find()) {
                                lastRunTime = matcher.groupCount() > 0 ? matcher.group(1) : matcher.group();
                            } else {
                                resultXML = "<RESULT><ISINCIDENT>true</ISINCIDENT><MSG>" + Base64.encode((String)"<ROWSET><ROW><Description>1st line of log file does not have time stamp, Please add Lastruntime  manually</Description></ROW></ROWSET>") + "</MSG><FORMATTEDVALUE>0</FORMATTEDVALUE></RESULT>";
                            }
                        }
                        if (lastRunTime.indexOf("###") != -1) {
                            String[] runTImrParams = lastRunTime.split("###");
                            lastRunTime = runTImrParams[0];
                            lrLineNum = runTImrParams[1];
                        }
                        GrepResults lineNumberRs = Grep4j.grep((GrepExpression)Grep4j.constantExpression((String)lastRunTime), (Profile)((Profile)Dictionary.on((Object)logProfile)), (Option[])new Option[]{Option.lineNumber(), Option.onlyFirstLines((int)1)});
                        String currentLineNumber = "";
                        if (lineNumberRs.toString().indexOf(":") != -1) {
                            currentLineNumber = lineNumberRs.toString().split(":")[0];
                        }
                        LOGGER.info((Object)("Previouslineid :: " + lrLineNum + " and CurrentLineNumber :: " + currentLineNumber + " : CheckID :: " + checkPointDetails.getCheckpointID()));
                        if (!lrLineNum.equalsIgnoreCase(currentLineNumber)) {
                            results = includeParams != null && excludeParams != null ? Grep4j.grep((GrepExpression)Grep4j.constantExpression((String)lastRunTime), (Profile)((Profile)Dictionary.on((Object)logProfile)), (Option[])new Option[]{Option.extraLinesAfter((int)Integer.parseInt(KeyValue.No_OF_LINES_TO_READ))}).filterBy(new GrepExpression(".*\\b(" + includeParams + ")\\b.*", true)).filterBy(new GrepExpression("^((?!" + excludeParams + ").)*$", true)) : (includeParams == null && excludeParams != null ? Grep4j.grep((GrepExpression)Grep4j.constantExpression((String)lastRunTime), (Profile)((Profile)Dictionary.on((Object)logProfile)), (Option[])new Option[]{Option.extraLinesAfter((int)Integer.parseInt(KeyValue.No_OF_LINES_TO_READ))}).filterBy(new GrepExpression("^((?!" + excludeParams + ").)*$", true)) : (includeParams != null && excludeParams == null ? Grep4j.grep((GrepExpression)Grep4j.constantExpression((String)lastRunTime), (Profile)((Profile)Dictionary.on((Object)logProfile)), (Option[])new Option[]{Option.extraLinesAfter((int)Integer.parseInt(KeyValue.No_OF_LINES_TO_READ))}).filterBy(new GrepExpression(".*\\b(" + includeParams + ")\\b.*", true)) : Grep4j.grep((GrepExpression)Grep4j.constantExpression((String)lastRunTime), (Profile)((Profile)Dictionary.on((Object)logProfile)), (Option[])new Option[]{Option.extraLinesAfter((int)Integer.parseInt(KeyValue.No_OF_LINES_TO_READ))})));
                            if (currentLineNumber.isEmpty()) {
                                currentLineNumber = lrLineNum;
                            }
                            this.writeLastFilePointRuntime(logProfile, lastRunTime, Integer.parseInt(KeyValue.No_OF_LINES_TO_READ), checkPointDetails.getFileRegExpression(), checkPointDetails.getCheckpointID(), currentLineNumber);
                            if (results != null && !results.isEmpty()) {
                                fresult.append(results.getSingleResult().getText());
                            }
                        } else {
                            resultXML = "<RESULT><ISINCIDENT>false</ISINCIDENT><MSG>" + Base64.encode((String)"<ROWSET><ROW><Description>There is no new lines appended to the log file</Description></ROW></ROWSET>") + "</MSG><FORMATTEDVALUE>0</FORMATTEDVALUE></RESULT>";
                        }
                        ++i;
                    }
                    if (fresult.length() > Integer.parseInt(KeyValue.MAX_CHARS_FOR_INCIDENT)) {
                        resultXML = "<RESULT><ISINCIDENT>true</ISINCIDENT><MSG>" + Base64.encode((String)new StringBuilder("<ROWSET><ROW><Description>").append(fresult.substring(0, Integer.parseInt(KeyValue.MAX_CHARS_FOR_INCIDENT))).append("</Description></ROW></ROWSET>").toString()) + "</MSG><FORMATTEDVALUE>0</FORMATTEDVALUE></RESULT>";
                    }
                    else if (fresult.length() > 0 && fresult.length() < Integer.parseInt(KeyValue.MAX_CHARS_FOR_INCIDENT)) {
                        resultXML = "<RESULT><ISINCIDENT>true</ISINCIDENT><MSG>" + Base64.encode((String)new StringBuilder("<ROWSET><ROW><Description>").append((Object)fresult).append("</Description></ROW></ROWSET>").toString()) + "</MSG><FORMATTEDVALUE>0</FORMATTEDVALUE></RESULT>";
                    }
                    else{ 
                    	resultXML = "<RESULT><ISINCIDENT>false</ISINCIDENT><MSG>" + Base64.encode((String)new StringBuilder("<ROWSET><ROW><Description>").append((Object)fresult).append("</Description></ROW></ROWSET>").toString()) + "</MSG><FORMATTEDVALUE>1</FORMATTEDVALUE></RESULT>";
                    }
                    resultDesc=fresult.toString();
                }
                catch (NullPointerException e) {
                	resultDesc="Please check for valid Port in Device configuration";
                	resultXML = "<RESULT><ISINCIDENT>true</ISINCIDENT><MSG>" + Base64.encode((String)new StringBuilder("<ROWSET><ROW><Description>").append(e).append("</Description></ROW></ROWSET>").toString()) + "</MSG><FORMATTEDVALUE>-1</FORMATTEDVALUE></RESULT>";
                    /*checkpointResult = new CheckpointResult();
                    checkpointResult.setCheckPointresult(resultXML);
                    checkpointResult.setConnectType(CommonUtilities.getProperty((String)"LOGMONITOR_CONNECT_TYPE"));
                    checkpointResult.setResultDesc(resultDesc);
                    checkpointResult.setCheckPointDetails(checkPointDetails);*/
                }
                catch (Exception e) {
                    LOGGER.error((Object)"", (Throwable)e);
                    errorMsg=e.getMessage();
                    if(errorMsg.equals("Error when executing the GrepTask")){
                    resultDesc="Please check for valid Ip Address,Username,Passwor and Port in Device configuration";
                    }
                    if(errorMsg.equals("No expression to grep was specified")){
                        resultDesc="Please check for valid File Name,location and Regular Expression in Device configuration";
                        }
                    resultXML = "<RESULT><ISINCIDENT>true</ISINCIDENT><MSG>" + Base64.encode((String)new StringBuilder("<ROWSET><ROW><Description>").append(e).append("</Description></ROW></ROWSET>").toString()) + "</MSG><FORMATTEDVALUE>-1</FORMATTEDVALUE></RESULT>";
                    /*checkpointResult = new CheckpointResult();
                    checkpointResult.setCheckPointresult(resultXML);
                    checkpointResult.setConnectType(CommonUtilities.getProperty((String)"LOGMONITOR_CONNECT_TYPE"));
                    checkpointResult.setResultDesc(resultDesc);
                    checkpointResult.setCheckPointDetails(checkPointDetails);*/
                }
            }
            finally {
                checkpointResult = new CheckpointResult();
                checkpointResult.setCheckPointresult(resultXML);
                checkpointResult.setConnectType(CommonUtilities.getProperty((String)"LOGMONITOR_CONNECT_TYPE"));
                checkpointResult.setCheckPointDetails(checkPointDetails);
                checkpointResult.setResultDesc(resultDesc);
            }
        
        return checkpointResult;
    }

    private String getExcludeParams(String excludeParams) {
        String finalIncludePrams = null;
        StringBuffer exclude_re = null;
        if (excludeParams != null && !"".equalsIgnoreCase(excludeParams)) {
            String[] exclude_arr;
            exclude_re = new StringBuffer("");
            String[] arrstring = exclude_arr = excludeParams.split("##");
            int n = arrstring.length;
            int n2 = 0;
            while (n2 < n) {
                String exlude = arrstring[n2];
                exclude_re.append(String.valueOf(exlude) + "|");
                ++n2;
            }
            finalIncludePrams = exclude_re.substring(0, exclude_re.length() - 1);
        }
        return finalIncludePrams;
    }

    private String getIncludeParams(String includeParams) {
        StringBuffer include_re = null;
        String finalIncludePrams = null;
        if (includeParams != null && !"".equalsIgnoreCase(includeParams)) {
            String[] exclude_arr;
            include_re = new StringBuffer("");
            String[] arrstring = exclude_arr = includeParams.split("##");
            int n = arrstring.length;
            int n2 = 0;
            while (n2 < n) {
                String exlude = arrstring[n2];
                include_re.append("(" + exlude + ")|");
                ++n2;
            }
            finalIncludePrams = include_re.substring(0, include_re.length() - 1);
        }
        return finalIncludePrams;
    }

    private Profile getBaseRemoteProfile(CheckPointDetails checkPointDetails) {
        Profile remoteProfile = null;
        try {
            remoteProfile = ProfileBuilder.newBuilder().name("File Monitoring").filePath(checkPointDetails.getFileLocation()).onRemotehostAndPort(checkPointDetails.getFileIpAddress(), checkPointDetails.getFilePort().intValue()).credentials(checkPointDetails.getFileUserName(), checkPointDetails.getFilePassword()).build();
        }
        catch (Exception e) {
            LOGGER.info((Object)"", (Throwable)e);
        }
        return remoteProfile;
    }

    private void writeLastFilePointRuntime(Profile logProfile, String lastRuntime, int noOfLinesForRead, String regExpression, int checkPointId, String cLineNumber) {
        String lRuntime = "";
        try {
            GrepResults endLineRT = Grep4j.grep((GrepExpression)Grep4j.constantExpression((String)lastRuntime), (Profile)((Profile)Dictionary.on((Object)logProfile)), (Option[])new Option[]{Option.extraLinesAfter((int)noOfLinesForRead)}).filterBy(new GrepExpression(regExpression, true));
            if (endLineRT.totalLines() == 0) {
                lRuntime = lastRuntime;
            } else {
                String[] patrenResult = endLineRT.toString().split("\\r?\\n");
                Pattern pattern = Pattern.compile(regExpression);
                Matcher matcher = pattern.matcher(patrenResult[patrenResult.length - 1]);
                if (matcher.find()) {
                    lRuntime = matcher.group();
                }
            }
            Properties prop = new Properties();
            FileInputStream in = new FileInputStream(String.valueOf(System.getProperty("user.dir")) + "/LastRuntime.properties");
            prop.load(in);
            in.close();
            FileOutputStream out = new FileOutputStream(String.valueOf(System.getProperty("user.dir")) + "/LastRuntime.properties");
            prop.setProperty(String.valueOf(checkPointId) + "_LAST_RUN_TIME", String.valueOf(lRuntime) + "###" + cLineNumber);
            prop.store(out, null);
            out.close();
        }
        catch (IOException e) {
            LOGGER.error((Object)"", (Throwable)e);
        }
    }

    private String readLastRunTime(int checkPointId) throws IOException {
        Properties prop = new Properties();
        FileInputStream input = new FileInputStream(String.valueOf(System.getProperty("user.dir")) + "/LastRuntime.properties");
        prop.load(input);
        return prop.getProperty(String.valueOf(checkPointId) + "_LAST_RUN_TIME");
    }

    private String handleSpecialCharacters(String lastRunTime) {
        String[] charArryToBeHandled = null;
        try {
            charArryToBeHandled = CommonUtilities.getProperty((String)"CHARS_TO_BE_HANDLED").split(",");
            int i = 0;
            while (i < charArryToBeHandled.length) {
                if (lastRunTime.contains(charArryToBeHandled[i])) {
                    lastRunTime = lastRunTime.replace(charArryToBeHandled[i], "\\" + charArryToBeHandled[i]);
                }
                ++i;
            }
        }
        catch (Exception e) {
            LOGGER.error((Object)"", (Throwable)e);
        }
        return lastRunTime;
    }
}

