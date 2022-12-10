package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONObject;

public class Document {

    public String ref, name, number, date, description;


    public Document(String ref, String name, String number, String date, String description) {
        this.ref = ref;
        this.name = name;
        this.number = number;
        this.date = date;
        this.description = description;
    }

    public static Document DocumentFromJson(JSONObject task_item) {

        String ref = JsonProcs.getStringFromJSON(task_item, "ref");
        String name = JsonProcs.getStringFromJSON(task_item, "name");
        String number = JsonProcs.getStringFromJSON(task_item, "number");
        String date = JsonProcs.getStringFromJSON(task_item, "date");
        String description = JsonProcs.getStringFromJSON(task_item, "description");

        date = date.substring(6, 8) + "." + date.substring(4, 6) + "." + date.substring(0, 4);

        return new Document(ref, name, number, date, description);


    }

}


