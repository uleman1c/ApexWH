package com.example.apexwh.ui.products;

import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
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
import com.example.apexwh.SoundPlayer;
import com.example.apexwh.objects.DocumentLine;
import com.example.apexwh.ui.BundleMethodInterface;
import com.example.apexwh.ui.Dialogs;
import com.example.apexwh.ui.adapters.DocumentLineAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

public class ProductsFragment extends Fragment {

    private ProductsViewModel mViewModel;
    private String description;

    public ProductsFragment() {
    }

    public ProductsFragment(String name, String ref, String description) {

        this.ref = ref;
        this.name = name;
        this.description = description;

    }

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
    private SoundPlayer soundPlayer;

    public interface ListUpdater{

        void update(String name, String ref, ArrayList<DocumentLine> lines, ProgressBar progressBar, DocumentLineAdapter adapter);

    }

    public void setListUpdater(ListUpdater listUpdater) {
        this.listUpdater = listUpdater;
    }

    private ListUpdater listUpdater;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Bundle args = getArguments();

        name = args.getString("name");
        ref = args.getString("ref");
        description = args.getString("nameStr") + " № " + args.getString("number") + " от " + args.getString("date");


        getParentFragmentManager().setFragmentResultListener("selectCharacteristic", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {

                String characteristicRef = bundle.getString("characteristicRef");
                String characteristicDescription = bundle.getString("characteristicDescription");
                String productRef = bundle.getString("productRef");
                String productName = bundle.getString("productName");
                String characterRef = bundle.getString("characterRef");
                String characterName = bundle.getString("characterName");

                Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), new BundleMethodInterface() {
                    @Override
                    public void callMethod(Bundle arguments) {

                        final HttpClient httpClient = new HttpClient(getContext());
                        httpClient.addParam("id", UUID.randomUUID().toString());
                        httpClient.addParam("appId", httpClient.getDbConstant("appId"));
                        httpClient.addParam("quantity", 1);
                        httpClient.addParam("type1c", "doc");
                        httpClient.addParam("name1c", name);
                        httpClient.addParam("id1c", ref);
                        httpClient.addParam("comment", "");
                        httpClient.addParam("productRef", productRef);
                        httpClient.addParam("characterRef", characterRef);
                        httpClient.addParam("characteristicRefNew", characteristicRef);
                        httpClient.addParam("characteristicDescriptionNew", characteristicDescription);

                        httpClient.request_get("/hs/dta/obj", "setChangeCharacteristic", new HttpRequestInterface() {
                            @Override
                            public void setProgressVisibility(int visibility) {

                                progressBar.setVisibility(visibility);
                            }

                            @Override
                            public void processResponse(String response) {

                                JSONObject jsonObjectResponse = JsonProcs.getJSONObjectFromString(response);

                                if (JsonProcs.getBooleanFromJSON(jsonObjectResponse, "success")) {

                                    updateList();

                                    //bundleMethodInterface.callMethod(new Bundle());

                                }

                            }
                        });


                    }
                }, bundle, "Номенклатура " + productName + ": заменить характеристику (" + characterName + ") на (" + characteristicDescription + ")",
                        "Замена характеристики");

            }
        });


        View root = inflater.inflate(R.layout.fragment_products, container, false);

        ((TextView) root.findViewById(R.id.tvHeader)).setText(description);

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

        soundPlayer = new SoundPlayer(getContext(), R.raw.hrn05);
        getActivity().setVolumeControlStream(soundPlayer.streamType);


        actvShtrihCode.setOnKeyListener(new View.OnKeyListener() {
                                            public boolean onKey(View v, int keyCode, KeyEvent event) {
                                                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                                                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                                                    String strCatName = actvShtrihCode.getText().toString();

                                                    actvShtrihCode.setText("");

                                                    shtrihCodeKeyboard = false;

                                                    imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

                                                    scanShtrihCode(strCatName, 1);

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

               Bundle bundle = new Bundle();
               bundle.putString("shtrihcode", documentLine.shtrihCodes.get(0));

               Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), new BundleMethodInterface() {
                   @Override
                   public void callMethod(Bundle arguments) {

                       scanShtrihCode(arguments.getString("shtrihcode"), 1);

                   }
               }, bundle, "Ввести вручную?", "Ввод");


           }
        });

        adapter.setOnDocumentLineItemLongClickListener(new DocumentLineAdapter.OnDocumentLineItemLongClickListener() {
            @Override
            public void onDocumentLineItemLongClick(DocumentLine documentLine) {

                Bundle bundle = new Bundle();
                bundle.putString("shtrihcode", documentLine.shtrihCodes.get(0));
                bundle.putInt("toScan", documentLine.quantity - documentLine.scanned);
                bundle.putString("productRef", documentLine.productRef);
                bundle.putString("productName", documentLine.productName);
                bundle.putString("characterRef", documentLine.characterRef);
                bundle.putString("characterName", documentLine.characterName);

                Dialogs.showProductMenu(getContext(), getActivity(), new BundleMethodInterface() {
                    @Override
                    public void callMethod(Bundle arguments) {

                        if (arguments.getString("btn").equals("Foto")){

                            Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.nav_gallery, arguments);

                        } else if (arguments.getString("btn").equals("InputNumber")) {

                            showInputNumber(arguments.getString("shtrihcode"), arguments.getInt("toScan"));

                        } else if (arguments.getString("btn").equals("ChangeCharcteristic")) {

                            Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.nav_characteristics, arguments);

                        }

                    }
                }, bundle, "Выберите", "Меню");


            }
        });

        recyclerView = root.findViewById(R.id.list);
        recyclerView.setAdapter(adapter);

        updateList();

        return root;
    }

    private void showInputNumber(String shtrihcode, int toScan){

        Bundle bundle = new Bundle();
        bundle.putString("shtrihcode", shtrihcode);

        Dialogs.showInputQuantity(getContext(), toScan, getActivity(), new BundleMethodInterface() {
            @Override
            public void callMethod(Bundle arguments) {

                scanShtrihCode(arguments.getString("shtrihcode"), arguments.getInt("quantity"));

            }
        }, bundle, "Введите", "Ввод количества");


    }

    private void onTaskItemFound(DocumentLine taskItem, Integer pos, Integer quantity) {

        taskItem.scanned = taskItem.scanned + quantity;

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

    private void scanShtrihCode(String strCatName, int quantity) {

        if (strCatName.isEmpty()){

            soundPlayer.play();

        }
        else {

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

                setShtrihCode(strCatName, documentLine, i - 1, quantity, new BundleMethodInterface() {
                    @Override
                    public void callMethod(Bundle arguments) {

                        Integer totalToScan = 0;

                        for ( DocumentLine line : lines
                             ) {

                            totalToScan = totalToScan + line.quantity - line.scanned;

                        }

                        if (totalToScan == 0){

                            Dialogs.showReturnMenu(getContext(), getActivity(), new BundleMethodInterface() {
                                @Override
                                public void callMethod(Bundle arguments) {

                                    final HttpClient httpClient = new HttpClient(getContext());
                                    httpClient.addParam("id", UUID.randomUUID().toString());
                                    httpClient.addParam("appId", httpClient.getDbConstant("appId"));
                                    httpClient.addParam("type1c", "doc");
                                    httpClient.addParam("name1c", name);
                                    httpClient.addParam("id1c", ref);
                                    httpClient.addParam("status", "closed");

                                    httpClient.request_get("/hs/dta/obj", "setDocumentStatus", new HttpRequestInterface() {
                                        @Override
                                        public void setProgressVisibility(int visibility) {

                                        }

                                        @Override
                                        public void processResponse(String response) {

                                            Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).popBackStack();

                                        }
                                    });


                                }
                            }, new Bundle(), "Завершить документ?", "Завершить");
                        }


                    }
                });

            } else {

                soundPlayer.play();

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

    }

    protected void setShtrihCode(String strCatName, final DocumentLine documentLine, final int pos, int quantity, BundleMethodInterface bundleMethodInterface) {

        onTaskItemFound(documentLine, pos, quantity);

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("id", UUID.randomUUID().toString());
        httpClient.addParam("shtrihCode", strCatName);
        httpClient.addParam("appId", httpClient.getDbConstant("appId"));
        httpClient.addParam("quantity", quantity);
        httpClient.addParam("type1c", "doc");
        httpClient.addParam("name1c", name);
        httpClient.addParam("id1c", ref);
        httpClient.addParam("comment", "");

        httpClient.request_get("/hs/dta/obj", "setShtrihCode", new HttpRequestInterface() {
                    @Override
                    public void setProgressVisibility(int visibility) {

                        progressBar.setVisibility(visibility);
                    }

                    @Override
                    public void processResponse(String response) {

                        JSONObject jsonObjectResponse = JsonProcs.getJSONObjectFromString(response);

                        if (JsonProcs.getBooleanFromJSON(jsonObjectResponse, "success")) {

                            bundleMethodInterface.callMethod(new Bundle());

                        }

                    }
                });

//                httpClient.request_get("setShtrihCode", new HttpRequestInterface() {
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
//                            if (pos != -1) {
//
//                                onTaskItemFound(curTask, pos);
//                            }
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

    }


    private void updateList() {

        listUpdater.update(name, ref, lines, progressBar, adapter);

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