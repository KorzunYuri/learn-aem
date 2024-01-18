package com.adobe.aem.guides.wknd.core.services.access.impl;

import com.adobe.aem.guides.wknd.core.services.access.DefaultResourceResolverProvider;
import com.adobe.aem.guides.wknd.core.services.access.SecuredResourceResolverProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;

import java.util.HashMap;
import java.util.Map;

@Component(
        service = DefaultResourceResolverProviderImpl.class
)
@ServiceDescription(value = "Default resource resolver provider")
@Slf4j
public class DefaultResourceResolverProviderImpl implements DefaultResourceResolverProvider, SecuredResourceResolverProvider {

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public ResourceResolver getResourceResolver(String serviceName) {
        ResourceResolver resolver = null;
        Map<String, Object> params = getServiceParams(serviceName);
        try {
            resolver = resourceResolverFactory.getServiceResourceResolver(params);
        } catch (LoginException e) {
            log.info(String.format("Failed to get resource resolver with params %s", params));
        }
        return resolver;
    }

    @Override
    public ResourceResolver getResourceResolver() {
        try {
            return resourceResolverFactory.getResourceResolver(new HashMap<>());
        } catch (LoginException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Object> getServiceParams(String serviceName) {
        Map<String, Object> params = new HashMap<>();
        params.put(ResourceResolverFactory.SUBSERVICE, serviceName);
        return params;
    }

}
