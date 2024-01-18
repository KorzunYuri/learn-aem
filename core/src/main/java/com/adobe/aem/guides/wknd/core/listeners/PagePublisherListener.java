package com.adobe.aem.guides.wknd.core.listeners;

import com.adobe.aem.guides.wknd.core.domains.product.ProductConstants;
import com.adobe.aem.guides.wknd.core.services.access.impl.ProductResourceResolverProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.List;

@Component(
        service = ResourceChangeListener.class
    ,   immediate = true
    ,   property = {
            ResourceChangeListener.PATHS + "=" + "glob:/content/wknd/**/products/product*"
        ,   ResourceChangeListener.CHANGES + "=" + "ADDED"
        ,   ResourceChangeListener.CHANGES + "=" + "CHANGED"
        ,   ResourceChangeListener.CHANGES + "=" + "REMOVED"
    }
)
@Slf4j
public class PagePublisherListener implements ResourceChangeListener {

    @Reference
    private ProductResourceResolverProvider resourceResolverProvider;

    @Override
    public void onChange(@NotNull List<ResourceChange> resourceChanges) {
        ResourceResolver resourceResolver = resourceResolverProvider.getResourceResolver();
        resourceChanges.forEach(change -> {
            String path = change.getPath();
            Resource resource = resourceResolver.getResource(path);
            if (resource != null) {
                if (resource.getResourceType().equals(ProductConstants.PRODUCT_PAGE_RESOURCE_TYPE)){
                    log.info(String.format("Change on page %s: %s", path, change.getType()));
                    if (change.getType() == ResourceChange.ChangeType.ADDED) {
                        log.info(String.format("Page %s ADDED", path));
                        //  TODO publish the page
                    }
                }
            }
        });
    }
}
