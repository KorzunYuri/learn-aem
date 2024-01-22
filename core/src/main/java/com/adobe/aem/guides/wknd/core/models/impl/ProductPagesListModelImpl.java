package com.adobe.aem.guides.wknd.core.models.impl;

import com.adobe.aem.guides.wknd.core.domains.product.ProductConstants;
import com.adobe.aem.guides.wknd.core.domains.product.ProductPageLink;
import com.adobe.aem.guides.wknd.core.models.ProductPagesListModel;
import com.adobe.aem.guides.wknd.core.services.ProductPagesGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.*;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;

import java.util.ArrayList;
import java.util.List;

@Model(
        adaptables      = {SlingHttpServletRequest.class}
    ,   adapters        = {ProductPagesListModel.class}
    ,   resourceType    = ProductPagesListModelImpl.RESOURCE_TYPE
    ,   defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporters(
    value = {
        @Exporter(
                name = "jackson"
            ,   selector = "export"
            ,   extensions = "json"
            ,   options = {
                @ExporterOption(name = "MapperFeature.SORT_PROPERTIES_ALPHABETICALLY", value = "false")
            }
        ),
        @Exporter(
                name = "jackson"
            ,   selector = "model"
            ,   extensions = "json"
            ,   options = {
                   @ExporterOption(name = "MapperFeature.SORT_PROPERTIES_ALPHABETICALLY", value = "true")
            }
        )
    }
)

@Slf4j
public class ProductPagesListModelImpl extends ProductListImpl implements ProductPagesListModel{

    protected static final String RESOURCE_TYPE = "wknd/components/productpageslinkslist";

    @Self
    private SlingHttpServletRequest request;

    @OSGiService
    private ProductPagesGenerator productPagesGenerator;

    @Override
    public List<ProductPageLink> getProductPagesLinks() {
        List<String> pagesLinks = productPagesGenerator.getPagesLinks();
        List<ProductPageLink> result = new ArrayList<>();
        ResourceResolver resourceResolver = request.getResourceResolver();
        for (String link : pagesLinks) {
            Resource page = resourceResolver.getResource(link);
            result.add(new ProductPageLink(
                    page.getChild("jcr:content")
                        .getChild("product")
                        .adaptTo(ValueMap.class)
                        .get(ProductConstants.FIELD_NAME_TITLE, String.class),
                    link)
            );
        }
        return result;
    }

}
