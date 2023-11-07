package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONException;
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

        Characteristic characteristic = Characteristic.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "characteristic"));

        if (characteristic.description.isEmpty()) {

            String characteristicDesc = JsonProcs.getStringFromJSON(task_item, "characteristic");
            characteristic.description = characteristicDesc;
        }

        int productNumber = JsonProcs.getIntegerFromJSON(task_item, "productNumber");
        int productUnitNumber = JsonProcs.getIntegerFromJSON(task_item, "productUnitNumber");
        int containerNumber = JsonProcs.getIntegerFromJSON(task_item, "containerNumber");

        return new ProductCell(product, productNumber, productUnitNumber, cell, container, containerNumber, characteristic);


    }

    public static JSONObject ToJson(ProductCell productCell) throws JSONException {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("cellRef", productCell.cell.ref);
        jsonObject.put("productRef", productCell.product.ref);
        jsonObject.put("characteristicRef", productCell.characteristic.ref);
        jsonObject.put("containerRef", productCell.container.ref);
        jsonObject.put("productNumber", productCell.productNumber);

        return jsonObject;

    }

}


