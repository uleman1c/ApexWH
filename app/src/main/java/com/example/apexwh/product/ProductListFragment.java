package com.example.apexwh.product;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

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
import com.example.apexwh.ui.shtrihcode_product.ShtrihcodeProductFragment;

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

                    RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladProducts", "filter=" + filter, new JSONObject(), 1,
                            new RequestToServer.ResponseResultInterface() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    progressBar.setVisibility(View.GONE);

                                    JSONArray responseItems = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladProducts");

                                    for (int j = 0; j < responseItems.length(); j++) {

                                        JSONObject objectItem = JsonProcs.getItemJSONArray(responseItems, j);

                                        items.add(Product.FromJson(objectItem));

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

                        textViews.add((TextView) itemView.findViewById(R.id.tvNumberDate));
                        textViews.add((TextView) itemView.findViewById(R.id.tvDescription));
                        textViews.add((TextView) itemView.findViewById(R.id.tvStatus));
                    }
                });

                getAdapter().setDrawViewHolder(new DataAdapter.DrawViewHolder<Product>() {
                    @Override
                    public void draw(DataAdapter.ItemViewHolder holder, Product document) {

                        ((TextView) holder.getTextViews().get(0)).setText("");
                        ((TextView) holder.getTextViews().get(1)).setText(document.artikul);
                        ((TextView) holder.getTextViews().get(2)).setText(document.name);
                    }
                });

                getAdapter().setOnClickListener(document -> {

                    Product product = ((Product) document);

                    Bundle bundle = new Bundle();
                    bundle.putString("ref", product.ref);
                    bundle.putString("name", product.name);
                    bundle.putString("artikul", product.artikul);

                    getParentFragmentManager().setFragmentResult("selectProduct", bundle);

                    NavHostFragment.findNavController(ProductListFragment.this).popBackStack();



                });

                getAdapter().setOnLongClickListener(document -> {});

            }
        });


    }


}