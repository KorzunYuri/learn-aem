package com.adobe.aem.guides.wknd.core.services.access.impl;

import com.adobe.aem.guides.wknd.core.services.access.DefaultResourceResolverProvider;
import com.adobe.aem.guides.wknd.core.services.access.SecuredResourceResolverProvider;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
        service = ProductResourceResolverProvider.class
)
public class ProductResourceResolverProvider implements DefaultResourceResolverProvider {

    @Reference
    private SecuredResourceResolverProvider resourceResolverProvider;

    @Override
    public ResourceResolver getResourceResolver() {
        return resourceResolverProvider.getResourceResolver("testServiceUser");
    }
}
