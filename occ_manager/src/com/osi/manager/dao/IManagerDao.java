package com.osi.manager.dao;

import java.util.List;
import java.util.Map;

import com.osi.manager.domain.AgentErrors;
import com.osi.manager.domain.CheckTestConnectionInfo;
import com.osi.manager.domain.DeviceCheckpointResults;
import com.osi.manager.domain.UMObject;
import com.osi.manager.exception.CreateException;
import com.osi.manager.exception.FinderException;
import com.osi.manager.exception.UpdateException;
import com.osi.manager.vo.Device;
import com.osi.manager.vo.DeviceXml;
import com.osi.manager.vo.MSAgent;

public interface IManagerDao {

	boolean saveMonitoringResult(DeviceCheckpointResults dcpResultList) throws Exception;
	boolean saveAgentErrors(AgentErrors agentErrors) throws Exception;
	public List<Device> getEntriesToPublish(MSAgent msAgent, String publishType) throws Exception;
	public List<Device> getDevicesToUnschedule(MSAgent msAgent) throws Exception;
	public List<MSAgent> getAgentsList() throws Exception;
	public boolean updatePublisherStatus(int deviceId,String status,String xmlFormat,String error) throws Exception;
	public boolean saveAgentPollAudit(int agentId,String status) throws CreateException,FinderException;
	public Map<Integer,UMObject> getUMFormatMap() throws Exception;
	public boolean saveDeviceXml(int agentId,String xml,String jobName) throws CreateException,FinderException;
	public List<DeviceXml> getDeviceXmlList(String jobname,int agentId) throws FinderException;
	public boolean updateSaveDeviceXml(int id,int agentId,String jobName,String status) throws FinderException,UpdateException;
	public boolean saveAgentHeartBeat(String agentId) throws FinderException,CreateException;
	public boolean agentAvailabilityStatus() throws FinderException,CreateException;
	public String getDeviceXmlList(String agentId) throws FinderException,CreateException;
	public String getDeviceXml(String agentId, String deviceXmlName) throws FinderException,CreateException;
	public String getCheckinfo(String agentId, String address, String tableOid, String mib, String communityStrg) throws FinderException,CreateException;
	public String sendTestConnectionInfo(String agentId) throws FinderException,CreateException;
	public String getTestConnectionInfoResult(String agentId, String checkresult) throws FinderException,CreateException;
	public String CheckTestConnResult(CheckTestConnectionInfo msg) throws FinderException,CreateException;
	public String SendTestConnResponse(String result) throws FinderException,CreateException;

}
