package com.example.apexwh.ui.productline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.RequestToServer;
import com.example.apexwh.objects.Product;
import com.example.apexwh.objects.Shtrihcode;
import com.example.apexwh.ui.BundleMethodInterface;
import com.example.apexwh.ui.Dialogs;
import com.example.apexwh.ui.adapters.DataAdapter;
import com.example.apexwh.ui.adapters.ScanListFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

public class ProductLineFragment extends ScanListFragment<Shtrihcode> {

    TextView tvProduct;

    Product product;
    Shtrihcode shtrihcode;

    String ref, name;
    private LinearLayout linearLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ref = getArguments().getString("ref");
        name = getArguments().getString("name");
    }

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

                        RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "setErpSkladProductShtrihcode",
                                "product=" + arguments.getString("product") + "&shtrihcode=" + arguments.getString("shtrihcode"), new JSONObject(), 1,
                                new RequestToServer.ResponseResultInterface() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        progressBar.setVisibility(View.GONE);

                                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main)
                                                .popBackStack();

                                    }
                                });


                    }
                }, bundle, "Установить " + product.artikul + " " + product.name + " штрихкод " + shtrihcode.value + "?" , "Установка штрихкода");

            }
        });

        return view;

    }

    public ProductLineFragment() {

        super(R.layout.fragment_scan_list, R.layout.product_cell_list_item);

        setListUpdater(new ListUpdater() {
            @Override
            public void update(ArrayList items, ProgressBar progressBar, DataAdapter adapter, String filter) {

                items.clear();

                RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladProductShtrihcodes", "filter=" + filter, new JSONObject(), 1,
                        new RequestToServer.ResponseResultInterface() {
                            @Override
                            public void onResponse(JSONObject response) {

                                progressBar.setVisibility(View.GONE);

                                JSONArray responseItems = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladProductShtrihcodes");

                                tvProduct.setText(filter + " не найден");

                                for (int j = 0; j < responseItems.length(); j++) {

                                    JSONObject objectItem = JsonProcs.getItemJSONArray(responseItems, j);

                                    product = Product.FromJson(JsonProcs.getJsonObjectFromJsonObject(objectItem, "product"));

                                    tvProduct.setText(product.artikul + " " + product.name);

                                    JSONArray cells = JsonProcs.getJsonArrayFromJsonObject(objectItem, "shtrihcodes");

                                    for (int k = 0; k < cells.length(); k++) {

                                        JSONObject productCell = JsonProcs.getItemJSONArray(cells, k);

                                        items.add(new Shtrihcode(JsonProcs.getStringFromJSON(productCell, "shtrihcode"), false));
                                    }


                                }

                                if (responseItems.length() == 0){

                                    product = null;

                                    shtrihcode = new Shtrihcode(filter, true);

                                    items.add(shtrihcode);

                                }


                                adapter.notifyDataSetChanged();

                                if (product != null){

                                    askForQuantity();

                                }

                            }
                        });


            }
        });


        setOnCreateViewElements(new OnCreateViewElements() {
            @Override
            public void execute(View root, NavController navController) {

                linearLayout = root.findViewById(R.id.LinearLayout);

                tvProduct = (TextView) root.findViewById(R.id.tvProduct);

                root.findViewById(R.id.llProduct).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (product == null){

                            Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main)
                                    .navigate(R.id.nav_productListFragment);

                        }

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

    private void askForQuantity() {

        Bundle bundle = new Bundle();
        bundle.putString("ref", ref);
        bundle.putString("name", name);
        bundle.putString("product", product.ref);

        Dialogs.showInputQuantity(getContext(), null, getActivity(), new BundleMethodInterface() {
            @Override
            public void callMethod(Bundle arguments) {

                linearLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "setErpSkladProductToTestBySender",
                        "doc=" + UUID.randomUUID().toString()
                                + "&name=" + name + "&ref=" + ref + "&product=" + arguments.getString("product") + "&quantity=-" + arguments.getInt("quantity"), new JSONObject(), 1,
                        new RequestToServer.ResponseResultInterface() {
                            @Override
                            public void onResponse(JSONObject response) {

                                progressBar.setVisibility(View.GONE);

                                Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).popBackStack();


                            }
                        });


            }
        }, bundle, "Введите количество " + product.artikul + " " + product.name, "Ввод количества");


    }


}