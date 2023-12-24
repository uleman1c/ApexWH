package com.example.apexwh.ui.invent_tasks;

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
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.RequestToServer;
import com.example.apexwh.SpanText;
import com.example.apexwh.objects.Inventarization;
import com.example.apexwh.ui.Inventarizations.InventarizationsViewModel;
import com.example.apexwh.ui.adapters.DataAdapter;
import com.example.apexwh.ui.adapters.ListFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class InventTasksFragment extends ListFragment<Inventarization> {

    public InventTasksFragment() {
        super(R.layout.fragment_filter_add_list, R.layout.inventarization_list_item);

        setListUpdater(new ListUpdater() {
            @Override
            public void update(ArrayList items, ProgressBar progressBar, DataAdapter adapter, String filter) {

                progressBar.setVisibility(View.VISIBLE);

                int removed = items.size();

                items.clear();

                RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladInventarizations",
                        "filter=" + filter, new JSONObject(), 1,
                        new RequestToServer.ResponseResultInterface() {
                            @Override
                            public void onResponse(JSONObject response) {

                                progressBar.setVisibility(View.GONE);

                                JSONArray responseItems = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladInventarizations");

                                for (int j = 0; j < responseItems.length(); j++) {

                                    JSONObject objectItem = JsonProcs.getItemJSONArray(responseItems, j);

                                    items.add(Inventarization.FromJson(objectItem));

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

                root.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        navController.navigate(R.id.nav_inventarizationProductFragment, new Bundle());

                    }
                });

                getAdapter().setInitViewsMaker(new DataAdapter.InitViewsMaker() {
                    @Override
                    public void init(View itemView, ArrayList<TextView> textViews) {

                        textViews.add((TextView) itemView.findViewById(R.id.tvNumberDate));
                        textViews.add((TextView) itemView.findViewById(R.id.tvDescription));
                    }
                });

                getAdapter().setDrawViewHolder(new DataAdapter.DrawViewHolder<Inventarization>() {
                    @Override
                    public void draw(DataAdapter.ItemViewHolder holder, Inventarization document) {

                        String filterString = etFilter.getText().toString();

                        ((TextView) holder.getTextViews().get(0)).setText(SpanText.GetFilteredString("№ " + document.number + " от " + DateStr.FromYmdhmsToDmyhms(document.date), filterString));
                        ((TextView) holder.getTextViews().get(1)).setText(SpanText.GetFilteredString(document.cell.name, filterString));
                    }
                });

                getAdapter().setOnClickListener(document -> {});

                getAdapter().setOnLongClickListener(document -> {});




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
                //menuInflater.inflate(R.menu.collect_list, menu);
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

}