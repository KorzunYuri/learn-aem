package com.adobe.aem.guides.wknd.core.services;

import com.adobe.aem.guides.wknd.core.domains.product.Product;

import java.util.List;

public interface ProductService {
    List<Product> getProducts(ProductsRequestParams params);
}
