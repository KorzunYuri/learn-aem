package com.adobe.aem.guides.wknd.core.models.impl;

import com.adobe.aem.guides.wknd.core.models.ProductList;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

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

    @ValueMapValue
    private String title;

    @ValueMapValue
    private Integer productsNumber;

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Integer getProductsNumber() {
        return productsNumber;
    }

    @Override
    public boolean isEmpty() {
        return  StringUtils.isBlank(getTitle())
            &&  (productsNumber == null || productsNumber < 1);
    }
}
