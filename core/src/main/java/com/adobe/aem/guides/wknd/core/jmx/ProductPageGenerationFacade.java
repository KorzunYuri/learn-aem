package com.adobe.aem.guides.wknd.core.jmx;

import com.adobe.granite.jmx.annotation.Description;

public interface ProductPageGenerationFacade {

    @Description("Regenerate product pages")
    void regeneratePages();

}
