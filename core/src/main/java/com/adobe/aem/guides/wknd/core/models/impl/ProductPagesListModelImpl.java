package com.adobe.aem.guides.wknd.core.models.impl;

import com.adobe.aem.guides.wknd.core.entities.ProductPageLink;
import com.adobe.aem.guides.wknd.core.models.ProductPagesListModel;
import com.adobe.aem.guides.wknd.core.services.ProductPagesGenerator;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.jcr.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Model(
        adaptables      = {SlingHttpServletRequest.class}
    ,   adapters        = {ProductPagesListModel.class}
    ,   resourceType    = ProductPagesListModelImpl.RESOURCE_TYPE
    ,   defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Slf4j
public class ProductPagesListModelImpl extends ProductListImpl implements ProductPagesListModel{

    protected static final String RESOURCE_TYPE = "wknd/components/productpageslinkslist";

    @Self
    private SlingHttpServletRequest request;

    @OSGiService
    private ProductPagesGenerator productPagesGenerator;

    @OSGiService
    private QueryBuilder queryBuilder;

    @Override
    public List<ProductPageLink> getProductPagesLinks() {
        List<ProductPageLink> result = new ArrayList<>();
        String pagesRoot = productPagesGenerator.getPagesRoot();
        if (StringUtils.isBlank(pagesRoot)) {
            log.error("Product pages root is unknown");
            return result;
        }
        //  make a query
        try {
            Session session = request.getResourceResolver().adaptTo(Session.class);
            Map<String, String> queryParams = new HashMap<>();
            queryParams.put("path", pagesRoot);
            queryParams.put("type", "cq:Page");
            Query query = queryBuilder.createQuery(PredicateGroup.create(queryParams), session);
            SearchResult queryResult = query.getResult();
            List<Hit> hits = queryResult.getHits();
            for (Hit hit : hits) {
                result.add(new ProductPageLink(hit.getTitle(), String.format("%s.html", hit.getPath())));
            }
        } catch (Exception e) {
            log.error(String.format("Failed to fetch product pages from path %s", pagesRoot));
        }
        return result;
    }

}
