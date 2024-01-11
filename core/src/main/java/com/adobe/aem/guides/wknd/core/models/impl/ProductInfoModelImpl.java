package com.adobe.aem.guides.wknd.core.models.impl;

import com.adobe.aem.guides.wknd.core.models.ProductInfoModel;
import com.adobe.granite.asset.api.Asset;
import com.adobe.granite.asset.api.AssetMetadata;
import com.adobe.xmp.core.XMPMetadata;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Model(
        adaptables                  = {SlingHttpServletRequest.class}
    ,   adapters                    = {ProductInfoModel.class}
    ,   resourceType                = ProductInfoModelImpl.RESOURCE_TYPE
    ,   defaultInjectionStrategy    = DefaultInjectionStrategy.OPTIONAL
)
@Slf4j
public class ProductInfoModelImpl implements ProductInfoModel {

    protected static final String RESOURCE_TYPE = "wknd/components/productInfo";

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private String imageUri;

    @PostConstruct
    public void init() {

    }

    @Override
    public List<String> getAssetProperties() {
        List<String> propertyInfoList = new ArrayList<>();
        if (!StringUtils.isBlank(imageUri)) {
            Asset asset = null;
            try {
                ResourceResolver resourceResolver = request.getResourceResolver();
                Resource resource = resourceResolver.getResource(imageUri);
                if (resource != null) {
                    asset = resource.adaptTo(Asset.class);
                }
                if (asset != null) {
                    populateWithProperties(propertyInfoList, asset.adaptTo(ValueMap.class));
//                    Resource contentResource = resource.getChild("jcr:content");
//                    populateWithProperties(propertyInfoList, contentResource.adaptTo(ValueMap.class));
//                    Resource metadataResource = contentResource.getChild("metadata");
//                    populateWithProperties(propertyInfoList, metadataResource.adaptTo(ValueMap.class));
                }
            } catch (Exception e) {
                log.error(String.format("Exception occured while trying to retrieve asset by path %s", imageUri));
            }
        }
        return propertyInfoList;
    }

    private void populateWithProperties(List<String> propertiesList, ValueMap valueMap) {
        valueMap.forEach((k,v) -> propertiesList.add(String.format("%s: %s", k, v)));
    }

}
