package com.adobe.aem.guides.wknd.core.entities;

/**
 *  Rating of a product fetched from external API
 *  json example:
 *  "rating": {
 *  *       "rate": 3.9,
 *  *       "count": 120
 *  *     }
 */
public class ProductRating {
    private float rate;
    private int count;
}
