package com.adobe.aem.guides.wknd.core.services.access.impl;

import com.adobe.aem.guides.wknd.core.services.access.RunModeProvider;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
        service = RunModeProvider.class
)
public class RunModeProviderImpl implements RunModeProvider {

    @Reference
    private SlingSettingsService settingsService;

    @Override
    public boolean isPublish() {
        return isRunMode("publish");
    }

    @Override
    public boolean isAuthor() {
        return isRunMode("author");
    }

    private boolean isRunMode(String runMode) {
        return this.settingsService.getRunModes().contains(runMode);
    }
}
