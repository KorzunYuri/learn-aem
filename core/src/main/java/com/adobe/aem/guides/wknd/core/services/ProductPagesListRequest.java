package com.adobe.aem.guides.wknd.core.services;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductPagesListRequest {
    private boolean autogeneratedOnly;
    private int limit;
    private int offset;
}
