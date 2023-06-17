package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONObject;

public class Cell {

    public String ref, name;
    public Cell(String ref, String name) {

        this.ref = ref;
        this.name = name;

    }

    public static Cell FromJson(JSONObject task_item) {

        String ref = JsonProcs.getStringFromJSON(task_item, "ref");
        String name = JsonProcs.getStringFromJSON(task_item, "name");

        return new Cell(ref, name);


    }
}
