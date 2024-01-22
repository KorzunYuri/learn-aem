package com.adobe.aem.guides.wknd.core.models;

import com.adobe.aem.guides.wknd.core.domains.product.ProductPageLink;

import java.util.List;

public interface ProductPagesListModel extends ProductList{
    List<ProductPageLink> getProductPagesLinks();
}
