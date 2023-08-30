package com.example.apexwh.ui.acceptment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.example.apexwh.DB;
import com.example.apexwh.DateStr;
import com.example.apexwh.HttpClient;
import com.example.apexwh.HttpRequestInterface;
import com.example.apexwh.HttpRequestJsonObjectInterface;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.RequestToServer;
import com.example.apexwh.SpanText;
import com.example.apexwh.objects.Acceptment;
import com.example.apexwh.objects.BuierOrder;
import com.example.apexwh.objects.Outcome;
import com.example.apexwh.ui.adapters.DataAdapter;
import com.example.apexwh.ui.adapters.ListFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class AcceptmentFragment extends ListFragment<Acceptment> {

    String request_id, request_date, request_result;

    Button btnUpdate;

    public AcceptmentFragment() {

        super(R.layout.fragment_filter_list, R.layout.movers_service_list_item);

        setListUpdater(new ListUpdater() {
            @Override
            public void update(ArrayList items, ProgressBar progressBar, DataAdapter adapter, String filter) {

                int removed = items.size();

                items.clear();

                if (filter.length() >= 32){

                    RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladRefByNumberValue", "ref=" + filter, new JSONObject(), 1,
                            new RequestToServer.ResponseResultInterface() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    progressBar.setVisibility(View.GONE);

                                    JSONObject responseItem = JsonProcs.getJsonObjectFromJsonObject(response, "ErpSkladRefByNumberValue");

                                    String order = JsonProcs.getStringFromJSON(responseItem, "Ордер");

                                    if (!order.isEmpty()) {

                                        Bundle bundle = new Bundle();
                                        bundle.putString("name", JsonProcs.getStringFromJSON(responseItem, "Имя"));
                                        bundle.putString("ref", JsonProcs.getStringFromJSON(responseItem, "Ссылка"));
                                        bundle.putString("order", order);

                                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main)
                                                .navigate(R.id.nav_collectProductsFragment, bundle);

                                    } else {

                                        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                                        builder.setTitle("Внимание")
                                                .setMessage("Документ " + filter + " не найден")
                                                .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        etFilter.setText("");

                                                        dialog.cancel();

                                                    }
                                                }).create().show();
                                    }
                                }
                            });
                }
                else {

                    RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladIncome",
                            "status=collect&filter=" + filter, new JSONObject(), 1,
                            new RequestToServer.ResponseResultInterface() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    progressBar.setVisibility(View.GONE);

                                    JSONArray responseItems = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladIncome");

                                    for (int j = 0; j < responseItems.length(); j++) {

                                        JSONObject objectItem = JsonProcs.getItemJSONArray(responseItems, j);

                                        items.add(Acceptment.FromJson(objectItem));

                                    }

                                    adapter.notifyItemRemoved(removed);

                                    adapter.notifyItemInserted(items.size());

                                    adapter.notifyDataSetChanged();
                                }
                            });
                }




//                HttpClient httpClient = new HttpClient(getContext());
//
//                httpClient.request_get("/hs/dta/obj?request=getIncomeUpr&warehouse=" + getWarehouseId() + "&filter=" + filter, new HttpRequestJsonObjectInterface() {
//                    @Override
//                    public void setProgressVisibility(int visibility) {
//
//                        progressBar.setVisibility(visibility);
//
//                    }
//
//                    @Override
//                    public void processResponse(JSONObject jsonObjectResponse) {
//
//                        JSONArray jsonArrayResponses = JsonProcs.getJsonArrayFromJsonObject(jsonObjectResponse, "responses");
//
//                        JSONObject jsonObjectItem = JsonProcs.getItemJSONArray(jsonArrayResponses, 0);
//
//                        JSONArray jsonArrayObjects = JsonProcs.getJsonArrayFromJsonObject(jsonObjectItem, "IncomeUpr");
//
//                        for (int j = 0; j < jsonArrayObjects.length(); j++) {
//
//                            JSONObject objectItem = JsonProcs.getItemJSONArray(jsonArrayObjects, j);
//
//                            items.add(Acceptment.FromJson(objectItem));
//
//                        }
//
//                        adapter.notifyDataSetChanged();
//
//                    }
//
//                });



            }
        });

        setOnCreateViewElements(new OnCreateViewElements() {
            @Override
            public void execute(View root, NavController navController) {

                getAdapter().setInitViewsMaker(new DataAdapter.InitViewsMaker() {
                    @Override
                    public void init(View itemView, ArrayList<TextView> textViews) {

                        textViews.add((TextView) itemView.findViewById(R.id.tvNumberDate));
                        textViews.add((TextView) itemView.findViewById(R.id.tvDescription));
                        textViews.add((TextView) itemView.findViewById(R.id.tvStatus));
                    }
                });

                getAdapter().setDrawViewHolder(new DataAdapter.DrawViewHolder<Acceptment>() {
                    @Override
                    public void draw(DataAdapter.ItemViewHolder holder, Acceptment document) {

                        String filterString = etFilter.getText().toString();

                        ((TextView) holder.getTextViews().get(0)).setText(SpanText.GetFilteredString(document.description, filterString));
                        ((TextView) holder.getTextViews().get(1)).setText(SpanText.GetFilteredString(document.senderDescription, filterString));
                        ((TextView) holder.getTextViews().get(2)).setText(SpanText.GetFilteredString(document.comment, filterString));
                    }
                });

                getAdapter().setOnClickListener(new DataAdapter.OnClickListener<Acceptment>() {
                    @Override
                    public void onItemClick(Acceptment document) {

                        JSONObject jsonObject = new JSONObject();

                        JsonProcs.putToJsonObject(jsonObject, "ref", document.ref);
                        JsonProcs.putToJsonObject(jsonObject, "name", document.type);

//                        Bundle result = getArguments();
//                        result.putString("selected", jsonObject.toString());
//                        getParentFragmentManager().setFragmentResult("acceptment_order_selected", result);

                        navController.popBackStack();

                    }
                });

                getAdapter().setOnLongClickListener(new DataAdapter.OnLongClickListener<Acceptment>() {
                    @Override
                    public void onLongItemClick(Acceptment document) {

                    }
                });

//                btnUpdate = root.findViewById(R.id.btnAction);

//                setUpdateBtn();

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
//
            }
        });

    }

    private void setUpdateBtn() {
        DB db = new DB(getContext());
        db.open();
        request_id = db.getConstant("UpdateIncomeRequestId");
        request_date = db.getConstant("UpdateIncomeRequestDate");
        request_result = db.getConstant("UpdateIncomeRequestResult");
        db.close();

//        btnUpdate.setText("Обновить" + (request_date == null || request_date.isEmpty() ? "" : " " + DateStr.FromYmdhmsToDmyhms(request_date)));
    }



}