package com.example.apexwh.ui.collects;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.android.volley.Request;
import com.example.apexwh.HttpClient;
import com.example.apexwh.HttpRequestJsonObjectInterface;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.RequestToServer;
import com.example.apexwh.objects.Cell;
import com.example.apexwh.objects.DocumentLine;
import com.example.apexwh.objects.ProductCell;
import com.example.apexwh.objects.ProductCellContainerOutcome;
import com.example.apexwh.ui.BundleMethodInterface;
import com.example.apexwh.ui.Dialogs;
import com.example.apexwh.ui.adapters.DataAdapter;
import com.example.apexwh.ui.adapters.DocumentLineAdapter;
import com.example.apexwh.ui.adapters.ListFragment;
import com.example.apexwh.ui.adapters.ScanListFragment;
import com.example.apexwh.ui.adapters.ScanProductsFragment;
import com.example.apexwh.ui.products.ProductsFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class CollectProductsFragment extends ScanListFragment<ProductCell> {

    String name, ref, order;

    TextView tvProduct;
    Cell cell;

    ArrayList<ProductCellContainerOutcome> productCellContainerOutcomes;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        name = getArguments().getString("name");
        ref = getArguments().getString("ref");
        order = getArguments().getString("order");

        productCellContainerOutcomes = new ArrayList<>();

    }

    public CollectProductsFragment() {

        super(R.layout.fragment_scan_cell_list_clear, R.layout.product_cell_list_item);

        setListUpdater(new ListFragment.ListUpdater() {
            @Override
            public void update(ArrayList items, ProgressBar progressBar, DataAdapter adapter, String filter) {

                items.clear();

                productCellContainerOutcomes.clear();

                RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladProductsToOutcome",
                        "name=" + name + "&ref=" + ref, new JSONObject(), 1,
                        new RequestToServer.ResponseResultInterface() {
                            @Override
                            public void onResponse(JSONObject response) {

                                progressBar.setVisibility(View.GONE);

                                JSONArray responseItems = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladProductsToOutcome");

                                for (int j = 0; j < responseItems.length(); j++) {

                                    JSONObject objectItem = JsonProcs.getItemJSONArray(responseItems, j);

                                    productCellContainerOutcomes.add(ProductCellContainerOutcome.FromJson(objectItem));

//                                    cell = Cell.FromJson(JsonProcs.getJsonObjectFromJsonObject(objectItem, "cell"));
//
//                                    int productNumber = JsonProcs.getIntegerFromJSON(objectItem, "productNumber");
//                                    int productUnitNumber = JsonProcs.getIntegerFromJSON(objectItem, "productUnitNumber");
//                                    int containerNumber = JsonProcs.getIntegerFromJSON(objectItem, "containerNumber");
//
//                                    tvProduct.setText(cell.name + " " + productNumber + " шт (" + productUnitNumber + " упак) " + containerNumber + " конт");
//
//                                    JSONArray products = JsonProcs.getJsonArrayFromJsonObject(objectItem, "products");
//
//                                    for (int k = 0; k < products.length(); k++) {
//
//                                        ProductCell productCell = ProductCell.FromJson(JsonProcs.getItemJSONArray(products, k));
//
//                                        items.add(productCell);
//                                    }


                                }

                                adapter.notifyDataSetChanged();
                            }
                        });


            }
        });


        setOnCreateViewElements(new ListFragment.OnCreateViewElements() {
            @Override
            public void execute(View root, NavController navController) {

                tvProduct = root.findViewById(R.id.tvProduct);

                root.findViewById(R.id.llCell).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String cellName = tvProduct.getText().toString();

                        if (!cellName.isEmpty() && items.size() > 0) {

                            Bundle args = new Bundle();
                            args.putString("ref", UUID.randomUUID().toString());
                            Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), new BundleMethodInterface() {
                                @Override
                                public void callMethod(Bundle arguments) {

                                    RequestToServer.executeRequestUW(getContext(), Request.Method.GET,
                                            "setErpSkladCellClear", "cell=" + cell.ref + "&ref=" + arguments.getString("ref"), new JSONObject(),
                                            RequestToServer.TypeOfResponse.JsonObjectWithArray, response -> {

                                                JSONObject res = JsonProcs.getJsonObjectFromJsonObject(response, "ErpSkladCellClear");

                                                listUpdater.update(items, progressBar, adapter, cell.name);

                                            });


                                }
                            }, args, "Очистить ячейку ?", "Очищение");
                        }
                    }
                });

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

                        ((TextView) holder.getTextViews().get(0)).setText(item.product.artikul + " " + item.product.name);
                        ((TextView) holder.getTextViews().get(1)).setText(item.container.name + " " + item.containerNumber + " шт");
                        ((TextView) holder.getTextViews().get(2)).setText(item.productNumber + " шт (" + item.productUnitNumber + " упак)");
                    }
                });

                getAdapter().setOnClickListener(document -> {});

                getAdapter().setOnLongClickListener(document -> {});

                updateList("");

            }
        });


    }


}
