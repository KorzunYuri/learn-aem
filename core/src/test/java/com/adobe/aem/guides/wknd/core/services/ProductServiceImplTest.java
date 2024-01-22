package com.adobe.aem.guides.wknd.core.services;

import com.adobe.aem.guides.wknd.core.domains.product.Product;
import com.adobe.aem.guides.wknd.core.services.impl.ProductServiceImpl;
import com.google.gson.JsonSyntaxException;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductServiceImplTest {

    private final AemContext aemContext = new AemContext();
    private final ProductServiceImpl service = new ProductServiceImpl();

    private final String    DEFAULT_VALUE_SERVICE_NAME = "Product service under test";
    private final String    DEFAULT_VALUE_API_URL      = "https://fakestoreapi.com/products";
    private final boolean   DEFAULT_VALUE_USE_FIXED_PRODUCTS_LIMIT = false;
    private final short     DEFAULT_VALUE_FIXED_PRODUCTS_LIMIT = 2;
    private final int       DEFAULT_VALUE_UNUSED_PROPERTY = 1;


    private Map<String, Object> getDefaultServiceParams() {
        Map<String, Object> params = new HashMap<>();
        params.put(ProductServiceImpl.Properties.SERVICE_NAME,              DEFAULT_VALUE_SERVICE_NAME);
        params.put(ProductServiceImpl.Properties.API_URL,                   DEFAULT_VALUE_API_URL);
        params.put(ProductServiceImpl.Properties.USE_FIXED_PRODUCTS_LIMIT,  DEFAULT_VALUE_USE_FIXED_PRODUCTS_LIMIT);
        params.put(ProductServiceImpl.Properties.FIXED_PRODUCTS_LIMIT,      DEFAULT_VALUE_FIXED_PRODUCTS_LIMIT);
        params.put(ProductServiceImpl.Properties.UNUSED,                    DEFAULT_VALUE_UNUSED_PROPERTY);
        return params;
    }

    private void registerService(ProductServiceImpl service, Map<String, Object> params) {
        aemContext.registerInjectActivateService(service, params);
    }

    private ProductsRequestParams getDefaultRequestParams() {
        return ProductsRequestParams.builder()
                .limit(3)
                .shuffle(false)
            .build();
    }

    @BeforeEach
    void setUp() {
        registerService(service, getDefaultServiceParams());
    }

    @Test
    @Order(1)
    void configInjectionTest() {
        System.out.println("running config injection test");
        assertEquals(DEFAULT_VALUE_SERVICE_NAME,                service.getServiceName());
        assertEquals(DEFAULT_VALUE_API_URL,                     service.getURL_API_PRODUCTS_LIST());
        assertEquals(DEFAULT_VALUE_USE_FIXED_PRODUCTS_LIMIT,    service.isUseFixedProductsLimit());
        assertEquals(DEFAULT_VALUE_FIXED_PRODUCTS_LIMIT,        service.getFixedProductsLimit());
        assertEquals(DEFAULT_VALUE_UNUSED_PROPERTY,             service.getUnusedProperty());
    }


    @Test
    @Order(2)
    void getProductsTestWithDefaults() {
        System.out.println("running getProducts test with default params");
        List<Product> emptyList = new ArrayList<>();
        assertEquals(emptyList, service.getProducts(ProductsRequestParams.builder().limit(0).build()));

        assertEquals(1, service.getProducts(ProductsRequestParams.builder().limit(1).build()).size());
        assertEquals(10, service.getProducts(ProductsRequestParams.builder().limit(10).build()).size());
    }

    @Test
    @Order(3)
    void getProductsTestWithCustomServiceParams() {
        System.out.println("running getProducts test with custom params");

        Map<String, Object> serviceParams;
        ProductsRequestParams requestParams = getDefaultRequestParams();
        Exception exception;

        //  test bad URL
        serviceParams = getDefaultServiceParams();
        serviceParams.put(ProductServiceImpl.Properties.API_URL, "https://bad.url.provided");
        registerService(service, serviceParams);
        exception = assertThrows(RuntimeException.class, () -> service.getProducts(requestParams));
        assertEquals(UnknownHostException.class, exception.getCause().getClass());

        //  test bad JSON
        serviceParams = getDefaultServiceParams();
        serviceParams.put(ProductServiceImpl.Properties.API_URL, "https://google.com");
        registerService(service, serviceParams);
        exception = assertThrows(RuntimeException.class, () -> service.getProducts(requestParams));
        assertEquals(JsonSyntaxException.class, exception.getCause().getClass());

        //  test overriding the products limit
        final int fixedProductsLimit = 7;
        serviceParams = getDefaultServiceParams();
        serviceParams.put(ProductServiceImpl.Properties.USE_FIXED_PRODUCTS_LIMIT, true);
        serviceParams.put(ProductServiceImpl.Properties.FIXED_PRODUCTS_LIMIT, fixedProductsLimit);
        registerService(service, serviceParams);
        assertEquals(fixedProductsLimit, service.getProducts(requestParams).size());

    }

}
