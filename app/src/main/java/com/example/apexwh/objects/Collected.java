package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Collected {

    public String date, cell, container, product, author, type, characteristic;
    public int quantity, unitQuantity;

    public Collected(String date, String cell, String container, String product, String author, int quantity, int unitQuantity, String type, String characteristic) {
        this.date = date;
        this.cell = cell;
        this.container = container;
        this.product = product;
        this.author = author;
        this.quantity = quantity;
        this.unitQuantity = unitQuantity;
        this.type = type;
        this.characteristic = characteristic;
    }

    public static Collected FromJson(JSONObject item) {

        return new Collected(
                JsonProcs.getStringFromJSON(item, "Дата"),
                JsonProcs.getStringFromJSON(item, "Ячейка"),
                JsonProcs.getStringFromJSON(item, "Контейнер"),
                JsonProcs.getStringFromJSON(item, "Номенклатура"),
                JsonProcs.getStringFromJSON(item, "СкладскойСотрудник"),
                JsonProcs.getIntegerFromJSON(item, "Количество"),
                JsonProcs.getIntegerFromJSON(item, "КоличествоУпаковок"),
                JsonProcs.getStringFromJSON(item, "Тип"),
                JsonProcs.getStringFromJSON(item, "Характеристика")
        );

    }

}
