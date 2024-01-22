package com.adobe.aem.guides.wknd.core.services;

import java.util.List;

public interface ProductPagesGenerator {
    void updateProductPages();
    String getPagesRoot();
    List<String> getPagesLinks();
    List<String> getPagesLinks(ProductPagesListRequest request);
}
