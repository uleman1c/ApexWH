package com.example.apexwh.ui.adapters;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apexwh.HttpClient;
import com.example.apexwh.HttpRequestInterface;
import com.example.apexwh.HttpRequestJsonObjectInterface;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.SoundPlayer;
import com.example.apexwh.objects.DocumentLine;
import com.example.apexwh.ui.BundleMethodInterface;
import com.example.apexwh.ui.Dialogs;
import com.example.apexwh.ui.products.ProductsViewModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

public class ScanProductsFragment<T> extends Fragment {

    private ProductsViewModel mViewModel;

    public ScanProductsFragment() {
    }

    public ScanProductsFragment(String name, String ref, String description) {

        this.ref = ref;
        this.name = name;
        this.description = description;

    }

    public static ScanProductsFragment newInstance() {
        return new ScanProductsFragment();
    }

    protected String ref, name, description;

    private ArrayList<T> lines;

    public DataAdapter<T> getAdapter() {
        return adapter;
    }

    private DataAdapter<T> adapter;

    protected ProgressBar progressBar;
    private RecyclerView recyclerView;

    private EditText actvShtrihCode;
    private InputMethodManager imm;
    private boolean shtrihCodeKeyboard = false, createdFromTsd = false;
    private TextView scannedText;

    Handler hSetFocus;
    protected SoundPlayer soundPlayer;

    public interface ListUpdater{

        void update(String name, String ref, ArrayList lines, ProgressBar progressBar, DataAdapter adapter);

    }

    public void setListUpdater(ListUpdater listUpdater) {
        this.listUpdater = listUpdater;
    }

    private ListUpdater listUpdater;

    public interface ScanCodeSetter<T>{

        void setScanCode(ArrayList<T> lines, String strCatName, int pos, int quantity);

    }

    public void setScanCodeSetter(ScanCodeSetter scanCodeSetter) {
        this.scanCodeSetter = scanCodeSetter;
    }

    private ScanCodeSetter scanCodeSetter;

    public interface OnCreateViewElements{

        void execute(View root);

    }


    public void setOnCreateViewElements(OnCreateViewElements onCreateViewElements) {
        this.onCreateViewElements = onCreateViewElements;
    }

    private OnCreateViewElements onCreateViewElements;

    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);

        Bundle args = getArguments();

        name = args.getString("name");
        ref = args.getString("ref");
        description = args.getString("description");

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

        lines = new ArrayList<T>();

        adapter = new DataAdapter(getContext(), lines, R.layout.document_line_list_item);
        adapter.setOnClickListener(new DataAdapter.OnClickListener<T>() {
            @Override
            public void onItemClick(T document) {

            }
        });
        adapter.setOnLongClickListener(new DataAdapter.OnLongClickListener<T>() {
            @Override
            public void onLongItemClick(T document) {

            }
        });

        recyclerView = root.findViewById(R.id.list);
        recyclerView.setAdapter(adapter);

        if (onCreateViewElements != null) {

            onCreateViewElements.execute(root);

        }

        updateList();

        return root;
    }

    private void showInputNumber(T documentLine){

        Bundle bundle = new Bundle();

//        Dialogs.showInputQuantity(getContext(), documentLine.quantity - documentLine.scanned, getActivity(), new BundleMethodInterface() {
//            @Override
//            public void callMethod(Bundle arguments) {
//
//                setShtrihCode("", documentLine, arguments.getInt("quantity"), new BundleMethodInterface() {
//                    @Override
//                    public void callMethod(Bundle arguments) {
//
//                        testForExecuted();
//
//                    }
//                });
//
//            }
//        }, bundle, "Ввести вручную "
//                + documentLine.productName
//                + (documentLine.characterName.equals("Основная характеристика") ? "" :
//                " (" + documentLine.characterName + ")" ) + " ?", "Ввод количества");


    }

    protected void setScanned(DocumentLine taskItem, Integer quantity) {

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

//            DocumentLine curTI = lines.get(i);
//            scanned += curTI.scanned;
//            quantity += curTI.quantity;
        }

        scannedText.setText(scanned.toString() + " из " + quantity.toString() + ", " + (quantity == 0 ? 0 : (scanned * 100 / quantity)) + "%");
    }

    public void scanShtrihCode(String strCatName, int quantity) {

        if (strCatName.isEmpty()){

            soundPlayer.play();

        }
        else {

            scanCodeSetter.setScanCode(lines, strCatName, -1, 1);

        }

    }

    protected void testForExecuted() {

//        Integer totalToScan = 0;
//
//        for ( DocumentLine line : lines
//             ) {
//
//            totalToScan = totalToScan + line.quantity - line.scanned;
//
//        }
//
//        if (totalToScan == 0){
//
//            Dialogs.showReturnMenu(getContext(), getActivity(), new BundleMethodInterface() {
//                @Override
//                public void callMethod(Bundle arguments) {
//
//                    setDocumentStatus();
//
//
//                }
//            }, new Bundle(), "Завершить документ?", "Завершить");
//        }
    }

    protected void setDocumentStatus() {

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

                navController.popBackStack();

            }
        });
    }

    protected Boolean allScanned(){

        Integer totalToScan = 0;

//        for ( DocumentLine line : lines) {
//
//            totalToScan = totalToScan + line.quantity - line.scanned;
//
//        }

        return  totalToScan == 0;


    }

    protected void updateList() {

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

    private void sendScanned(DocumentLine documentLine, int quantity) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("id", UUID.randomUUID().toString());
        httpClient.addParam("shtrihCode", "");
        httpClient.addParam("appId", httpClient.getDbConstant("appId"));
        httpClient.addParam("quantity", quantity);
        httpClient.addParam("type1c", "doc");
        httpClient.addParam("name1c", name);
        httpClient.addParam("id1c", ref);
        httpClient.addParam("productRef", documentLine.productRef);
        httpClient.addParam("characterRef", documentLine.characterRef);
        httpClient.addParam("characterName", documentLine.characterName);
        httpClient.addParam("comment", "");

        httpClient.request_get("/hs/dta/obj", "setTestProduct", new HttpRequestJsonObjectInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                setScanned(documentLine, quantity);

                if (allScanned()){

                    Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), new BundleMethodInterface() {
                        @Override
                        public void callMethod(Bundle arguments) {

                            setDocumentStatus();

                        }
                    }, new Bundle(), "Завершить проверку?", "Вопрос");

                }

            }
        });



    }



}