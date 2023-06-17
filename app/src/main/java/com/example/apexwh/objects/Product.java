package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONObject;

public class Product {

    public String ref, name, artikul;

    public Product(String ref, String name, String artikul) {

        this.ref = ref;
        this.name = name;
        this.artikul = artikul;

    }

    public static Product FromJson(JSONObject task_item) {

        String ref = JsonProcs.getStringFromJSON(task_item, "ref");
        String name = JsonProcs.getStringFromJSON(task_item, "name");
        String artikul = JsonProcs.getStringFromJSON(task_item, "artikul");

        return new Product(ref, name, artikul);


    }

}
