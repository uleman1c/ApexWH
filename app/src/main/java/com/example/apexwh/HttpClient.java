package com.example.apexwh;

import android.content.Context;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;


public class HttpClient {

    private Context mCtx;
    private AsyncHttpClient client;
    private JSONObject requestParams;

    private String serverUrl = Connections.addr;

    private MultipartEntityBuilder builder;
    private String pathToFile;



    public HttpClient(Context Ctx) {

        Init(Ctx, false);

    }

    private void Init(Context Ctx, Boolean sync){

        client = sync ? new SyncHttpClient() : new AsyncHttpClient();

        mCtx = Ctx;

        requestParams = new JSONObject();

//        DB db = new DB(mCtx);
//
//        db.open();
//
//        String prog_id = db.getConstant("prog_id");
//        if (prog_id == null) {
//
//            prog_id = UUID.randomUUID().toString();
//            db.updateConstant("prog_id", prog_id);
//
//        }
//
//        String base_name = db.getConstant("base_name");
//        String user = db.getConstant("user");
//        String pwd = db.getConstant("pwd");
//
//        db.close();
//
//        addParam("prog_id", prog_id);
//        addParam("version", Ctx.getResources().getString(R.string.version));
//
//        setServerUrl(base_name, mCtx.getString(R.string.hs_wms));


        client.addHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((Connections.user + ":" + Connections.password).getBytes(StandardCharsets.UTF_8)));

    }


    public RequestHandle request_get(final String url, final HttpRequestInterface httpRequestInterface) {

        return client.get(mCtx, serverUrl + url, null, "application/json", new AsyncHttpResponseHandler(){

            @Override
            public void onStart() {
                super.onStart();

                httpRequestInterface.setProgressVisibility(View.VISIBLE);

            }

            @Override
            public void onFinish() {
                super.onFinish();

                httpRequestInterface.setProgressVisibility(View.GONE);

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                httpRequestInterface.processResponse(getResponseString(responseBody));

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showMessageOnFailure(statusCode, headers, responseBody, error);
            }
        });

    }

    public RequestHandle postBinary(Context context, String url, HttpEntity entity, ResponseHandlerInterface responseHandler) {

        return client.post(context, serverUrl + url, entity, entity.getContentType().toString(), responseHandler);

    }

    public void showMessageOnFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

        String message = error.getLocalizedMessage() + ", status code " + String.valueOf(statusCode) + ": " + getResponseString(responseBody);

//                if (debug){

        try {

            Toast toast = Toast.makeText(mCtx, message, Toast.LENGTH_LONG);
            toast.show();
        } catch (Exception e){};


//            }
//                Log.v("HTTPLOG", message);

    }

    public String getResponseString(byte[] responseBody){

        String[] str = new String[1];

        str[0] = null;
        try {
            str[0] = responseBody == null ? "" : new String(responseBody, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return str[0];
    }



}
