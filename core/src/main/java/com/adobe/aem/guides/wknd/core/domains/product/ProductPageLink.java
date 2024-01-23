package com.adobe.aem.guides.wknd.core.domains.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@AllArgsConstructor
@NoArgsConstructor
@Getter
//@XmlRootElement
public class ProductPageLink {
    @XmlElement
    private String title;
    @XmlElement
    private String link;
}
