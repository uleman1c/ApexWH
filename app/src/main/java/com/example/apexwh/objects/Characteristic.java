package com.example.apexwh.objects;

import org.json.JSONObject;

public class Characteristic extends Reference {


    public Characteristic(String ref, String description) {
        super(ref, description);
    }

    public Characteristic(Reference reference) {

        super(reference.ref, reference.description);

    }

    public static Characteristic CharacteristicFromJson(JSONObject objectItem) {

        return new Characteristic(Reference.ReferenceFromJson(objectItem));

    }

    public static Characteristic FromJson(JSONObject objectItem) {

        return new Characteristic(Reference.ReferenceFromJson(objectItem));

    }

    public static String getString(Characteristic characteristic){

        return characteristic.description.isEmpty() || characteristic.description.equals("Основная характеристика") ? "" : characteristic.description;

    }


}
