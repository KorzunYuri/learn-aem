package com.adobe.aem.guides.wknd.core.jmx;

import com.adobe.aem.guides.wknd.core.services.ProductPagesGenerator;
import com.adobe.aem.guides.wknd.core.services.access.RunModeProvider;
import com.adobe.granite.jmx.annotation.AnnotatedStandardMBean;
import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.management.DynamicMBean;
import javax.management.NotCompliantMBeanException;

@Component(
        service = DynamicMBean.class
    ,   immediate = true
    ,   property = {
                "jmx.objectname = com.adobe.aem.guides.wknd.core.jmx:type=Product page generation faсade"
    }
)
@Slf4j
public class ProductPageGenerationFacadeImpl extends AnnotatedStandardMBean implements ProductPageGenerationFacade {

    @Reference
    private ProductPagesGenerator productPagesGenerator;

    @Reference
    private RunModeProvider runModeProvider;

    public ProductPageGenerationFacadeImpl() throws NotCompliantMBeanException {
        super(ProductPageGenerationFacade.class);
    }

    @Override
    public String regeneratePages() {
        String message = null;
        if (!runModeProvider.isAuthor()) {
            message = "Product page generation is for author instance only";
            log.error(message);
            return message;
        }
        log.info("Product pages generation launched manually");
        productPagesGenerator.updateProductPages();
        return "Product pages generation complete, watch logs for details";
    }
}
