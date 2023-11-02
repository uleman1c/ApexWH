package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONObject;

public class Inventarization {

    public String ref, number, date, comment;
    public Cell cell;
    public Container container;
    public Product product;
    public Characteristic characteristic;


    public int quantity;

    public Inventarization(String ref, String number, String date, String comment, Cell cell, Container container, Product product, Characteristic characteristic, int quantity) {
        this.ref = ref;
        this.number = number;
        this.date = date;
        this.comment = comment;
        this.cell = cell;
        this.container = container;
        this.product = product;
        this.characteristic = characteristic;
        this.quantity = quantity;
    }

    public static Inventarization FromJson(JSONObject task_item) {

        String ref = JsonProcs.getStringFromJSON(task_item, "ref");
        String number = JsonProcs.getStringFromJSON(task_item, "number");
        String date = JsonProcs.getStringFromJSON(task_item, "date");
        String comment = JsonProcs.getStringFromJSON(task_item, "comment");

        Cell cell = Cell.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "cell"));

        Container container = Container.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "container"));

        Product product = Product.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "product"));
        Characteristic characteristic = Characteristic.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "characteristic"));

        int quantity = JsonProcs.getIntegerFromJSON(task_item, "quantity");

        return new Inventarization(ref, number, date, comment, cell, container, product, characteristic, quantity);


    }





}
