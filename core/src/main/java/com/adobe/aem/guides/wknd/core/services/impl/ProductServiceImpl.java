package com.adobe.aem.guides.wknd.core.services.impl;

import com.adobe.aem.guides.wknd.core.entities.Product;
import com.adobe.aem.guides.wknd.core.services.ProductService;
import com.adobe.aem.guides.wknd.core.services.ProductsRequestParams;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.osgi.service.component.annotations.Component;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

@Component(service = ProductService.class)
public class ProductServiceImpl implements ProductService {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private static final String URL_API_PRODUCTS_LIST = "https://fakestoreapi.com/products";

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
