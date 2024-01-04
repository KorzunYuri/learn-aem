(function(){
    "use strict";

    var selectors = {
        self:      '[data-cmp-is="productlist"]',
        product:   '[data-cmp-is="product"]'
    };

    var productLists = document.querySelectorAll(selectors.self);
    productLists.forEach(function (productList){
        var productIds = Array.from(
            productList.querySelectorAll(selectors.product),
            (product) => product.attributes['data-id'].value
        ).join(',');
        console.log('products: ' + productIds);
    });

}());
