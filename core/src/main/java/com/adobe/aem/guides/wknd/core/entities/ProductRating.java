package com.adobe.aem.guides.wknd.core.entities;

import lombok.Getter;

/**
 *  Rating of a product fetched from external API
 *  json example:
 *  "rating": {
 *  *       "rate": 3.9,
 *  *       "count": 120
 *  *     }
 */
@Getter
public class ProductRating {
    private float rate;
    private int count;
}
