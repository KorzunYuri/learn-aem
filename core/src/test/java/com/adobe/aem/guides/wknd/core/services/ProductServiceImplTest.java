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
        assertEquals(emptyList, service.getProducts(0));

        assertEquals(1, service.getProducts(1).size());
        assertEquals(10, service.getProducts(10).size());
    }

}
