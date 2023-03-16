package com.example.apexwh.ui.orders_to_change_characteristics;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.apexwh.HttpClient;
import com.example.apexwh.HttpRequestInterface;
import com.example.apexwh.HttpRequestJsonObjectInterface;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.objects.DocumentLine;
import com.example.apexwh.objects.OrderToChangeCharactericticLine;
import com.example.apexwh.ui.BundleMethodInterface;
import com.example.apexwh.ui.Dialogs;
import com.example.apexwh.ui.adapters.DataAdapter;
import com.example.apexwh.ui.adapters.ScanProductsFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderToChangeCharacteristicProductsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderToChangeCharacteristicProductsFragment extends ScanProductsFragment<OrderToChangeCharactericticLine> {

    private ArrayList<OrderToChangeCharactericticLine> baseProducts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListUpdater(new ListUpdater() {
            @Override
            public void update(String name, String ref, ArrayList lines, ProgressBar progressBar, DataAdapter adapter) {

                lines.clear();

                HttpClient httpClient = new HttpClient(getContext());

                httpClient.request_get("/hs/dta/obj?request=getOrderToChangeCharacteristic&name=" + name + "&id=" + ref, new HttpRequestJsonObjectInterface() {

                    @Override
                    public void setProgressVisibility(int visibility) {

                        progressBar.setVisibility(visibility);

                    }

                    @Override
                    public void processResponse(JSONObject jsonObjectResponse) {

                        JSONArray jsonArrayResponses = JsonProcs.getJsonArrayFromJsonObject(jsonObjectResponse, "responses");

                        JSONObject jsonObjectItem = JsonProcs.getItemJSONArray(jsonArrayResponses, 0);

                        JSONArray jsonArrayObjects = JsonProcs.getJsonArrayFromJsonObject(jsonObjectItem, "OrderToChangeCharacteristic");

                        JSONObject objectItem = JsonProcs.getItemJSONArray(jsonArrayObjects, 0);

                        JSONArray products = JsonProcs.getJsonArrayFromJsonObject(objectItem, "products");

                        lines.clear();

                        for (int j = 0; j < products.length(); j++) {

                            JSONObject productItem = JsonProcs.getItemJSONArray(products, j);

                            lines.add(OrderToChangeCharactericticLine.FromJson(productItem));

                        }

                        JSONArray baproducts = JsonProcs.getJsonArrayFromJsonObject(objectItem, "baseProducts");

                        baseProducts.clear();

                        for (int j = 0; j < baproducts.length(); j++) {

                            JSONObject productItem = JsonProcs.getItemJSONArray(products, j);

                            baseProducts.add(OrderToChangeCharactericticLine.FromJson(productItem));

                        }

                        adapter.notifyDataSetChanged();

                    }

                });


            }
        });

        setOnCreateViewElements(new ScanProductsFragment.OnCreateViewElements() {
            @Override
            public void execute(View root) {

                baseProducts = new ArrayList<>();

                getParentFragmentManager().setFragmentResultListener("selectCharacteristic", getViewLifecycleOwner(), new FragmentResultListener() {
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

                                                }

                                            }
                                        });


                                    }
                                }, bundle, "Номенклатура " + productName + ": заменить характеристику (" + characterName + ") на (" + characteristicDescription + ")",
                                "Замена характеристики");

                    }
                });

                getAdapter().setInitViewsMaker(new DataAdapter.InitViewsMaker() {
                    @Override
                    public void init(View itemView, ArrayList<TextView> textViews) {

                        textViews.add(itemView.findViewById(R.id.tvProduct));
                        textViews.add(itemView.findViewById(R.id.tvShtrihCodes));
                        textViews.add(itemView.findViewById(R.id.tvScanned));
                    }
                });

                getAdapter().setDrawViewHolder(new DataAdapter.DrawViewHolder<OrderToChangeCharactericticLine>() {
                    @Override
                    public void draw(DataAdapter.ItemViewHolder holder, OrderToChangeCharactericticLine documentLine) {

                        ((TextView) holder.getTextViews().get(0)).setText(documentLine.productDescription
                                + (documentLine.characterDescription.isEmpty() || documentLine.characterDescription.equals("Основная характеристика") ? ""
                                    : ", " + documentLine.characterDescription));

                        String allSK = "";

                        for (String curSK:   documentLine.shtrihCodes          ) {

                            allSK = allSK + (allSK.isEmpty() ? "" : ", ") + curSK;

                        }

                        ((TextView) holder.getTextViews().get(1)).setText(allSK);

                        ((TextView) holder.getTextViews().get(2)).setText(documentLine.scanned.toString() + " из " + documentLine.number.toString());




                    }
                });

                getAdapter().setOnClickListener(new DataAdapter.OnClickListener<OrderToChangeCharactericticLine>() {
                   @Override
                   public void onItemClick(OrderToChangeCharactericticLine documentLine) {

                       Bundle bundle = new Bundle();
                       //bundle.putString("shtrihcode", documentLine.shtrihCodes.get(0));

                       Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), new BundleMethodInterface() {
                           @Override
                           public void callMethod(Bundle arguments) {

        //                       scanShtrihCode(arguments.getString("shtrihcode"), 1);

                               setShtrihCode("", documentLine, 1, new BundleMethodInterface() {
                                   @Override
                                   public void callMethod(Bundle arguments) {

                                       testForExecuted();


                                   }
                               });

                               //sendScanned(documentLine, 1);

                           }
                       }, bundle, "Ввести вручную "
                               + documentLine.productDescription
                               + (documentLine.characterDescription.equals("Основная характеристика") ? "" :
                               " (" + documentLine.characterDescription + ")" ) + " ?", "Ввод");


                   }
                });

//        adapter.setOnDocumentLineItemLongClickListener(new DocumentLineAdapter.OnDocumentLineItemLongClickListener() {
//            @Override
//            public void onDocumentLineItemLongClick(DocumentLine documentLine) {
//
//                Bundle bundle = new Bundle();
//                bundle.putString("shtrihcode", "");
//                bundle.putInt("toScan", documentLine.quantity - documentLine.scanned);
//                bundle.putString("productRef", documentLine.productRef);
//                bundle.putString("productName", documentLine.productName);
//                bundle.putString("characterRef", documentLine.characterRef);
//                bundle.putString("characterName", documentLine.characterName);
//
//                Dialogs.showProductMenu(getContext(), getActivity(), new BundleMethodInterface() {
//                    @Override
//                    public void callMethod(Bundle arguments) {
//
//                        if (arguments.getString("btn").equals("Foto")){
//
//                            Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.nav_gallery, arguments);
//
//                        } else if (arguments.getString("btn").equals("InputNumber")) {
//
//                            showInputNumber(documentLine);
//
//                        } else if (arguments.getString("btn").equals("ChangeCharcteristic")) {
//
//                            Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.nav_characteristics, arguments);
//
//                        }
//
//                    }
//                }, bundle, "Выберите", "Меню");
//
//
//            }
//        });



            }
        });

        setScanCodeSetter(new ScanCodeSetter<OrderToChangeCharactericticLine>() {
            @Override
            public void setScanCode(String strCatName, int pos, int quantity) {

                Boolean found = false;
                OrderToChangeCharactericticLine documentLine = null;

                int i;
                for (i = 0; i < lines.size() && !found; i++) {

                    documentLine = lines.get(i);

                    found = documentLine.shtrihCodes.indexOf(strCatName) > -1;

                    if (found) {
                        //                scannedItems.get(0).product = curTask.productName;
                        //                scannedItems.get(0).character = curTask.characterName;
                    }

                }

                if (found && documentLine.number > documentLine.scanned) {

                    setShtrihCode(strCatName, documentLine, quantity, new BundleMethodInterface() {
                        @Override
                        public void callMethod(Bundle arguments) {

                            testForExecuted();


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
        });

    }

    private void setShtrihCode(String strCatName, final OrderToChangeCharactericticLine documentLine, int quantity, BundleMethodInterface bundleMethodInterface) {

        //scanCodeSetter.setScanCode(strCatName, );


        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("id", UUID.randomUUID().toString());
        httpClient.addParam("shtrihCode", strCatName);
        httpClient.addParam("appId", httpClient.getDbConstant("appId"));
        httpClient.addParam("quantity", quantity);
        httpClient.addParam("type1c", "doc");
        httpClient.addParam("name1c", name);
        httpClient.addParam("id1c", ref);
        httpClient.addParam("comment", "");
        httpClient.addParam("productRef", documentLine.productRef);
        httpClient.addParam("characterRef", documentLine.characterRef);
        httpClient.addParam("characterName", documentLine.characterDescription);

        httpClient.request_get("/hs/dta/obj", "setShtrihCode", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(String response) {

                JSONObject jsonObjectResponse = JsonProcs.getJSONObjectFromString(response);

                if (JsonProcs.getBooleanFromJSON(jsonObjectResponse, "success")) {

                    //setScanned(documentLine, quantity);

                    if (allScanned()) {

                        bundleMethodInterface.callMethod(new Bundle());

                    }

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

    private void testForExecuted() {

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

    private void sendScanned(OrderToChangeCharactericticLine documentLine, int quantity) {

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
        httpClient.addParam("characterName", documentLine.characterDescription);
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


    private void showInputNumber(OrderToChangeCharactericticLine documentLine){

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

    private void setScanned(OrderToChangeCharactericticLine taskItem, Integer quantity) {

        taskItem.scanned = taskItem.scanned + quantity;

        Integer scanned = 0;
        Integer number = 0;
        for (int i = 0; i < lines.size(); i++) {

//            DocumentLine curTI = lines.get(i);
//            scanned += curTI.scanned;
//            quantity += curTI.quantity;
        }

        setScannedText(scanned.toString() + " из " + number.toString() + ", " + (number == 0 ? 0 : (scanned * 100 / number)) + "%");

        if (taskItem.scanned == taskItem.number) {

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

        getAdapter().notifyDataSetChanged();
    }

    private void setDocumentStatus() {

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

    private Boolean allScanned(){

        Integer totalToScan = 0;

//        for ( DocumentLine line : lines) {
//
//            totalToScan = totalToScan + line.quantity - line.scanned;
//
//        }

        return  totalToScan == 0;


    }



}