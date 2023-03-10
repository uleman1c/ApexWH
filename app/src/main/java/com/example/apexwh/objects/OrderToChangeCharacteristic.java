package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONObject;

public class OrderToChangeCharacteristic {

    public String ref, name, number, date, description, baseIncomeNumber, baseIncomeDate, baseDescription, baseContractor;

    public OrderToChangeCharacteristic(String ref, String name, String number, String date, String description, String baseIncomeNumber, String baseIncomeDate, String baseDescription, String baseContractor) {
        this.ref = ref;
        this.name = name;
        this.number = number;
        this.date = date;
        this.description = description;
        this.baseIncomeNumber = baseIncomeNumber;
        this.baseIncomeDate = baseIncomeDate;
        this.baseDescription = baseDescription;
        this.baseContractor = baseContractor;
    }

    public static OrderToChangeCharacteristic FromJson(JSONObject task_item) {

        String ref = JsonProcs.getStringFromJSON(task_item, "ref");
        String name = JsonProcs.getStringFromJSON(task_item, "name");
        String number = JsonProcs.getStringFromJSON(task_item, "number");
        String date = JsonProcs.getStringFromJSON(task_item, "date");
        String description = JsonProcs.getStringFromJSON(task_item, "description");
        String baseIncomeNumber = JsonProcs.getStringFromJSON(task_item, "baseIncomeNumber");
        String baseIncomeDate = JsonProcs.getStringFromJSON(task_item, "baseIncomeDate");
        String baseDescription = JsonProcs.getStringFromJSON(task_item, "baseDescription");
        String baseContractor = JsonProcs.getStringFromJSON(task_item, "baseContractor");

        return new OrderToChangeCharacteristic(ref, name, number, date, description, baseIncomeNumber, baseIncomeDate, baseDescription, baseContractor);


    }

}