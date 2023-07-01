package com.example.apexwh.objects;

import android.widget.Button;

import com.example.apexwh.JsonProcs;

import org.json.JSONObject;

public class MenuItem {

    public String ref, name, navigation;
    public Boolean isGroup;
    public int order;

    public Button button;

    public MenuItem(String ref, String name, String navigation, Boolean isGroup, int order) {
        this.ref = ref;
        this.name = name;
        this.navigation = navigation;
        this.isGroup = isGroup;
        this.order = order;
    }

    public static MenuItem FromJson(JSONObject task_item){

        String ref = JsonProcs.getStringFromJSON(task_item, "ref");
        String name = JsonProcs.getStringFromJSON(task_item, "name");
        String navigation = JsonProcs.getStringFromJSON(task_item, "navigation");
        Boolean isGroup = JsonProcs.getBooleanFromJSON(task_item, "isGroup");
        int order = JsonProcs.getIntegerFromJSON(task_item, "order");

        return new MenuItem(ref, name, navigation, isGroup, order);


    }

}
