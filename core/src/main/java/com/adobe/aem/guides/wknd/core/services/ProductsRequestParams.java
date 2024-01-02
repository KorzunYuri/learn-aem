package com.adobe.aem.guides.wknd.core.services;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class ProductsRequestParams {
    private int limit;
    private boolean shuffle;
}
