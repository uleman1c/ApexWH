package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONObject;

public class Movement {

    public String ref, number, date, description, cell, container, cellDestination;


    public Movement(String ref, String number, String date, String description, String cell, String container, String cellDestination) {
        this.ref = ref;
        this.number = number;
        this.date = date;
        this.description = description;
        this.cell = cell;
        this.container = container;
        this.cellDestination = cellDestination;
    }

    public static Movement FromJson(JSONObject task_item) {

        String ref = JsonProcs.getStringFromJSON(task_item, "ref");
        String number = JsonProcs.getStringFromJSON(task_item, "number");
        String date = JsonProcs.getStringFromJSON(task_item, "date");
        String description = JsonProcs.getStringFromJSON(task_item, "description");
        String cell = JsonProcs.getStringFromJSON(task_item, "cell");
        String container = JsonProcs.getStringFromJSON(task_item, "container");
        String cellDestination = JsonProcs.getStringFromJSON(task_item, "cellDestination");

        return new Movement(ref, number, date, description, cell, container, cellDestination);


    }

}


