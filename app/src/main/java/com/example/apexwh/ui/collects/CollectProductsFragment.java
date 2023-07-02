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
import java.util.Comparator;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class CollectProductsFragment extends ScanListFragment<ProductCellContainerOutcome> {

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

        super(R.layout.fragment_scan_list_clear, R.layout.product_cell_border_list_item);

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

                                    items.add(ProductCellContainerOutcome.FromJson(objectItem));

                                }

                                items.sort(new Comparator() {
                                    @Override
                                    public int compare(Object o, Object t1) {
                                        return ((ProductCellContainerOutcome) o).cell.name.compareTo(((ProductCellContainerOutcome) t1).cell.name);
                                    }
                                });

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

                getAdapter().setDrawViewHolder(new DataAdapter.DrawViewHolder<ProductCellContainerOutcome>() {
                    @Override
                    public void draw(DataAdapter.ItemViewHolder holder, ProductCellContainerOutcome item) {

                        ((TextView) holder.getTextViews().get(0)).setText("Ячейка: " + item.cell.name);
                        ((TextView) holder.getTextViews().get(1)).setText(item.product.artikul + " " + item.product.name + ", " + item.number + " шт");
                        ((TextView) holder.getTextViews().get(2)).setText(item.container.name);
                    }
                });

                getAdapter().setOnClickListener(document -> {});

                getAdapter().setOnLongClickListener(document -> {});

                updateList("");

            }
        });


    }


}
