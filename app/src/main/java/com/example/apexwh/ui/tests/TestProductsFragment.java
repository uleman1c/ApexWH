package com.example.apexwh.ui.tests;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.example.apexwh.HttpClient;
import com.example.apexwh.HttpRequestInterface;
import com.example.apexwh.HttpRequestJsonObjectInterface;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.RequestToServer;
import com.example.apexwh.SoundPlayer;
import com.example.apexwh.objects.Cell;
import com.example.apexwh.objects.DocumentLine;
import com.example.apexwh.objects.ProductCellContainerOutcome;
import com.example.apexwh.ui.BundleMethodInterface;
import com.example.apexwh.ui.Dialogs;
import com.example.apexwh.ui.adapters.BeforeEndOnCreateViewHolder;
import com.example.apexwh.ui.adapters.DataAdapter;
import com.example.apexwh.ui.adapters.DocumentLineAdapter;
import com.example.apexwh.ui.adapters.ListFragment;
import com.example.apexwh.ui.adapters.OnGetItemViewType;
import com.example.apexwh.ui.adapters.ScanListFragment;
import com.example.apexwh.ui.products.ProductsFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class TestProductsFragment extends ScanListFragment<ProductCellContainerOutcome> {

    String name, ref, order;

    TextView tvProduct;

    LinearLayout linearLayout;
    Cell cell;
    protected SoundPlayer soundPlayer;

    ArrayList<ProductCellContainerOutcome> productCellContainerOutcomes;

    public TestProductsFragment() {

            super(R.layout.fragment_scan_list_clear, R.layout.product_border_list_item);


            setListUpdater(new ListFragment.ListUpdater() {
                @Override
                public void update(ArrayList items, ProgressBar progressBar, DataAdapter adapter, String filter) {

                    if (filter.isEmpty()) {

                        updateToScan(items, progressBar, adapter, "");

                    }
                    else {

                        searchShtrih(items, filter);

                    }
                }
            });


            setOnCreateViewElements(new ListFragment.OnCreateViewElements() {
                @Override
                public void execute(View root, NavController navController) {

                    tvProduct = root.findViewById(R.id.tvProduct);

                    linearLayout = root.findViewById(R.id.LinearLayout);

                    soundPlayer = new SoundPlayer(getContext(), R.raw.hrn05);
                    getActivity().setVolumeControlStream(soundPlayer.streamType);


                    root.findViewById(R.id.llCell).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String cellName = tvProduct.getText().toString();

                            if (!cellName.isEmpty() && items.size() > 0) {

                                Bundle args = new Bundle();
                                args.putString("ref", UUID.randomUUID().toString());
                                Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), new BundleMethodInterface() {
                                    @Override
                                    public void callMethod(Bundle arguments) {

                                        RequestToServer.executeRequestUW(getContext(), Request.Method.GET,
                                                "setErpSkladCellClear", "cell=" + cell.ref + "&ref=" + arguments.getString("ref"), new JSONObject(),
                                                RequestToServer.TypeOfResponse.JsonObjectWithArray, response -> {

                                                    JSONObject res = JsonProcs.getJsonObjectFromJsonObject(response, "ErpSkladCellClear");

                                                    listUpdater.update(items, progressBar, adapter, cell.name);

                                                });


                                    }
                                }, args, "Очистить ячейку ?", "Очищение");
                            }
                        }
                    });

                    getAdapter().setOnGetItemViewType(new OnGetItemViewType() {
                        @Override
                        public int Do(int position) {
                            return ((ProductCellContainerOutcome)items.get(position)).mode;
                        }
                    });

                    getAdapter().setBeforeEndOnCreateViewHolder(new BeforeEndOnCreateViewHolder() {
                        @Override
                        public View Do(LayoutInflater inflater, ViewGroup parent, int viewType) {

                            View view = null;

                            if (viewType == 0) {

                                view = inflater.inflate(R.layout.product_border_list_item, parent, false);
                            }
                            else if (viewType == 1) {

                                view = inflater.inflate(R.layout.product_cell_border2_list_item, parent, false);
                            }
                            else {

                                view = inflater.inflate(R.layout.product_cell_border3_list_item, parent, false);
                            }

                            return view;

                        }
                    });


                    getAdapter().setInitViewsMaker(new DataAdapter.InitViewsMaker() {
                        @Override
                        public void init(View itemView, ArrayList<TextView> textViews) {

                            textViews.add(itemView.findViewById(R.id.tvDescription));
                            textViews.add(itemView.findViewById(R.id.tvStatus));
                        }
                    });

                    getAdapter().setDrawViewHolder(new DataAdapter.DrawViewHolder<ProductCellContainerOutcome>() {
                        @Override
                        public void draw(DataAdapter.ItemViewHolder holder, ProductCellContainerOutcome item) {

                            ((TextView) holder.getTextViews().get(0)).setText(item.product.artikul + " " + item.product.name);
                            ((TextView) holder.getTextViews().get(1)).setText(item.number + " шт");
                        }
                    });

                    getAdapter().setOnClickListener(document -> {

                        ProductCellContainerOutcome curPCCO = ((ProductCellContainerOutcome)document);

                        if (true || curPCCO.product.shtrihCodes.size() == 0){

                            Bundle bundle = new Bundle();
                            bundle.putInt("index", items.indexOf(curPCCO));
                            Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), new BundleMethodInterface() {
                                @Override
                                public void callMethod(Bundle arguments) {

                                    ProductCellContainerOutcome foundProduct = ((ProductCellContainerOutcome) items.get(arguments.getInt("index")));

                                    askQuantity(foundProduct);

                                }
                            }, bundle, "Начать отбор номенклатуры " + curPCCO.product.name + "?", "Начать отбор номенклатуры");

                        }

                    });

                    getAdapter().setOnLongClickListener(document -> {

                        ProductCellContainerOutcome curPCCO = (ProductCellContainerOutcome) document;

                        if (curPCCO.mode == 1 && !askQuantityAfterProductScan){

                            askQuantity(curPCCO);

                        }

                    });

                    updateList("");

                }
            });


        }


    private void searchShtrih(ArrayList items, String filter) {
        ProductCellContainerOutcome foundPCCO = null;
        ProductCellContainerOutcome foundProduct = null;
        for (int i = 0; i < items.size() && foundPCCO == null; i++) {

            ProductCellContainerOutcome curPCCO = ((ProductCellContainerOutcome) items.get(i));

            if (curPCCO.mode == 0 && filter.toLowerCase().equals(curPCCO.cell.name.toLowerCase())){

                foundPCCO = curPCCO;

            } else if (curPCCO.mode == 1) {

                if (filter.toLowerCase().equals(curPCCO.product.artikul.toLowerCase())){

                    foundPCCO = curPCCO;
                    foundProduct = curPCCO;

                }
                else {

                    for (int j = 0; j < curPCCO.product.shtrihCodes.size() && foundProduct == null; j++) {

                        if (filter.toLowerCase().equals(curPCCO.product.shtrihCodes.get(j))){

                            foundPCCO = curPCCO;
                            foundProduct = curPCCO;

                        }

                    }

                }

            }

        }

        if (foundPCCO == null && foundProduct == null) {

            soundPlayer.play();
        }

        if (foundPCCO != null && foundProduct == null){

            for (int i = 0; i < items.size(); i++) {

                ProductCellContainerOutcome curPCCO = ((ProductCellContainerOutcome) items.get(i));

                if (curPCCO.mode < 2) {

                    curPCCO.mode = curPCCO == foundPCCO ? 1 : 0;
                    shtrihCodeInput.actvShtrihCode.setHint("Штрихкод товара");
                }
            }

            items.remove(items.indexOf(foundPCCO));

            items.add(0 , foundPCCO);

        }

        if (foundProduct != null){

            if (askQuantityAfterProductScan){

                askQuantity(foundProduct);
            }
            else {

                Bundle bundle = new Bundle();
                bundle.putString("ref", ref);
                bundle.putString("name", name);
                bundle.putString("order", order);
                bundle.putString("cell", foundProduct.cell.ref);
                bundle.putString("container", foundProduct.container.ref);
                bundle.putString("product", foundProduct.product.ref);
                bundle.putInt("quantity", 1);

                doCollect(bundle);


            }


        }

        getAdapter().notifyDataSetChanged();
    }

    protected void setDocumentStatus(String newStatus) {

        RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "setErpSkladDocumentStatus",
                "name=" + name + "&ref=" + ref + "&status=" + newStatus, new JSONObject(), 1, new RequestToServer.ResponseResultInterface() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).popBackStack();

                    }
                });
    }



    private void updateToScan(ArrayList items, ProgressBar progressBar, DataAdapter adapter, String shtrih) {
        shtrihCodeInput.actvShtrihCode.setHint("Штрихкод товара");

        progressBar.setVisibility(View.VISIBLE);

        items.clear();

        productCellContainerOutcomes.clear();

        RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladProductsToTest",
                "name=" + name + "&ref=" + ref, new JSONObject(), 1,
                new RequestToServer.ResponseResultInterface() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progressBar.setVisibility(View.GONE);

                        JSONArray responseItems = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladProductsToTest");

                        for (int j = 0; j < responseItems.length(); j++) {

                            JSONObject objectItem = JsonProcs.getItemJSONArray(responseItems, j);

                            items.add(ProductCellContainerOutcome.FromJson(objectItem));

                        }

                        if (items.size() == 0){

                            Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), new BundleMethodInterface() {
                                @Override
                                public void callMethod(Bundle arguments) {

                                    setDocumentStatus("toTest");

                                }
                            }, new Bundle(), "Завершить документ?", "Завершить");
                        } else {


                            items.sort(new Comparator() {
                                @Override
                                public int compare(Object o, Object t1) {

                                    String l1 = ((ProductCellContainerOutcome) o).cell.level;
                                    String l2 = ((ProductCellContainerOutcome) t1).cell.level;
                                    String cl1 = (l1.equals("P") ? "0" : "") + l1 + ((ProductCellContainerOutcome) o).cell.name;
                                    String cl2 = (l2.equals("P") ? "0" : "") + l2 + ((ProductCellContainerOutcome) t1).cell.name;

                                    return cl1.compareTo(cl2);
                                }
                            });

                            adapter.notifyDataSetChanged();

                            if (shtrih != null && !shtrih.isEmpty()) {

                                searchShtrih(items, shtrih);

                            }
                        }




                    }
                });
    }

    private void addScannedProducts() {

        RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladProductsToOutcomeScanned",
                "name=" + name + "&ref=" + ref, new JSONObject(), 1,
                new RequestToServer.ResponseResultInterface() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progressBar.setVisibility(View.GONE);

                        JSONArray responseItems = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladProductsToOutcomeScanned");

                        for (int j = 0; j < responseItems.length(); j++) {

                            JSONObject objectItem = JsonProcs.getItemJSONArray(responseItems, j);

                            ProductCellContainerOutcome pcco = ProductCellContainerOutcome.FromJson(objectItem);

                            pcco.mode = 2;

                            items.add(pcco);

                        }

                        adapter.notifyDataSetChanged();

                    }
                });



    }

    private void askQuantity(ProductCellContainerOutcome foundProduct) {
        Bundle bundle = new Bundle();
        bundle.putString("ref", ref);
        bundle.putString("name", name);
        bundle.putString("order", order);
        bundle.putString("cell", foundProduct.cell.ref);
        bundle.putString("cellName", foundProduct.cell.name);
        bundle.putString("container", foundProduct.container.ref);
        bundle.putString("product", foundProduct.product.ref);

        Dialogs.showInputQuantity(getContext(), foundProduct.number, getActivity(), new BundleMethodInterface() {
            @Override
            public void callMethod(Bundle arguments) {

                doCollect(arguments);

            }
        }, bundle, "Введите количество " + foundProduct.product.artikul + " " + foundProduct.product.name, "Ввод количества");
    }

    void doCollect(Bundle bundle){


        linearLayout.setVisibility(View.GONE);

        progressBar.setVisibility(View.VISIBLE);


        RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "setErpSkladProductsToTest",
                "doc=" + UUID.randomUUID().toString() + "&cell=" + bundle.getString("cell")
                        + "&container=" + bundle.getString("container")
                        + "&name=" + name + "&ref=" + ref + "&product=" + bundle.getString("product") + "&quantity=" + bundle.getInt("quantity"), new JSONObject(), 1,
                new RequestToServer.ResponseResultInterface() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progressBar.setVisibility(View.GONE);

                        linearLayout.setVisibility(View.VISIBLE);

                        updateToScan(items, progressBar, adapter, askQuantityAfterProductScan ? "" : bundle.getString("cellName"));
                    }
                });

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        name = getArguments().getString("name");
        ref = getArguments().getString("ref");
        order = getArguments().getString("order");

        productCellContainerOutcomes = new ArrayList<>();



//        setListUpdater(new ListUpdater() {
//            @Override
//            public void update(String name, String ref, ArrayList<DocumentLine> lines, ProgressBar progressBar, DocumentLineAdapter adapter) {
//
////                lines.clear();
////
////                HttpClient httpClient = new HttpClient(getContext());
////
////                httpClient.request_get("/hs/dta/obj?request=getLinesToTest&name=" + name + "&id=" + ref, new HttpRequestJsonObjectInterface() {
////
////                    @Override
////                    public void setProgressVisibility(int visibility) {
////
////                        progressBar.setVisibility(visibility);
////
////                    }
////
////                    @Override
////                    public void processResponse(JSONObject jsonObjectResponse) {
////
////                        JSONArray jsonArrayResponses = JsonProcs.getJsonArrayFromJsonObject(jsonObjectResponse, "responses");
////
////                        JSONObject jsonObjectItem = JsonProcs.getItemJSONArray(jsonArrayResponses, 0);
////
////                        JSONArray jsonArrayObjects = JsonProcs.getJsonArrayFromJsonObject(jsonObjectItem, "LinesToTest");
////
////                        for (int j = 0; j < jsonArrayObjects.length(); j++) {
////
////                            JSONObject objectItem = JsonProcs.getItemJSONArray(jsonArrayObjects, j);
////
////                            lines.add(DocumentLine.DocumentLineFromJson(objectItem));
////
////                        }
////
////                        adapter.notifyDataSetChanged();
////
////                    }
////
////                });
//
//
//
//            }
//        });
//
//        setOnCreateViewElements(new OnCreateViewElements() {
//            @Override
//            public void execute(View root) {
//
//                getAdapter().setonBindViewHolderI(new DocumentLineAdapter.onBindViewHolderI() {
//                    @Override
//                    public void OnBindViewHolder(DocumentLineAdapter.DocumentLineItemViewHolder holder, int position, ArrayList<DocumentLine> documentLines) {
//
//                        DocumentLine documentLine = documentLines.get(position);
//
//                        holder.tvArtikul.setText(documentLine.productArtikul);
//                        holder.tvProduct.setText(documentLine.productName
//                                + (documentLine.characterName.isEmpty() || documentLine.characterName.equals("Основная характеристика") ? "" : ", " + documentLine.characterName));
//
//                        String allSK = "";
//
//                        for (String curSK : documentLine.shtrihCodes) {
//
//                            allSK = allSK + (allSK.isEmpty() ? "" : ", ") + curSK;
//
//                        }
//
//                        holder.tvShtrihCodes.setText(allSK);
//
//                        holder.tvScanned.setText(documentLine.scanned.toString() + " из " + documentLine.quantity.toString());
//
//                        if (documentLine.scanned == documentLine.quantity){
//
//                            holder.llMain.setBackgroundColor(Color.parseColor("#00ff00"));
//
//                        }
//
//
//                    }
//                });
//
//                getAdapter().setOnDocumentLineItemClickListener(new DocumentLineAdapter.OnDocumentLineItemClickListener() {
//                    @Override
//                    public void onDocumentLineItemClick(DocumentLine documentLine) {
//
//                        //curdocumentLine = documentLine;
//
//                        Bundle bundle = new Bundle();
////                        bundle.putString("productRef", documentLine.productRef);
////                        bundle.putString("productName", documentLine.productName);
////                        bundle.putString("characterRef", documentLine.characterRef);
////                        bundle.putString("characterName", documentLine.characterName);
//
//                        Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), new BundleMethodInterface() {
//                            @Override
//                            public void callMethod(Bundle arguments) {
//
//                                sendScanned(documentLine, 1);
//
//                            }
//                        }, bundle, "Ввести вручную "
//                                + documentLine.productName
//                                + (documentLine.characterName.equals("Основная характеристика") ? "" :
//                                    " (" + documentLine.characterName + ")" ) + " ?", "Ввод");
//
//
//                    }
//                });
//
//                getAdapter().setOnDocumentLineItemLongClickListener(new DocumentLineAdapter.OnDocumentLineItemLongClickListener() {
//                    @Override
//                    public void onDocumentLineItemLongClick(DocumentLine documentLine) {
//
//                        Bundle bundle = new Bundle();
//
//                        Dialogs.showInputQuantity(getContext(), documentLine.quantity - documentLine.scanned, getActivity(), new BundleMethodInterface() {
//                            @Override
//                            public void callMethod(Bundle arguments) {
//
//                                sendScanned(documentLine, arguments.getInt("quantity"));
//
//                            }
//                        }, bundle, "Ввести вручную "
//                                + documentLine.productName
//                                + (documentLine.characterName.equals("Основная характеристика") ? "" :
//                                " (" + documentLine.characterName + ")" ) + " ?", "Ввод количества");
//
//
//
//                    }
//                });
//
//            }
//        });

    }

//    private void sendScanned(DocumentLine documentLine, int quantity) {
//
//        final HttpClient httpClient = new HttpClient(getContext());
//        httpClient.addParam("id", UUID.randomUUID().toString());
//        httpClient.addParam("shtrihCode", "");
//        httpClient.addParam("appId", httpClient.getDbConstant("appId"));
//        httpClient.addParam("quantity", quantity);
//        httpClient.addParam("type1c", "doc");
//        httpClient.addParam("name1c", name);
//        httpClient.addParam("id1c", ref);
//        httpClient.addParam("productRef", documentLine.productRef);
//        httpClient.addParam("characterRef", documentLine.characterRef);
//        httpClient.addParam("characterName", documentLine.characterName);
//        httpClient.addParam("comment", "");
//
//        httpClient.request_get("/hs/dta/obj", "setTestProduct", new HttpRequestJsonObjectInterface() {
//            @Override
//            public void setProgressVisibility(int visibility) {
//
//                progressBar.setVisibility(visibility);
//            }
//
//            @Override
//            public void processResponse(JSONObject response) {
//
//                setScanned(documentLine, quantity);
//
//                if (allScanned()){
//
//                    Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), new BundleMethodInterface() {
//                        @Override
//                        public void callMethod(Bundle arguments) {
//
//                            setDocumentStatus();
//
//                        }
//                    }, new Bundle(), "Завершить проверку?", "Вопрос");
//
//                }
//
//            }
//        });
//
//
//
//    }
//
}