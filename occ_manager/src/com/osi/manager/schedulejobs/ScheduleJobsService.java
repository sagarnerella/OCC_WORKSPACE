package com.osi.manager.schedulejobs;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.osi.manager.services.ManagerStartupProcessor;

public class ScheduleJobsService extends HttpServlet{
	private static final Logger LOGGER = Logger.getLogger(ScheduleJobsService.class);
	ManagerStartupProcessor managerStartupProcessor=null;
	 public void init() throws ServletException {
		    
		    try {
		    	LOGGER.info("loaded ScheduleJobsService :: ");
		        LOGGER.info("loaded ScheduleJobsService ::");
		        managerStartupProcessor=new ManagerStartupProcessor();
		        managerStartupProcessor.scheduleConfigPublisherJobs();
		        managerStartupProcessor.ScheduleAgentAvailabilityStatus();
		      }
		    catch (Exception ex) {
		      ex.printStackTrace();
		      LOGGER.error("error occure while loading ScheduleJobsService");
		    }
		    
		  }
		 
		}