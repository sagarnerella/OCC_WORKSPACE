package com.osi.agent.monitor;

import com.osi.agent.exception.OCCAgentCheckpointMonitoringException;
import com.osi.agent.vo.CheckPointDetails;
import com.osi.agent.vo.CheckpointResult;

public interface IMonitor {
	public CheckpointResult performCheck(CheckPointDetails checkPointDetails) throws OCCAgentCheckpointMonitoringException;
}
