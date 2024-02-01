package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONObject;

import java.util.ArrayList;

public class Container {

    public String ref, name, productionDate;


    public Container(String ref, String name) {
        this.ref = ref;
        this.name = name;
        this.productionDate = "";
    }

    public Container(String ref, String name, String productionDate) {

        this.ref = ref;
        this.name = name;

        this.productionDate = productionDate;
    }

    public static Container FromJson(JSONObject task_item) {

        String ref = JsonProcs.getStringFromJSON(task_item, "ref");
        String name = JsonProcs.getStringFromJSON(task_item, "name");
        String productionDate = JsonProcs.getStringFromJSON(task_item, "productionDate");

        return new Container(ref, name, productionDate);


    }

    public static JSONObject toJson(Container container) {

        JSONObject jsonObject = new JSONObject();

        JsonProcs.putToJsonObject(jsonObject,"ref", container.ref);
        JsonProcs.putToJsonObject(jsonObject,"name", container.name);
        JsonProcs.putToJsonObject(jsonObject,"productionDate", container.productionDate);

        return jsonObject;
    }


    public static ArrayList<Container> getTestArray(){

        ArrayList<Container> containers = new ArrayList<>();

        containers.add(new Container("4be208b8-460f-4f83-9741-25809c007bef", "dsfsfd3414134"));
        containers.add(new Container("fa22f696-12ec-49e2-b630-570c822aaced", "sgtehrxshse245"));
        containers.add(new Container("1dab51b0-bc46-4a46-b9d1-bf277532e3dd", "tfgnfdt134124124"));

        return containers;
    }

}


