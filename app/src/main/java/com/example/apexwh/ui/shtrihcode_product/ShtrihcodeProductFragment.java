package com.example.apexwh.ui.shtrihcode_product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.Request;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.RequestToServer;
import com.example.apexwh.objects.Product;
import com.example.apexwh.objects.ProductCell;
import com.example.apexwh.objects.Shtrihcode;
import com.example.apexwh.ui.BundleMethodInterface;
import com.example.apexwh.ui.Dialogs;
import com.example.apexwh.ui.adapters.DataAdapter;
import com.example.apexwh.ui.adapters.ScanListFragment;
import com.example.apexwh.ui.characteristics.CharacteristicsFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShtrihcodeProductFragment extends ScanListFragment<Shtrihcode> {

    TextView tvProduct;

    Product product;
    Shtrihcode shtrihcode;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        getParentFragmentManager().setFragmentResultListener("selectProduct", this, new FragmentResultListener() {

            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                product = new Product(result.getString("ref"), result.getString("name"), result.getString("artikul"));
                tvProduct.setText(product.artikul + " " + product.name);

                Bundle bundle = new Bundle();
                bundle.putString("product", product.ref);
                bundle.putString("shtrihcode", shtrihcode.value);
                Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), new BundleMethodInterface() {
                    @Override
                    public void callMethod(Bundle arguments) {




                    }
                }, bundle, "Установить " + product.artikul + " " + product.name + " штрихкод " + shtrihcode.value + "?" , "Установка штрихкода");

            }
        });

        return view;

    }

    public ShtrihcodeProductFragment() {

        super(R.layout.fragment_scan_list, R.layout.product_cell_list_item);

        setListUpdater(new ListUpdater() {
            @Override
            public void update(ArrayList items, ProgressBar progressBar, DataAdapter adapter, String filter) {

                items.clear();

                RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladProductCells", "filter=" + filter, new JSONObject(), 1,
                        new RequestToServer.ResponseResultInterface() {
                            @Override
                            public void onResponse(JSONObject response) {

                                progressBar.setVisibility(View.GONE);

                                JSONArray responseItems = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladProductCells");

                                tvProduct.setText(filter + " не найден");

                                for (int j = 0; j < responseItems.length(); j++) {

                                    JSONObject objectItem = JsonProcs.getItemJSONArray(responseItems, j);

                                    Product product = Product.FromJson(JsonProcs.getJsonObjectFromJsonObject(objectItem, "product"));

                                    int productNumber = JsonProcs.getIntegerFromJSON(objectItem, "productNumber");
                                    int productUnitNumber = JsonProcs.getIntegerFromJSON(objectItem, "productUnitNumber");
                                    int containerNumber = JsonProcs.getIntegerFromJSON(objectItem, "containerNumber");

                                    tvProduct.setText(product.artikul + " " + product.name + " " + productNumber + " шт (" + productUnitNumber + " упак) " + containerNumber + " конт");

                                    JSONArray cells = JsonProcs.getJsonArrayFromJsonObject(objectItem, "cells");

                                    for (int k = 0; k < cells.length(); k++) {

                                        ProductCell productCell = ProductCell.FromJson(JsonProcs.getItemJSONArray(cells, k));

                                        items.add(productCell);
                                    }


                                }

                                if (responseItems.length() == 0){

                                    shtrihcode = new Shtrihcode(filter, true);

                                    items.add(shtrihcode);

                                }


                                adapter.notifyDataSetChanged();
                            }
                        });


            }
        });


        setOnCreateViewElements(new OnCreateViewElements() {
            @Override
            public void execute(View root, NavController navController) {

                tvProduct = (TextView) root.findViewById(R.id.tvProduct);

                root.findViewById(R.id.llProduct).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main)
                                .navigate(R.id.nav_productListFragment);

                    }
                });

                getAdapter().setInitViewsMaker(new DataAdapter.InitViewsMaker() {
                    @Override
                    public void init(View itemView, ArrayList<TextView> textViews) {

                        textViews.add((TextView) itemView.findViewById(R.id.tvNumberDate));
                        textViews.add((TextView) itemView.findViewById(R.id.tvDescription));
                        textViews.add((TextView) itemView.findViewById(R.id.tvStatus));
                    }
                });

                getAdapter().setDrawViewHolder(new DataAdapter.DrawViewHolder<Shtrihcode>() {
                    @Override
                    public void draw(DataAdapter.ItemViewHolder holder, Shtrihcode item) {

                        ((TextView) holder.getTextViews().get(0)).setText("Штрихкод");
                        ((TextView) holder.getTextViews().get(1)).setText(item.value);
                        ((TextView) holder.getTextViews().get(2)).setText(item.isNew ? "новый" : "");
                    }
                });

                getAdapter().setOnClickListener(document -> {

                });

                getAdapter().setOnLongClickListener(document -> {});



            }
        });


    }


}