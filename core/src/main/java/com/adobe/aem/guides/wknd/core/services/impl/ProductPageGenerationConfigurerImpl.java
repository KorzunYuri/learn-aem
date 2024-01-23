package com.adobe.aem.guides.wknd.core.services.impl;

import com.adobe.aem.guides.wknd.core.services.PageGenerationConfigurer;
import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Component(
        service = PageGenerationConfigurer.class
    ,   configurationPolicy = ConfigurationPolicy.REQUIRE
)
@Designate(
        ocd = ProductPageGenerationConfigurerImpl.Config.class
)
@Slf4j
public class ProductPageGenerationConfigurerImpl implements PageGenerationConfigurer {

    private boolean isGenerationEnabled;
    private boolean isRetrievalEnabled;

    @Override
    public boolean isGenerationEnabled() {
        return isGenerationEnabled;
    }

    @Override
    public boolean isRetrievalEnabled() {
        return isRetrievalEnabled;
    }

    @ObjectClassDefinition(
            name = "Product pages generation config"
        ,   description = "Product pages generation settings, that imply more than one service's functionality"
    )
    public @interface Config {

        @AttributeDefinition(
                name        = "enable generation"
                ,   description = "enable the product pages generation"
                ,   type        = AttributeType.BOOLEAN
        )
        boolean enableGeneration() default false;

        @AttributeDefinition(
                name        = "enable retrieval"
                ,   description = "enable retrieval via servlet"
                ,   type        = AttributeType.BOOLEAN
        )
        boolean enableRetrieval() default false;
    }

    @Activate
    @Modified
    public void activate(Config config) {
        this.isGenerationEnabled    = config.enableGeneration();
        this.isRetrievalEnabled     = config.enableRetrieval();
    }

}
