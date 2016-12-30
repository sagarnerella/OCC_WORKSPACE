/**
 * 
 */
package com.osi.agent.results;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.osi.agent.exception.OCCResultsCacheException;
import com.osi.agent.vo.CheckpointResult;

/**
 * @author jkorada1
 *
 */

public class CacheManagerImpl implements ICacheManager {
	
	private static final Logger LOGGER = Logger.getLogger(CacheManagerImpl.class);
	private static boolean requestPolled=false;
	private static Long lastRequestPolledTime=System.currentTimeMillis();
	private static List<CheckpointResult> checkpointResultsCache=new ArrayList<CheckpointResult>(0);
	private static String managerHeartBeat="";
	

	/* (non-Javadoc)
	 * @see com.osi.agent.results.ICacheManager#addToResultCache(com.osi.agent.vo.CheckpointResult)
	 */
	@Override
	public void addToResultCache(CheckpointResult checkpointResult)
			throws OCCResultsCacheException {
		try {
			checkpointResultsCache.add(checkpointResult);
		} catch (Exception e) {
			LOGGER.error("",e);
			throw new OCCResultsCacheException("", "Error occurred while adding results to the cache", e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see com.osi.agent.results.ICacheManager#getResultCache()
	 */
	@Override
	public List<CheckpointResult> getResultCache() throws OCCResultsCacheException {
		return checkpointResultsCache;
	}

	@Override
	public synchronized void removeResultsFromCache(List<CheckpointResult> removeResultsList) throws OCCResultsCacheException {
		try {
			checkpointResultsCache.removeAll(removeResultsList);
		} catch (Exception e) {
			LOGGER.error("",e);
			throw new OCCResultsCacheException("", "Error occurred while clearing cache", e.getMessage());
		}
	}

	public boolean isRequestPolled() {
		return requestPolled;
	}

	public void setRequestPolled(boolean requestPolled) {
		CacheManagerImpl.requestPolled = requestPolled;
	}

	public Long getLastRequestPolledTime() {
		return lastRequestPolledTime;
	}

	public void setLastRequestPolledTime(Long lastRequestPolledTime) {
		CacheManagerImpl.lastRequestPolledTime = lastRequestPolledTime;
	}
	
	
	
	public  String getManagerHeartBeat() {
		return managerHeartBeat;
	}

	public  void setManagerHeartBeat(String managerHeartBeat) {
		CacheManagerImpl.managerHeartBeat = managerHeartBeat;
	}

}
