/**
 * 
 */
package com.osi.agent.results;

import java.util.List;

import com.osi.agent.exception.OCCResultsCacheException;
import com.osi.agent.vo.CheckpointResult;

/**
 * @author jkorada1
 *
 */
public interface ICacheManager {
	public void addToResultCache(CheckpointResult checkpointResult) throws OCCResultsCacheException;
	public List<CheckpointResult> getResultCache() throws OCCResultsCacheException;
	public void removeResultsFromCache(List<CheckpointResult> removeResultsList)  throws  OCCResultsCacheException;
	public boolean isRequestPolled();
	public void setRequestPolled(boolean requestPolled);
	public Long getLastRequestPolledTime();
	public void setLastRequestPolledTime(Long lastRequestPolledTime);
	public void setManagerHeartBeat(String output);
	public String getManagerHeartBeat();
}
