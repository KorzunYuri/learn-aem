package com.adobe.aem.guides.wknd.core.models;

import com.adobe.aem.guides.wknd.core.entities.Product;

import java.util.List;

public interface ProductList {

    int PRODUCTS_LIMIT_SHOW_ALL = -1;

    String getTitle();

    int getProductsNumber();

    List<Product> getProducts();

    boolean isEmpty();
}
