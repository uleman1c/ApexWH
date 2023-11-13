package com.example.apexwh.ui.Inventarizations;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.RequestToServer;
import com.example.apexwh.objects.Cell;
import com.example.apexwh.objects.Characteristic;
import com.example.apexwh.objects.Container;
import com.example.apexwh.objects.Product;
import com.example.apexwh.objects.ProductCell;
import com.example.apexwh.ui.BundleMethodInterface;
import com.example.apexwh.ui.Dialogs;
import com.example.apexwh.ui.adapters.DataAdapter;
import com.example.apexwh.ui.adapters.ScanListFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class InventarizationProductFragment extends ScanListFragment<ProductCell> {

    class RefNum {
        public RefNum(String ref) {
            this.ref = ref;
            this.number = 0;
            this.scanned = 0;
        }

        public String ref;
        public int number, scanned;

    }

    TextView tvProduct, tvCell, tvInvented;

    Cell cell;

    ArrayList<ProductCell> accProductCells;

    ArrayList<RefNum> refNums;
    int sumRefNums, sumRefScanned, sumRefScannedProducts;

    String modRefNums;

    int productNumber, productUnitNumber, containerNumber;

    public InventarizationProductFragment() {

        super(R.layout.fragment_scan_invent_list, R.layout.product_cell_list_item);

        accProductCells = new ArrayList<>();

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

                                        JSONArray products = JsonProcs.getJsonArrayFromJsonObject(objectItem, "products");

                                        accProductCells.clear();

                                        for (int k = 0; k < products.length(); k++) {

                                            ProductCell productCell = ProductCell.FromJson(JsonProcs.getItemJSONArray(products, k));

                                            accProductCells.add(productCell);
                                        }


                                    }

                                    refNums = new ArrayList<>();

                                    for (ProductCell productCell:accProductCells) {

                                        if (productCell.productNumber > 0) {

                                            String curRef = productCell.product.ref;

                                            Boolean found = false;

                                            RefNum curRefNum = null;

                                            for (int i = 0; i < refNums.size() && !found; i++) {
                                                found = refNums.get(i).ref.equals(curRef);

                                                if (found) {
                                                    curRefNum = refNums.get(i);
                                                }
                                            }

                                            if (curRefNum == null){

                                                curRefNum = new RefNum(curRef);

                                                refNums.add(curRefNum);

                                            }

                                            curRefNum.number = curRefNum.number + productCell.productNumber;

                                        }

                                    }

                                    adapter.notifyDataSetChanged();

                                    if (cell != null) {

                                        int curDecMod = refNums.size() % 100;
                                        int curMod = refNums.size() % 10;

                                        modRefNums = "";

                                        if (curDecMod > 20 || curDecMod < 10) {
                                            modRefNums = curMod == 1 ? "" : (curMod > 5 ? "а" : "ов");
                                        } else {
                                            modRefNums = "ов";
                                        }

                                        sumRefNums = 0;
                                        for (RefNum refNum: refNums) {
                                            sumRefNums = sumRefNums + refNum.number;
                                        }

                                        tvCell.setText(cell.name);

                                        sumRefScanned = 0;
                                        sumRefScannedProducts = 0;

                                        tvProduct.setText(String.valueOf(refNums.size()) + " товар" + modRefNums + ", " + sumRefNums + " шт");
                                        tvInvented.setText(sumRefScanned + " товаров, " + sumRefScanned + " шт отсканировано");
                                    }
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

                                        JSONArray products = JsonProcs.getJsonArrayFromJsonObject(container, "products");

                                        for (int i = 0; i < products.length(); i++) {

                                            JSONObject jProduct = JsonProcs.getItemJSONArray(products, i);

                                            Product product = Product.FromJson(JsonProcs.getJsonObjectFromJsonObject(jProduct, "product"));

                                            Characteristic characteristic = Characteristic.FromJson(JsonProcs.getJsonObjectFromJsonObject(jProduct, "characteristic"));

                                            int productNumber = JsonProcs.getIntegerFromJSON(jProduct, "quantity");
                                            int productUnitNumber = JsonProcs.getIntegerFromJSON(jProduct, "unitQuantity");

                                            ProductCell productCell = new ProductCell(product, productNumber, productUnitNumber, cell, sourceContainer, 0, characteristic);

                                            String curRef = productCell.product.ref;

                                            Boolean found = false;

                                            RefNum curRefNum = null;

                                            for (int j = 0; j < refNums.size() && !found; j++) {
                                                found = refNums.get(j).ref.equals(curRef);

                                                if (found) {
                                                    curRefNum = refNums.get(j);
                                                }
                                            }

                                            if (curRefNum != null){

                                                if (curRefNum.scanned == 0){
                                                    sumRefScannedProducts = sumRefScannedProducts + 1;
                                                }

                                                curRefNum.scanned = curRefNum.scanned + productCell.productNumber;

                                                sumRefScanned = sumRefScanned + productCell.productNumber;

                                                tvInvented.setText(sumRefScanned + " товаров, " + sumRefScanned + " шт отсканировано");
                                            }




                                            items.add(0, productCell);

                                        }

                                        adapter.notifyDataSetChanged();

                                    } else {

                                        Product product = Product.FromJson(container);

                                        Characteristic characteristic = Characteristic.FromJson(JsonProcs.getJsonObjectFromJsonObject(container, "characteristic"));

                                        ProductCell productCell = new ProductCell(product, 0, 0, cell, new Container("", ""), 0, characteristic);

                                        items.add(0, productCell);

                                        adapter.notifyDataSetChanged();

                                        args.putString("productRef", product.ref);
                                        args.putString("characteristicRef", characteristic.ref);

                                        Dialogs.showInputQuantity(getContext(), null, getActivity(), arguments -> {

                                            Boolean found = false;
                                                    for (int i = 0; i < items.size() && !found; i++) {

                                                        ProductCell curPC = (ProductCell) items.get(i);

                                                        found = curPC.product.ref.equals(arguments.getString("productRef"))
                                                                && curPC.characteristic.ref.equals(arguments.getString("characteristicRef"));

                                                        if (found){

                                                            curPC.productNumber = arguments.getInt("quantity");
                                                            curPC.productUnitNumber = arguments.getInt("quantity");
                                                            adapter.notifyDataSetChanged();

                                                            String curRef = curPC.product.ref;

                                                            Boolean found1 = false;

                                                            RefNum curRefNum = null;

                                                            for (int j = 0; j < refNums.size() && !found1; j++) {
                                                                found1 = refNums.get(j).ref.equals(curRef);

                                                                if (found1) {
                                                                    curRefNum = refNums.get(j);
                                                                }
                                                            }

                                                            if (curRefNum != null){

                                                                if (curRefNum.scanned == 0){
                                                                    sumRefScannedProducts = sumRefScannedProducts + 1;
                                                                }

                                                                curRefNum.scanned = curRefNum.scanned + productCell.productNumber;

                                                                sumRefScanned = sumRefScanned + productCell.productNumber;

                                                                tvInvented.setText(sumRefScannedProducts + " товаров, " + sumRefScanned + " шт отсканировано");
                                                            }




                                                        }

                                                    }

                                                },
                                                args, "Ввести вручную " + product.name + " ?", "Ввод количества");


                                    }

                                }

                            });



                }

            }
        });


        setOnCreateViewElements(new OnCreateViewElements() {
            @Override
            public void execute(View root, NavController navController) {

                root.findViewById(R.id.btnSaveInvent).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (cell != null) {
                            Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), new BundleMethodInterface() {
                                @Override
                                public void callMethod(Bundle arguments) {

                                    JSONArray jsonArray = new JSONArray();

                                    for (ProductCell productCell: (ArrayList<ProductCell>) items) {

                                        try {
                                            jsonArray.put(ProductCell.ToJson(productCell));
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }


                                    }

                                JSONObject jsonObject = new JSONObject();
                                JsonProcs.putToJsonObject(jsonObject,"ref", UUID.randomUUID().toString());
                                JsonProcs.putToJsonObject(jsonObject,"cellRef", cell.ref);
                                JsonProcs.putToJsonObject(jsonObject, "items", jsonArray.toString());


                                RequestToServer.executeRequestBodyUW(getContext(), Request.Method.POST, "setErpSkladInventarization", jsonObject,
                                        RequestToServer.TypeOfResponse.JsonObject, response1 -> {

                                            if (!JsonProcs.getStringFromJSON(response1, "ref").isEmpty()){

                                                navController.popBackStack();

//                                                cell = null;
//
//                                                updateList("");

                                            }



                                        });

                                }
                            }, new Bundle(), "Сохранить инвентаризацию ячейки " + cell.name + " ?", "Сохранить");
                        }
                    }
                });
                root.findViewById(R.id.llCell).setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        if (cell != null) {

                            Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), new BundleMethodInterface() {
                                @Override
                                public void callMethod(Bundle arguments) {

                                    items.clear();

                                    adapter.notifyDataSetChanged();

                                    tvProduct.setText(cell.name);

                                }
                            }, new Bundle(), "Очистить ячейку ?", "Очистить");
                        }

                        return false;
                    }
                });

                tvProduct = root.findViewById(R.id.tvProduct);
                tvCell = root.findViewById(R.id.tvCell);
                tvInvented = root.findViewById(R.id.tvInvented);

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



            }
        });


    }


}