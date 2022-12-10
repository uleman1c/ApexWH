package com.example.apexwh.ui.returns;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.apexwh.HttpClient;
import com.example.apexwh.HttpRequestInterface;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.objects.Return;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReturnsFragment extends Fragment {

    private ReturnsViewModel mViewModel;

    public static ReturnsFragment newInstance() {
        return new ReturnsFragment();
    }

    private ProgressBar progressBar;
    private ArrayList<Return> returns;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_returns, container, false);

        progressBar = root.findViewById(R.id.progressBar);

        returns = new ArrayList<>();

        HttpClient httpClient = new HttpClient(getContext());

        httpClient.request_get("/hs/dta/obj?request=getKorrByFilter&filter=", new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

                progressBar.setVisibility(visibility);

            }

            @Override
            public void processResponse(String response) {

                JSONObject jsonObjectResponse = JsonProcs.getJSONObjectFromString(response);

                if (JsonProcs.getBooleanFromJSON(jsonObjectResponse, "success")){

                    JSONArray jsonArrayR = JsonProcs.getJsonArrayFromJsonObject(jsonObjectResponse, "responses");

                    JSONObject jsonObjectItem = JsonProcs.getItemJSONArray(jsonArrayR, 0);

                    JSONArray jsonArrayKorrs = JsonProcs.getJsonArrayFromJsonObject(jsonObjectItem, "KorrByFilter");

                    for (int j = 0; j < jsonArrayKorrs.length(); j++) {

                        JSONObject task_item = JsonProcs.getItemJSONArray(jsonArrayKorrs, j);

                        returns.add(Return.ReturnFromJson(task_item));

                    }

                }

            }

//            @Override
//            public void processResponse(JSONObject response) {
//
//                JSONArray tasksJSON = ParseResponse.ArrayFromObject(response, "OrdersByFilter");
//
//                for (int j = 0; j < tasksJSON.length(); j++) {
//
//                    JSONObject task_item = ParseResponse.ObjectFromArray(tasksJSON, j);
//
//                    testDocuments.add(Order.OrderFromJson(task_item));
//
//                }
//
//
//                adapter.notifyDataSetChanged();
//            }
        });




        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ReturnsViewModel.class);
        // TODO: Use the ViewModel
    }

}