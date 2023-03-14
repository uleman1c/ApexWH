package com.example.apexwh.ui.returns;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.apexwh.ui.BundleMethodInterface;
import com.example.apexwh.ui.Dialogs;
import com.example.apexwh.ui.adapters.DataAdapter;
import com.example.apexwh.ui.adapters.ListFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

public class ReturnsOfProductsFragment extends ListFragment<ReturnOfProducts> {

    Button btnUpdate;

    public ReturnsOfProductsFragment() {

        super(R.layout.fragment_filter_btn_list, R.layout.outcome_list_item);

        setListUpdater(new ListUpdater() {
            @Override
            public void update(ArrayList items, ProgressBar progressBar, DataAdapter adapter, String filter) {

                items.clear();

                HttpClient httpClient = new HttpClient(getContext());

                httpClient.request_get("/hs/dta/obj?request=getReturns&warehouseId=" + getWarehouseId() + "&filter=" + filter, new HttpRequestJsonObjectInterface() {
                    @Override
                    public void setProgressVisibility(int visibility) {

                        progressBar.setVisibility(visibility);

                    }

                    @Override
                    public void processResponse(JSONObject jsonObjectResponse) {

                        JSONArray jsonArrayResponses = JsonProcs.getJsonArrayFromJsonObject(jsonObjectResponse, "responses");

                        JSONObject jsonObjectItem = JsonProcs.getItemJSONArray(jsonArrayResponses, 0);

                        JSONArray jsonArrayObjects = JsonProcs.getJsonArrayFromJsonObject(jsonObjectItem, "Returns");

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

                        ((TextView) holder.getTextViews().get(0)).setText(document.description);
                        ((TextView) holder.getTextViews().get(1)).setText(document.contractor + ", " + document.incomeNumber + " от " + DateStr.FromYmdhmsToDmy(document.incomeDate));
                        ((TextView) holder.getTextViews().get(2)).setText(document.comment);
                    }
                });

                getAdapter().setOnClickListener(new DataAdapter.OnClickListener<ReturnOfProducts>() {
                    @Override
                    public void onItemClick(ReturnOfProducts document) {

                        JSONObject jsonObject = new JSONObject();

                        JsonProcs.putToJsonObject(jsonObject, "id", UUID.randomUUID().toString());
                        JsonProcs.putToJsonObject(jsonObject, "baseId", document.ref);
                        JsonProcs.putToJsonObject(jsonObject, "baseName", document.name);

                        Bundle bundle = new Bundle();
                        bundle.putString("selected", jsonObject.toString());

                        Dialogs.showReturnOfProductsMenu(getContext(), getActivity(), new BundleMethodInterface() {
                            @Override
                            public void callMethod(Bundle arguments) {

                                JSONObject selected = JsonProcs.getJSONObjectFromString(arguments.getString("selected"));

                                if (arguments.getString("btn").equals("OrderToChangeCharacteristic")){

                                    HttpClient httpClient = new HttpClient(getContext());

                                    httpClient.request_get("/hs/dta/obj?request=getOrderToChangeCharacteristic"
                                            + "&id=" + JsonProcs.getStringFromJSON(selected, "id")
                                            + "&baseId=" + JsonProcs.getStringFromJSON(selected, "baseId")
                                            + "&baseName=" + JsonProcs.getStringFromJSON(selected, "baseName"), new HttpRequestJsonObjectInterface() {
                                        @Override
                                        public void setProgressVisibility(int visibility) {

                                            //progressBar.setVisibility(visibility);

                                        }

                                        @Override
                                        public void processResponse(JSONObject jsonObjectResponse) {

                                            JSONArray jsonArrayResponses = JsonProcs.getJsonArrayFromJsonObject(jsonObjectResponse, "responses");

                                            JSONObject jsonObjectItem = JsonProcs.getItemJSONArray(jsonArrayResponses, 0);

                                            JSONArray jsonArrayObjects = JsonProcs.getJsonArrayFromJsonObject(jsonObjectItem, "OrderToChangeCharacteristic");

                                            JSONObject objectItem = JsonProcs.getItemJSONArray(jsonArrayObjects, 0);

                                            navController.popBackStack();

                                            Bundle bundle1 = new Bundle();
                                            bundle1.putString("ref", JsonProcs.getStringFromJSON(objectItem, "ref"));
                                            bundle1.putString("name", "ЗаявкаНаКорректировкуТоваров");


                                            navController.navigate(R.id.nav_orderToChangeCharacteristicProductsFragment, bundle1);

                                        }

                                    });


                                }

                            }
                        }, bundle, "Выберите", "Меню");

//                        Bundle result = getArguments();
//                        result.putString("selected", jsonObject.toString());
//                        getParentFragmentManager().setFragmentResult("acceptment_order_selected", result);


                    }
                });

                getAdapter().setOnLongClickListener(new DataAdapter.OnLongClickListener<ReturnOfProducts>() {
                    @Override
                    public void onLongItemClick(ReturnOfProducts document) {

                    }
                });

                btnUpdate = root.findViewById(R.id.btnAction);

                btnUpdate.setText("Обновить");

                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        updateList("");

                    }
                });

            }
        });



    }

}