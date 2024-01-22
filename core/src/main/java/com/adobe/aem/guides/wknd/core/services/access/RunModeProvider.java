package com.adobe.aem.guides.wknd.core.services.access;

public interface RunModeProvider {
    boolean isPublish();
    boolean isAuthor();
}
