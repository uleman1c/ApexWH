package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrderToChangeCharactericticLine {

    public String productRef, productDescription, characterRef, characterDescription, newCharacterRef, newCharacterDescription;
    public ArrayList<String> shtrihCodes;
    public Integer number, scanned, lineNumber;


    public OrderToChangeCharactericticLine(String productRef, String productDescription, String characterRef, String characterDescription,
                                           Integer number, Integer scanned, ArrayList<String> shtrihCodes, Integer lineNumber,
                                           String newCharacterRef, String newCharacterDescription) {
        this.productRef = productRef;
        this.productDescription = productDescription;
        this.characterRef = characterRef;
        this.characterDescription = characterDescription;
        this.number = number;
        this.scanned = scanned;
        this.shtrihCodes = shtrihCodes;
        this.lineNumber = lineNumber;
        this.newCharacterRef = newCharacterRef;
        this.newCharacterDescription = newCharacterDescription;
    }

    public static OrderToChangeCharactericticLine FromJson(JSONObject task_item){

        String productRef = JsonProcs.getStringFromJSON(task_item, "productRef");
        String productDescription = JsonProcs.getStringFromJSON(task_item, "productDescription");
        String characterRef = JsonProcs.getStringFromJSON(task_item, "characterRef");
        String characterDescription = JsonProcs.getStringFromJSON(task_item, "characterDescription");
        Integer number = JsonProcs.getIntegerFromJSON(task_item, "number");
        Integer scanned = JsonProcs.getIntegerFromJSON(task_item, "scanned");
        String newCharacterRef = JsonProcs.getStringFromJSON(task_item, "newCharacterRef");
        String newCharacterDescription = JsonProcs.getStringFromJSON(task_item, "newCharacterDescription");

        JSONArray jsShtrihCodes = JsonProcs.getJsonArrayFromJsonObject(task_item, "shtrihCodes");

        ArrayList<String> shtrihCodes = new ArrayList<>();

        for (int j = 0; j < jsShtrihCodes.length(); j++) {

            JSONObject objectItem = JsonProcs.getItemJSONArray(jsShtrihCodes, j);

            shtrihCodes.add(JsonProcs.getStringFromJSON(objectItem, "shtrihCode"));

        }




        Integer lineNumber = JsonProcs.getIntegerFromJSON(task_item, "lineNumber");

        return new OrderToChangeCharactericticLine(productRef, productDescription, characterRef, characterDescription,
                number, scanned, shtrihCodes, lineNumber, newCharacterRef, newCharacterDescription);

    }


}
