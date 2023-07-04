package com.example.apexwh.product;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.example.apexwh.DateStr;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.RequestToServer;
import com.example.apexwh.objects.Outcome;
import com.example.apexwh.objects.Product;
import com.example.apexwh.ui.BundleMethodInterface;
import com.example.apexwh.ui.Dialogs;
import com.example.apexwh.ui.adapters.DataAdapter;
import com.example.apexwh.ui.adapters.ListFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductListFragment extends ListFragment<Product> {

    public ProductListFragment() {

        super(R.layout.fragment_filter_list, R.layout.movers_service_list_item);

        setListUpdater(new ListUpdater() {
            @Override
            public void update(ArrayList items, ProgressBar progressBar, DataAdapter adapter, String filter) {

                //filter = "334942186980041030224284193605956262215";

                if (filter.length() >= 32){

                    RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladRefByNumberValue", "ref=" + filter, new JSONObject(), 1,
                            new RequestToServer.ResponseResultInterface() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    progressBar.setVisibility(View.GONE);

                                    JSONObject responseItem = JsonProcs.getJsonObjectFromJsonObject(response, "ErpSkladRefByNumberValue");

                                    Bundle bundle = new Bundle();
                                    bundle.putString("name", JsonProcs.getStringFromJSON(responseItem, "Имя"));
                                    bundle.putString("ref", JsonProcs.getStringFromJSON(responseItem, "Ссылка"));
                                    bundle.putString("order", JsonProcs.getStringFromJSON(responseItem, "Ордер"));

                                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main)
                                            .navigate(R.id.nav_collectProductsFragment, bundle);

                                }
                            });
                }
                else {

                    items.clear();

                    RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladOutcome", "filter=" + filter, new JSONObject(), 1,
                            new RequestToServer.ResponseResultInterface() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    progressBar.setVisibility(View.GONE);

                                    JSONArray responseItems = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladOutcome");

                                    for (int j = 0; j < responseItems.length(); j++) {

                                        JSONObject objectItem = JsonProcs.getItemJSONArray(responseItems, j);

                                        items.add(Outcome.FromJson(objectItem));

                                    }

                                    adapter.notifyDataSetChanged();
                                }
                            });
                }

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

                getAdapter().setDrawViewHolder(new DataAdapter.DrawViewHolder<Outcome>() {
                    @Override
                    public void draw(DataAdapter.ItemViewHolder holder, Outcome document) {

                        ((TextView) holder.getTextViews().get(0)).setText("№ " + document.number + " от " + DateStr.FromYmdhmsToDmyhms(document.date));
                        ((TextView) holder.getTextViews().get(1)).setText(document.receiver + ", " + document.orderDescription
                                + (document.comment.isEmpty() ? "" : ", " + document.comment) );
                        ((TextView) holder.getTextViews().get(2)).setText(document.status);
                    }
                });

                getAdapter().setOnClickListener(document -> {

                    Outcome curOutcome = ((Outcome) document);

                    Bundle bundle = new Bundle();
                    bundle.putString("name", curOutcome.orderType);
                    bundle.putString("ref", curOutcome.order);
                    bundle.putString("order", "");

                    Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), new BundleMethodInterface() {
                        @Override
                        public void callMethod(Bundle arguments) {

                            Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main)
                                    .navigate(R.id.nav_collectProductsFragment, arguments);

                        }
                    }, bundle, "Начать отбор " + curOutcome.orderDescription + "?", "Начать отбор");

                });

                getAdapter().setOnLongClickListener(document -> {});

            }
        });


    }


}