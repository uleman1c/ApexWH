package com.example.apexwh.ui.collects;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
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
import com.example.apexwh.HttpRequestInterface;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.RequestToServer;
import com.example.apexwh.SpanText;
import com.example.apexwh.objects.MoversService;
import com.example.apexwh.objects.Outcome;
import com.example.apexwh.objects.Placement;
import com.example.apexwh.ui.BundleMethodInterface;
import com.example.apexwh.ui.Dialogs;
import com.example.apexwh.ui.adapters.DataAdapter;
import com.example.apexwh.ui.adapters.ListFragment;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.UUID;

public class CollectListFragment extends ListFragment<Outcome> {

    public CollectListFragment() {

        super(R.layout.fragment_filter_list, R.layout.movers_service_list_item);


        setListUpdater(new ListUpdater() {
            @Override
            public void update(ArrayList items, ProgressBar progressBar, DataAdapter adapter, String filter) {

                //filter = "334942186980041030224284193605956262215";

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

                                        String message = JsonProcs.getStringFromJSON(responseItem, "Сообщение");

                                        if (!message.isEmpty()) {

                                            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                                            builder.setTitle("Внимание")
                                                    .setMessage(message)
                                                    .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            goToProduts(responseItem, order);


                                                            dialog.cancel();

                                                        }
                                                    }).create().show();


                                        } else {

                                            goToProduts(responseItem, order);
                                        }
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

                    RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladOutcome",
                            "status=collect&filter=" + filter, new JSONObject(), 1,
                            new RequestToServer.ResponseResultInterface() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    progressBar.setVisibility(View.GONE);

                                    JSONArray responseItems = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladOutcome");

                                    for (int j = 0; j < responseItems.length(); j++) {

                                        JSONObject objectItem = JsonProcs.getItemJSONArray(responseItems, j);

                                        items.add(Outcome.FromJson(objectItem));

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

                getAdapter().setInitViewsMaker(new DataAdapter.InitViewsMaker() {
                    @Override
                    public void init(View itemView, ArrayList<TextView> textViews) {

                        textViews.add((TextView) itemView.findViewById(R.id.tvNumberDate));
                        textViews.add((TextView) itemView.findViewById(R.id.tvDescription));
                        textViews.add((TextView) itemView.findViewById(R.id.tvStatus));
                    }
                });

                getAdapter().setDrawViewHolder(new DataAdapter.DrawViewHolder<Outcome>() {
                    @Override
                    public void draw(DataAdapter.ItemViewHolder holder, Outcome document) {

                        String filterString = etFilter.getText().toString();

                        ((TextView) holder.getTextViews().get(0)).setText(SpanText.GetFilteredString("№ " + document.number + " от " + DateStr.FromYmdhmsToDmyhms(document.date), filterString));
                        ((TextView) holder.getTextViews().get(1)).setText(SpanText.GetFilteredString(document.receiver + ", " + document.orderDescription
                                + (document.comment.isEmpty() ? "" : ", " + document.comment) , filterString));
                        ((TextView) holder.getTextViews().get(2)).setText(SpanText.GetFilteredString(document.status, filterString));
                    }
                });

                getAdapter().setOnClickListener(document -> {

                    Outcome curOutcome = ((Outcome) document);

                    Bundle bundle = new Bundle();
                    bundle.putString("name", curOutcome.orderType);
                    bundle.putString("ref", curOutcome.order);
                    bundle.putString("order", "");

                    Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), new BundleMethodInterface() {
                        @Override
                        public void callMethod(Bundle arguments) {

                            Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main)
                                    .navigate(R.id.nav_collectProductsFragment, arguments);

                        }
                    }, bundle, "Начать отбор " + curOutcome.orderDescription + "?", "Начать отбор");

                });

                getAdapter().setOnLongClickListener(document -> {});

            }
        });


    }

    private void goToProduts(JSONObject responseItem, String order) {
        Bundle bundle = new Bundle();
        bundle.putString("name", JsonProcs.getStringFromJSON(responseItem, "Имя"));
        bundle.putString("ref", JsonProcs.getStringFromJSON(responseItem, "Ссылка"));
        bundle.putString("order", order);

        Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main)
                .navigate(R.id.nav_collectProductsFragment, bundle);
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
                menuInflater.inflate(R.menu.collect_list, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull android.view.MenuItem menuItem) {

                boolean res = false;

                switch (menuItem.getItemId()) {

                    case R.id.miSettings:

                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.nav_settings);

                        res = true;

                        break;

                    case R.id.miCollectByReceiver:

                        Bundle bundle = new Bundle();
                        bundle.putString("mode", "collect");
                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.nav_receiversListFragment, bundle);

                        res = true;

                        break;

                };

                return res;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);



    }
}