package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONObject;

public class ProductCell {

    public Product product;
    public int productNumber;
    public int productUnitNumber;
    public Cell cell;
    public Container container;
    public int containerNumber;

    public Characteristic characteristic;

    public ProductCell(Product product, int productNumber, int productUnitNumber, Cell cell, Container container, int containerNumber) {
        this.product = product;
        this.productNumber = productNumber;
        this.productUnitNumber = productUnitNumber;
        this.cell = cell;
        this.container = container;
        this.containerNumber = containerNumber;
    }

    public ProductCell(Product product, int productNumber, int productUnitNumber, Cell cell, Container container, int containerNumber, Characteristic characteristic) {
        this.product = product;
        this.productNumber = productNumber;
        this.productUnitNumber = productUnitNumber;
        this.cell = cell;
        this.container = container;
        this.containerNumber = containerNumber;
        this.characteristic = characteristic;
    }

    public static ProductCell FromJson(JSONObject task_item) {

        Product product = Product.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "product"));
        Cell cell = Cell.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "cell"));
        Container container = Container.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "container"));

        String characteristicDesc = JsonProcs.getStringFromJSON(task_item,"characteristic");
        Characteristic characteristic = new Characteristic("", characteristicDesc);

        int productNumber = JsonProcs.getIntegerFromJSON(task_item, "productNumber");
        int productUnitNumber = JsonProcs.getIntegerFromJSON(task_item, "productUnitNumber");
        int containerNumber = JsonProcs.getIntegerFromJSON(task_item, "containerNumber");

        return new ProductCell(product, productNumber, productUnitNumber, cell, container, containerNumber, characteristic);


    }

}


