package com.example.apexwh.ui.cell_content;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.navigation.NavController;

import com.android.volley.Request;
import com.example.apexwh.DateStr;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.RequestToServer;
import com.example.apexwh.objects.Movement;
import com.example.apexwh.objects.MoversService;
import com.example.apexwh.objects.Placement;
import com.example.apexwh.objects.Product;
import com.example.apexwh.objects.ProductCell;
import com.example.apexwh.ui.adapters.DataAdapter;
import com.example.apexwh.ui.adapters.ListFragment;
import com.example.apexwh.ui.adapters.ScanListFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.UUID;

public class CellContentListFragment extends ScanListFragment<ProductCell> {

    TextView tvProduct;

    public CellContentListFragment() {

        super(R.layout.fragment_scan_cell_list, R.layout.product_cell_list_item);

        setListUpdater(new ListUpdater() {
            @Override
            public void update(ArrayList items, ProgressBar progressBar, DataAdapter adapter, String filter) {

                items.clear();

                RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladCellContent", "filter=" + filter, new JSONObject(), 1,
                        new RequestToServer.ResponseResultInterface() {
                            @Override
                            public void onResponse(JSONObject response) {

                                progressBar.setVisibility(View.GONE);

                                JSONArray responseItems = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladCellContent");

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

                                adapter.notifyDataSetChanged();
                            }
                        });


            }
        });


        setOnCreateViewElements(new OnCreateViewElements() {
            @Override
            public void execute(View root, NavController navController) {

                tvProduct = root.findViewById(R.id.tvProduct);

                getAdapter().setInitViewsMaker(new DataAdapter.InitViewsMaker() {
                    @Override
                    public void init(View itemView, ArrayList<TextView> textViews) {

                        textViews.add(itemView.findViewById(R.id.tvNumberDate));
                        textViews.add(itemView.findViewById(R.id.tvDescription));
                        textViews.add(itemView.findViewById(R.id.tvStatus));
                    }
                });

                getAdapter().setDrawViewHolder(new DataAdapter.DrawViewHolder<ProductCell>() {
                    @Override
                    public void draw(DataAdapter.ItemViewHolder holder, ProductCell item) {

                        ((TextView) holder.getTextViews().get(0)).setText(item.cell.name);
                        ((TextView) holder.getTextViews().get(1)).setText(item.container.name + " " + item.containerNumber + " шт");
                        ((TextView) holder.getTextViews().get(2)).setText(item.productNumber + " шт (" + item.productUnitNumber + " упак)");
                    }
                });

                getAdapter().setOnClickListener(document -> {});

                getAdapter().setOnLongClickListener(document -> {});



            }
        });


    }


}