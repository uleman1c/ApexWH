package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONObject;

public class ProductionDateAssigning {

    public SDocument document;
    public Cell cell;
    public int order;

    public ProductionDateAssigning(SDocument document, Cell cell, int order) {
        this.document = document;
        this.cell = cell;
        this.order = order;
    }

    public static ProductionDateAssigning FromJson(JSONObject task_item) {

        SDocument document = SDocument.FromJson(task_item);
        Cell cell = Cell.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "cell"));

        int order = JsonProcs.getIntegerFromJSON(task_item, "order");

        return new ProductionDateAssigning(document, cell, order);


    }

    public static ProductionDateAssigning FromJsonObject(JSONObject task_item) {

        SDocument document = SDocument.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "document"));
        Cell cell = Cell.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "cell"));

        int order = JsonProcs.getIntegerFromJSON(task_item, "order");

        return new ProductionDateAssigning(document, cell, order);


    }

    public static JSONObject toJson(ProductionDateAssigning inventTask) {

        JSONObject jsonObject = new JSONObject();

        JsonProcs.putToJsonObject(jsonObject,"document", SDocument.toJson(inventTask.document));
        JsonProcs.putToJsonObject(jsonObject,"cell", Cell.toJson(inventTask.cell));
        JsonProcs.putToJsonObject(jsonObject,"order", inventTask.order);

        return jsonObject;
    }




}
