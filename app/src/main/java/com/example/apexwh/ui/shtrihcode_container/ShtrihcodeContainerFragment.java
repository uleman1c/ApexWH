package com.example.apexwh.ui.shtrihcode_container;

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

import com.android.volley.Request;
import com.example.apexwh.DB;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.RequestToServer;
import com.example.apexwh.objects.Container;
import com.example.apexwh.objects.Product;
import com.example.apexwh.objects.ProductWithQuantity;
import com.example.apexwh.objects.Shtrihcode;
import com.example.apexwh.ui.BundleMethodInterface;
import com.example.apexwh.ui.Dialogs;
import com.example.apexwh.ui.adapters.DataAdapter;
import com.example.apexwh.ui.adapters.ScanListFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

public class ShtrihcodeContainerFragment extends ScanListFragment<ProductWithQuantity> {

    TextView tvProduct;

    Product product;

    ArrayList<Product> products;
    Container container;
    Shtrihcode shtrihcode;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        products = new ArrayList<>();
        container = null;

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        ((TextView)view.findViewById(R.id.tvObjectName)).setText("Контейнер");

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

    public ShtrihcodeContainerFragment() {

        super(R.layout.fragment_scan_wo_proc_list, R.layout.product_cell_list_item);

        setListUpdater(new ListUpdater() {
            @Override
            public void update(ArrayList items, ProgressBar progressBar, DataAdapter adapter, String filter) {

                RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladContainersProducts", "filter=" + filter, new JSONObject(), 1,
                        new RequestToServer.ResponseResultInterface() {
                            @Override
                            public void onResponse(JSONObject response) {

                                progressBar.setVisibility(View.GONE);

                                JSONArray responseItems = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladContainersProducts");

                                tvProduct.setText(filter + " не найден");

                                for (int j = 0; j < responseItems.length(); j++) {

                                    JSONObject objectItem = JsonProcs.getItemJSONArray(responseItems, j);

                                    if (JsonProcs.getStringFromJSON(objectItem, "type").equals("Контейнер")){

                                        items.clear();

                                        container = Container.FromJson(objectItem);

                                        tvProduct.setText(container.name);

                                        JSONArray products = JsonProcs.getJsonArrayFromJsonObject(objectItem, "products");

                                        for (int k = 0; k < products.length(); k++) {

                                            JSONObject curProduct = JsonProcs.getItemJSONArray(products, k);

                                            items.add(ProductWithQuantity.FromJson(curProduct));
                                        }

                                    }
                                    else {

                                        Product curProduct = Product.FromJson(objectItem);

                                        items.add(0, new ProductWithQuantity(curProduct, 0, 0));

                                        Dialogs.showInputQuantity(getContext(), null, getActivity(), new BundleMethodInterface() {
                                            @Override
                                            public void callMethod(Bundle arguments) {

                                                ((ProductWithQuantity)items.get(0)).quantity = arguments.getInt("quantity");
                                                adapter.notifyDataSetChanged();

                                            }
                                        }, new Bundle(), "Введите количество " + curProduct.name, "Ввод количества");


                                    }

                                }

                                if (responseItems.length() == 0){

//                                    product = null;
//
//                                    shtrihcode = new Shtrihcode(filter, true);
//
//                                    items.add(shtrihcode);

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

                        Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), new BundleMethodInterface() {
                            @Override
                            public void callMethod(Bundle arguments) {

                                createContainer();

                            }
                        }, new Bundle(), "Генерировать новый контейнер?", "Вопрос");

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

                getAdapter().setDrawViewHolder(new DataAdapter.DrawViewHolder<ProductWithQuantity>() {
                    @Override
                    public void draw(DataAdapter.ItemViewHolder holder, ProductWithQuantity item) {

                        ((TextView) holder.getTextViews().get(0)).setText(item.product.artikul);
                        ((TextView) holder.getTextViews().get(1)).setText(item.product.name);
                        ((TextView) holder.getTextViews().get(2)).setText(item.quantity + " (" + item.unitQuantity + ")");
                    }
                });

                getAdapter().setOnClickListener(document -> {

                });

                getAdapter().setOnLongClickListener(document -> {});



            }
        });


    }

    private void createContainer() {

        progressBar.setVisibility(View.VISIBLE);

        JSONArray content = new JSONArray();

         for (ProductWithQuantity item : (ArrayList<ProductWithQuantity>) items) {

             JSONObject jItem = new JSONObject();

             JsonProcs.putToJsonObject(jItem,"ref", item.product.ref);
             JsonProcs.putToJsonObject(jItem,"quantity", item.quantity);
             JsonProcs.putToJsonObject(jItem,"unitQuantity", item.unitQuantity);

             content.put(jItem);

         }


        JSONObject jsonObject = new JSONObject();
        JsonProcs.putToJsonObject(jsonObject,"ref", UUID.randomUUID().toString());
        JsonProcs.putToJsonObject(jsonObject,"content", String.valueOf(content));

        RequestToServer.executeRequestBodyUW(getContext(), Request.Method.POST, "setErpSkladContainerWithContent", jsonObject,
                RequestToServer.TypeOfResponse.JsonObject, response -> {

            progressBar.setVisibility(View.GONE);

            if (!JsonProcs.getStringFromJSON(response, "ref").isEmpty()){

                items.clear();

                adapter.notifyDataSetChanged();

                tvProduct.setText(JsonProcs.getStringFromJSON(response, "container") + " создан");



            }


        });



    }


}