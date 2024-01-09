package com.adobe.aem.guides.wknd.core.schedulers;

import com.adobe.aem.guides.wknd.core.jobs.JobTopics;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import java.util.HashMap;

//@Component(
//        service = Runnable.class
//    ,   immediate = true
//    ,   configurationPolicy = ConfigurationPolicy.REQUIRE
//)
//@Designate(ocd = ProductPageGeneratorScheduler.Config.class)
@Slf4j
public class ProductPageGeneratorScheduler implements Runnable{

    @Reference
    private Scheduler scheduler;

    @Reference
    private JobManager jobManager;

    private String schedulerName;
    private String cronExpression;

    public final static class Properties {
        public final static String SCHEDULER_NAME = "scheduler.name";
        public final static String CRON_EXPRESSION = "cronExpression";
    }

    @ObjectClassDefinition(description = "Product list updates scheduler configuration")
    public @interface Config {

        @AttributeDefinition(
                name = Properties.SCHEDULER_NAME
            ,   description = "Scheduler name"
            ,   type = AttributeType.STRING
        )
        String scheduler_name() default "ProductPageGeneratorScheduler";

        @AttributeDefinition(
                name = Properties.CRON_EXPRESSION
            ,   description = "Scheduler's cron expression"
            ,   type = AttributeType.STRING
        )
        String cronExpression();
    }

    public void setProperties(Config config) {
        this.schedulerName          = config.scheduler_name();
        this.cronExpression         = config.cronExpression();
    }

    private void addScheduler() {
        ScheduleOptions options = scheduler.EXPR(cronExpression);
        options.canRunConcurrently(false);

        scheduler.schedule(this, options);
        log.info(String.format("Scheduler %s was added with cron expression %s", schedulerName, cronExpression));
    }

    private void removeScheduler() {
        scheduler.unschedule(schedulerName);
        log.info(String.format("Scheduler %s was removed!", schedulerName));
    }

    private void onConfigChanged(Config config) {
        setProperties(config);
        validateConfig();
    }

    private void validateConfig() {
        if (StringUtils.isBlank(cronExpression)) {
            throw new IllegalArgumentException(String.format("Cron expression %s is invalid", cronExpression));
        }
    }

    @Activate
    public void activate(ComponentContext context, Config config) throws IllegalArgumentException {
        onConfigChanged(config);
        addScheduler();
    }

    @Modified
    public void modified(Config config) throws IllegalArgumentException{
        removeScheduler();
        onConfigChanged(config);
        addScheduler();
    }

    @Deactivate
    public void deactivate(Config config) {
        removeScheduler();
    }

    @Override
    public void run() {
        log.info(String.format("Scheduler %s is running!", schedulerName));
        jobManager.addJob(JobTopics.PRODUCT_PAGES_GENERATION, new HashMap<>());
    }

}
