package com.osi.re.dao;

import java.util.List;

import com.osi.re.exception.FinderException;
import com.osi.re.vo.CheckpointAlert;
import com.osi.re.vo.CheckpointResult;
import com.osi.re.vo.MSAgent;
import com.osi.re.vo.MSAgentPollAudit;


public interface IREDao {

	public List<CheckpointResult> fetchResults(String checkType) throws FinderException;
	public boolean updateResultProcessStatus(List<Integer> resultIds, String checkType) throws FinderException;
	public boolean updateIntermediateResultProcess(List<Integer> ctresultIds) throws FinderException;
	public boolean updateResultProcessStatus(int resultId) throws FinderException;
	public boolean saveCheckpointAlert(CheckpointResult checkpointResult, String alertStatus) throws FinderException;
	public boolean updateCheckpointAlert(int deviceId, int checkpointId) throws FinderException;
	public boolean updateCheckpointAlertStatus(int deviceId, int checkpointId, String alertStatus) throws FinderException;
	public boolean updateCheckpointAlertIncidentStatus(int checkpointAlertID, String alertStatus) throws FinderException;
	public boolean checkCheckpointAlertEntry(int deviceId, int checkpointId) throws FinderException;
	public List<CheckpointAlert> getCheckpointAlerts() throws FinderException;
	public List<CheckpointAlert> getCheckpointIncidentAlerts() throws FinderException;
	public List<MSAgent> getAgentsList() throws FinderException;
	public List<MSAgentPollAudit> getAgentPollingDetails(int agentID) throws FinderException; 
	public boolean savePublisherEntry(CheckpointAlert checkpointAlert) throws FinderException;
	//For saving and updating incident history
	public boolean saveAlertIncidents(int assetId, int checkpointMasterId, int checkpointAlertId) throws FinderException;
	public boolean updateAlertIncidents(int incidentId, int checkpointAlertId) throws FinderException;
	public boolean updateAlertIncidentsErrorText(String errorText, int checkpointAlertId) throws FinderException;
	public boolean verifyAlertIncident(int checkpointAlertId) throws FinderException;
	
	public int getChecckPointMasterId(int deviceCheckPointId) throws FinderException;
	public boolean saveCheckpointAndupdateResult(CheckpointResult checkpointResult,String alertStatus) throws FinderException;
	public boolean updateIncidentsErrorTextAndCheckpointAlertStatus(String errorMessage, int checkpointAlertId,	String alertStatus) throws FinderException;
	public boolean updateAlertIncidentsAndAlertStatus(int incidentId, int checkpointAlertId, String alertStatus) throws FinderException;
	public boolean savePublisherAndAlertStatus(CheckpointAlert checkpointAlert, int deviceId, int deviceCheckpointId, String alertStatus) throws FinderException;
	public boolean updatePollAuditStatus(int agentID, CheckpointResult checkpointResult) throws FinderException;
	public List<MSAgentPollAudit> getAgentErrorPollingDetails(int agentID) throws FinderException; 
}
