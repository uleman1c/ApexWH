package com.example.apexwh.ui.collects;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.example.apexwh.DateStr;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.RequestToServer;
import com.example.apexwh.SpanText;
import com.example.apexwh.objects.Collected;
import com.example.apexwh.objects.Outcome;
import com.example.apexwh.objects.ProductCellContainerOutcome;
import com.example.apexwh.ui.BundleMethodInterface;
import com.example.apexwh.ui.Dialogs;
import com.example.apexwh.ui.adapters.BeforeEndOnCreateViewHolder;
import com.example.apexwh.ui.adapters.DataAdapter;
import com.example.apexwh.ui.adapters.ListFragment;
import com.example.apexwh.ui.adapters.OnGetItemViewType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CollectScannedListFragment extends ListFragment<Collected> {

    String ref, name;
    public CollectScannedListFragment() {

        super(R.layout.fragment_filter_list, R.layout.collected_list_item);


        setListUpdater(new ListUpdater() {
            @Override
            public void update(ArrayList items, ProgressBar progressBar, DataAdapter adapter, String filter) {

                progressBar.setVisibility(View.VISIBLE);

                if (filter.length() >= 32){

                    RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladRefByNumberValue", "ref=" + filter, new JSONObject(), 1,
                            new RequestToServer.ResponseResultInterface() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    progressBar.setVisibility(View.GONE);

                                    JSONObject responseItem = JsonProcs.getJsonObjectFromJsonObject(response, "ErpSkladRefByNumberValue");

                                    String order = JsonProcs.getStringFromJSON(responseItem, "Ордер");

                                    if (!order.isEmpty()) {

                                        Bundle bundle = new Bundle();
                                        bundle.putString("name", JsonProcs.getStringFromJSON(responseItem, "Имя"));
                                        bundle.putString("ref", JsonProcs.getStringFromJSON(responseItem, "Ссылка"));
                                        bundle.putString("order", order);

                                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main)
                                                .navigate(R.id.nav_collectProductsFragment, bundle);

                                    } else {

                                        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                                        builder.setTitle("Внимание")
                                                .setMessage("Документ " + filter + " не найден")
                                                .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        etFilter.setText("");

                                                        dialog.cancel();

                                                    }
                                                }).create().show();
                                    }
                                }
                            });
                }
                else {

                    int removed = items.size();

                    items.clear();

                    RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladScanned",
                            "name=" + name + "&ref=" + ref, new JSONObject(), 1,
                            new RequestToServer.ResponseResultInterface() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    progressBar.setVisibility(View.GONE);

                                    JSONArray responseItems = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladScanned");

                                    for (int j = 0; j < responseItems.length(); j++) {

                                        JSONObject objectItem = JsonProcs.getItemJSONArray(responseItems, j);

                                        items.add(Collected.FromJson(objectItem));

                                    }

                                    adapter.notifyItemRemoved(removed);

                                    adapter.notifyItemInserted(items.size());

                                    adapter.notifyDataSetChanged();
                                }
                            });
                }

            }
        });


        setOnCreateViewElements(new OnCreateViewElements() {
            @Override
            public void execute(View root, NavController navController) {

                ref = getArguments().getString("ref");
                name = getArguments().getString("name");
                getAdapter().setInitViewsMaker(new DataAdapter.InitViewsMaker() {
                    @Override
                    public void init(View itemView, ArrayList<TextView> textViews) {

                        textViews.add(itemView.findViewById(R.id.tvNumberDate));
                        textViews.add(itemView.findViewById(R.id.tvDescription));
                        textViews.add(itemView.findViewById(R.id.tvCharacteristic));
                        textViews.add(itemView.findViewById(R.id.tvStatus));
                        textViews.add(itemView.findViewById(R.id.tvQuantity));
                        textViews.add(itemView.findViewById(R.id.tvCell));
                        textViews.add(itemView.findViewById(R.id.tvType));
                    }
                });

                getAdapter().setOnGetItemViewType(new OnGetItemViewType() {
                    @Override
                    public int Do(int position) {
                        return ((Collected)items.get(position)).type.equals("КПроверкеОтправителем") ? 1 : 0;
                    }
                });

                getAdapter().setBeforeEndOnCreateViewHolder(new BeforeEndOnCreateViewHolder() {
                    @Override
                    public View Do(LayoutInflater inflater, ViewGroup parent, int viewType) {

                        View view = null;

                        if (viewType == 0) {

                            view = inflater.inflate(R.layout.collected_list_item, parent, false);
                        }
                        else if (viewType == 1) {

                            view = inflater.inflate(R.layout.collected_warn_list_item, parent, false);
                        }

                        return view;

                    }
                });



                getAdapter().setDrawViewHolder(new DataAdapter.DrawViewHolder<Collected>() {
                    @Override
                    public void draw(DataAdapter.ItemViewHolder holder, Collected document) {

                        SpanText spanText = new SpanText();
                        if (document.characteristic.equals("Основная характеристика")){
                            spanText.Append(document.characteristic);
                        }
                        else {
                            spanText.AppendColor(document.characteristic, Color.MAGENTA);
                        }
                        ((TextView) holder.getTextViews().get(0)).setText(DateStr.FromYmdhmsToDmyhms(document.date));
                        ((TextView) holder.getTextViews().get(1)).setText(document.product);
                        ((TextView) holder.getTextViews().get(2)).setText(spanText.GetSpannableString());
                        ((TextView) holder.getTextViews().get(3)).setText(document.author);
                        ((TextView) holder.getTextViews().get(4)).setText(String.valueOf(document.quantity));
                        ((TextView) holder.getTextViews().get(5)).setText(document.cell);
                        ((TextView) holder.getTextViews().get(6)).setText(document.type);
                    }
                });

                getAdapter().setOnClickListener(document -> {


                });

                getAdapter().setOnLongClickListener(document -> {});

            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();

        RegisterReceiver(getActivity());

    }

    @Override
    public void onPause() {
        super.onPause();

        UnRegisterReceiver(getActivity());

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MenuHost menuHost = requireActivity();

        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                //menuInflater.inflate(R.menu.home_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull android.view.MenuItem menuItem) {

                boolean res = false;

                switch (menuItem.getItemId()) {

                    case R.id.miSettings:

                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.nav_settings);

                        res = true;

                    case R.id.miItem:
                        res = true;

                };

                return res;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);



    }
}