package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONObject;

public class Takement {

    public String ref, number, date, description, cell, container;


    public Takement(String ref, String number, String date, String description, String cell, String container) {
        this.ref = ref;
        this.number = number;
        this.date = date;
        this.description = description;
        this.cell = cell;
        this.container = container;
    }

    public static Takement FromJson(JSONObject task_item) {

        String ref = JsonProcs.getStringFromJSON(task_item, "ref");
        String number = JsonProcs.getStringFromJSON(task_item, "number");
        String date = JsonProcs.getStringFromJSON(task_item, "date");
        String description = JsonProcs.getStringFromJSON(task_item, "description");
        String cell = JsonProcs.getStringFromJSON(task_item, "cell");
        String container = JsonProcs.getStringFromJSON(task_item, "container");

        return new Takement(ref, number, date, description, cell, container);


    }

}


