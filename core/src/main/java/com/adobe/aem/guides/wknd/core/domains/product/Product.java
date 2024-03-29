package com.adobe.aem.guides.wknd.core.domains.product;

import lombok.Getter;
import lombok.ToString;

/**
 *  a product fetched from external API
 *  example of product JSON:
 *  "id": 1,
 *     "title": "Fjallraven - Foldsack No. 1 Backpack, Fits 15 Laptops",
 *     "price": 109.95,
 *     "description": "Your perfect pack for everyday use and walks in the forest. Stash your laptop (up to 15 inches) in the padded sleeve, your everyday",
 *     "category": "men's clothing",
 *     "image": "https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg",
 *     "rating": {
 *       "rate": 3.9,
 *       "count": 120
 *     }
 */
@Getter
@ToString(onlyExplicitlyIncluded = true)
public class Product {
    @ToString.Include
    private int id;
    @ToString.Include
    private String title;
    private float price;
    private String description;
    private String category;
    private String image;   //  image URL
    private ProductRating rating;
}
