package com.adobe.aem.guides.wknd.core.models.impl;

import com.adobe.aem.guides.wknd.core.caconfigs.RegionalConfig;
import com.adobe.aem.guides.wknd.core.models.RegionInfo;
import com.day.cq.wcm.api.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;

@Model(
        adaptables = {SlingHttpServletRequest.class}
    ,   adapters = {RegionInfo.class}
    ,   resourceType = RegionInfoImpl.RESOURCE_TYPE
    ,   defaultInjectionStrategy = DefaultInjectionStrategy.REQUIRED
)
@Slf4j
public class RegionInfoImpl implements RegionInfo {

    protected static final String RESOURCE_TYPE = "wknd/models/regioninfo";

    @SlingObject
    private ResourceResolver resourceResolver;

    @ScriptVariable
    private Page currentPage;

    private String countryCode;
    private String supportEmail;
    private String configPath;

    @Override
    public String getCountryCode() {
        return countryCode;
    }

    @Override
    public String getSupportEmail() {
        return supportEmail;
    }

    @Override
    public String getConfigPath() {
        return configPath;
    }

    @PostConstruct
    public void postConstruct() {
        try {
            Resource resource = resourceResolver.getResource(currentPage.getPath());
            if (resource != null) {
                ConfigurationBuilder confBuilder = resource.adaptTo(ConfigurationBuilder.class);
                if (confBuilder != null) {
                    RegionalConfig regionalConfig = confBuilder.as(RegionalConfig.class);
                    countryCode     = regionalConfig.country_code();
                    supportEmail    = regionalConfig.supportEmail();
                    Resource pageContent = currentPage.getContentResource();
                    ValueMap parameters = pageContent.adaptTo(ValueMap.class);
                    configPath = parameters.get("sling:configRef", String.class);
                }

            }

        } catch (Exception e) {

            //Your Exception Handling code

        }
    }
}
