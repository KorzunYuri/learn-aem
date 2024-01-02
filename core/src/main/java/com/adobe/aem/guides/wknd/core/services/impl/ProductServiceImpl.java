package com.adobe.aem.guides.wknd.core.services.impl;

import com.adobe.aem.guides.wknd.core.entities.Product;
import com.adobe.aem.guides.wknd.core.services.ProductService;
import com.adobe.aem.guides.wknd.core.services.ProductsRequestParams;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

@Component(service = ProductService.class)
@Designate(ocd = ProductServiceImpl.ServiceConfig.class)
public class ProductServiceImpl implements ProductService {

    /*  properties definition  */

    public final static String PROPERTY_NAME_SERVICE_NAME = "serviceName";
    public final static String PROPERTY_NAME_API_URL = "apiUrl";
    public final static String PROPERTY_NAME_UNUSED = "unused.property";

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
                name = "unused property"
            ,   description = "Unused property with and ierarchic name to test property injection in the test class"
            ,   type = AttributeType.INTEGER
        )
        int unused_property() default 1;
    }

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    /* actual logic */

    //  the getters here are only to test config injection
    @Getter
    private String serviceName;
    @Getter
    private String URL_API_PRODUCTS_LIST;
    @Getter
    private int unusedProperty;

    @Activate
    protected void activate(ServiceConfig config) {
        this.URL_API_PRODUCTS_LIST  = config.apiUrl();
        this.serviceName            = config.serviceName();
        this.unusedProperty         = config.unused_property();
    }

    //  API doesn't provide the limit
    @Override
    public List<Product> getProducts(ProductsRequestParams params) {
        final int limit = params.getLimit();
        final boolean shuffle = params.isShuffle();
        if (limit == 0) return new ArrayList<>();
        HttpGet getRequest = new HttpGet(URL_API_PRODUCTS_LIST);
        try (
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(getRequest);
        ){
            HttpEntity entity = response.getEntity();
            String json = EntityUtils.toString(entity);
            //  parse the response
            Gson gson = new Gson();
            Type type = new TypeToken<List<Product>>(){}.getType();
            List<Product> products = gson.fromJson(json, type);
            logger.info(String.format("Retrieved %s records", products.size()));
            //  apply requested options
            if (shuffle) Collections.shuffle(products);
            return limit < 0 ? products : products.subList(0, limit);
        } catch (IOException e) {
            logger.info("Failed to retrieve data from API");
            throw new RuntimeException(e);
        }
    }

}
