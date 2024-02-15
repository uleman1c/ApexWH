package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONObject;

public class RefillTask {

    public SDocument document, takement, placement;
    public Cell cell, source;
    public Product product;
    public Characteristic characteristic;
    public int order;

    public RefillTask(SDocument document,Cell cell, Cell source,  SDocument takement, SDocument placement,
                      Product product, Characteristic characteristic, int order) {
        this.document = document;
        this.cell = cell;
        this.source = source;
        this.takement = takement;
        this.placement = placement;
        this.product = product;
        this.characteristic = characteristic;
        this.order = order;
    }

    public static RefillTask FromJson(JSONObject task_item) {

        SDocument document = SDocument.FromJson(task_item);
        Cell cell = Cell.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "cell"));
        Cell source = Cell.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "source"));
        SDocument takement = SDocument.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "takement"));
        SDocument placement = SDocument.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "placement"));
        Product product = Product.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "product"));
        Characteristic characteristic = Characteristic.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "characteristic"));

        int order = JsonProcs.getIntegerFromJSON(task_item, "order");

        return new RefillTask(document, cell, source, takement, placement, product, characteristic, order);


    }

    public static RefillTask FromJsonObject(JSONObject task_item) {

        SDocument document = SDocument.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "document"));
        Cell cell = Cell.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "cell"));
        Cell source = Cell.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "source"));
        SDocument takement = SDocument.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "takement"));
        SDocument placement = SDocument.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "placement"));
        Product product = Product.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "product"));
        Characteristic characteristic = Characteristic.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "characteristic"));

        int order = JsonProcs.getIntegerFromJSON(task_item, "order");

        return new RefillTask(document, cell, source, takement, placement, product, characteristic, order);


    }

    public static JSONObject toJson(RefillTask inventTask) {

        JSONObject jsonObject = new JSONObject();

        JsonProcs.putToJsonObject(jsonObject,"document", SDocument.toJson(inventTask.document));
        JsonProcs.putToJsonObject(jsonObject,"cell", Cell.toJson(inventTask.cell));
        JsonProcs.putToJsonObject(jsonObject,"source", Cell.toJson(inventTask.source));
        JsonProcs.putToJsonObject(jsonObject,"takement", SDocument.toJson(inventTask.takement));
        JsonProcs.putToJsonObject(jsonObject,"placement", SDocument.toJson(inventTask.placement));
        JsonProcs.putToJsonObject(jsonObject,"product", Product.toJson(inventTask.product));
        JsonProcs.putToJsonObject(jsonObject,"characteristic", Characteristic.toJson(inventTask.characteristic));
        JsonProcs.putToJsonObject(jsonObject,"order", inventTask.order);

        return jsonObject;
    }




}
