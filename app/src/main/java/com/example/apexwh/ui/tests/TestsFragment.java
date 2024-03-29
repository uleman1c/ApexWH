package com.example.apexwh.ui.tests;

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
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.example.apexwh.DateStr;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.RequestToServer;
import com.example.apexwh.SpanText;
import com.example.apexwh.objects.Outcome;
import com.example.apexwh.ui.BundleMethodInterface;
import com.example.apexwh.ui.Dialogs;
import com.example.apexwh.ui.adapters.DataAdapter;
import com.example.apexwh.ui.adapters.ListFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TestsFragment extends ListFragment<Outcome>{

    public TestsFragment() {

        super(R.layout.fragment_filter_list, R.layout.test_list_item);

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
                                        bundle.putString("type", JsonProcs.getStringFromJSON(responseItem, "Имя"));
                                        bundle.putString("ref", JsonProcs.getStringFromJSON(responseItem, "Ссылка"));
                                        bundle.putString("order", order);

                                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main)
                                                .navigate(R.id.nav_testProductsFragment, bundle);

//                                        DoStartTest(Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main),
//                                                JsonProcs.getStringFromJSON(responseItem, "Имя"),
//                                                JsonProcs.getStringFromJSON(responseItem, "Ссылка"));

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
                            "status=test&filter=" + filter, new JSONObject(), 1,
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

                        textViews.add(itemView.findViewById(R.id.tvNumberDate));
                        textViews.add(itemView.findViewById(R.id.tvDescription));
                        textViews.add(itemView.findViewById(R.id.tvStatus));
                    }
                });

                getAdapter().setDrawViewHolder(new DataAdapter.DrawViewHolder<Outcome>() {
                    @Override
                    public void draw(DataAdapter.ItemViewHolder holder, Outcome document) {

                        String filterString = etFilter.getText().toString();

                        ((TextView) holder.getTextViews().get(0)).setText(SpanText.GetFilteredString("№ " + document.number + " от " + DateStr.FromYmdhmsToDmyhms(document.date), filterString));
                        ((TextView) holder.getTextViews().get(1)).setText(SpanText.GetFilteredString(document.receiver + ", " + document.orderDescription
                                + (document.comment.isEmpty() ? "" : ", " + document.comment), filterString) );
                        ((TextView) holder.getTextViews().get(2)).setText(SpanText.GetFilteredString(document.status, filterString));

//                        HashMap statuses = new HashMap();
//                        statuses.put("closed", "Завершена");
//
//                        String curStatus = (String) statuses.get(document.status);
//
//                        ((TextView) holder.getTextViews().get(2)).setText(curStatus != null ? curStatus : document.status);
                    }
                });

                getAdapter().setOnClickListener(new DataAdapter.OnClickListener<Outcome>() {
                    @Override
                    public void onItemClick(Outcome document) {

                        Outcome curOutcome = ((Outcome) document);

                        Bundle bundle = new Bundle();
                        bundle.putString("name", curOutcome.orderType);
                        bundle.putString("type", curOutcome.orderType);
                        bundle.putString("ref", curOutcome.order);
                        bundle.putString("order", "");

                        Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), new BundleMethodInterface() {
                            @Override
                            public void callMethod(Bundle arguments) {

                                navController.navigate(R.id.nav_testProductsFragment, bundle);

                            }
                        }, bundle, "Начать проверку " + curOutcome.orderDescription + "?", "Начать проверку");


                    }
                });

                getAdapter().setOnLongClickListener(document -> {

                    Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), new BundleMethodInterface() {
                        @Override
                        public void callMethod(Bundle arguments) {

                            setDocumentStatus((Outcome) document, "toShipping");

                        }
                    }, new Bundle(), "Завершить документ?", "Завершить");



                });



                getParentFragmentManager().setFragmentResultListener("buier_order_selected", getViewLifecycleOwner(), new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {

                        JSONObject jsonObject = JsonProcs.getJSONObjectFromString(bundle.getString("selected"));

//                        DoStartTest(navController, JsonProcs.getStringFromJSON(jsonObject, "name"), JsonProcs.getStringFromJSON(jsonObject, "ref"));
//
//                        navController.navigate(R.id.nav_testProductsFragment, bundle);


                    }
                });



            }
        });

    }

    protected void setDocumentStatus(Outcome document, String newStatus) {

        RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "setErpSkladDocumentStatus",
                "type1c=" + (document.orderType.equals("Партнеры") ? "ref" : "doc")
                        + "&name=" + document.orderType + "&ref=" + document.order + "&status=" + newStatus, new JSONObject(), 1, new RequestToServer.ResponseResultInterface() {
                    @Override
                    public void onResponse(JSONObject response) {

                        listUpdater.update(items, progressBar, adapter, "");

                        //Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).popBackStack();

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
                menuInflater.inflate(R.menu.test_list, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull android.view.MenuItem menuItem) {

                boolean res = false;

                switch (menuItem.getItemId()) {

                    case R.id.miSettings:

                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.nav_settings);

                        res = true;

                        break;

                    case R.id.miTestByReceiver:

                        Bundle bundle = new Bundle();
                        bundle.putString("mode", "test");
                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.nav_receiversListFragment, bundle);

                        res = true;

                        break;

                };

                return res;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);



    }


}