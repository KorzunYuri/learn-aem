package com.adobe.aem.guides.wknd.core.services;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class ProductsRequestParams {
    public static final int LIMIT_UNLIMITED = -1;
    private int limit;
    private boolean shuffle;
}
