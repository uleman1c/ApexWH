package com.example.apexwh.ui.refill_tasks;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.example.apexwh.DB;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.RequestToServer;
import com.example.apexwh.objects.Cell;
import com.example.apexwh.objects.Characteristic;
import com.example.apexwh.objects.Container;
import com.example.apexwh.objects.InventTask;
import com.example.apexwh.objects.Product;
import com.example.apexwh.objects.ProductCell;
import com.example.apexwh.objects.RefillTask;
import com.example.apexwh.ui.BundleMethodInterface;
import com.example.apexwh.ui.Dialogs;
import com.example.apexwh.ui.adapters.DataAdapter;
import com.example.apexwh.ui.adapters.ScanListFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

public class RefillTaskFragment extends ScanListFragment<ProductCell> {

    class RefNum {
        public RefNum(String ref) {
            this.ref = ref;
            this.number = 0;
            this.scanned = 0;
        }

        public String ref;
        public int number, scanned;

    }

    TextView tvProduct, tvSourceCell, tvCell, tvInvented, tvSourceDescription, tvDescription;

    Cell cell;

    ArrayList<ProductCell> accProductCells;

    ArrayList<RefNum> refNums;
    int sumRefNums, sumRefScanned, sumRefScannedProducts;

    String modRefNums;

    int productNumber, productUnitNumber, containerNumber;

    RefillTask refillTask;



    public RefillTaskFragment() {

        super(R.layout.fragment_refill_task, R.layout.product_cell_scanned_list_item);

        accProductCells = new ArrayList<>();

        setListUpdater(new ListUpdater() {
            @Override
            public void update(ArrayList items, ProgressBar progressBar, DataAdapter adapter, String filter) {

                RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladRefillTaskStatus", "filter=" + filter, new JSONObject(),
                        RequestToServer.TypeOfResponse.JsonObjectWithArray, response -> {

                            JSONArray containers = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladRefillTaskStatus");

                            if (containers.length() == 1){

                                refillTask = RefillTask.FromJsonObject(JsonProcs.getItemJSONArray(containers, 0));

                                tvSourceCell.setText(refillTask.source.name);
                                tvCell.setText(refillTask.cell.name);


                                if (!refillTask.takement.ref.equals(DB.nil)){

                                    tvSourceDescription.setText("Взятие: " + refillTask.takement.description);

                                }
                                else {

                                    tvSourceDescription.setText("Ожидается взятие");

                                }

                                if (!refillTask.placement.ref.equals(DB.nil)){

                                    tvDescription.setText("Размещение: " + refillTask.placement.description);

                                }
                                else {

                                    tvDescription.setText("Ожидается размещение");

                                }

                            }

                        });




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
                                if (refillTask != null){
                                    JsonProcs.putToJsonObject(jsonObject,"inventTaskRef", refillTask.document.ref);
                                }
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



                        return false;
                    }
                });

                root.findViewById(R.id.llSourceCell).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (refillTask.takement.ref.equals(DB.nil)) {

                            Bundle bundle = new Bundle();
                            bundle.putString("refillTask", RefillTask.toJson(refillTask).toString());

                            if (refillTask.mode.equals("Номенклатура")){

                                Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.nav_productTakementFragment, bundle);
                            } else {

                                Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.nav_takementFragment, bundle);
                            }

                        }
                    }
                });

                root.findViewById(R.id.llCell).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!refillTask.takement.ref.equals(DB.nil)){

                            Bundle bundle = new Bundle();
                            bundle.putString("refillTask", RefillTask.toJson(refillTask).toString());

                            if (refillTask.mode.equals("Номенклатура")){

                                Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.nav_productPlacementFragment, bundle);
                            } else {

                                Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.nav_placementFragment, bundle);

                            }

                        }

                    }
                });




                tvProduct = root.findViewById(R.id.tvProduct);
                tvCell = root.findViewById(R.id.tvCell);
                tvSourceCell = root.findViewById(R.id.tvSourceCell);
                tvInvented = root.findViewById(R.id.tvInvented);
                tvSourceDescription = root.findViewById(R.id.tvSourceDescription);
                tvDescription = root.findViewById(R.id.tvDescription);



                getAdapter().setInitViewsMaker(new DataAdapter.InitViewsMaker() {
                    @Override
                    public void init(View itemView, ArrayList<TextView> textViews) {

                        textViews.add(itemView.findViewById(R.id.tvNumberDate));
                        textViews.add(itemView.findViewById(R.id.tvDescription));
                        textViews.add(itemView.findViewById(R.id.tvStatus));
                        textViews.add(itemView.findViewById(R.id.tvNumber));
                    }
                });

                getAdapter().setDrawViewHolder(new DataAdapter.DrawViewHolder<ProductCell>() {
                    @Override
                    public void draw(DataAdapter.ItemViewHolder holder, ProductCell item) {

                        ((TextView) holder.getTextViews().get(0)).setText(item.product.artikul + " " + item.product.name);
                        ((TextView) holder.getTextViews().get(1)).setText(item.container.name + " " + item.containerNumber + " шт");
                        ((TextView) holder.getTextViews().get(2)).setText(item.productNumber + " шт (" + item.productUnitNumber + " упак)");
                        ((TextView) holder.getTextViews().get(3)).setText("по учету " + item.scanned + " шт");
                    }
                });

                getAdapter().setOnClickListener(document -> {});

                getAdapter().setOnLongClickListener(document -> {

                    Bundle bundle = new Bundle();
                    bundle.putString("productRef", ((ProductCell) document).product.ref);
                    bundle.putString("productName", ((ProductCell) document).product.name);
                    bundle.putString("characteristicRef", ((ProductCell) document).characteristic.ref);
                    bundle.putString("characteristicName", ((ProductCell) document).characteristic.description);
                    bundle.putInt("index", items.indexOf(document));
                    Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), new BundleMethodInterface() {
                        @Override
                        public void callMethod(Bundle arguments) {

                            items.remove(arguments.getInt("index"));

                            adapter.notifyDataSetChanged();

                        }
                    }, bundle, "Удалить", "Удалить " + ((ProductCell) document).product.name
                        + ", " + ((ProductCell) document).characteristic.description);

                });

                if (arguments != null){

                    String jo = arguments.getString("refillTask");

                    if (jo != null && !jo.isEmpty()) {

                        JSONObject jsonObject = JsonProcs.getJSONObjectFromString(jo);

                        refillTask = RefillTask.FromJsonObject(jsonObject);

                        cell = refillTask.source;

                        listUpdater.update(items, progressBar, adapter, refillTask.document.ref);
                    }
                }


            }
        });


    }

    private void updateCell(ArrayList items, ProgressBar progressBar, DataAdapter adapter, String filter) {
        items.clear();

        RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladCellContent", "filter=" + filter, new JSONObject(), 1,
                new RequestToServer.ResponseResultInterface() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progressBar.setVisibility(View.GONE);

                        JSONArray responseItems = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladCellContent");

                        tvProduct.setText(filter + " не найден");

                        if (responseItems.length() == 0) {

                            RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladRefillTaskStatus", "filter=" + filter, new JSONObject(), 1,
                                    new RequestToServer.ResponseResultInterface() {
                                        @Override
                                        public void onResponse(JSONObject response) {

                                            JSONArray responseItems2 = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladCells");

                                            if (responseItems2.length() > 0) {

                                                JSONObject objectItem = JsonProcs.getItemJSONArray(responseItems2, 0);

                                                cell = Cell.FromJson(objectItem);

                                                productNumber = 0;
                                                productUnitNumber = 0;
                                                containerNumber = 0;

                                                tvProduct.setText(cell.name + " " + productNumber + " шт (" + productUnitNumber + " упак) " + containerNumber + " конт");

                                                accProductCells.clear();

                                            }
                                        }

                                    });

                        } else {

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
    }


}