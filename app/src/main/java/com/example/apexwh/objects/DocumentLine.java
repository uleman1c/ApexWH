package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONObject;

public class DocumentLine {

    public String productRef, productName, characterRef, characterName, seriesRef, seriesName, shtrihCode, shtrihCodeAdd;
    public Integer quantity, scanned, lineNumber;


    public DocumentLine(String productRef, String productName, String characterRef, String characterName, String seriesRef, String seriesName,
                        Integer quantity, Integer scanned, String shtrihCode, Integer lineNumber, String shtrihCodeAdd) {
        this.productRef = productRef;
        this.productName = productName;
        this.characterRef = characterRef;
        this.characterName = characterName;
        this.seriesRef = seriesRef;
        this.seriesName = seriesName;
        this.quantity = quantity;
        this.scanned = scanned;
        this.shtrihCode = shtrihCode;
        this.shtrihCodeAdd = shtrihCodeAdd;
        this.lineNumber = lineNumber;
    }

    public static DocumentLine DocumentLineFromJson(JSONObject task_item){

        String productRef = JsonProcs.getStringFromJSON(task_item, "ProductRef");
        String productName = JsonProcs.getStringFromJSON(task_item, "ProductName");
        String characterRef = JsonProcs.getStringFromJSON(task_item, "CharacterRef");
        String characterName = JsonProcs.getStringFromJSON(task_item, "CharacterName");
        String seriesRef = JsonProcs.getStringFromJSON(task_item, "SeriesRef");
        String seriesName = JsonProcs.getStringFromJSON(task_item, "SeriesName");
        Integer quantity = JsonProcs.getIntegerFromJSON(task_item, "Quantity");
        Integer scanned = JsonProcs.getIntegerFromJSON(task_item, "Scanned");
        String shtrihCode = JsonProcs.getStringFromJSON(task_item, "ShtrihCode");
        String shtrihCodeAdd = JsonProcs.getStringFromJSON(task_item, "ShtrihCodeAdd");
        Integer lineNumber = JsonProcs.getIntegerFromJSON(task_item, "LineNumber");

        return new DocumentLine(productRef, productName, characterRef, characterName, seriesRef,  seriesName,
                quantity, scanned, shtrihCode, lineNumber, shtrihCodeAdd);

    }


}
