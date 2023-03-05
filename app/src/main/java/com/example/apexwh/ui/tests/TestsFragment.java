package com.example.apexwh.ui.tests;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavController;

import com.example.apexwh.HttpClient;
import com.example.apexwh.HttpRequestInterface;
import com.example.apexwh.HttpRequestJsonObjectInterface;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.objects.Test;
import com.example.apexwh.ui.adapters.DataAdapter;
import com.example.apexwh.ui.adapters.ListFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TestsFragment extends ListFragment<Test>{


    public TestsFragment() {

        super(R.layout.fragment_filter_add_list, R.layout.test_list_item);

        setListUpdater(new ListUpdater() {
            @Override
            public void update(ArrayList items, ProgressBar progressBar, DataAdapter adapter, String filter) {

                items.clear();

                HttpClient httpClient = new HttpClient(getContext());

                httpClient.request_get("/hs/dta/obj?request=getTests&warehouseId=" + getWarehouseId() + "&filter=" + filter, new HttpRequestInterface() {
                    @Override
                    public void setProgressVisibility(int visibility) {

                        progressBar.setVisibility(visibility);

                    }

                    @Override
                    public void processResponse(String response) {

                        JSONObject jsonObjectResponse = JsonProcs.getJSONObjectFromString(response);

                        if (JsonProcs.getBooleanFromJSON(jsonObjectResponse, "success")){

                            JSONArray jsonArrayResponses = JsonProcs.getJsonArrayFromJsonObject(jsonObjectResponse, "responses");

                            JSONObject jsonObjectItem = JsonProcs.getItemJSONArray(jsonArrayResponses, 0);

                            JSONArray jsonArrayObjects = JsonProcs.getJsonArrayFromJsonObject(jsonObjectItem, "Tests");

                            for (int j = 0; j < jsonArrayObjects.length(); j++) {

                                JSONObject objectItem = JsonProcs.getItemJSONArray(jsonArrayObjects, j);

                                items.add(Test.TestFromJson(objectItem));

                            }

                            adapter.notifyDataSetChanged();

                        }

                    }

                });



            }
        });

        setOnCreateViewElements(new OnCreateViewElements() {
            @Override
            public void execute(View root, NavController navController) {

                getAdapter().setInitViewsMaker(new DataAdapter.InitViewsMaker() {
                    @Override
                    public void init(View itemView, ArrayList<TextView> textViews) {

                        textViews.add(itemView.findViewById(R.id.tvNumberDate));
                        textViews.add(itemView.findViewById(R.id.tvDescription));
                        textViews.add(itemView.findViewById(R.id.tvStatus));
                    }
                });

                getAdapter().setDrawViewHolder(new DataAdapter.DrawViewHolder<Test>() {
                    @Override
                    public void draw(DataAdapter.ItemViewHolder holder, Test document) {

                        ((TextView) holder.getTextViews().get(0)).setText(document.nameStr + " № " + document.number + " от " + document.date);
                        ((TextView) holder.getTextViews().get(1)).setText(document.description);
                    }
                });

                root.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Bundle bundle = new Bundle();

                        navController.navigate(R.id.nav_BuierOrdersFragment, bundle);

                    }
                });

                getAdapter().setOnClickListener(new DataAdapter.OnClickListener<Test>() {
                    @Override
                    public void onItemClick(Test document) {

                        startTest(navController, document.ref);

                    }
                });

                getParentFragmentManager().setFragmentResultListener("buier_order_selected", getViewLifecycleOwner(), new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {

                        JSONObject jsonObject = JsonProcs.getJSONObjectFromString(bundle.getString("selected"));

                        HttpClient httpClient = new HttpClient(getContext());

                        httpClient.request_get("/hs/dta/obj?request=setTest&type=" + JsonProcs.getStringFromJSON(jsonObject, "name")
                                + "&ref=" + JsonProcs.getStringFromJSON(jsonObject, "ref")
                                + "&warehouseId=" + getWarehouseId(), new HttpRequestJsonObjectInterface() {
                            @Override
                            public void setProgressVisibility(int visibility) {

                            }

                            @Override
                            public void processResponse(JSONObject jsonObjectResponse) {

                                JSONArray jsonArrayResponses = JsonProcs.getJsonArrayFromJsonObject(jsonObjectResponse, "responses");

                                JSONObject jsonObjectItem = JsonProcs.getItemJSONArray(jsonArrayResponses, 0);

                                String ref = JsonProcs.getStringFromJSON(jsonObjectItem, "Test");

                                startTest(navController, ref);

                            }
                        });


                    }
                });



            }
        });

    }

    private void startTest(NavController navController, String ref) {

        Bundle bundle = new Bundle();
        bundle.putString("ref", ref);
        bundle.putString("name", "ПроверкаДокумента");
        bundle.putString("description", "Проверка документа");

        navController.navigate(R.id.nav_testProductsFragment, bundle);


    }




}