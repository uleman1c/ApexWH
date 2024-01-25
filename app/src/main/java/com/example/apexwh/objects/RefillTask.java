package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONObject;

public class RefillTask {

    public SDocument document;
    public Cell cell;
    public int order;

    public RefillTask(SDocument document, Cell cell, int order) {
        this.document = document;
        this.cell = cell;
        this.order = order;
    }

    public static RefillTask FromJson(JSONObject task_item) {

        SDocument document = SDocument.FromJson(task_item);
        Cell cell = Cell.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "cell"));

        int order = JsonProcs.getIntegerFromJSON(task_item, "order");

        return new RefillTask(document, cell, order);


    }

    public static RefillTask FromJsonObject(JSONObject task_item) {

        SDocument document = SDocument.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "document"));
        Cell cell = Cell.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "cell"));

        int order = JsonProcs.getIntegerFromJSON(task_item, "order");

        return new RefillTask(document, cell, order);


    }

    public static JSONObject toJson(RefillTask inventTask) {

        JSONObject jsonObject = new JSONObject();

        JsonProcs.putToJsonObject(jsonObject,"document", SDocument.toJson(inventTask.document));
        JsonProcs.putToJsonObject(jsonObject,"cell", Cell.toJson(inventTask.cell));
        JsonProcs.putToJsonObject(jsonObject,"order", inventTask.order);

        return jsonObject;
    }




}
