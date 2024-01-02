package com.adobe.aem.guides.wknd.core.models.impl;

import com.adobe.aem.guides.wknd.core.entities.Product;
import com.adobe.aem.guides.wknd.core.models.ProductList;
import com.adobe.aem.guides.wknd.core.services.ProductService;
import com.adobe.aem.guides.wknd.core.services.ProductsRequestParams;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.List;

@Model(
        adaptables = {SlingHttpServletRequest.class}
    ,   adapters = {ProductList.class}
    ,   resourceType = ProductListImpl.RESOURCE_TYPE
    ,   defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ProductListImpl implements ProductList {

    protected static final String RESOURCE_TYPE = "wknd/components/productList";

    @Self
    private SlingHttpServletRequest request;

    @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
    private ProductService productService;

    @ValueMapValue
    private String title;

    @ValueMapValue
    private int productsNumber = PRODUCTS_LIMIT_ALL;

    @ValueMapValue
    private boolean shuffle;

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public int getProductsNumber() {
        return productsNumber;
    }

    @Override
    public List<Product> getProducts() {
        return productService.getProducts(ProductsRequestParams.builder()
                .limit(getProductsNumber())
                .shuffle(shuffle)
            .build());
    }

    @Override
    public boolean isEmpty() {
        return  StringUtils.isBlank(getTitle());
    }
}
