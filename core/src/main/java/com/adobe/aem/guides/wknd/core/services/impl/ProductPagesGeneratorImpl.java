package com.adobe.aem.guides.wknd.core.services.impl;

import com.adobe.aem.guides.wknd.core.entities.Product;
import com.adobe.aem.guides.wknd.core.services.ProductPagesGenerator;
import com.adobe.aem.guides.wknd.core.services.ProductService;
import com.adobe.aem.guides.wknd.core.services.ProductsRequestParams;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.PageManagerFactory;
import com.day.cq.wcm.api.WCMException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import javax.jcr.Node;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.adobe.aem.guides.wknd.core.services.ProductsRequestParams.LIMIT_UNLIMITED;

@Component(
        service = ProductPagesGenerator.class
    ,   immediate = true
)
@Designate(ocd = ProductPagesGeneratorImpl.Config.class)
@Slf4j
public class ProductPagesGeneratorImpl implements ProductPagesGenerator {

    private String pagesPathRoot;
    private boolean keepOldPages;

    private static final String templatePath = "/conf/wknd/settings/wcm/templates/product-page-manual-template";

    @Reference
    private ProductService productService;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private PageManagerFactory pageManagerFactory;


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
    public void activate(Config config) {
        configure(config);
    }

    @Modified
    public void modified(Config config) {
        //   TODO if the path has changed - move pages to the new path
        if (!StringUtils.isEmpty(config.pagesPathRoot()) && !StringUtils.isEmpty(this.pagesPathRoot) && !config.pagesPathRoot().equals(this.pagesPathRoot)) {
            //  move the pages to the new destination
        }
        configure(config);
    }

    private void configure(Config config) {
        this.pagesPathRoot      = config.pagesPathRoot();
        this.keepOldPages       = config.keepOldProduct();
    }

    @Override
    public void updateProductPages() {
        if (StringUtils.isEmpty(pagesPathRoot)) {
            log.error("Page path root is not provided! Configure the service correctly");
            throw new IllegalArgumentException(String.format("%s is not configured correctly", this.getClass().getName()));
        }

        List<Product> products = productService.getProducts(ProductsRequestParams.builder()
                        .limit(LIMIT_UNLIMITED)
                        .shuffle(false)
                .build());

        // TODO remove pages for products that are not present in fetched data
        if (!keepOldPages) {

        }

        //  get JCR/page tools
        try (ResourceResolver resourceResolver = getResourceResolver()) {
            String userId = resourceResolver.getUserID();
            log.info("Got resourceResolver for user " + userId);
            PageManager pageManager = pageManagerFactory.getPageManager(resourceResolver);

            //  create new pages
            products.forEach(product -> {
                deleteOldPage(product, pageManager);
                Page newPage = createPage(product, resourceResolver, pageManager);
                if (newPage != null) {
                    log.info(String.format("created page from product %s", product));
                } else {
                    log.error(String.format("Unable to create page from product %s", product));
                };
            });
        }

        log.info("Pages generation is complete");
    }

    @Override
    public String getPagesRoot() {
        return pagesPathRoot;
    }

    private void deleteOldPage(Product product, PageManager pageManager) {
        String pagePath = getPagePath(product);
        //  TODO compare to the page properties and don't remove if they haven't changed
        try {
            Page oldPage = pageManager.getPage(pagePath);
            if (oldPage != null) {
                pageManager.delete(oldPage, false, true);
                log.info(String.format("Deleted old version of a page for product %s", product));
            }
        } catch (WCMException e) {
            log.error(String.format("Unable to delete old version of a page %s: %s", pagePath, e.getMessage()));
        }
    }

    private Page createPage(Product product, ResourceResolver resourceResolver, PageManager pageManager) {
        String pageName = getPageName(product);
        String pagePath = getPagePath(product);
        String pageTitle = getPageTitle(product);

        Page page = null;
        try {
            page = pageManager.create(pagesPathRoot, pageName, templatePath, pageTitle, true);
        } catch (WCMException e) {
            log.error(String.format("Failed to create page %s with title %s from template %s : %s", pagePath, pageTitle, templatePath, e.getMessage()));
        }
        boolean isPagePropertiesUpdated = false;
        if (page != null) {
            try {
                setPageContentByResourceApi(page, product, resourceResolver);
                resourceResolver.commit();
                isPagePropertiesUpdated = true;
            } catch (Exception e) {
                log.error(String.format("Failed to populate page %s with title %s from template %s : %s", pagePath, pageTitle, templatePath, e.getMessage()));
            }
        }
        if (page != null && !isPagePropertiesUpdated) {
            try {
                pageManager.delete(page, true);
            } catch (WCMException e) {
                log.error(String.format("Unable to delete incorrectly created page %s", pagePath));
                page = null;
            }
        }
        return page;
    }

    private void setPageContentByNodeApi(Page page, Product product, ResourceResolver resolver) throws Exception {
        Node pageNode = page.adaptTo(Node.class);
        Node jcrNode = null;
        //  get/create jcr:content node
        if (page.hasContent()) {
            jcrNode = page.getContentResource().adaptTo(Node.class);
        } else {
            jcrNode = pageNode.addNode("jcr:content", "cq:PageContent");
        }
        //  get/create product node
        Node productNode = null;
        if (jcrNode.hasNode("product")) {
            productNode = jcrNode.getNode("product");
        } else {
            jcrNode.addNode("product", "nt:unstructured");
        }
        //  populate properties
        productNode.setProperty("id", product.getId());
        productNode.setProperty("title", product.getTitle());
        productNode.setProperty("description", product.getDescription());
        productNode.setProperty("image", product.getImage());
    }

    private void setPageContentByResourceApi(Page page, Product product, ResourceResolver resolver) throws Exception {
        //  get/create content resource
        Resource pageResource = page.adaptTo(Resource.class);
        Resource pageContentResource = page.getContentResource();
        if (pageResource == null) {
            Map<String, Object> contentProps = new HashMap<>();
            contentProps.put("sling:primaryType", "cq:PageContent");
            pageContentResource = resolver.create(pageResource, "jcr:content", contentProps);
        }
        //  get/create product resource
        Resource productResource = pageContentResource.getChild("product");
        if (productResource == null) {
            productResource = resolver.create(pageContentResource, "product", new HashMap<>());
        }
        //  populate with properties
        ModifiableValueMap productProperties = productResource.adaptTo(ModifiableValueMap.class);
        productProperties.put("id", product.getId());
        productProperties.put("title", product.getTitle());
        productProperties.put("description", product.getDescription());
        productProperties.put("image", product.getImage());
    }

    private String getPagePath(Product product) {
        return String.format("%s/%s", pagesPathRoot, getPageName(product));
    }

    private String getPageTitle(Product product) {
        return product.getTitle();
    }

    private String getPageName(Product product) {
        return String.format("product%s", product.getId());
    }

    private ResourceResolver getResourceResolver() {
        ResourceResolver resolver = null;
        Map<String, Object> params = getServiceParams();
        try {
            resolver = resourceResolverFactory.getServiceResourceResolver(params);
        } catch (LoginException e) {
            log.info(String.format("Failed to get resource resolver with params %s", params));
        }
        return resolver;
    }

    private Map<String, Object> getServiceParams() {
        Map<String, Object> params = new HashMap<>();
        params.put(ResourceResolverFactory.SUBSERVICE, "testServiceUser");
        return params;
    }

}
