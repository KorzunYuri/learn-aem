package com.adobe.aem.guides.wknd.core.services.access;

import org.apache.sling.api.resource.ResourceResolver;

public interface SecuredResourceResolverProvider {
    ResourceResolver getResourceResolver(String serviceName);
}
