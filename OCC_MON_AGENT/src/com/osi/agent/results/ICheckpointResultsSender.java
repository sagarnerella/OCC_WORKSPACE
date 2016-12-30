/**
 * 
 */
package com.osi.agent.results;

import com.osi.agent.exception.OCCCheckpointResultsException;

/**
 * @author jkorada1
 *
 */
public interface ICheckpointResultsSender {
	public boolean sendResults() throws OCCCheckpointResultsException;
}
