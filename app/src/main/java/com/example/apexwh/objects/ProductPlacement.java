package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONObject;

public class ProductPlacement {

    public String ref, number, date, description, container, product;
    public int quantity;


    public ProductPlacement(String ref, String number, String date, String description, String container, String product, int quantity) {
        this.ref = ref;
        this.number = number;
        this.date = date;
        this.description = description;
        this.product = product;
        this.container = container;
        this.quantity = quantity;
    }

    public static ProductPlacement FromJson(JSONObject task_item) {

        String ref = JsonProcs.getStringFromJSON(task_item, "ref");
        String number = JsonProcs.getStringFromJSON(task_item, "number");
        String date = JsonProcs.getStringFromJSON(task_item, "date");
        String description = JsonProcs.getStringFromJSON(task_item, "description");
        String product = JsonProcs.getStringFromJSON(task_item, "product");
        String container = JsonProcs.getStringFromJSON(task_item, "container");
        int quantity = JsonProcs.getIntegerFromJSON(task_item, "quantity");

        return new ProductPlacement(ref, number, date, description, container, product, quantity);


    }

}


