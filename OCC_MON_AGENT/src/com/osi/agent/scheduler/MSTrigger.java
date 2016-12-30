package com.osi.agent.scheduler;

import java.util.Date;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;

public class MSTrigger {
	private MSTrigger(){
		
	}
	
	public static SimpleTrigger createSimpleTrigger(String triggerName, String groupName, int repeatInterval) {
		SimpleTrigger simpletrigger = TriggerBuilder
				.newTrigger()
				.withIdentity(triggerName, groupName)
				.startNow()
				.withSchedule(SimpleScheduleBuilder.simpleSchedule()
				.withIntervalInSeconds(repeatInterval)
				.repeatForever().withMisfireHandlingInstructionFireNow())
				.build();
    	return simpletrigger;
    }
	public static SimpleTrigger createSimpleTriggerWRCount(String triggerName, String groupName, int repeatCount, int repeatInterval) {
		SimpleTrigger simpletrigger = TriggerBuilder
				.newTrigger()
				.withIdentity(triggerName, groupName)
				.startNow()
				.withSchedule(SimpleScheduleBuilder.simpleSchedule()
				.withIntervalInSeconds(repeatInterval)
				.withRepeatCount(repeatCount-1).withMisfireHandlingInstructionFireNow())
				.build();
    	return simpletrigger;
    }
    
    public static CronTrigger createCronTrigger(String triggerName, String groupName, String cronExpression, Date startDate, Date endDate) {
    	CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerName, groupName)
    			.startAt(startDate)
    		    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionFireAndProceed())
    		    .endAt(endDate)
    		    .build();
    	return cronTrigger;
    }
    
}
