package com.example.apexwh.ui.placement;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.navigation.NavController;

import com.android.volley.Request;
import com.example.apexwh.DateStr;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.RequestToServer;
import com.example.apexwh.objects.MoversService;
import com.example.apexwh.objects.Placement;
import com.example.apexwh.objects.ProductPlacement;
import com.example.apexwh.ui.adapters.DataAdapter;
import com.example.apexwh.ui.adapters.ListFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.UUID;

public class ProductPlacementListFragment extends ListFragment<ProductPlacement> {

    public ProductPlacementListFragment() {

        super(R.layout.fragment_filter_add_list, R.layout.movers_service_list_item);

        setListUpdater(new ListUpdater() {
            @Override
            public void update(ArrayList items, ProgressBar progressBar, DataAdapter adapter, String filter) {

                items.clear();

                RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladProductPlacements", "filter=" + filter, new JSONObject(), 1,
                        new RequestToServer.ResponseResultInterface() {
                            @Override
                            public void onResponse(JSONObject response) {

                                progressBar.setVisibility(View.GONE);

                                JSONArray responseItems = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladProductPlacements");

                                for (int j = 0; j < responseItems.length(); j++) {

                                    JSONObject objectItem = JsonProcs.getItemJSONArray(responseItems, j);

                                    items.add(ProductPlacement.FromJson(objectItem));

                                }

                                adapter.notifyDataSetChanged();
                            }
                        });


            }
        });


        setOnCreateViewElements(new OnCreateViewElements() {
            @Override
            public void execute(View root, NavController navController) {

                getAdapter().setInitViewsMaker(new DataAdapter.InitViewsMaker() {
                    @Override
                    public void init(View itemView, ArrayList<TextView> textViews) {

                        textViews.add(itemView.findViewById(R.id.tvNumberDate));
                        textViews.add(itemView.findViewById(R.id.tvDescription));
                        textViews.add(itemView.findViewById(R.id.tvStatus));
                    }
                });

                getAdapter().setDrawViewHolder(new DataAdapter.DrawViewHolder<ProductPlacement>() {
                    @Override
                    public void draw(DataAdapter.ItemViewHolder holder, ProductPlacement document) {

                        ((TextView) holder.getTextViews().get(0)).setText("№ " + document.number + " от " + DateStr.FromYmdhmsToDmyhms(document.date));
                        ((TextView) holder.getTextViews().get(1)).setText(document.description);
                        ((TextView) holder.getTextViews().get(2)).setText("Контейнер: " + document.container
                                + ", " + document.product + ", " + document.quantity + " шт");
                    }
                });

                getAdapter().setOnClickListener(document -> {});

                getAdapter().setOnLongClickListener(document -> {});

                root.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Bundle bundle = new Bundle();

                        TimeZone timeZone = TimeZone.getTimeZone("Europe/Moscow");

                        Calendar calendar = new GregorianCalendar();
                        calendar.roll(Calendar.HOUR_OF_DAY, timeZone.getRawOffset() / (3600 * 1000));

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");


                        MoversService moversService = new MoversService(UUID.randomUUID().toString(), "",
                                simpleDateFormat.format(calendar.getTime()),
                                "", "", 0, 0.0, "", new ArrayList<>());

                        bundle.putString("record", new JSONArray(moversService.getObjectDescription()).toString());

                        navController.navigate(R.id.nav_placementFragment, bundle);

                    }
                });



            }
        });


    }


}