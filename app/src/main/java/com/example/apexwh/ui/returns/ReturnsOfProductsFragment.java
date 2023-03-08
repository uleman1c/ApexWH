package com.example.apexwh.ui.returns;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.apexwh.DB;
import com.example.apexwh.DateStr;
import com.example.apexwh.HttpClient;
import com.example.apexwh.HttpRequestJsonObjectInterface;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.objects.Acceptment;
import com.example.apexwh.objects.Return;
import com.example.apexwh.objects.ReturnOfProducts;
import com.example.apexwh.ui.adapters.DataAdapter;
import com.example.apexwh.ui.adapters.ListFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReturnsOfProductsFragment extends ListFragment<ReturnOfProducts> {


    public ReturnsOfProductsFragment() {

        super(R.layout.fragment_filter_btn_list, R.layout.outcome_list_item);

        setListUpdater(new ListUpdater() {
            @Override
            public void update(ArrayList items, ProgressBar progressBar, DataAdapter adapter, String filter) {

                items.clear();

                HttpClient httpClient = new HttpClient(getContext());

                httpClient.request_get("/hs/dta/obj?request=getIncomeUpr&warehouse=" + getWarehouseId() + "&filter=" + filter, new HttpRequestJsonObjectInterface() {
                    @Override
                    public void setProgressVisibility(int visibility) {

                        progressBar.setVisibility(visibility);

                    }

                    @Override
                    public void processResponse(JSONObject jsonObjectResponse) {

                        JSONArray jsonArrayResponses = JsonProcs.getJsonArrayFromJsonObject(jsonObjectResponse, "responses");

                        JSONObject jsonObjectItem = JsonProcs.getItemJSONArray(jsonArrayResponses, 0);

                        JSONArray jsonArrayObjects = JsonProcs.getJsonArrayFromJsonObject(jsonObjectItem, "IncomeUpr");

                        for (int j = 0; j < jsonArrayObjects.length(); j++) {

                            JSONObject objectItem = JsonProcs.getItemJSONArray(jsonArrayObjects, j);

                            items.add(ReturnOfProducts.FromJson(objectItem));

                        }

                        adapter.notifyDataSetChanged();

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
                        textViews.add(itemView.findViewById(R.id.tvComment));
                    }
                });

                getAdapter().setDrawViewHolder(new DataAdapter.DrawViewHolder<ReturnOfProducts>() {
                    @Override
                    public void draw(DataAdapter.ItemViewHolder holder, ReturnOfProducts document) {

                        ((TextView) holder.getTextViews().get(0)).setText(document.number);
                        ((TextView) holder.getTextViews().get(1)).setText(document.contractor);
                        ((TextView) holder.getTextViews().get(2)).setText(document.date);
                    }
                });

                getAdapter().setOnClickListener(new DataAdapter.OnClickListener<ReturnOfProducts>() {
                    @Override
                    public void onItemClick(ReturnOfProducts document) {

                        JSONObject jsonObject = new JSONObject();

                        JsonProcs.putToJsonObject(jsonObject, "ref", document.ref);
                        JsonProcs.putToJsonObject(jsonObject, "name", document.number);

//                        Bundle result = getArguments();
//                        result.putString("selected", jsonObject.toString());
//                        getParentFragmentManager().setFragmentResult("acceptment_order_selected", result);

                        navController.popBackStack();

                    }
                });

                getAdapter().setOnLongClickListener(new DataAdapter.OnLongClickListener<ReturnOfProducts>() {
                    @Override
                    public void onLongItemClick(ReturnOfProducts document) {

                    }
                });

//                btnUpdate = root.findViewById(R.id.btnAction);
//
//                setUpdateBtn();
//
//                btnUpdate.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        if (request_result == null || request_result.isEmpty()){
//
//                            HttpClient httpClient = new HttpClient(getContext());
//                            httpClient.addParam("warehouse", getWarehouseId());
//
//                            httpClient.post( "/hs/dta/obj", "getIncomeFromUpr", new HttpRequestJsonObjectInterface() {
//
//                                @Override
//                                public void setProgressVisibility(int visibility) {
//
//                                }
//
//                                @Override
//                                public void processResponse(JSONObject jsonObjectResponse) {
//
//                                    request_id = JsonProcs.getStringFromJSON(jsonObjectResponse, "requestid");
//                                    request_result = "start";
//                                    request_date = DateStr.NowYmdhms();
//
//                                    DB db = new DB(getContext());
//                                    db.open();
//                                    db.updateConstant("UpdateIncomeRequestId", request_id);
//                                    db.updateConstant("UpdateIncomeRequestDate", request_date);
//                                    db.updateConstant("UpdateIncomeRequestResult", request_result);
//                                    db.close();
//
//                                    setUpdateBtn();
//
//                                }
//
//                            });
//
//
//                            //navController.navigate(R.id.nav_BuierOrdersFragment, bundle);
//
//
//                        }
//
//                        else if (request_result.equals("start")){
//
//                            HttpClient httpClient = new HttpClient(getContext());
//                            httpClient.addParam("requestid", request_id);
//
//                            httpClient.request_get("/hs/dta/obj", "getRequestExecuted", new HttpRequestJsonObjectInterface() {
//                                @Override
//                                public void setProgressVisibility(int visibility) {
//
//                                }
//
//                                @Override
//                                public void processResponse(JSONObject jsonObjectResponse) {
//
//                                    JSONArray jsonArrayResponses = JsonProcs.getJsonArrayFromJsonObject(jsonObjectResponse, "responses");
//
//                                    JSONObject jsonObjectItem = JsonProcs.getItemJSONArray(jsonArrayResponses, 0);
//
//                                    if (JsonProcs.getBooleanFromJSON(jsonObjectItem, "RequestExecuted")) {
//
//                                        request_result = "";
//                                        request_date = "";
//
//                                        DB db = new DB(getContext());
//                                        db.open();
//                                        db.updateConstant("UpdateIncomeRequestResult", request_result);
//                                        db.updateConstant("UpdateIncomeRequestDate", request_date);
//                                        db.close();
//
//                                        setUpdateBtn();
//
//                                        updateList("");
//
//                                    }
//
//                                }
//                            });
//
//
//                        }
//
//                    }
//                });

            }
        });



    }

}