package com.example.apexwh.ui.acceptment;

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
public class AcceptmentProductsFragment extends ScanListFragment<ProductCellContainerOutcome> {

    String name, ref, order, mode;

    TextView tvProduct;

    LinearLayout linearLayout;
    Cell cell;
    protected SoundPlayer soundPlayer;

    ArrayList<ProductCellContainerOutcome> productCellContainerOutcomes = new ArrayList<>();

    public AcceptmentProductsFragment() {

        super(R.layout.fragment_scan_list_clear, R.layout.test_product_border_list_item);


        setListUpdater(new ListUpdater() {
            @Override
            public void update(ArrayList items, ProgressBar progressBar, DataAdapter adapter, String filter) {

                if (filter.isEmpty()) {

                    updateToScan(items, progressBar, adapter, "");

                } else {

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

                    }
                });

                getAdapter().setOnGetItemViewType(new OnGetItemViewType() {
                    @Override
                    public int Do(int position) {
                        return ((ProductCellContainerOutcome) items.get(position)).mode;
                    }
                });

                getAdapter().setBeforeEndOnCreateViewHolder(new BeforeEndOnCreateViewHolder() {
                    @Override
                    public View Do(LayoutInflater inflater, ViewGroup parent, int viewType) {

                        View view = null;

                        if (viewType == 0) {

                            view = inflater.inflate(R.layout.test_product_border_list_item, parent, false);
                        } else if (viewType == 1) {

                            view = inflater.inflate(R.layout.product_cell_border2_list_item, parent, false);
                        } else {

                            view = inflater.inflate(R.layout.product_cell_border3_list_item, parent, false);
                        }

                        return view;

                    }
                });


                getAdapter().setInitViewsMaker(new DataAdapter.InitViewsMaker() {
                    @Override
                    public void init(View itemView, ArrayList<TextView> textViews) {

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

                        ((TextView) holder.getTextViews().get(0)).setText(String.valueOf(item.number));
                        ((TextView) holder.getTextViews().get(1)).setText(item.product.artikul);
                        ((TextView) holder.getTextViews().get(2)).setText(item.product.name);
                        ((TextView) holder.getTextViews().get(3)).setText(spanText.GetSpannableString());
                    }
                });

                getAdapter().setOnClickListener(document -> {

                    ProductCellContainerOutcome curPCCO = ((ProductCellContainerOutcome) document);

                    if (true || curPCCO.product.shtrihCodes.size() == 0) {

                        Bundle bundle = new Bundle();
                        bundle.putInt("index", items.indexOf(curPCCO));
                        Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), new BundleMethodInterface() {
                            @Override
                            public void callMethod(Bundle arguments) {

                                ProductCellContainerOutcome foundProduct = ((ProductCellContainerOutcome) items.get(arguments.getInt("index")));

                                askQuantity(foundProduct);

                            }
                        }, bundle, "Начать приемку номенклатуры " + curPCCO.product.name + "?", "Начать приемку номенклатуры");

                    }

                });

                getAdapter().setOnLongClickListener(document -> {

                    ProductCellContainerOutcome curPCCO = (ProductCellContainerOutcome) document;

                    askForTest(curPCCO);

                });

                updateList("");

            }
        });


    }

    private void askForTest(ProductCellContainerOutcome foundProduct) {
        Bundle bundle = new Bundle();
        bundle.putString("ref", ref);
        bundle.putString("name", name);
        bundle.putString("order", order);
        bundle.putString("cell", foundProduct.cell.ref);
        bundle.putString("cellName", foundProduct.cell.name);
        bundle.putString("container", foundProduct.container.ref);
        bundle.putString("product", foundProduct.product.ref);
        bundle.putInt("quantity", foundProduct.number);

        Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), new BundleMethodInterface() {
            @Override
            public void callMethod(Bundle arguments) {

                sendProductToTestBySender(arguments);

            }
        }, bundle, "Отправить в проверку " + foundProduct.product.artikul + " " + foundProduct.product.name + ", " + foundProduct.productNumber + "?", "Отправить в проверку");
    }

    private void sendProductToTestBySender(Bundle bundle) {

        linearLayout.setVisibility(View.GONE);

        progressBar.setVisibility(View.VISIBLE);

        RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "setErpSkladProductToTestBySender",
                "doc=" + UUID.randomUUID().toString()
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


    private void searchShtrih(ArrayList items, String filter) {

        ProductCellContainerOutcome foundPCCO = null;
        ProductCellContainerOutcome foundProduct = null;
        for (int i = 0; i < items.size() && foundPCCO == null; i++) {

            ProductCellContainerOutcome curPCCO = ((ProductCellContainerOutcome) items.get(i));

            if (filter.toLowerCase().equals(curPCCO.product.artikul.toLowerCase())) {

                foundPCCO = curPCCO;
                foundProduct = curPCCO;

            } else {

                for (int j = 0; j < curPCCO.product.shtrihCodes.size() && foundProduct == null; j++) {

                    if (filter.toLowerCase().equals(curPCCO.product.shtrihCodes.get(j))) {

                        foundPCCO = curPCCO;
                        foundProduct = curPCCO;

                    }

                }

            }

        }

        if (foundPCCO == null && foundProduct == null) {

            soundPlayer.play();
        }

        if (foundProduct != null) {

            if (askQuantityAfterProductScan) {

                askQuantity(foundProduct);

            } else {

                Bundle bundle = new Bundle();
                bundle.putString("ref", ref);
                bundle.putString("name", name);
                bundle.putString("order", order);
                bundle.putString("cell", foundProduct.cell.ref);
                bundle.putString("container", foundProduct.container.ref);
                bundle.putString("product", foundProduct.product.ref);
                bundle.putString("characteristic", foundProduct.characteristic.ref);
                bundle.putInt("quantity", 1);

                doAccept(bundle);


            }


        }

        getAdapter().notifyDataSetChanged();
    }

    protected void setDocumentStatus(String newStatus) {

        progressBar.setVisibility(View.VISIBLE);

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

        int removed = items.size();

        items.clear();

        productCellContainerOutcomes.clear();

        RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladProductsToAccept",
                "name=" + name + "&ref=" + ref, new JSONObject(), 1,
                new RequestToServer.ResponseResultInterface() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progressBar.setVisibility(View.GONE);

                        JSONArray responseItems = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladProductsToAccept");

                        for (int j = 0; j < responseItems.length(); j++) {

                            JSONObject objectItem = JsonProcs.getItemJSONArray(responseItems, j);

                            items.add(ProductCellContainerOutcome.FromJson(objectItem));

                        }

                        adapter.notifyItemRemoved(removed);

                        adapter.notifyItemInserted(items.size());

                        if (items.size() == 0) {

                            Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), new BundleMethodInterface() {
                                @Override
                                public void callMethod(Bundle arguments) {

                                    setDocumentStatus("Accepted");

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

                doAccept(arguments);

            }
        }, bundle, "Введите количество " + foundProduct.product.artikul + " " + foundProduct.product.name
                + (foundProduct.characteristic.description.isEmpty() || foundProduct.characteristic.description.equals("Основная характеристика") ? ""
                    : ", " + foundProduct.characteristic.description) , "Ввод количества");
    }

    void doAccept(Bundle bundle) {

        linearLayout.setVisibility(View.GONE);

        progressBar.setVisibility(View.VISIBLE);

        RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "setErpSkladProductsToAccept",
                "doc=" + UUID.randomUUID().toString()
                        + "&name=" + name + "&ref=" + ref
                        + "&product=" + bundle.getString("product")
                        + "&characteristic=" + bundle.getString("characteristic")
                        + "&quantity=" + bundle.getInt("quantity"), new JSONObject(), 1,
                new RequestToServer.ResponseResultInterface() {
                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray res = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladProductsToAccept");
                        JSONObject res0 = JsonProcs.getItemJSONArray(res, 0);

                        int curLeft = JsonProcs.getIntegerFromJSON(res0, "left");

                        if(curLeft > 0 && curLeft < bundle.getInt("quantity")){

                            bundle.putInt("quantity", curLeft);

                            doAccept(bundle);

                        } else {


                            progressBar.setVisibility(View.GONE);

                            linearLayout.setVisibility(View.VISIBLE);

                            updateToScan(items, progressBar, adapter, askQuantityAfterProductScan ? "" : bundle.getString("cellName"));
                        }
                    }
                });
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        name = getArguments().getString("name");
        ref = getArguments().getString("ref");
        order = getArguments().getString("order");
        mode = getArguments().getString("mode");

        productCellContainerOutcomes = new ArrayList<>();


    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MenuHost menuHost = requireActivity();

        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.accept_product, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull android.view.MenuItem menuItem) {

                boolean res = false;

                Bundle bundle = new Bundle();
                bundle.putString("ref", ref);
                bundle.putString("name", name);

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

                    case R.id.miAdd:

                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.nav_productLineFragment, bundle);

                        res = true;

                        break;

                };

                return res;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);


    }



}