package com.example.apexwh.ui.orders_to_change_characteristics;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.apexwh.HttpClient;
import com.example.apexwh.HttpRequestJsonObjectInterface;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.objects.DocumentLine;
import com.example.apexwh.ui.adapters.DocumentLineAdapter;
import com.example.apexwh.ui.products.ProductsFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderToChangeCharacteristicProductsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderToChangeCharacteristicProductsFragment extends ProductsFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListUpdater(new ListUpdater() {
            @Override
            public void update(String name, String ref, ArrayList<DocumentLine> lines, ProgressBar progressBar, DocumentLineAdapter adapter) {

                lines.clear();

                HttpClient httpClient = new HttpClient(getContext());

                httpClient.request_get("/hs/dta/obj?request=getOrderToChangeCharacteristic&name=" + name + "&id=" + ref, new HttpRequestJsonObjectInterface() {

                    @Override
                    public void setProgressVisibility(int visibility) {

                        progressBar.setVisibility(visibility);

                    }

                    @Override
                    public void processResponse(JSONObject jsonObjectResponse) {

                        JSONArray jsonArrayResponses = JsonProcs.getJsonArrayFromJsonObject(jsonObjectResponse, "responses");

                        JSONObject jsonObjectItem = JsonProcs.getItemJSONArray(jsonArrayResponses, 0);

                        JSONArray jsonArrayObjects = JsonProcs.getJsonArrayFromJsonObject(jsonObjectItem, "OrderToChangeCharacteristic");

                        JSONObject objectItem = JsonProcs.getItemJSONArray(jsonArrayObjects, 0);

                        JSONArray products = JsonProcs.getJsonArrayFromJsonObject(objectItem, "products");

                        for (int j = 0; j < products.length(); j++) {

                            JSONObject productItem = JsonProcs.getItemJSONArray(products, j);

                            lines.add(DocumentLine.DocumentLineFromJson(productItem));

                        }

                        adapter.notifyDataSetChanged();

                    }

                });


            }
        });



    }

}