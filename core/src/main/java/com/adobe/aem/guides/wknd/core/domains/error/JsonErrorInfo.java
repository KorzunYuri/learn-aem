package com.adobe.aem.guides.wknd.core.domains.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class JsonErrorInfo{
    String message;
    String causeMessage;
}
