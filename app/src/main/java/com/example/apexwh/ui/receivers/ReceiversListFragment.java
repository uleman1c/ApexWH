package com.example.apexwh.ui.receivers;

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
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.RequestToServer;
import com.example.apexwh.objects.OutcomeReceiver;
import com.example.apexwh.ui.BundleMethodInterface;
import com.example.apexwh.ui.Dialogs;
import com.example.apexwh.ui.adapters.DataAdapter;
import com.example.apexwh.ui.adapters.ListFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReceiversListFragment extends ListFragment<OutcomeReceiver> {

    String mode;

    public ReceiversListFragment() {

        super(R.layout.fragment_filter_list, R.layout.outcome_receiver_list_item);

        mode = getArguments().getString("mode");

        setListUpdater(new ListUpdater() {
            @Override
            public void update(ArrayList items, ProgressBar progressBar, DataAdapter adapter, String filter) {

                progressBar.setVisibility(View.VISIBLE);

                    int removed = items.size();

                    items.clear();

                    RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladOutcomeByReceiver",
                            "status=collect&filter=" + filter, new JSONObject(), 1,
                            new RequestToServer.ResponseResultInterface() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    progressBar.setVisibility(View.GONE);

                                    JSONArray responseItems = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladOutcomeByReceiver");

                                    for (int j = 0; j < responseItems.length(); j++) {

                                        JSONObject objectItem = JsonProcs.getItemJSONArray(responseItems, j);

                                        items.add(OutcomeReceiver.FromJson(objectItem));

                                    }

                                    adapter.notifyItemRemoved(removed);


                                    adapter.notifyItemInserted(items.size());

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

                        textViews.add((TextView) itemView.findViewById(R.id.tvDescription));
                    }
                });

                getAdapter().setDrawViewHolder(new DataAdapter.DrawViewHolder<OutcomeReceiver>() {
                    @Override
                    public void draw(DataAdapter.ItemViewHolder holder, OutcomeReceiver document) {

                        ((TextView) holder.getTextViews().get(0)).setText(document.name);
                    }
                });

                getAdapter().setOnClickListener(document -> {

                    OutcomeReceiver curOutcome = ((OutcomeReceiver) document);

                    Bundle bundle = new Bundle();
                    bundle.putString("name", curOutcome.name);
                    bundle.putString("ref", curOutcome.ref);
                    bundle.putString("type", curOutcome.type);
                    bundle.putString("order", "");

                    if (mode.equals("collect")){

                        Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), new BundleMethodInterface() {
                            @Override
                            public void callMethod(Bundle arguments) {

                                Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main)
                                        .navigate(R.id.nav_collectProductsByReceiverFragment, arguments);

                            }
                        }, bundle, "Начать отбор " + curOutcome.name + "?", "Начать отбор");

                    }
                    else if (mode.equals("test")){

                        Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), new BundleMethodInterface() {
                            @Override
                            public void callMethod(Bundle arguments) {

                                Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main)
                                        .navigate(R.id.nav_collectProductsByReceiverFragment, arguments);

                            }
                        }, bundle, "Начать проверку " + curOutcome.name + "?", "Начать проверку");

                    }

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


                        res = true;
                        break;

                };

                return res;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);



    }
}