package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONObject;

public class ReturnOfProducts {

    public String ref, number, date, contractor;

    public ReturnOfProducts(String ref, String number, String date, String contractor) {
        this.ref = ref;
        this.number = number;
        this.date = date;
        this.contractor = contractor;
    }

    public static ReturnOfProducts FromJson(JSONObject task_item) {

        String ref = JsonProcs.getStringFromJSON(task_item, "korr");
        String number = JsonProcs.getStringFromJSON(task_item, "number");
        String date = JsonProcs.getStringFromJSON(task_item, "date");
        String contractor = JsonProcs.getStringFromJSON(task_item, "contractor");

        //date = date.substring(6, 8) + "." + date.substring(4, 6) + "." + date.substring(0, 4);

        return new ReturnOfProducts(ref, number, date, contractor);


    }

}