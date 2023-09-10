package com.example.apexwh.ui.warehouses;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.example.apexwh.DB;
import com.example.apexwh.HttpClient;
import com.example.apexwh.HttpRequestInterface;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.RequestToServer;
import com.example.apexwh.objects.Cell;
import com.example.apexwh.objects.ProductCellContainerOutcome;
import com.example.apexwh.objects.Reference;
import com.example.apexwh.objects.Return;
import com.example.apexwh.objects.Warehouse;
import com.example.apexwh.ui.BundleMethodInterface;
import com.example.apexwh.ui.Dialogs;
import com.example.apexwh.ui.adapters.ReferenceDataAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WarehousesFragment extends Fragment {

    private WarehousesViewModel mViewModel;

    public static WarehousesFragment newInstance() {
        return new WarehousesFragment();
    }

    private ProgressBar progressBar;
    private ArrayList<Reference> warehouses;

    private ReferenceDataAdapter adapter;
    private RecyclerView recyclerView;
    private EditText etFilter;
    private InputMethodManager imm;

    private String mode;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_warehouses, container, false);

        Bundle args = getArguments();

        if (args != null) {

            mode = args.getString("mode");

        } else {

            mode = "";
        }

        progressBar = root.findViewById(R.id.progressBar);

        warehouses = new ArrayList<>();

        adapter = new ReferenceDataAdapter(getContext(), warehouses);
        adapter.setOnReferenceItemClickListener(new ReferenceDataAdapter.OnReferenceItemClickListener() {
            @Override
            public void onReferenceItemClick(Reference reference) {

                if (mode.equals("selectWarehouseSetting")) {

                    Bundle bundle = new Bundle();
                    bundle.putString("ref", reference.ref);
                    bundle.putString("description", reference.description);
                    bundle.putString("mode", mode);

                    DB db = new DB(getContext());

                    db.open();

                    db.updateConstant("warehouseId", reference.ref);
                    db.updateConstant("warehouseDescription", reference.description);

                    db.close();

                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).popBackStack();
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).popBackStack();
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.nav_settings, bundle);
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).clearBackStack(R.id.nav_warehouses);
                }
            }

        });

        recyclerView = root.findViewById(R.id.list);
        recyclerView.setAdapter(adapter);

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        etFilter = root.findViewById(R.id.etFilter);
        etFilter.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    String strCatName = etFilter.getText().toString();

                    imm.hideSoftInputFromWindow(etFilter.getWindowToken(), 0);

                    updateList(strCatName);

                    return true;
                }

                return false;
            }
        });

        root.findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                etFilter.setText("");
                updateList(etFilter.getText().toString());

            }
        });


        updateList(etFilter.getText().toString());


        return root;
    }

    private void updateList(String filter) {

        warehouses.clear();

        RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getWarehouses",
                "filter=" + filter, new JSONObject(), 1,
                new RequestToServer.ResponseResultInterface() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progressBar.setVisibility(View.GONE);

                        JSONArray jsonArrayObjects = JsonProcs.getJsonArrayFromJsonObject(response, "Warehouses");

                        for (int j = 0; j < jsonArrayObjects.length(); j++) {

                            JSONObject objectItem = JsonProcs.getItemJSONArray(jsonArrayObjects, j);

                            warehouses.add(Warehouse.WarehouseFromJson(objectItem));

                        }

                        adapter.notifyDataSetChanged();



//
//                        JSONArray responseItems = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladProductsToOutcome");
//
//                        for (int j = 0; j < responseItems.length(); j++) {
//
//                            JSONObject objectItem = JsonProcs.getItemJSONArray(responseItems, j);
//
//                            ProductCellContainerOutcome productCellContainerOutcome = ProductCellContainerOutcome.FromJson(objectItem);
//
//                            Cell curCell = productCellContainerOutcome.cell;
//                            if (
//                                    (curCell.section.equals(section) || section.isEmpty())
//                                            && (curCell.line.equals(line) || line.isEmpty())
//                                            && (curCell.rack.equals(rack) || rack.isEmpty())
//                                            && (curCell.level.equals(level) || level.isEmpty())
//                                            && (curCell.position.equals(position) || position.isEmpty())
//                            ) {
//
//
//                                items.add(productCellContainerOutcome);
//                            }
//                        }
//
//                        adapter.notifyItemRemoved(removed);
//
//                        adapter.notifyItemInserted(items.size());
//
//                        if (responseItems.length() == 0){
//
//                            Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), new BundleMethodInterface() {
//                                @Override
//                                public void callMethod(Bundle arguments) {
//
//                                    setDocumentStatus("toTest");
//
//                                }
//                            }, new Bundle(), "Завершить документ?", "Завершить");
//                        } else {
//
//
//                            items.sort(new Comparator() {
//                                @Override
//                                public int compare(Object o, Object t1) {
//
//                                    // Ладожский  ZB-ZA-ZE-ZH-ZK-ZC-ZD-ZF-ZI-ZL
//
//                                    ProductCellContainerOutcome pcco1 = ((ProductCellContainerOutcome) o);
//                                    ProductCellContainerOutcome pcco2 = ((ProductCellContainerOutcome) t1);
//
//                                    String l1 = pcco1.cell.level;
//                                    String l2 = pcco2.cell.level;
//                                    String cl1 = pcco1.cell.order + l1 + pcco1.cell.name;
//                                    String cl2 = pcco2.cell.order + l2 + pcco2.cell.name;
//
//                                    return cl1.compareTo(cl2);
//                                }
//                            });
//
//                            adapter.notifyDataSetChanged();
//
//                            if (shtrih != null && !shtrih.isEmpty()) {
//
//                                searchShtrih(items, shtrih);
//
//                            }
//                        }
//



                    }
                });




//        HttpClient httpClient = new HttpClient(getContext());
//
//        httpClient.request_get("/hs/dta/obj?request=getWarehouses&filter=" + filter, new HttpRequestInterface() {
//            @Override
//            public void setProgressVisibility(int visibility) {
//
//                progressBar.setVisibility(visibility);
//
//            }
//
//            @Override
//            public void processResponse(String response) {
//
//                JSONObject jsonObjectResponse = JsonProcs.getJSONObjectFromString(response);
//
//                if (JsonProcs.getBooleanFromJSON(jsonObjectResponse, "success")){
//
//                    JSONArray jsonArrayResponses = JsonProcs.getJsonArrayFromJsonObject(jsonObjectResponse, "responses");
//
//                    JSONObject jsonObjectItem = JsonProcs.getItemJSONArray(jsonArrayResponses, 0);
//
//                    JSONArray jsonArrayObjects = JsonProcs.getJsonArrayFromJsonObject(jsonObjectItem, "Warehouses");
//
//                    for (int j = 0; j < jsonArrayObjects.length(); j++) {
//
//                        JSONObject objectItem = JsonProcs.getItemJSONArray(jsonArrayObjects, j);
//
//                        warehouses.add(Warehouse.WarehouseFromJson(objectItem));
//
//                    }
//
//                    adapter.notifyDataSetChanged();
//
//                }
//
//            }
//
//        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(WarehousesViewModel.class);
        // TODO: Use the ViewModel
    }

}