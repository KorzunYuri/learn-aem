package com.adobe.aem.guides.wknd.core.services.impl;

import com.adobe.aem.guides.wknd.core.domains.product.Product;
import com.adobe.aem.guides.wknd.core.models.ProductList;
import com.adobe.aem.guides.wknd.core.services.ProductService;
import com.adobe.aem.guides.wknd.core.services.ProductsRequestParams;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import java.lang.reflect.Type;
import java.util.*;

@Component(service = ProductService.class)
@Designate(ocd = ProductServiceImpl.ServiceConfig.class)
@Slf4j
@Getter //  the getters are used only to test config injection
public class ProductServiceImpl implements ProductService {

    /*  properties definition  */
    public static class Properties {
        public static final String SERVICE_NAME = "serviceName";
        public static final String API_URL = "apiUrl";
        public static final String USE_FIXED_PRODUCTS_LIMIT = "useFixedProductsLimit";
        public static final String FIXED_PRODUCTS_LIMIT = "fixedProductsLimit";
        public static final String UNUSED = "unused.property";
    }

    @ObjectClassDefinition(
            name = "Product service OSGI configuration"
        ,   description = "Product service OSGI configuration"
    )
    public @interface ServiceConfig {

        @AttributeDefinition(
                name = "Service name"
            ,   description = "The name of the service"
            ,   type = AttributeType.STRING
        )
        public String serviceName() default "Products service";

        @AttributeDefinition(
                name = "products API URL"
            ,   description = "URL of products API"
            ,   type = AttributeType.STRING
        )
        String apiUrl() default "https://fakestoreapi.com/products";

        @AttributeDefinition(
                name = "Use fixed products number limit"
            ,   description = "Check to replace products limit provided by component"
            ,   type = AttributeType.BOOLEAN
        )
        boolean useFixedProductsLimit() default false;

        @AttributeDefinition(
                name = "fixed products number limit"
            ,   description = "fixed limit for products number to be applied instead of provided by component"
            ,   type = AttributeType.SHORT
        )
        short fixedProductsLimit() default ProductList.PRODUCTS_LIMIT_SHOW_ALL;

        @AttributeDefinition(
                name = "unused property"
            ,   description = "Unused property with and ierarchic name to test property injection in the test class"
            ,   type = AttributeType.INTEGER
        )
        int unused_property() default 1;
    }

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    /* actual logic */

    //  the getters here are only to test config injection
    private String serviceName;
    private String URL_API_PRODUCTS_LIST;
    private boolean useFixedProductsLimit;
    private short fixedProductsLimit;
    private int unusedProperty;

    @Activate
    protected void activate(ServiceConfig config) {
        this.URL_API_PRODUCTS_LIST  = config.apiUrl();
        this.serviceName            = config.serviceName();
        this.useFixedProductsLimit  = config.useFixedProductsLimit();
        this.fixedProductsLimit     = config.fixedProductsLimit();
        this.unusedProperty         = config.unused_property();
    }

    //  API doesn't provide the limit
    @Override
    public List<Product> getProducts(ProductsRequestParams params) {
        final int limit = useFixedProductsLimit ? getFixedProductsLimit() : params.getLimit();
        final boolean shuffle = params.isShuffle();
        if (limit == 0) return new ArrayList<>();
        HttpGet getRequest = new HttpGet(URL_API_PRODUCTS_LIST);
        try (
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(getRequest);
        ){
            HttpEntity entity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();
            String body = EntityUtils.toString(entity);
            if (statusCode != 200) {
                throw new RuntimeException(String.format("Failed to fetch data from API: response code: %s, message: %S", statusCode, body));
            }
            //  parse the response
            Gson gson = new Gson();
            Type type = new TypeToken<List<Product>>(){}.getType();
            List<Product> products = gson.fromJson(body, type);
            log.info(String.format("Retrieved %s records", products.size()));
            //  apply requested options
            if (shuffle) Collections.shuffle(products);
            return limit < 0 ? products : products.subList(0, limit);
        } catch (Exception e) {
            log.info(String.format("Failed to retrieve data from API, reason is %s - %s", e.getClass(), e.getMessage()));
            throw new RuntimeException(e);
        }
    }
}
