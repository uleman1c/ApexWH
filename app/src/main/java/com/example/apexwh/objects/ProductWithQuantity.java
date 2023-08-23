package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONObject;

public class ProductWithQuantity {

    public Product product;
    public int quantity, unitQuantity;

    public ProductWithQuantity(Product product, int quantity, int unitQuantity) {
        this.product = product;
        this.quantity = quantity;
        this.unitQuantity = unitQuantity;
    }

    public static ProductWithQuantity FromJson(JSONObject task_item) {

        int quantity = JsonProcs.getIntegerFromJSON(task_item, "quantity");
        int unitQuantity = JsonProcs.getIntegerFromJSON(task_item, "unitQuantity");

        return new ProductWithQuantity(Product.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "product")), quantity, unitQuantity);


    }

}
