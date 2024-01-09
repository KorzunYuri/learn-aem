package com.adobe.aem.guides.wknd.core.schedulers;

import com.adobe.aem.guides.wknd.core.jobs.JobTopics;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.event.jobs.JobBuilder;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.event.jobs.ScheduledJobInfo;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import java.util.Collection;


@Component(
        immediate = true
    ,   configurationPolicy = ConfigurationPolicy.REQUIRE
)
@Designate(ocd = ProductPageGeneratorSchedulerWithBuilderApproach.Config.class)
@Slf4j
public class ProductPageGeneratorSchedulerWithBuilderApproach {

    private final static String TOPIC = JobTopics.PRODUCT_PAGES_GENERATION;

    @Reference
    private JobManager jobManager;

    private String cronExpression;

    @ObjectClassDefinition(description = "Product list updates scheduler configuration")
    public @interface Config {

        @AttributeDefinition(
                name = ProductPageGeneratorScheduler.Properties.SCHEDULER_NAME
                ,   description = "Scheduler name"
                ,   type = AttributeType.STRING
        )
        String scheduler_name() default "ProductPageGeneratorSchedulerWithBuilderApproach";

        @AttributeDefinition(
                name = ProductPageGeneratorScheduler.Properties.CRON_EXPRESSION
                ,   description = "Scheduler's cron expression"
                ,   type = AttributeType.STRING
        )
        String cronExpression();
    }

    @Activate
    public void activate(Config config) {
        log.info(String.format("Scheduling ProductPageGeneratorSchedulerWithBuilderApproach, previous cron: %s , new cron: %s", this.cronExpression, config.cronExpression()));
        if (StringUtils.equals(config.cronExpression(), this.cronExpression)) {
            log.info("Crons are similar, skipping scheduling");
            return;
        }
        this.cronExpression = config.cronExpression();

        //  unregister all jobs
        Collection<ScheduledJobInfo> jobs = jobManager.getScheduledJobs(TOPIC, 1 ,null);
        if (!jobs.isEmpty()) {
            jobs.forEach(ScheduledJobInfo::unschedule);
        }

        //  schedule the job
        if (!isValidCron(this.cronExpression)) {
            log.error(String.format("Cron %s is not valid", this.cronExpression));
            return;
        }
        JobBuilder.ScheduleBuilder scheduleBuilder = jobManager.createJob(TOPIC).schedule();
        scheduleBuilder.cron(this.cronExpression);
        ScheduledJobInfo jobInfo = scheduleBuilder.add();
        if (jobInfo == null) {
            throw new RuntimeException(String.format("Failed to register job with topic %s", TOPIC));
        } else {
            log.info(String.format("Scheduled job with topic %s", TOPIC));
        }
    }

    private boolean isValidCron(String cronExpression) {
        return !StringUtils.isEmpty(cronExpression);
    }

}
