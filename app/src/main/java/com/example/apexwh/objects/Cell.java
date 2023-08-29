package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONObject;

public class Cell {

    public String ref, name, line, section, rack, level, position, order;
    public Boolean main;
//    public Cell(String ref, String name) {
//
//        this.ref = ref;
//        this.name = name;
//        this.line = "";
//        this.section = "";
//        this.rack = "";
//        this.level = "";
//        this.position = "";
//
//    }
//
    public Cell(String ref, String name, String line, String section, String rack, String level, String position, String order, Boolean main) {
        this.ref = ref;
        this.name = name;
        this.line = line;
        this.section = section;
        this.rack = rack;
        this.level = level;
        this.position = position;
        this.order = order;
        this.main = main;
    }

    public static Cell FromJson(JSONObject task_item) {

        String ref = JsonProcs.getStringFromJSON(task_item, "ref");
        String name = JsonProcs.getStringFromJSON(task_item, "name");
        String line = JsonProcs.getStringFromJSON(task_item, "line");
        String section = JsonProcs.getStringFromJSON(task_item, "section");
        String rack = JsonProcs.getStringFromJSON(task_item, "rack");
        String level = JsonProcs.getStringFromJSON(task_item, "level");
        String position = JsonProcs.getStringFromJSON(task_item, "position");
        String order = JsonProcs.getStringFromJSON(task_item, "order");
        Boolean main = JsonProcs.getBooleanFromJSON(task_item, "main");

        return new Cell(ref, name, line, section, rack, level, position, order, main);


    }
}
