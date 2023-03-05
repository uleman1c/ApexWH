package com.example.apexwh;

import org.json.JSONObject;

public interface HttpRequestJsonObjectInterface {

    void setProgressVisibility(int visibility);
    void processResponse(JSONObject response);

}
