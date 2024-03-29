package com.example.apexwh.ui.containers;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.apexwh.HttpClient;
import com.example.apexwh.HttpRequestInterface;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.objects.Container;
import com.example.apexwh.objects.MoversService;
import com.example.apexwh.objects.Test;
import com.example.apexwh.ui.adapters.DataAdapter;
import com.example.apexwh.ui.adapters.ListFragment;
import com.example.apexwh.ui.movers.MoversFragment;
import com.example.apexwh.ui.movers.MoversServiceRecordFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.UUID;

public class ContainersFragment extends ListFragment<Container> {

    ArrayList<Container> selected;

    public ContainersFragment() {

        super(R.layout.fragment_filter_list, R.layout.containers_list_item);

        selected = new ArrayList<>();

        setListUpdater(new ListUpdater() {
            @Override
            public void update(ArrayList items, ProgressBar progressBar, DataAdapter adapter, String filter) {

                items.clear();

                HttpClient httpClient = new HttpClient(getContext());

                httpClient.request_get("/hs/dta/obj?request=getContainers&warehouseId=" + getWarehouseId() + "&filter=" + filter, new HttpRequestInterface() {
                    @Override
                    public void setProgressVisibility(int visibility) {

                        progressBar.setVisibility(visibility);

                    }

                    @Override
                    public void processResponse(String response) {

                        JSONObject jsonObjectResponse = JsonProcs.getJSONObjectFromString(response);

                        if (JsonProcs.getBooleanFromJSON(jsonObjectResponse, "success")){

                            JSONArray jsonArrayResponses = JsonProcs.getJsonArrayFromJsonObject(jsonObjectResponse, "responses");

                            JSONObject jsonObjectItem = JsonProcs.getItemJSONArray(jsonArrayResponses, 0);

                            JSONArray jsonArrayObjects = JsonProcs.getJsonArrayFromJsonObject(jsonObjectItem, "Containers");

                            for (int j = 0; j < jsonArrayObjects.length(); j++) {

                                JSONObject objectItem = JsonProcs.getItemJSONArray(jsonArrayObjects, j);

                                items.add(Container.FromJson(objectItem));

                            }


                            for (Container c: Container.getTestArray()) {

                                items.add(c);

                            }

                            adapter.notifyDataSetChanged();

                        }

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

                        textViews.add(itemView.findViewById(R.id.tvDescription));
                    }
                });

                getAdapter().setDrawViewHolder(new DataAdapter.DrawViewHolder<Container>() {
                    @Override
                    public void draw(DataAdapter.ItemViewHolder holder, Container document) {

                        ((TextView) holder.getTextViews().get(0)).setText(document.name);
                    }
                });

                getAdapter().setOnClickListener(new DataAdapter.OnClickListener<Container>() {
                    @Override
                    public void onItemClick(Container document) {

                        if (selected.size() == 0){

                            selected.add(document);

                            JSONArray jsonArray = new JSONArray();

                            for (Container curContainer:selected ) {

                                JSONObject jsonObject = new JSONObject();

                                JsonProcs.putToJsonObject(jsonObject, "ref", curContainer.ref);
                                JsonProcs.putToJsonObject(jsonObject, "name", curContainer.name);

                                jsonArray.put(jsonObject);

                            }

                            Bundle result = getArguments();
                            result.putString("selected", jsonArray.toString());
                            getParentFragmentManager().setFragmentResult("selected", result);

                            Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).popBackStack();

                        }


                    }
                });



            }
        });


    }


}