package com.example.apexwh;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DefaultJson {

    public static String getString(JSONObject jsonObject, String key, String def){

        String result = def;

        try {
            result = jsonObject.getString(key);
        } catch (JSONException e) {
            //throw new RuntimeException(e);
        }

        return result;

    }

    public static Boolean getBoolean(JSONObject jsonObject, String key, Boolean def){

        Boolean result = def;

        try {
            result = !jsonObject.getString(key).equals("0");
        } catch (JSONException e) {
            //throw new RuntimeException(e);
        }

        return result;

    }

    public static void put(JSONObject jsonObject, String key, String value){

        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            //throw new RuntimeException(e);
        }

    }

    public static void put(JSONObject jsonObject, String key, int value){

        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            //throw new RuntimeException(e);
        }

    }

    public static void put(JSONObject jsonObject, String key, ArrayList<String> value) {

        JSONArray jsonArray = new JSONArray();



        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            //throw new RuntimeException(e);
        }

    }

    public static void put(JSONObject jsonObject, String key, boolean value) {

        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            //throw new RuntimeException(e);
        }



    }
}
