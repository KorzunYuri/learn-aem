package com.adobe.aem.guides.wknd.core.services;

import com.adobe.aem.guides.wknd.core.entities.Product;

import java.util.List;

public interface ProductService {
    List<Product> getProducts();
    List<Product> getProducts(int limit);
}
