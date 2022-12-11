package com.example.apexwh.ui.products;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.apexwh.HttpClient;
import com.example.apexwh.HttpRequestInterface;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.objects.Document;
import com.example.apexwh.objects.DocumentLine;
import com.example.apexwh.ui.adapters.DocumentDataAdapter;
import com.example.apexwh.ui.adapters.DocumentLineAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductsFragment extends Fragment {

    private ProductsViewModel mViewModel;

    public static ProductsFragment newInstance() {
        return new ProductsFragment();
    }

    private ProgressBar progressBar;
    private String ref, name;

    private ArrayList<DocumentLine> lines;

    private DocumentLineAdapter adapter;
    private RecyclerView recyclerView;

    private EditText actvShtrihCode;
    private InputMethodManager imm;
    private boolean shtrihCodeKeyboard = false, createdFromTsd = false;
    private TextView scannedText, tvBoxNumber, tvExchStatus;

    Handler hSetFocus;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_products, container, false);

        Bundle args = getArguments();

        ref = args.getString("ref");
        name = args.getString("name");

        ((TextView) root.findViewById(R.id.tvHeader)).setText(args.getString("nameStr") + " № " + args.getString("number") + " от " + args.getString("date"));

        actvShtrihCode = root.findViewById(R.id.actvShtrihCode);
        actvShtrihCode.requestFocus();

        hSetFocus = new Handler();
        Thread t = new Thread(new Runnable() {
            public void run() {
                hSetFocus.post(setFocus);
            }
        });
        t.start();

        scannedText = root.findViewById(R.id.scannedText);

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

        actvShtrihCode.setOnKeyListener(new View.OnKeyListener() {
                                            public boolean onKey(View v, int keyCode, KeyEvent event) {
                                                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                                                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                                                    String strCatName = actvShtrihCode.getText().toString();

                                                    actvShtrihCode.setText("");

                                                    shtrihCodeKeyboard = false;

                                                    imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

                                                    scanShtrihCode(strCatName);

                                                    return true;
                                                }
                                                return false;
                                            }
                                        }
        );

        root.findViewById(R.id.ibKeyboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                actvShtrihCode.requestFocus();

                if (shtrihCodeKeyboard) {

                    imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

                } else {

                    imm.showSoftInput(actvShtrihCode, 0, null);
                }

                shtrihCodeKeyboard = !shtrihCodeKeyboard;


            }
        });

        progressBar = root.findViewById(R.id.progressBar);

        lines = new ArrayList<>();

        adapter = new DocumentLineAdapter(getContext(), lines);
        adapter.setOnDocumentLineItemClickListener(new DocumentLineAdapter.OnDocumentLineItemClickListener() {
           @Override
           public void onDocumentLineItemClick(DocumentLine documentLine) {

           }
        });

        recyclerView = root.findViewById(R.id.list);
        recyclerView.setAdapter(adapter);

        updateList();

        return root;
    }

    private void onTaskItemFound(DocumentLine taskItem, Integer pos) {

        taskItem.scanned = taskItem.scanned + 1;

        setScannedText();

        if (taskItem.scanned == taskItem.quantity) {

            //update();


//            testDocumentsLines.sort((testDocumentLine, t1) -> testDocumentLine.);
        }
//        if (taskItem. < 2){
//
//            if (!taskItem.childExist) {
//
//                Integer curPos = pos;
//
//                if (taskItem.level == 0) {
//
//                    Collections.rotate(deliveryOrderTasks, deliveryOrderTasks.size() - pos);
//
//                    curPos = 0;
//                }
//
//                taskItem.childExist = true;
//
//                DeliveryOrderTask cell = new DeliveryOrderTask(taskItem.ref, taskItem.status, taskItem.product, taskItem.shtrih_code, taskItem.product_status,
//                        taskItem.product_part, taskItem.container, taskItem.container_shtrih_code, taskItem.cell, taskItem.cell_shtrih_code, taskItem.quantity);
//                cell.level = taskItem.level + 1;
//
//                deliveryOrderTasks.add(curPos + 1, cell);
//
//                rvTasks.getLayoutManager().scrollToPosition(curPos);
//
//            }
//
//        }

//        if (taskItem.level == 2){
//
//            taskItem.scanned = taskItem.scanned + 1;
//
//            if (taskItem.quantity == taskItem.scanned){
//
//                final HttpClient httpClient = new HttpClient(getContext());
//                httpClient.addParam("refTask", taskItem.ref);
//
//                httpClient.postProc("setSelectTask", new HttpRequestInterface() {
//                    @Override
//                    public void setProgressVisibility(int visibility) {
//                        progressBar.setVisibility(visibility);
//                    }
//
//                    @Override
//                    public void processResponse(JSONObject response) {
//
//                        if (httpClient.getBooleanFromJSON(response, "Success")) {
//
//                            getShtrihs();
//
////                    if (inputQuantity && httpClient.getBooleanFromJSON(response, "IsNew")) {
////
////                        inputQuantity(shtrihcode);
////
////                    } else {
////                        getShtrihs();
////                    }
//
//                        }
//                    }
//                });
//
//
//
//
//            }
//
//
//        }
//

        adapter.notifyDataSetChanged();
    }

    private void setScannedText() {
        Integer scanned = 0;
        Integer quantity = 0;
        for (int i = 0; i < lines.size(); i++) {

            DocumentLine curTI = lines.get(i);
            scanned += curTI.scanned;
            quantity += curTI.quantity;
        }

//        if (!mode.equals("orders")) {
//
//            scanned = 0;
//
//            for (int i = 0; i < scannedItems.size(); i++) {
//                scanned += scannedItems.get(i).quantity;
//            }
//
//        }

        Boolean modeFrom = true; //mode.equals("orders")

        scannedText.setText(scanned.toString() + (modeFrom ? " из " + quantity.toString() + ", " + (quantity == 0 ? 0 : (scanned * 100 / quantity)) + "%" : ""));
    }

    private void scanShtrihCode(String strCatName) {

        Boolean found = false;
        DocumentLine documentLine = null;

        int i;
        for (i = 0; i < lines.size() && !found; i++) {

            documentLine = lines.get(i);

            found = documentLine.shtrihCodes.indexOf(strCatName) > -1;

            if (found) {
//                scannedItems.get(0).product = curTask.productName;
//                scannedItems.get(0).character = curTask.characterName;
            }

        }

        if (found && documentLine.quantity > documentLine.scanned) {

            setShtrihCode(documentLine, i - 1);

        } else {
//
//            if (!sendingInProgress) {
//
//                Integer index = toScan.indexOf(strCatName);
//
//                if (index < 0) {
//
//            error.setText("Не найден штрихкод " + strCatName);

//            setNotFoundShtrihCode(strCatName);
//
//            setProductNames();
//
//            if (createdFromTsd) {
//
//                setScannedText();
//
//            } else {
//
//                soundPlayer.play();
//            }
//                    Boolean present = false;
//                    for (ScannedShtrihCode scannedShtrihCode : scanned) {
//                        present = scannedShtrihCode.shtrihCode.equals(strCatName);
//                        if (present) break;
//                    }
//
//                    if (present) {
//
//                        askForRepeatCode(strCatName);
//
//                    } else {
//
//                        askForNotFoundCode(strCatName);
//
//                    }
//
//
//                } else {
//                    error.setText("");
//
//                    toScan.remove(strCatName);
//
//                    scanned.add(0, new ScannedShtrihCode(strCatName, "", false));
//                    adapterScanned.notifyDataSetChanged();
//
//                    setShtrihs(strCatName);
//                }
//            }
        }



    }

    protected void setShtrihCode(final DocumentLine documentLine, final int pos) {

        onTaskItemFound(documentLine, pos);

//        final HttpClient httpClient = new HttpClient(getContext());
//        httpClient.addParam("ref", ref);
//        httpClient.addParam("shtrihCode", curTask.shtrihCode);
//        httpClient.addParam("boxNumber", boxNumber);
//
//        httpClient.postForResultDelayed("setScanTestProduct", new HttpRequestInterface() {
//            @Override
//            public void setProgressVisibility(int visibility) {
//                progressBar.setVisibility(visibility);
//            }
//
//            @Override
//            public void processResponse(JSONObject response) {
//
//                if (httpClient.getBooleanFromJSON(response, "Success")) {
//
//                    if (pos != -1) {
//
//                        onTaskItemFound(curTask, pos);
//                    }
////                    if (inputQuantity && httpClient.getBooleanFromJSON(response, "IsNew")) {
////
////                        inputQuantity(shtrihcode);
////
////                    } else {
////                        getShtrihs();
////                    }
//
//                }
//            }
//        });

    }

    private void updateList() {

        lines.clear();

        HttpClient httpClient = new HttpClient(getContext());

        httpClient.request_get("/hs/dta/obj?request=getLinesToAccept&name=" + name + "&id=" + ref, new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

                progressBar.setVisibility(visibility);

            }

            @Override
            public void processResponse(String response) {

                JSONObject jsonObjectResponse = JsonProcs.getJSONObjectFromString(response);

                if (JsonProcs.getBooleanFromJSON(jsonObjectResponse, "success")){

                    JSONArray jsonArrayResponses = JsonProcs.getJsonArrayFromJsonObject(jsonObjectResponse, "responses");

                    JSONObject jsonObjectItem = JsonProcs.getItemJSONArray(jsonArrayResponses, 0);

                    JSONArray jsonArrayObjects = JsonProcs.getJsonArrayFromJsonObject(jsonObjectItem, "LinesToAccept");

                    for (int j = 0; j < jsonArrayObjects.length(); j++) {

                        JSONObject objectItem = JsonProcs.getItemJSONArray(jsonArrayObjects, j);

                        lines.add(DocumentLine.DocumentLineFromJson(objectItem));

                    }

                    adapter.notifyDataSetChanged();

                }

            }

        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProductsViewModel.class);
        // TODO: Use the ViewModel
    }

    final Runnable setFocus = new Runnable() {
        public void run() {

            actvShtrihCode.requestFocus();

            if (actvShtrihCode.isFocused() && !shtrihCodeKeyboard) {

                imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

            }

            hSetFocus.postDelayed(setFocus, 500);

        }
    };



}