/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.osi.manager;

import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author psangineni
 */
@Path("/")
@WebService(name="managerService")
public interface ManagerService {
    
// Manager Tasks :
 	
 /*1) Receive heart beat and store in heart beat table */
 	@POST
 	@Produces({MediaType.APPLICATION_JSON})
 	@Path("/getAgentHeartBeat")
 	public Response getAgentHeartBeat(@QueryParam("agentId") String agentId);
 	
 /*2) crontrigger to run for every 5 minutes whether to check the agent is live or not  */
	@POST
 	@Produces({MediaType.APPLICATION_JSON})
 	@Path("/agentAvailabilityStatus")
 	public Response agentAvailabilityStatus();
	
 	@POST
 	@Produces({MediaType.APPLICATION_JSON})
 	@Path("/ScheduleAgentAvailabilityStatus")
    public void ScheduleAgentAvailabilityStatus();
	
/*3) crontrigger to run for every 5 minutes to see if any new check is configured or not  */	
	
	@POST
 	@Produces({MediaType.APPLICATION_JSON})
 	@Path("/scheduleConfigPublisherJobs")
	public void scheduleConfigPublisherJobs();
	
/*4) i) send the devicexmlnamelist  to the agent 	*/
	@POST
 	@Produces({MediaType.APPLICATION_JSON})
 	@Path("/getDeviceXmlList")
	//public Response getDeviceXmlList(@QueryParam("agentId") String agentId, @QueryParam("publishType") String publishType);
	public Response getDeviceXmlList(@QueryParam("agentId") String agentId);
	
	
/*4) ii) send the devicexml content  to the agent 	*/
	@POST
 	@Produces({MediaType.APPLICATION_JSON})
 	@Path("/getDeviceXml")
	public Response getDeviceXml(@QueryParam("agentId") String agentId, @QueryParam("deviceXmlName")String deviceXmlName);
	
/*5) cron to receive the success reult from agent and store it in the local db.	*/
	@POST
 	@Produces({MediaType.APPLICATION_JSON})
 	@Path("/receiveResultXml")
	public String receiveResultXml(String resultXml);


/*6) save Error details	*/
	@POST
 	@Produces({MediaType.APPLICATION_JSON})
 	@Path("/saveErrorDetailes")
	public String saveErrorDetailes(String errorResultXml);

/*7) TestConnectionCheckInfo */
	
/*i) getCheckdetails */
		@POST
	 	@Produces({MediaType.APPLICATION_JSON})
	 	@Path("/getCheckinfo")
		public Response getCheckinfo(@QueryParam("agentId")String agentId, @QueryParam("address")String address, @QueryParam("tableOid")String tableOid,
			                            @QueryParam("mib")String mib, @QueryParam("communityStrg")String communityStrg);
	
/*ii) sendTestConnectionStatus to the agent */
		@POST
	 	@Produces({MediaType.APPLICATION_JSON})
	 	@Path("/sendTestConnectionInfo")
		public Response sendTestConnectionInfo(@QueryParam("agentId")String agentId);
		
/*iii) getTestConnectionStatus from the manager */
		@POST
	 	@Produces({MediaType.APPLICATION_JSON})
	 	@Path("/getTestConnectionInfoResult")
		public Response getTestConnectionInfoResult(@QueryParam("agentId")String agentId, String result);

}
