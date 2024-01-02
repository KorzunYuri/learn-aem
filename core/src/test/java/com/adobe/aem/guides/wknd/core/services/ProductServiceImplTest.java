package com.adobe.aem.guides.wknd.core.services;

import com.adobe.aem.guides.wknd.core.entities.Product;
import com.adobe.aem.guides.wknd.core.services.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductServiceImplTest {

    @Test
    void getProductsTest() {
        ProductServiceImpl service = new ProductServiceImpl();
        List<Product> emptyList = new ArrayList<>();
        assertEquals(emptyList, service.getProducts(ProductsRequestParams.builder().limit(0).build()));

        assertEquals(1, service.getProducts(ProductsRequestParams.builder().limit(1).build()).size());
        assertEquals(10, service.getProducts(ProductsRequestParams.builder().limit(10).build()).size());
    }

}
