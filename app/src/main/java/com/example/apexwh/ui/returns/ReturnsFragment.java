package com.example.apexwh.ui.returns;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.apexwh.DB;
import com.example.apexwh.HttpClient;
import com.example.apexwh.HttpRequestInterface;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.objects.Document;
import com.example.apexwh.objects.Reference;
import com.example.apexwh.objects.Return;
import com.example.apexwh.objects.Warehouse;
import com.example.apexwh.ui.adapters.DocumentDataAdapter;
import com.example.apexwh.ui.adapters.ReferenceDataAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReturnsFragment extends Fragment {

    private ReturnsViewModel mViewModel;

    public static ReturnsFragment newInstance() {
        return new ReturnsFragment();
    }

    private ProgressBar progressBar;
    private ArrayList<Document> returns;

    private DocumentDataAdapter adapter;
    private RecyclerView recyclerView;
    private EditText etFilter;
    private InputMethodManager imm;

    private String warehouseId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_returns, container, false);

        Bundle settings = DB.getSettings(getContext());

        warehouseId = settings.getString("warehouseId");

        progressBar = root.findViewById(R.id.progressBar);

        returns = new ArrayList<>();

        adapter = new DocumentDataAdapter(getContext(), returns);
        adapter.setOnDocumentItemClickListener(new DocumentDataAdapter.OnDocumentItemClickListener() {
            @Override
            public void onDocumentItemClick(Document document) {

                Bundle bundle = new Bundle();
                bundle.putString("ref", document.ref);
                bundle.putString("name", document.name);
                bundle.putString("number", document.number);
                bundle.putString("date", document.date);
                bundle.putString("description", document.description);
                bundle.putString("mode", "return");

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.nav_products, bundle);

            }

        });

        recyclerView = root.findViewById(R.id.list);
        recyclerView.setAdapter(adapter);

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        etFilter = root.findViewById(R.id.etFilter);
        etFilter.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    String strCatName = etFilter.getText().toString();

                    imm.hideSoftInputFromWindow(etFilter.getWindowToken(), 0);

                    updateList(strCatName);

                    return true;
                }

                return false;
            }
        });

        root.findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                etFilter.setText("");
                updateList(etFilter.getText().toString());

            }
        });


        updateList(etFilter.getText().toString());

        return root;
    }

    private void updateList(String filter) {

        returns.clear();

        HttpClient httpClient = new HttpClient(getContext());

        httpClient.request_get("/hs/dta/obj?request=getReturnsToAccept&warehouseId=" + warehouseId + "&filter=" + filter, new HttpRequestInterface() {
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

                        returns.add(Document.DocumentFromJson(objectItem));

                    }

                    adapter.notifyDataSetChanged();

                }

            }

        });

    }





    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ReturnsViewModel.class);
        // TODO: Use the ViewModel
    }

}