package com.adobe.aem.guides.wknd.core.models.exporters;

import lombok.extern.slf4j.Slf4j;
import org.apache.sling.models.export.spi.ModelExporter;
import org.apache.sling.models.factory.ExportException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.Map;

@Component(
        service = ModelExporter.class
)
@Slf4j
public class JaxbModelExporter implements ModelExporter {

    public static final String EXPORTER_NAME = "jaxb";


    @Nullable
    @Override
    public <T> T export(@NotNull Object model, @NotNull Class<T> aClass, @NotNull Map<String, String> map) throws ExportException {
        StringWriter stringWriter = new StringWriter();
        try {
            JAXBContext context = JAXBContext.newInstance(model.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.marshal(model, stringWriter);
        } catch (JAXBException e) {
            log.error(String.format("Failed to export model %s: %s, cause: %s", model.getClass(), e.getMessage(), e.getCause().getMessage()));
        }
        String resultStr = stringWriter.toString();
        return (T) resultStr;
    }

    @NotNull
    @Override
    public String getName() {
        return EXPORTER_NAME;
    }

    @Override
    public boolean isSupported(@NotNull Class<?> aClass) {
        return true;
    }
}
