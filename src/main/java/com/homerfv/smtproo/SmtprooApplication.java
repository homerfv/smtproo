package com.homerfv.smtproo;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SmtprooApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmtprooApplication.class, args);
	}
	
	@Bean
	public JobDetail sampleJobDetail() {
		return JobBuilder.newJob(HousekeepJob.class).withIdentity("housekeepJob")
				.usingJobData("name", "World").storeDurably().build();
	}

	@Bean
	public Trigger housekeepJobTrigger() {
		SimpleScheduleBuilder scheduleBuilder = 
					SimpleScheduleBuilder.simpleSchedule()
					.withIntervalInHours(1)
					/*.withIntervalInSeconds(10)*/
					.repeatForever();

		return TriggerBuilder.newTrigger().forJob(sampleJobDetail())
				.withIdentity("housekeepTrigger").withSchedule(scheduleBuilder).build();
	}
}
