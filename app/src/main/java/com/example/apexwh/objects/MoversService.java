package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class MoversService {

    public String ref, number, date, start, finish;
    Integer quantity;
    public ArrayList<String> containers;

    public ArrayList<String> fields;

    //public String[][] f = [[]];

    public MoversService(String ref, String number, String date, String start, String finish, Integer quantity, ArrayList<String> containers) {
        this.ref = ref;
        this.number = number;
        this.date = date;
        this.start = start;
        this.finish = finish;
        this.quantity = quantity;
        this.containers = containers;

        this.fields = new ArrayList<>();

        this.fields.add("ref");
        this.fields.add("number");
        this.fields.add("date");
        this.fields.add("start");
        this.fields.add("finish");
        this.fields.add("quantity");
        this.fields.add("containers");

    }

    private HashMap<String, String> getFieldDescription(String curName){

        HashMap<String, String> field = new HashMap<>();

        if (curName.equals("ref")){

            field.put("type", "string");
            field.put("value", ref);

        } else if (curName.equals("number")){

            field.put("type", "string");
            field.put("value", number);

        } else if (curName.equals("date")){

            field.put("type", "string");
            field.put("value", date);

        } else if (curName.equals("start")){

            field.put("type", "string");
            field.put("value", start);

        } else if (curName.equals("finish")){

            field.put("type", "string");
            field.put("value", finish);

        } else if (curName.equals("quantity")) {

            field.put("type", "integer");
            field.put("value", String.valueOf(quantity));

        } else if (curName.equals("containers")) {

            field.put("type", "array");
            field.put("value", String.valueOf(containers));

        }


        return field;
    }

    public ArrayList<HashMap<String, Object>> getObjectDescription(){

        ArrayList<HashMap<String, Object>> fields = new ArrayList<>();

        for (String curName : this.fields) {

            HashMap field = new HashMap();
            field.put(curName, getFieldDescription(curName));

            fields.add(field);

        }

        return fields;
    }

    public static MoversService MoversServiceFromJson(JSONObject task_item) {

        String ref = JsonProcs.getStringFromJSON(task_item, "ref");
        String number = JsonProcs.getStringFromJSON(task_item, "number");
        String date = JsonProcs.getStringFromJSON(task_item, "date");
        String start = JsonProcs.getStringFromJSON(task_item, "start");
        String finish = JsonProcs.getStringFromJSON(task_item, "finish");
        Integer quantity = JsonProcs.getIntegerFromJSON(task_item, "finish");
        JSONArray ja_containers = JsonProcs.getJsonArrayFromJsonObject(task_item, "containers");

        ArrayList<String> containers = new ArrayList<>();
        for (int i = 0; i < ja_containers.length(); i++) {

            //containers.add(JsonProcs.getStringFromJSON( JsonProcs.getItemJSONArray(ja_containers, i) ));

        }

        date = date.substring(6, 8) + "." + date.substring(4, 6) + "." + date.substring(0, 4);

        return new MoversService(ref, number, date, start, finish, quantity, containers);


    }

}


