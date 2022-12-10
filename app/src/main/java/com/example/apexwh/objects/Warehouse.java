package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONObject;

public class Warehouse extends Reference {

    public Warehouse(String ref, String description) {

        super(ref, description);

    }

    public Warehouse(Reference reference) {

        super(reference.ref, reference.description);

    }

    public static Warehouse WarehouseFromJson(JSONObject objectItem) {

        return new Warehouse(Reference.ReferenceFromJson(objectItem));

    }


}
