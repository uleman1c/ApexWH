package com.example.apexwh;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonProcs {

    public static JSONArray getJSONArrayFromString(String responseString){

        JSONArray readerArray = new JSONArray();
        try {
            readerArray = new JSONArray(responseString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return readerArray;
    }

    public static JSONObject getJSONObjectFromString(String responseString){

        JSONObject readerArray = new JSONObject();
        try {
            readerArray = new JSONObject(responseString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return readerArray;
    }

    public static JSONArray getJsonArrayFromJsonObject(JSONObject readerArray, String name) {
        JSONArray response = new JSONArray();
        try {
            response = (JSONArray) readerArray.get(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static JSONObject getJsonObjectFromJsonObject(JSONObject readerArray, String name) {
        JSONObject response = new JSONObject();
        try {
            response = (JSONObject) readerArray.get(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static JSONObject getItemJSONArray(JSONArray response, Integer i){

        JSONObject response_item = new JSONObject();
        try {
            response_item = (JSONObject) response.get(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return response_item;

    }

    public static String getStringFromJSON(JSONObject accept_item, String field_name) {
        String date = "";
        try {
            date = accept_item.getString(field_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Integer getIntegerFromJSON(JSONObject accept_item, String field_name) {

        Integer date = 0;
        try {
            date = accept_item.getInt(field_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Double getDoubleFromJSON(JSONObject accept_item, String field_name) {

        Double date = 0.;
        try {
            date = accept_item.getDouble(field_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Long getLongFromJSON(JSONObject accept_item, String field_name) {

        Long date = Long.valueOf(0);
        try {
            date = accept_item.getLong(field_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Boolean getBooleanFromJSON(JSONObject accept_item, String field_name) {

        Boolean date = false;
        try {
            date = accept_item.getBoolean(field_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static void putToJsonObject(JSONObject jsonObject, String key, String value){

        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public static void putToJsonObject(JSONObject jsonObject, String key, int value){

        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public static JSONArray getJsonArrayFromString(String key){

        JSONArray jsonArray = null;

        try {
            jsonArray = new JSONArray(key);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return jsonArray;

    }


    public static void putToJsonObject(JSONObject jsonObject, String parameters, JSONObject parameters1) {

        try {
            jsonObject.put(parameters, parameters1);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
}
