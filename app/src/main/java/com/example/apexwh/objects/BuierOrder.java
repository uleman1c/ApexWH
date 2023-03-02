package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONObject;

public class BuierOrder {

    public String ref, name, nameStr, number, date, description, status;


    public BuierOrder(String ref, String name, String nameStr, String number, String date, String description, String status) {
        this.ref = ref;
        this.name = name;
        this.nameStr = nameStr;
        this.number = number;
        this.date = date;
        this.description = description;
        this.status = status;
    }

    public static BuierOrder FromJson(JSONObject task_item) {

        String ref = JsonProcs.getStringFromJSON(task_item, "ref");
        String name = JsonProcs.getStringFromJSON(task_item, "name");
        String nameStr = JsonProcs.getStringFromJSON(task_item, "nameStr");
        String number = JsonProcs.getStringFromJSON(task_item, "number");
        String date = JsonProcs.getStringFromJSON(task_item, "date");
        String description = JsonProcs.getStringFromJSON(task_item, "description");
        String status = JsonProcs.getStringFromJSON(task_item, "status");

        date = date.substring(6, 8) + "." + date.substring(4, 6) + "." + date.substring(0, 4);

        return new BuierOrder(ref, name, nameStr, number, date, description, status);


    }

}


