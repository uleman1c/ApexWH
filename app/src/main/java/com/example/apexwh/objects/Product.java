package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Product {

    public String ref, name, artikul, weight;
    public int strong;

    public ArrayList<String> shtrihCodes;

    public Product(String ref, String name, String artikul) {

        this.ref = ref;
        this.name = name;
        this.artikul = artikul;
        this.strong = 0;
        this.weight = "000000";

        this.shtrihCodes = new ArrayList<>();

    }

    public Product(String ref, String name, String artikul, int strong, String weight) {

        this.ref = ref;
        this.name = name;
        this.artikul = artikul;
        this.strong = strong;
        this.weight = weight;

        this.shtrihCodes = new ArrayList<>();

    }
    public Product(String ref, String name, String artikul, int strong, String weight, ArrayList shtrihCodes) {

        this.ref = ref;
        this.name = name;
        this.artikul = artikul;
        this.strong = strong;
        this.weight = weight;
        this.shtrihCodes = shtrihCodes;

    }

    public static Product FromJson(JSONObject task_item) {

        String ref = JsonProcs.getStringFromJSON(task_item, "ref");
        String name = JsonProcs.getStringFromJSON(task_item, "name");
        String artikul = JsonProcs.getStringFromJSON(task_item, "artikul");
        int strong = JsonProcs.getIntegerFromJSON(task_item, "strong");
        String weight = JsonProcs.getStringFromJSON(task_item, "weight");

        return new Product(ref, name, artikul, strong, weight);


    }

    public static Product FromJsonWS(JSONObject task_item) {

        String ref = JsonProcs.getStringFromJSON(task_item, "ref");
        String name = JsonProcs.getStringFromJSON(task_item, "name");
        String artikul = JsonProcs.getStringFromJSON(task_item, "artikul");
        int strong = JsonProcs.getIntegerFromJSON(task_item, "strong");
        String weight = JsonProcs.getStringFromJSON(task_item, "weight");

        ArrayList<String> shtrihCodes = new ArrayList<>();

        JSONArray jsonArray = JsonProcs.getJsonArrayFromJsonObject(task_item, "shtrihCodes");

        for (int i = 0; i < jsonArray.length(); i++) {

            try {
                shtrihCodes.add(jsonArray.get(i).toString());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }

        return new Product(ref, name, artikul, strong, weight, shtrihCodes);


    }

}
