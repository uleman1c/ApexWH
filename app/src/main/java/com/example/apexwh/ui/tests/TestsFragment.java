package com.example.apexwh.ui.tests;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.apexwh.HttpClient;
import com.example.apexwh.HttpRequestInterface;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.objects.Test;
import com.example.apexwh.ui.adapters.DataAdapter;
import com.example.apexwh.ui.adapters.ListFragment;

import org.json.JSONArray;
import org.json.JSONException;
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

                getParentFragmentManager().setFragmentResultListener("buier_order_selected", getViewLifecycleOwner(), new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {

                        int btnId = bundle.getInt("id");

//                        JSONArray containers = JsonProcs.getJsonArrayFromString(bundle.getString("selected"));
//
//                        JSONObject field = getFieldByViewId(btnId, "btn");
//
//                        try {
//                            field.put("containers", containers);
//                        } catch (JSONException e) {
//                            throw new RuntimeException(e);
//                        }
//
//                        String strContainers = "";
//
//                        for (int i = 0; i < containers.length(); i++) {
//
//                            JSONObject jsonObject = JsonProcs.getItemJSONArray(containers, i);
//
//                            strContainers = strContainers + (strContainers.isEmpty() ? "" : ", ") + JsonProcs.getStringFromJSON(jsonObject, "name");
//
//                        }
//
//                        JsonProcs.putToJsonObject(field,"value", strContainers);
//
//                        View view = inflate.findViewById(JsonProcs.getIntegerFromJSON(field, "input"));
//                        view.setBackgroundColor(Color.parseColor("#FFFFFF"));
//                        ((TextView) view).setText(strContainers);

                    }
                });



            }
        });

    }


}