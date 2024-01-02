package com.adobe.aem.guides.wknd.core.services;

import com.adobe.aem.guides.wknd.core.entities.Product;
import com.adobe.aem.guides.wknd.core.services.impl.ProductServiceImpl;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductServiceImplTest {

    private final AemContext aemContext = new AemContext();
    private final ProductServiceImpl service = new ProductServiceImpl();

    private final String    DEFAULT_VALUE_SERVICE_NAME = "Product service under test";
    private final String    DEFAULT_VALUE_API_URL      = "https://fakestoreapi.com/products";
    private final int       DEFAULT_VALUE_UNUSED_PROPERTY = 1;

    @BeforeEach
    void setUp() {
        Map<String, Object> params = new HashMap<>();
        params.put(ProductServiceImpl.PROPERTY_NAME_API_URL,        DEFAULT_VALUE_API_URL);
        params.put(ProductServiceImpl.PROPERTY_NAME_SERVICE_NAME,   DEFAULT_VALUE_SERVICE_NAME);
        params.put(ProductServiceImpl.PROPERTY_NAME_UNUSED,         DEFAULT_VALUE_UNUSED_PROPERTY);
        aemContext.registerInjectActivateService(service, params);
    }

    @Test
    @Order(1)
    void configInjectionTest() {
        System.out.println("running config injection test");
        assertEquals(DEFAULT_VALUE_SERVICE_NAME,    service.getServiceName());
        assertEquals(DEFAULT_VALUE_API_URL,         service.getURL_API_PRODUCTS_LIST());
        assertEquals(DEFAULT_VALUE_UNUSED_PROPERTY, service.getUnusedProperty());
    }


    @Test
    @Order(2)
    void getProductsTest() {
        System.out.println("running getProducts test");
        List<Product> emptyList = new ArrayList<>();
        assertEquals(emptyList, service.getProducts(ProductsRequestParams.builder().limit(0).build()));

        assertEquals(1, service.getProducts(ProductsRequestParams.builder().limit(1).build()).size());
        assertEquals(10, service.getProducts(ProductsRequestParams.builder().limit(10).build()).size());
    }

}
