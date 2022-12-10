package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONObject;

public class Reference {

    public String ref, description;


    public Reference(String ref, String description) {
        this.ref = ref;
        this.description = description;
    }

    public static Reference ReferenceFromJson(JSONObject task_item) {

        String ref = JsonProcs.getStringFromJSON(task_item, "ref");
        String description = JsonProcs.getStringFromJSON(task_item, "description");

        return new Reference(ref, description);


    }



}
