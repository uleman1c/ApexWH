package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONObject;

public class ProductCellContainerOutcome {

    public Product product;
    public Characteristic characteristic;
    public int number;

    public Cell cell;
    public Container container;
    public int containerNumber;
    public int productNumber;
    public int productUnitNumber;

    public ProductCellContainerOutcome(Product product, Characteristic characteristic, int number, Cell cell,
                                       Container container, int containerNumber, int productNumber, int productUnitNumber) {
        this.product = product;
        this.characteristic = characteristic;
        this.number = number;
        this.cell = cell;
        this.container = container;
        this.containerNumber = containerNumber;
        this.productNumber = productNumber;
        this.productUnitNumber = productUnitNumber;
    }

    public static ProductCellContainerOutcome FromJson(JSONObject task_item) {

        Product product = Product.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "product"));
        Characteristic characteristic = Characteristic.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "characteristic"));
        Cell cell = Cell.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "cell"));
        Container container = Container.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "container"));

        int number = JsonProcs.getIntegerFromJSON(task_item, "number");
        int productNumber = JsonProcs.getIntegerFromJSON(task_item, "productNumber");
        int productUnitNumber = JsonProcs.getIntegerFromJSON(task_item, "productUnitNumber");
        int containerNumber = JsonProcs.getIntegerFromJSON(task_item, "containerNumber");

        return new ProductCellContainerOutcome(product, characteristic, number, cell, container, containerNumber, productNumber, productUnitNumber);


    }

}


