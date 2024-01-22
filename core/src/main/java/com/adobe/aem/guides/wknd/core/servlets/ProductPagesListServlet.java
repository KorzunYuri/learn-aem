package com.adobe.aem.guides.wknd.core.servlets;

import com.adobe.aem.guides.wknd.core.services.ProductPagesGenerator;
import com.adobe.aem.guides.wknd.core.services.access.RunModeProvider;
import com.adobe.aem.guides.wknd.core.services.access.impl.ProductResourceResolverProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;

@Component(
        service = Servlet.class
    ,   property = {
                Constants.SERVICE_DESCRIPTION + "=" + "Product pages list export"
            ,   "sling.servlet.methods" + "=" + HttpConstants.METHOD_POST
            ,   "sling.servlet.paths"   + "=" + "/bin/servlets/wknd/products"
        }
)
@Slf4j
public class ProductPagesListServlet extends SlingSafeMethodsServlet {

    @Reference
    private ProductResourceResolverProvider resourceResolverProvider;

    @Reference
    private ProductPagesGenerator productPagesGenerator;

    @Reference
    private RunModeProvider runModeProvider;

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response) throws ServletException, IOException {
        if (!isProcessingRequired()) {
            throw new IllegalStateException(String.format("Servlet %s is supposed to be used on publish environment only", this.getClass().getName()));
        }
        try (ResourceResolver resourceResolver = resourceResolverProvider.getResourceResolver()) {
            List<String> pagesPaths = productPagesGenerator.getPagesLinks();
            JSONObject result = new JSONObject();
            JSONArray pagesArray = new JSONArray();
            try {
                for (String path : pagesPaths) {
                    pagesArray.put(makePageJson(path, resourceResolver));
                }
                result.put("pages", pagesArray);
            } catch (Exception e) {
                log.error(String.format("Failed to export product pages"));
            }
            response.getWriter().write(result.toString());
        }
    }

    private JSONObject makePageJson(String path, ResourceResolver resourceResolver) throws org.json.JSONException {
        Resource productResource = resourceResolver.getResource(path).getChild("jcr:content").getChild("product");
        ValueMap props = productResource.getValueMap();
        JSONObject page = new JSONObject();
        props.forEach((k, v) -> {
            try {
                page.put(k, v);
            } catch (JSONException e) {
                log.error(String.format("Failed to set page property {\"%s\": \"%s\"} in export", k, v));
            }
        });
        page.put("link", path);
        return page;
    }

    private boolean isProcessingRequired() {
        return this.runModeProvider.isPublish();
    }

}
