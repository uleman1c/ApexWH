package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONObject;

public class Acceptment {

    public String ref, name, nameStr, number, date, description, status;


    public Acceptment(String ref, String name, String nameStr, String number, String date, String description, String status) {
        this.ref = ref;
        this.name = name;
        this.nameStr = nameStr;
        this.number = number;
        this.date = date;
        this.description = description;
        this.status = status;
    }

    public static Acceptment FromJson(JSONObject task_item) {

        String ref = JsonProcs.getStringFromJSON(task_item, "ref");
        String name = JsonProcs.getStringFromJSON(task_item, "name");
        String nameStr = JsonProcs.getStringFromJSON(task_item, "nameStr");
        String number = JsonProcs.getStringFromJSON(task_item, "number");
        String date = JsonProcs.getStringFromJSON(task_item, "date");
        String description = JsonProcs.getStringFromJSON(task_item, "description");
        String status = JsonProcs.getStringFromJSON(task_item, "status");

        return new Acceptment(ref, name, nameStr, number, date, description, status);


    }

}


