package com.adobe.aem.guides.wknd.core.services.access.impl;

import com.adobe.aem.guides.wknd.core.services.access.DefaultResourceResolverProvider;
import com.adobe.aem.guides.wknd.core.services.access.SecuredResourceResolverProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;

@Component(
        service = SecuredResourceResolverProvider.class
)
@ServiceDescription(value = "Resource resolver provider with user-based access only")
@Slf4j
public class SecuredResourceResolverProviderImpl implements SecuredResourceResolverProvider {

    @Reference
    private DefaultResourceResolverProviderImpl defaultResourceResolverProvider;

    @Override
    public ResourceResolver getResourceResolver(String serviceName) {
        return defaultResourceResolverProvider.getResourceResolver(serviceName);
    }

}
