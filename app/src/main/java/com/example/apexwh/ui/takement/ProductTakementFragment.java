package com.example.apexwh.ui.takement;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.navigation.NavController;

import com.android.volley.Request;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.RequestToServer;
import com.example.apexwh.objects.Cell;
import com.example.apexwh.objects.Characteristic;
import com.example.apexwh.objects.Container;
import com.example.apexwh.objects.Product;
import com.example.apexwh.objects.ProductCell;
import com.example.apexwh.ui.Dialogs;
import com.example.apexwh.ui.adapters.DataAdapter;
import com.example.apexwh.ui.adapters.ScanListFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

public class ProductTakementFragment extends ScanListFragment<ProductCell> {

    TextView tvProduct;

    Cell cell;
    int productNumber, productUnitNumber, containerNumber;

    public ProductTakementFragment() {

        super(R.layout.fragment_scan_cell_list, R.layout.product_cell_list_item);



        setListUpdater(new ListUpdater() {
            @Override
            public void update(ArrayList items, ProgressBar progressBar, DataAdapter adapter, String filter) {

                if (cell == null) {

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

                                        cell = Cell.FromJson(JsonProcs.getJsonObjectFromJsonObject(objectItem, "cell"));

                                        productNumber = JsonProcs.getIntegerFromJSON(objectItem, "productNumber");
                                        productUnitNumber = JsonProcs.getIntegerFromJSON(objectItem, "productUnitNumber");
                                        containerNumber = JsonProcs.getIntegerFromJSON(objectItem, "containerNumber");

                                        tvProduct.setText(cell.name + " " + productNumber + " шт (" + productUnitNumber + " упак) " + containerNumber + " конт");

                                        JSONArray products = JsonProcs.getJsonArrayFromJsonObject(objectItem, "products");

                                        for (int k = 0; k < products.length(); k++) {

                                            ProductCell productCell = ProductCell.FromJson(JsonProcs.getItemJSONArray(products, k));

                                            items.add(productCell);
                                        }


                                    }

                                    adapter.notifyDataSetChanged();
                                }
                            });
                } else {

                    RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladContainersProducts", "filter=" + filter, new JSONObject(),
                            RequestToServer.TypeOfResponse.JsonObjectWithArray, response -> {

                                JSONArray containers = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladContainersProducts");

                                if (containers.length() == 1){

                                    JSONObject container = JsonProcs.getItemJSONArray(containers, 0);

                                    String type = JsonProcs.getStringFromJSON(container, "type");

                                    Bundle args = new Bundle();
                                    args.putString("cellRef", cell.ref);

                                    if (type.equals("Контейнер")){

                                        Container sourceContainer = Container.FromJson(container);

                                        args.putString("containerRef", sourceContainer.ref);
                                        args.putString("containerName", sourceContainer.name);

                                        Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), arguments -> {

                                            JSONObject jsonObject = new JSONObject();
                                            JsonProcs.putToJsonObject(jsonObject,"ref", UUID.randomUUID().toString());
                                            JsonProcs.putToJsonObject(jsonObject,"cellRef", arguments.getString("cellRef"));
                                            JsonProcs.putToJsonObject(jsonObject,"containerRef", arguments.getString("containerRef"));
                                            JsonProcs.putToJsonObject(jsonObject,"containerName", arguments.getString("containerName"));

//                                            RequestToServer.executeRequestBodyUW(getContext(), Request.Method.POST, "setErpSkladTakement", jsonObject,
//                                                    RequestToServer.TypeOfResponse.JsonObject, response1 -> {
//
//                                                        if (!JsonProcs.getStringFromJSON(response1, "ref").isEmpty()){
//                                                            navController.popBackStack();
//                                                        }
//
//
//
//                                                    });


                                        },  args, "Взять контейнер " + sourceContainer.name + " в ячейку " + cell.name + " ?", "Взятие");

                                    } else {

                                        Product product = Product.FromJson(container);

                                        Characteristic characteristic = new Characteristic("", "");
                                        if (type.equals("НоменклатураСХарактеристикой")) {
                                            characteristic = Characteristic.FromJson(JsonProcs.getJsonObjectFromJsonObject(container, "characteristic"));
                                            args.putString("characterRef", characteristic.ref);
                                        }


                                        int curQuantity = 0;
                                        for (int i = 0; i < items.size(); i++) {

                                            ProductCell cp = ((ProductCell) items.get(i));

                                            if (cp.product.ref.equals(product.ref)){
                                                curQuantity = cp.productNumber;
                                            }



                                        }

                                        if (curQuantity > 0) {

                                            args.putString("productRef", product.ref);

                                            Dialogs.showInputQuantity(getContext(), curQuantity, getActivity(), arguments -> {

                                                        JSONObject jsonObject = new JSONObject();
                                                        JsonProcs.putToJsonObject(jsonObject, "ref", UUID.randomUUID().toString());
                                                        JsonProcs.putToJsonObject(jsonObject, "cellRef", arguments.getString("cellRef"));
                                                        JsonProcs.putToJsonObject(jsonObject, "productRef", arguments.getString("productRef"));
                                                        if (type.equals("НоменклатураСХарактеристикой")) {
                                                            JsonProcs.putToJsonObject(jsonObject, "characterRef", arguments.getString("characterRef"));
                                                        }
                                                        JsonProcs.putToJsonObject(jsonObject, "quantity", arguments.getInt("quantity"));

                                                        RequestToServer.executeRequestBodyUW(getContext(), Request.Method.POST, "setErpSkladTakement", jsonObject,
                                                                RequestToServer.TypeOfResponse.JsonObject, response1 -> {

                                                                    if (!JsonProcs.getStringFromJSON(response1, "ref").isEmpty()) {

                                                                        String cfilter = cell.name;

                                                                        cell = null;

                                                                        update(items, progressBar, adapter, cfilter);

                                                                    }


                                                                });


                                                    },
                                                    args, "Ввести вручную " + product.name
                                                            + (type.equals("НоменклатураСХарактеристикой") ? ", " + characteristic.description : "")
                                                            + " ?", "Ввод количества");
                                        }

                                    }

                                }

                            });



                }

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

                        String curChar = Characteristic.getString(item.characteristic);

                        ((TextView) holder.getTextViews().get(0)).setText(item.product.artikul + " " + item.product.name);
                        ((TextView) holder.getTextViews().get(1)).setText(item.container.name + " " + item.containerNumber + " шт");
                        ((TextView) holder.getTextViews().get(2)).setText(item.productNumber
                                + (curChar.isEmpty() ? "" : ", " + curChar)
                                + " шт (" + item.productUnitNumber + " упак)");
                    }
                });

                getAdapter().setOnClickListener(document -> {

                    listUpdater.update(items, progressBar, adapter, ((ProductCell) document).product.artikul);

                });

                getAdapter().setOnLongClickListener(document -> {});



            }
        });


    }


}