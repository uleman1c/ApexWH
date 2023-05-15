package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DocumentLine {

    public String productRef, productName, productArtikul, characterRef, characterName, seriesRef, seriesName;
    public ArrayList<String> shtrihCodes;
    public Integer quantity, scanned, lineNumber;


    public DocumentLine(String productRef, String productName, String productArtikul, String characterRef, String characterName, String seriesRef, String seriesName,
                        Integer quantity, Integer scanned, ArrayList<String> shtrihCodes, Integer lineNumber) {
        this.productRef = productRef;
        this.productName = productName;
        this.productArtikul = productArtikul;
        this.characterRef = characterRef;
        this.characterName = characterName;
        this.seriesRef = seriesRef;
        this.seriesName = seriesName;
        this.quantity = quantity;
        this.scanned = scanned;
        this.shtrihCodes = shtrihCodes;
        this.lineNumber = lineNumber;
    }

    public static DocumentLine DocumentLineFromJson(JSONObject task_item){

        String productRef = JsonProcs.getStringFromJSON(task_item, "productRef");
        String productName = JsonProcs.getStringFromJSON(task_item, "productName");
        String productArtikul = JsonProcs.getStringFromJSON(task_item, "productArtikul");
        String characterRef = JsonProcs.getStringFromJSON(task_item, "characterRef");
        String characterName = JsonProcs.getStringFromJSON(task_item, "characterName");
        String seriesRef = JsonProcs.getStringFromJSON(task_item, "seriesRef");
        String seriesName = JsonProcs.getStringFromJSON(task_item, "seriesName");
        Integer quantity = JsonProcs.getIntegerFromJSON(task_item, "quantity");
        Integer scanned = JsonProcs.getIntegerFromJSON(task_item, "scanned");

        JSONArray jsShtrihCodes = JsonProcs.getJsonArrayFromJsonObject(task_item, "shtrihCodes");

        ArrayList<String> shtrihCodes = new ArrayList<>();

        for (int j = 0; j < jsShtrihCodes.length(); j++) {

            JSONObject objectItem = JsonProcs.getItemJSONArray(jsShtrihCodes, j);

            shtrihCodes.add(JsonProcs.getStringFromJSON(objectItem, "shtrihCode"));

        }




        Integer lineNumber = JsonProcs.getIntegerFromJSON(task_item, "lineNumber");

        return new DocumentLine(productRef, productName, productArtikul, characterRef, characterName, seriesRef,  seriesName,
                quantity, scanned, shtrihCodes, lineNumber);

    }

    public static DocumentLine DocumentLineFromJsonOTCC(JSONObject task_item){

        String productRef = JsonProcs.getStringFromJSON(task_item, "productRef");
        String productName = JsonProcs.getStringFromJSON(task_item, "productDescription");
        String productArtikul = JsonProcs.getStringFromJSON(task_item, "productArtikul");
        String characterRef = JsonProcs.getStringFromJSON(task_item, "characterRef");
        String characterName = JsonProcs.getStringFromJSON(task_item, "characterDescription");
        String seriesRef = JsonProcs.getStringFromJSON(task_item, "seriesRef");
        String seriesName = JsonProcs.getStringFromJSON(task_item, "seriesName");
        Integer quantity = JsonProcs.getIntegerFromJSON(task_item, "number");
        Integer scanned = JsonProcs.getIntegerFromJSON(task_item, "scanned");

        JSONArray jsShtrihCodes = JsonProcs.getJsonArrayFromJsonObject(task_item, "shtrihCode");

        ArrayList<String> shtrihCodes = new ArrayList<>();

        for (int j = 0; j < jsShtrihCodes.length(); j++) {

            try {
                shtrihCodes.add(jsShtrihCodes.getString(j));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }




        Integer lineNumber = JsonProcs.getIntegerFromJSON(task_item, "lineNumber");

        return new DocumentLine(productRef, productName, productArtikul, characterRef, characterName, seriesRef,  seriesName,
                quantity, scanned, shtrihCodes, lineNumber);

    }


}
