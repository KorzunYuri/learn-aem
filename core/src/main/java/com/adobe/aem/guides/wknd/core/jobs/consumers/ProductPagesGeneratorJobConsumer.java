package com.adobe.aem.guides.wknd.core.jobs.consumers;

import com.adobe.aem.guides.wknd.core.jobs.JobTopics;
import com.adobe.aem.guides.wknd.core.services.ProductPagesGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Slf4j
@Component(
        service = JobConsumer.class
    ,   immediate = true
    ,   property = {
                Constants.SERVICE_DESCRIPTION + "=Product pages generation"
            ,   JobConsumer.PROPERTY_TOPICS + "=" + JobTopics.PRODUCT_PAGES_GENERATION
    })
public class ProductPagesGeneratorJobConsumer implements JobConsumer {

    private volatile boolean isProcessed = false;

    @Reference
    private ProductPagesGenerator pagesGenerator;

    @Override
    public JobResult process(Job job) {
        if (isProcessed) {
            log.error(String.format("previous run of job %s is still being processed so the new job is cancelled", JobTopics.PRODUCT_PAGES_GENERATION));
            return JobResult.CANCEL;
        } else {
            try {
                isProcessed = true;
                log.info(String.format("Job %s has been consumed", job.getId()));
                pagesGenerator.updateProductPages();
                log.info(String.format("Job %s complete", job.getId()));
                return JobResult.OK;
            } catch (Exception e) {
                log.error(String.format("Job %s failed: %s", job.getId(), e.getMessage()));
                return JobResult.FAILED;
            } finally {
                isProcessed = false;
            }
        }
    }
}
