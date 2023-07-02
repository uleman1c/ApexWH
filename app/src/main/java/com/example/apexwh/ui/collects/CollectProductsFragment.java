package com.example.apexwh.ui.collects;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.android.volley.Request;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.RequestToServer;
import com.example.apexwh.objects.Cell;
import com.example.apexwh.objects.ProductCellContainerOutcome;
import com.example.apexwh.ui.BundleMethodInterface;
import com.example.apexwh.ui.Dialogs;
import com.example.apexwh.ui.adapters.BeforeEndOnCreateViewHolder;
import com.example.apexwh.ui.adapters.DataAdapter;
import com.example.apexwh.ui.adapters.ListFragment;
import com.example.apexwh.ui.adapters.OnGetItemViewType;
import com.example.apexwh.ui.adapters.ScanListFragment;

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

                if (filter.isEmpty()) {

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
                else {

                    ProductCellContainerOutcome foundPCCO = null;
                    ProductCellContainerOutcome foundProduct = null;
                    for (int i = 0; i < items.size() && foundPCCO == null; i++) {

                        ProductCellContainerOutcome curPCCO = ((ProductCellContainerOutcome) items.get(i));

                        if (filter.toLowerCase().equals(curPCCO.cell.name.toLowerCase())){

                            foundPCCO = curPCCO;

                        } else if (curPCCO.mode == 1) {

                            if (filter.toLowerCase().equals(curPCCO.product.artikul.toLowerCase())){

                                foundPCCO = curPCCO;
                                foundProduct = curPCCO;

                            }
                            else {

                                for (int j = 0; j < curPCCO.product.shtrihCodes.size() && foundProduct == null; j++) {

                                    if (filter.toLowerCase().equals(curPCCO.product.shtrihCodes.get(j))){

                                        foundPCCO = curPCCO;
                                        foundProduct = curPCCO;

                                    }

                                }

                            }

                        }

                    }

                    if (foundPCCO != null && foundProduct == null){

                        for (int i = 0; i < items.size(); i++) {

                            ProductCellContainerOutcome curPCCO = ((ProductCellContainerOutcome) items.get(i));

                            curPCCO.mode = curPCCO == foundPCCO ? 1 : 0;

                        }

                        items.remove(items.indexOf(foundPCCO));

                        items.add(0 , foundPCCO);

                    }

                    if (foundProduct != null){

                        Bundle bundle = new Bundle();
                        bundle.putString("ref", ref);
                        bundle.putString("name", name);
                        bundle.putString("order", order);
                        bundle.putString("cell", foundProduct.cell.ref);
                        bundle.putString("container", foundProduct.container.ref);
                        bundle.putString("product", foundProduct.product.ref);

                        Dialogs.showInputQuantity(getContext(), foundProduct.number, getActivity(), new BundleMethodInterface() {
                            @Override
                            public void callMethod(Bundle arguments) {

                                doCollect(arguments);

                            }
                        }, bundle, "Введите количество " + foundProduct.product.artikul + " " + foundProduct.product.name, "Ввод количества");

                    }

                    getAdapter().notifyDataSetChanged();

                }
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

                getAdapter().setOnGetItemViewType(new OnGetItemViewType() {
                    @Override
                    public int Do(int position) {
                        return ((ProductCellContainerOutcome)items.get(position)).mode;
                    }
                });

                getAdapter().setBeforeEndOnCreateViewHolder(new BeforeEndOnCreateViewHolder() {
                    @Override
                    public View Do(LayoutInflater inflater, ViewGroup parent, int viewType) {

                        View view = null;

                        if (viewType == 0) {

                            view = inflater.inflate(R.layout.product_cell_border_list_item, parent, false);
                        }
                        else {

                            view = inflater.inflate(R.layout.product_cell_border2_list_item, parent, false);
                        }

                        return view;

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

    void doCollect(Bundle bundle){

        RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "setErpSkladProductsToOutcome",
                "doc=" + UUID.randomUUID().toString() + "&cell=" + bundle.getString("cell")
                        + "&container=" + bundle.getString("container")
                        + "&name=" + name + "&ref=" + ref + "&product=" + bundle.getString("product") + "&quantity=" + bundle.getInt("quantity"), new JSONObject(), 1,
                new RequestToServer.ResponseResultInterface() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progressBar.setVisibility(View.GONE);

                        updateList("");
                    }
                });

    }


}