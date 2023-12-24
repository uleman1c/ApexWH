package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONObject;

public class SDocument {

    public String ref, number, date, description, comment;


    public SDocument(String ref, String number, String date, String description, String comment) {
        this.ref = ref;
        this.number = number;
        this.date = date;
        this.description = description;
        this.comment = comment;
    }

    public static SDocument FromJson(JSONObject task_item) {

        String ref = JsonProcs.getStringFromJSON(task_item, "ref");
        String number = JsonProcs.getStringFromJSON(task_item, "number");
        String date = JsonProcs.getStringFromJSON(task_item, "date");
        String description = JsonProcs.getStringFromJSON(task_item, "description");
        String comment = JsonProcs.getStringFromJSON(task_item, "comment");

        return new SDocument(ref, number, date, description, comment);


    }

    public static JSONObject toJson(SDocument document) {

        JSONObject jsonObject = new JSONObject();

        JsonProcs.putToJsonObject(jsonObject,"ref", document.ref);
        JsonProcs.putToJsonObject(jsonObject,"number", document.number);
        JsonProcs.putToJsonObject(jsonObject,"date", document.date);
        JsonProcs.putToJsonObject(jsonObject,"description", document.description);
        JsonProcs.putToJsonObject(jsonObject,"comment", document.comment);

        return jsonObject;
    }
}


