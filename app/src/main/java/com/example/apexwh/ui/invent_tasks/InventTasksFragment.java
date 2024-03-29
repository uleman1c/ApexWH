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
import com.example.apexwh.objects.InventTask;
import com.example.apexwh.objects.Inventarization;
import com.example.apexwh.ui.Inventarizations.InventarizationsViewModel;
import com.example.apexwh.ui.adapters.DataAdapter;
import com.example.apexwh.ui.adapters.ListFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class InventTasksFragment extends ListFragment<InventTask> {

    public InventTasksFragment() {
        super(R.layout.fragment_filter_list, R.layout.inventarization_list_item);

        setListUpdater(new ListUpdater() {
            @Override
            public void update(ArrayList items, ProgressBar progressBar, DataAdapter adapter, String filter) {

                progressBar.setVisibility(View.VISIBLE);

                int removed = items.size();

                items.clear();

                RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladInventTasks",
                        "filter=" + filter, new JSONObject(), 1,
                        new RequestToServer.ResponseResultInterface() {
                            @Override
                            public void onResponse(JSONObject response) {

                                progressBar.setVisibility(View.GONE);

                                JSONArray responseItems = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladInventTasks");

                                for (int j = 0; j < responseItems.length(); j++) {

                                    JSONObject objectItem = JsonProcs.getItemJSONArray(responseItems, j);

                                    items.add(InventTask.FromJson(objectItem));

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

                        textViews.add((TextView) itemView.findViewById(R.id.tvNumberDate));
                        textViews.add((TextView) itemView.findViewById(R.id.tvDescription));
                    }
                });

                getAdapter().setDrawViewHolder(new DataAdapter.DrawViewHolder<InventTask>() {
                    @Override
                    public void draw(DataAdapter.ItemViewHolder holder, InventTask inventTask) {

                        String filterString = etFilter.getText().toString();

                        ((TextView) holder.getTextViews().get(0)).setText(SpanText.GetFilteredString("№ " + inventTask.document.number
                                + " от " + DateStr.FromYmdhmsToDmyhms(inventTask.document.date), filterString));
                        ((TextView) holder.getTextViews().get(1)).setText(SpanText.GetFilteredString(inventTask.cell.name, filterString.toUpperCase()));
                    }
                });

                getAdapter().setOnClickListener(inventTask -> {

                    Bundle bundle = new Bundle();
                    bundle.putString("inventTask", InventTask.toJson((InventTask) inventTask).toString());

                    navController.navigate(R.id.nav_inventarizationProductFragment, bundle);

                });

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