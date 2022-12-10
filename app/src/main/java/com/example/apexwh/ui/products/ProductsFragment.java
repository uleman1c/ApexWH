package com.example.apexwh.ui.products;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.apexwh.HttpClient;
import com.example.apexwh.HttpRequestInterface;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.objects.Document;
import com.example.apexwh.objects.DocumentLine;
import com.example.apexwh.ui.adapters.DocumentDataAdapter;
import com.example.apexwh.ui.adapters.DocumentLineAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductsFragment extends Fragment {

    private ProductsViewModel mViewModel;

    public static ProductsFragment newInstance() {
        return new ProductsFragment();
    }

    private ProgressBar progressBar;
    private String ref;

    private ArrayList<DocumentLine> lines;

    private DocumentLineAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_products, container, false);

        Bundle args = getArguments();

        ref = args.getString("ref");

        ((TextView) root.findViewById(R.id.tvHeader)).setText(args.getString("name") + " № " + args.getString("number") + " от " + args.getString("date"));

        progressBar = root.findViewById(R.id.progressBar);

        lines = new ArrayList<>();

        adapter = new DocumentLineAdapter(getContext(), lines);
        adapter.setOnDocumentLineItemClickListener(new DocumentLineAdapter.OnDocumentLineItemClickListener() {
           @Override
           public void onDocumentLineItemClick(DocumentLine documentLine) {

           }
        });

        recyclerView = root.findViewById(R.id.list);
        recyclerView.setAdapter(adapter);

        updateList();

        return root;
    }

    private void updateList() {

        lines.clear();

        HttpClient httpClient = new HttpClient(getContext());

        httpClient.request_get("/hs/dta/obj?request=getLinesToAccept&orderId=" + ref, new HttpRequestInterface() {
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

                    JSONArray jsonArrayObjects = JsonProcs.getJsonArrayFromJsonObject(jsonObjectItem, "ReturnsToAccept");

                    for (int j = 0; j < jsonArrayObjects.length(); j++) {

                        JSONObject objectItem = JsonProcs.getItemJSONArray(jsonArrayObjects, j);

                        lines.add(DocumentLine.DocumentLineFromJson(objectItem));

                    }

                    adapter.notifyDataSetChanged();

                }

            }

        });

    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProductsViewModel.class);
        // TODO: Use the ViewModel
    }

}