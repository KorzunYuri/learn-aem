package com.adobe.aem.guides.wknd.core.services.access;

import org.apache.sling.api.resource.ResourceResolver;

/**
 * Should provide you with a resourceProvider instance with a acl based on the serviceName, or default acl if serviceName is omitted
 */
public interface DefaultResourceResolverProvider {
    ResourceResolver getResourceResolver();
}
