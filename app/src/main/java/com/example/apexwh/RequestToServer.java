package com.example.apexwh;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class RequestToServer {

    public interface TypeOfResponse {
        int JsonObject = 0;
        int JsonObjectWithArray = 1;
    }
     public interface ResponseResultInterface{

        void onResponse(JSONObject response);

    }

    public static void execute(Context context, int method, String url, JSONObject params, ResponseResultInterface responseResultInterface){

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                responseResultInterface.onResponse(response);

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        };

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, params, listener, errorListener){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return Connections.headers();

            };


        };
        Volley.newRequestQueue(context).add(jsonObjectRequest);


    }
    public static void executeRequest(Context context, int method, String request, String url, JSONObject params, ResponseResultInterface responseResultInterface){

         execute(context, method, Connections.addrDta + "?request=" + request + "&" + url, params, new ResponseResultInterface() {
             @Override
             public void onResponse(JSONObject response) {

                 if (DefaultJson.getBoolean(response, "success", false)) {

                     JSONArray responses = JsonProcs.getJsonArrayFromJsonObject(response, "responses");

                     if (responses.length() > 0) {

                         JSONObject response0 = JsonProcs.getItemJSONArray(responses, 0);

                         JSONObject responseR = JsonProcs.getJsonObjectFromJsonObject(response0, request.substring(3));

                         responseResultInterface.onResponse(responseR);

                     }
                 }

             }
         });


    }
    public static void executeRequestUW(Context context, int method, String request, String url, JSONObject params, int type, ResponseResultInterface responseResultInterface){

        DB db = new DB(context);
        db.open();
        String appId = db.getConstant("appId");
        String userId = db.getConstant("userId");
        String warehouseId = db.getConstant("warehouseId");
        db.close();

        String urlToSend = "?request=" + request + "&appId=" + appId + "&userId=" + userId + "&warehouseId=" + warehouseId + "&" + url;

        execute(context, method, Connections.addrDta + urlToSend, params, new ResponseResultInterface() {
            @Override
            public void onResponse(JSONObject response) {

                if (DefaultJson.getBoolean(response, "success", false)) {

                    JSONArray responses = JsonProcs.getJsonArrayFromJsonObject(response, "responses");

                    if (responses.length() > 0) {

                        JSONObject response0 = JsonProcs.getItemJSONArray(responses, 0);

                        if (type == TypeOfResponse.JsonObject){

                            JSONObject responseR = JsonProcs.getJsonObjectFromJsonObject(response0, request.substring(3));

                            responseResultInterface.onResponse(responseR);

                        } else {

                            responseResultInterface.onResponse(response0);

                        }

                    }
                }

            }
        });


    }

    public static void executeRequestBodyUW(Context context, int method, String request, JSONObject parameters, int type, ResponseResultInterface responseResultInterface){

        DB db = new DB(context);
        db.open();
        String appId = db.getConstant("appId");
        String userId = db.getConstant("userId");
        String warehouseId = db.getConstant("warehouseId");

        JsonProcs.putToJsonObject(parameters, "appId", appId);
        JsonProcs.putToJsonObject(parameters, "userId", userId);
        JsonProcs.putToJsonObject(parameters, "warehouseId", warehouseId);

        db.close();

        JSONObject jsonObject = new JSONObject();
        JsonProcs.putToJsonObject(jsonObject,"request", request);
        JsonProcs.putToJsonObject(jsonObject,"parameters", parameters);

        execute(context, method, Connections.addrDta, jsonObject, new ResponseResultInterface() {
            @Override
            public void onResponse(JSONObject response) {

                if (DefaultJson.getBoolean(response, "success", false)) {

                    JSONArray responses = JsonProcs.getJsonArrayFromJsonObject(response, "responses");

                    if (responses.length() > 0) {

                        JSONObject response0 = JsonProcs.getItemJSONArray(responses, 0);

                        if (type == TypeOfResponse.JsonObject){

                            JSONObject responseR = JsonProcs.getJsonObjectFromJsonObject(response0, request.substring(3));

                            responseResultInterface.onResponse(responseR);

                        } else {

                            responseResultInterface.onResponse(response0);

                        }

                    }
                }

            }
        });


    }

    public static byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }

    public static void uploadBitmap(Context context, String url, final Bitmap bitmap, ResponseResultInterface responseResultInterface) {

        VolleyRawRequest volleyMultipartRequest = new VolleyRawRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {


                        try {

                            JSONObject obj = new JSONObject(new String(response.data));
                            responseResultInterface.onResponse(obj);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("GotError",""+error.getMessage());
                    }
                }) {


            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("image", new DataPart(imagename + ".jpg", getFileDataFromDrawable(bitmap)));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Connections.headers();
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(context).add(volleyMultipartRequest);
    }

}
