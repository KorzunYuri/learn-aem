package com.adobe.aem.guides.wknd.core.caconfigs;

import org.apache.sling.caconfig.annotation.Configuration;
import org.apache.sling.caconfig.annotation.Property;

@Configuration(
        label = "region/country-aware configuration"
    ,   description = "country code, support email etc. depending on the country"
)
public @interface RegionalConfig {

    @Property(
            label = "Country code"
        ,   description = "Unified country code to be used in country-wise personalization"
    )
    String country_code();

    @Property(
            label = "Support email"
        ,   description = "Tech support email provided to users"
    )
    String supportEmail();

}
