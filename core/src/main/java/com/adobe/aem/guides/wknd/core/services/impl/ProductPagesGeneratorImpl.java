package com.adobe.aem.guides.wknd.core.services.impl;

import com.adobe.aem.guides.wknd.core.services.ProductPagesGenerator;
import com.adobe.aem.guides.wknd.core.services.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import java.util.concurrent.TimeUnit;

@Component(
        service = ProductPagesGenerator.class
    ,   immediate = true
)
@Designate(ocd = ProductPagesGeneratorImpl.Config.class)
@Slf4j
public class ProductPagesGeneratorImpl implements ProductPagesGenerator {

    private String pagesPathRoot;
    private boolean keepOldPages;

    @OSGiService
    private ProductService productService;

    @ObjectClassDefinition(
            name = "Product pages generator configuration"
        ,   description = "Defines pages path and other options"
    )
    public @interface Config {

        @AttributeDefinition(
                name = "pagesPathRoot"
            ,   description = "Path for all the pages"
            ,   type = AttributeType.STRING
        )
        String pagesPathRoot();

        @AttributeDefinition(
                name = "keepOldProducts"
            ,   description = "Keep products that are not returned by the API anymore"
            ,   type = AttributeType.BOOLEAN
        )
        boolean keepOldProduct() default false;
    }

    @Activate
    @Modified
    public void configure(Config config) {
        this.pagesPathRoot      = config.pagesPathRoot();
        this.keepOldPages       = config.keepOldProduct();
    }

    @Override
    public void updateProductPages() {
        if (StringUtils.isEmpty(pagesPathRoot)) {
            log.error("Page path root is not provided! Configure the service correctly");
//            throw new IllegalArgumentException(String.format("%s is not configured correctly", this.getClass().getName()));
        }
        log.info("Pages are not being generated yet, but we work on it");
        try {
            TimeUnit.MINUTES.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("Pages generation is complete");
    }

}
