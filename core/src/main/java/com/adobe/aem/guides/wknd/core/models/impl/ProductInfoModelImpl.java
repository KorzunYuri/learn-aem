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
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

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
    public String getAssetInfo() {
        String assetInfo = "no asset info";
        if (!StringUtils.isBlank(imageUri)) {
            Asset asset = null;
            try {
                ResourceResolver resourceResolver = request.getResourceResolver();
                Resource resource = resourceResolver.getResource(imageUri);
                if (resource != null) {
                    asset = resource.adaptTo(Asset.class);
                }
                if (asset != null) {
                    AssetMetadata assetMetadata = asset.getAssetMetadata();
                    XMPMetadata xmp = assetMetadata.getXMP();
                    StringBuilder sb = new StringBuilder();
                    sb.append(xmp.getAboutURI()).append("\n");
                    xmp.spliterator().forEachRemaining(xmpNode -> {
                        sb.append(xmpNode.getName()).append("\n");
                    });
                    assetInfo = sb.toString();
                    log.info(String.format("asset info: %s", assetInfo));
                }
            } catch (Exception e) {
                log.error(String.format("Exception occured while trying to retrieve asset by path %s", imageUri));
            }
        }
        return assetInfo;
    }

}
