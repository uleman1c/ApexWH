package com.example.apexwh.ui.returns;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.widget.ProgressBar;

import com.example.apexwh.HttpClient;
import com.example.apexwh.HttpRequestInterface;
import com.example.apexwh.HttpRequestJsonObjectInterface;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.objects.DocumentLine;
import com.example.apexwh.ui.adapters.DocumentLineAdapter;
import com.example.apexwh.ui.products.ProductsFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReturnProductsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReturnProductsFragment extends ProductsFragment {


    public ReturnProductsFragment() {

        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListUpdater(new ListUpdater() {
            @Override
            public void update(String name, String ref, ArrayList<DocumentLine> lines, ProgressBar progressBar, DocumentLineAdapter adapter) {

                lines.clear();

                HttpClient httpClient = new HttpClient(getContext());

                httpClient.request_get("/hs/dta/obj?request=getLinesToAccept&name=" + name + "&id=" + ref, new HttpRequestJsonObjectInterface() {

                    @Override
                    public void setProgressVisibility(int visibility) {

                        progressBar.setVisibility(visibility);

                    }

                    @Override
                    public void processResponse(JSONObject jsonObjectResponse) {

                        JSONArray jsonArrayResponses = JsonProcs.getJsonArrayFromJsonObject(jsonObjectResponse, "responses");

                        JSONObject jsonObjectItem = JsonProcs.getItemJSONArray(jsonArrayResponses, 0);

                        JSONArray jsonArrayObjects = JsonProcs.getJsonArrayFromJsonObject(jsonObjectItem, "LinesToAccept");

                        for (int j = 0; j < jsonArrayObjects.length(); j++) {

                            JSONObject objectItem = JsonProcs.getItemJSONArray(jsonArrayObjects, j);

                            lines.add(DocumentLine.DocumentLineFromJson(objectItem));

                        }

                        adapter.notifyDataSetChanged();

                    }

                });


            }
        });




    }

}