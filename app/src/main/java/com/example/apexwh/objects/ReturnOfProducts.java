package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONObject;

public class ReturnOfProducts {

    public String ref, name, number, date, contractor, incomeNumber, incomeDate, description, comment;

    public ReturnOfProducts(String ref, String name, String number, String date, String contractor, String incomeNumber, String incomeDate, String description, String comment) {
        this.ref = ref;
        this.name = name;
        this.number = number;
        this.date = date;
        this.contractor = contractor;
        this.incomeNumber = incomeNumber;
        this.incomeDate = incomeDate;
        this.description = description;
        this.comment = comment;
    }

    public static ReturnOfProducts FromJson(JSONObject task_item) {

        String ref = JsonProcs.getStringFromJSON(task_item, "ref");
        String name = JsonProcs.getStringFromJSON(task_item, "name");
        String number = JsonProcs.getStringFromJSON(task_item, "number");
        String date = JsonProcs.getStringFromJSON(task_item, "date");
        String contractor = JsonProcs.getStringFromJSON(task_item, "contractor");
        String incomeNumber = JsonProcs.getStringFromJSON(task_item, "incomeNumber");
        String incomeDate = JsonProcs.getStringFromJSON(task_item, "incomeDate");
        String description = JsonProcs.getStringFromJSON(task_item, "description");
        String comment = JsonProcs.getStringFromJSON(task_item, "comment");

        return new ReturnOfProducts(ref, name, number, date, contractor, incomeNumber, incomeDate, description, comment);


    }

}