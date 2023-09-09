package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONObject;

public class Ttn {

    public String ref, name, description, number, date, car, attach, comment;

    public Ttn(String ref, String name, String description, String number, String date, String car, String attach, String comment) {
        this.ref = ref;
        this.name = name;
        this.description = description;
        this.number = number;
        this.date = date;
        this.car = car;
        this.attach = attach;
        this.comment = comment;
    }

    public static Ttn FromJson(JSONObject task_item) {

        String ref = JsonProcs.getStringFromJSON(task_item, "ref");
        String name = JsonProcs.getStringFromJSON(task_item, "name");
        String description = JsonProcs.getStringFromJSON(task_item, "description");
        String number = JsonProcs.getStringFromJSON(task_item, "number");
        String date = JsonProcs.getStringFromJSON(task_item, "date");
        String car = JsonProcs.getStringFromJSON(task_item, "car");
        String attach = JsonProcs.getStringFromJSON(task_item, "attach");
        String comment = JsonProcs.getStringFromJSON(task_item, "comment");

        return new Ttn(ref, name, description, number, date, car, attach, comment);


    }

}


