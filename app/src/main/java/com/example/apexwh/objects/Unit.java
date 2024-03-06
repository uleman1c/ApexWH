package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Unit {

    public String ref, name;
    public int coefficient;

    public Unit(String ref, String name, int coefficient) {

        this.ref = ref;
        this.name = name;
        this.coefficient = coefficient;

    }

    public static Unit FromJson(JSONObject task_item) {

        String ref = JsonProcs.getStringFromJSON(task_item, "ref");
        String name = JsonProcs.getStringFromJSON(task_item, "name");
        int coefficient = JsonProcs.getIntegerFromJSON(task_item, "coefficient");

        return new Unit(ref, name, coefficient);


    }

    public static JSONObject toJson(Unit document) {

        JSONObject jsonObject = new JSONObject();

        JsonProcs.putToJsonObject(jsonObject,"ref", document.ref);
        JsonProcs.putToJsonObject(jsonObject,"name", document.name);
        JsonProcs.putToJsonObject(jsonObject,"coefficient", document.coefficient);

        return jsonObject;
    }



}
