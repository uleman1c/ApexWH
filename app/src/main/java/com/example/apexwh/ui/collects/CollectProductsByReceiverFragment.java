package com.example.apexwh.ui.collects;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.RequestToServer;
import com.example.apexwh.SoundPlayer;
import com.example.apexwh.SpanText;
import com.example.apexwh.objects.Cell;
import com.example.apexwh.objects.ProductCellContainerOutcome;
import com.example.apexwh.ui.BundleMethodInterface;
import com.example.apexwh.ui.Dialogs;
import com.example.apexwh.ui.adapters.BeforeEndOnCreateViewHolder;
import com.example.apexwh.ui.adapters.DataAdapter;
import com.example.apexwh.ui.adapters.OnGetItemViewType;
import com.example.apexwh.ui.adapters.ScanListFragment;

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
public class CollectProductsByReceiverFragment extends ScanListFragment<ProductCellContainerOutcome> {

    String name, ref, type, order;

    String section, line, rack, level, position;

    TextView tvProduct;

    LinearLayout linearLayout;
    Cell cell;
    protected SoundPlayer soundPlayer;

    ArrayList<ProductCellContainerOutcome> productCellContainerOutcomes;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        name = getArguments().getString("name");
        ref = getArguments().getString("ref");
        type = getArguments().getString("type");

        productCellContainerOutcomes = new ArrayList<>();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        getParentFragmentManager().setFragmentResultListener("setFilter", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                section = result.getString("section");
                line = result.getString("line");
                rack = result.getString("rack");
                level = result.getString("level");
                position = result.getString("position");

            }
        });




        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public CollectProductsByReceiverFragment() {

        super(R.layout.fragment_scan_list_clear, R.layout.product_cell_border_list_item);

        section = "";
        line = "";
        rack = "";
        level = "";
        position = "";

        setListUpdater(new ListUpdater() {
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


        setOnCreateViewElements(new OnCreateViewElements() {
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

                            view = inflater.inflate(R.layout.product_cell_border_list_item, parent, false);
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

                        textViews.add(itemView.findViewById(R.id.tvCell));
                        textViews.add(itemView.findViewById(R.id.tvQuantity));
                        textViews.add(itemView.findViewById(R.id.tvArtikul));
                        textViews.add(itemView.findViewById(R.id.tvDescription));
                        textViews.add(itemView.findViewById(R.id.tvCharacteristic));
                    }
                });

                getAdapter().setDrawViewHolder(new DataAdapter.DrawViewHolder<ProductCellContainerOutcome>() {
                    @Override
                    public void draw(DataAdapter.ItemViewHolder holder, ProductCellContainerOutcome item) {

                        SpanText spanText = new SpanText();
                        if (item.characteristic.description.equals("Основная характеристика")){
                            spanText.Append(item.characteristic.description);
                        }
                        else {
                            spanText.AppendColor(item.characteristic.description, Color.MAGENTA);
                        }

                        ((TextView) holder.getTextViews().get(0)).setText("Ячейка: " + item.cell.name);
                        ((TextView) holder.getTextViews().get(1)).setText(String.valueOf(item.number));
                        ((TextView) holder.getTextViews().get(2)).setText(item.product.artikul);
                        ((TextView) holder.getTextViews().get(3)).setText(item.product.name);
                        ((TextView) holder.getTextViews().get(4)).setText(spanText.GetSpannableString());
                    }
                });

                getAdapter().setOnClickListener(document -> {

                    ProductCellContainerOutcome curPCCO = ((ProductCellContainerOutcome)document);

                    if (curPCCO.mode == 0 ) {

                        if (curPCCO.cell.name.isEmpty()) {

                            Bundle bundle = new Bundle();
                            bundle.putInt("index", items.indexOf(curPCCO));
                            Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), new BundleMethodInterface() {
                                @Override
                                public void callMethod(Bundle arguments) {

                                    ProductCellContainerOutcome foundPCCO = null;

                                    for (int i = 0; i < items.size(); i++) {

                                        ProductCellContainerOutcome curPCCO = ((ProductCellContainerOutcome) items.get(i));

                                        if (i == arguments.getInt("index")) {

                                            foundPCCO = curPCCO;

                                            shtrihCodeInput.actvShtrihCode.setHint("Штрихкод ячейки или товара");

                                        }

                                        if (curPCCO.mode < 2) {

                                            curPCCO.mode = i == arguments.getInt("index") ? 1 : 0;
                                        }

                                    }

                                    items.remove(items.indexOf(foundPCCO));

                                    items.add(0, foundPCCO);

                                    getAdapter().notifyDataSetChanged();

                                    getRecyclerView().smoothScrollToPosition(0);

                                }
                            }, bundle, "Начать отбор из ячейки " + curPCCO.cell.name + "?", "Начать отбор из ячейки");
                        }

                    }
                    else if (false && curPCCO.product.shtrihCodes.size() == 0){

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


    protected void setDocumentStatus(String newStatus) {

        RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "setErpSkladDocumentStatus",
                "name=" + name + "&ref=" + ref + "&status=" + newStatus, new JSONObject(), 1, new RequestToServer.ResponseResultInterface() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).popBackStack();

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
                    shtrihCodeInput.actvShtrihCode.setHint("Штрихкод ячейки или товара");
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
                bundle.putString("characteristic", foundProduct.characteristic.ref);
                bundle.putInt("quantity", 1);

                 doCollect(bundle);


            }


        }

        getAdapter().notifyDataSetChanged();
    }

    private void updateToScan(ArrayList items, ProgressBar progressBar, DataAdapter adapter, String shtrih) {
        shtrihCodeInput.actvShtrihCode.setHint("Штрихкод ячейки");

        items.clear();

        productCellContainerOutcomes.clear();

        RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladProductsToOutcomeByReceiver",
                "type=" + type + "&ref=" + ref, new JSONObject(), 1,
                new RequestToServer.ResponseResultInterface() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progressBar.setVisibility(View.GONE);

                        JSONArray responseItems = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladProductsToOutcomeByReceiver");

                        for (int j = 0; j < responseItems.length(); j++) {

                            JSONObject objectItem = JsonProcs.getItemJSONArray(responseItems, j);

                            ProductCellContainerOutcome productCellContainerOutcome = ProductCellContainerOutcome.FromJson(objectItem);

                            Cell curCell = productCellContainerOutcome.cell;
                            if (
                                    (curCell.section.equals(section) || section.isEmpty())
                                            && (curCell.line.equals(line) || line.isEmpty())
                                            && (curCell.rack.equals(rack) || rack.isEmpty())
                                            && (curCell.level.equals(level) || level.isEmpty())
                                            && (curCell.position.equals(position) || position.isEmpty())
                            ) {


                                items.add(productCellContainerOutcome);
                            }



                        }

                        if (responseItems.length() == 0){

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
        bundle.putString("characteristic", foundProduct.characteristic.ref);

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


        RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "setErpSkladProductsToOutcomeByReceiver",
                "doc=" + UUID.randomUUID().toString() + "&cell=" + bundle.getString("cell")
                        + "&container=" + bundle.getString("container")
                        + "&type=" + type + "&ref=" + ref
                        + "&product=" + bundle.getString("product")
                        + "&characteristic=" + bundle.getString("characteristic")
                        + "&quantity=" + bundle.getInt("quantity"), new JSONObject(), 1,
                new RequestToServer.ResponseResultInterface() {
                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray res = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladProductsToOutcomeByReceiver");
                        JSONObject res0 = JsonProcs.getItemJSONArray(res, 0);

                        int curLeft = JsonProcs.getIntegerFromJSON(res0, "left");

                        if(curLeft > 0 && curLeft < bundle.getInt("quantity")){

                                bundle.putInt("quantity", curLeft);

                                doCollect(bundle);

                        } else {

                            progressBar.setVisibility(View.GONE);

                            linearLayout.setVisibility(View.VISIBLE);

                            updateToScan(items, progressBar, adapter, askQuantityAfterProductScan ? "" : bundle.getString("cellName"));
                        }

                    }
                });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MenuHost menuHost = requireActivity();

        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.collect_product, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull android.view.MenuItem menuItem) {

                boolean res = false;

                Bundle bundle = new Bundle();
                bundle.putString("ref", ref);
                bundle.putString("name", name);
                bundle.putString("type", type);
                bundle.putString("mode", "ByReceiver");

                switch (menuItem.getItemId()) {

                    case R.id.miSettings:

                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.nav_settings);

                        res = true;

                        break;

                    case R.id.miOrderInfo:

                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.nav_OrderInfoFragment, bundle);

                        res = true;

                        break;

                    case R.id.miScanned:

                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.nav_collectScannedListFragment, bundle);

                        res = true;

                        break;

                    case R.id.miFilter:

                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.nav_filterFragment, bundle);

                        res = true;

                        break;

                };

                return res;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);


    }
}
