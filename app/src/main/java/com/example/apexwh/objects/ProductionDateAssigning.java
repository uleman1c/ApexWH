package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONObject;

public class ProductionDateAssigning {

    public SDocument document;
    public Container container;

    public ProductionDateAssigning(SDocument document, Container container) {
        this.document = document;
        this.container = container;
    }

    public static ProductionDateAssigning FromJson(JSONObject task_item) {

        SDocument document = SDocument.FromJson(task_item);
        Container container = Container.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "container"));

        return new ProductionDateAssigning(document, container);


    }

    public static ProductionDateAssigning FromJsonObject(JSONObject task_item) {

        SDocument document = SDocument.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "document"));
        Container container = Container.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "container"));

        return new ProductionDateAssigning(document, container);


    }

    public static JSONObject toJson(ProductionDateAssigning inventTask) {

        JSONObject jsonObject = new JSONObject();

        JsonProcs.putToJsonObject(jsonObject,"document", SDocument.toJson(inventTask.document));
        JsonProcs.putToJsonObject(jsonObject,"container", Container.toJson(inventTask.container));

        return jsonObject;
    }




}
