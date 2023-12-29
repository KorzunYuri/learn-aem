package com.adobe.aem.guides.wknd.core.models;

import com.adobe.aem.guides.wknd.core.entities.Product;

import java.util.List;

public interface ProductList {
    String getTitle();

    Integer getProductsNumber();
    List<Product> getProducts();

    boolean isEmpty();
}
