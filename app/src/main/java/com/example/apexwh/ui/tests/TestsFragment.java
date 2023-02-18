package com.example.apexwh.ui.tests;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import org.json.JSONObject;

import java.util.ArrayList;

public class TestsFragment extends ListFragment<Test>{


    public TestsFragment() {

        super(R.layout.fragment_filter_add_list, R.layout.test_list_item);

        setInitViewsMaker(new DataAdapter.InitViewsMaker() {
            @Override
            public void init(View itemView, ArrayList<TextView> textViews) {

                textViews.add(itemView.findViewById(R.id.tvNumberDate));
                textViews.add(itemView.findViewById(R.id.tvDescription));
                textViews.add(itemView.findViewById(R.id.tvStatus));

            }
        });

        setDrawViewHolder(new DataAdapter.DrawViewHolder<Test>() {
            @Override
            public void draw(DataAdapter.ItemViewHolder holder, Test document) {

                ((TextView) holder.getTextViews().get(0)).setText(document.nameStr + " № " + document.number + " от " + document.date);
                ((TextView) holder.getTextViews().get(1)).setText(document.description);

//                if (document.status.isEmpty()){
//
//                    holder.tvStatus.setText("Новый");
//                    holder.tvStatus.setBackgroundColor(Color.parseColor("#ffffff"));
//
//
//                } else if (document.status.equals("closed")){
//
//                    holder.tvStatus.setText("Закрыт");
//                    holder.tvStatus.setBackgroundColor(Color.parseColor("#00ff00"));
//                }



            }
        });

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

                root.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Bundle bundle = new Bundle();

                        navController.navigate(R.id.nav_products, bundle);

                    }
                });

            }
        });

    }
}