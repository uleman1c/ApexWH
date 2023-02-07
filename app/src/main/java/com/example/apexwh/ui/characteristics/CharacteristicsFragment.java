package com.example.apexwh.ui.characteristics;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.apexwh.HttpClient;
import com.example.apexwh.HttpRequestInterface;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.objects.Characteristic;
import com.example.apexwh.objects.DocumentLine;
import com.example.apexwh.ui.BundleMethodInterface;
import com.example.apexwh.ui.Dialogs;
import com.example.apexwh.ui.adapters.CharacteristicAdapter;
import com.example.apexwh.ui.adapters.DocumentLineAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CharacteristicsFragment extends Fragment {

    ArrayList<Characteristic> items;
    CharacteristicAdapter adapter;

    String ref;

    Bundle args;

    private RecyclerView recyclerView;

    private CharacteristicsViewModel mViewModel;

    public static CharacteristicsFragment newInstance() {
        return new CharacteristicsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_characteristics, container, false);

        args = getArguments();

        ref = args.getString("productRef");

        items = new ArrayList<>();

        adapter = new CharacteristicAdapter(getContext(), items);
        adapter.setOnItemClickListener(new CharacteristicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Characteristic characteristic) {

                args.putString("characteristicRef", characteristic.ref);
                args.putString("characteristicDescription", characteristic.description);
                getParentFragmentManager().setFragmentResult("selectCharacteristic", args);

                NavHostFragment.findNavController(CharacteristicsFragment.this).popBackStack();


            }
        });

        adapter.setOnItemLongClickListener(new CharacteristicAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(Characteristic documentLine) {

//                Bundle bundle = new Bundle();
//                bundle.putString("shtrihcode", documentLine.shtrihCodes.get(0));
//                bundle.putInt("toScan", documentLine.quantity - documentLine.scanned);
//                bundle.putString("productRef", documentLine.productRef);
//
//                Dialogs.showProductMenu(getContext(), getActivity(), new BundleMethodInterface() {
//                    @Override
//                    public void callMethod(Bundle arguments) {
//
//                        if (arguments.getString("btn").equals("Foto")){
//
//                            Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.nav_gallery, arguments);
//
//                        } else if (arguments.getString("btn").equals("InputNumber")) {
//
//                            showInputNumber(arguments.getString("shtrihcode"), arguments.getInt("toScan"));
//
//                        } else if (arguments.getString("btn").equals("ChangeCharcteristic")) {
//
//                            Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.nav_characteristics, arguments);
//
//                        }
//
//                    }
//                }, bundle, "Выберите", "Меню");


            }
        });

        recyclerView = root.findViewById(R.id.list);
        recyclerView.setAdapter(adapter);

        updateList();


        return root;
    }

    private void updateList() {

        items.clear();

        HttpClient httpClient = new HttpClient(getContext());

        httpClient.request_get("/hs/dta/obj?request=getCharacteristics&id=" + ref, new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

                //progressBar.setVisibility(visibility);

            }

            @Override
            public void processResponse(String response) {

                JSONObject jsonObjectResponse = JsonProcs.getJSONObjectFromString(response);

                if (JsonProcs.getBooleanFromJSON(jsonObjectResponse, "success")){

                    JSONArray jsonArrayResponses = JsonProcs.getJsonArrayFromJsonObject(jsonObjectResponse, "responses");

                    JSONObject jsonObjectItem = JsonProcs.getItemJSONArray(jsonArrayResponses, 0);

                    JSONArray jsonArrayObjects = JsonProcs.getJsonArrayFromJsonObject(jsonObjectItem, "Characteristics");

                    for (int j = 0; j < jsonArrayObjects.length(); j++) {

                        JSONObject objectItem = JsonProcs.getItemJSONArray(jsonArrayObjects, j);

                        items.add(Characteristic.CharacteristicFromJson(objectItem));

                    }

                    adapter.notifyDataSetChanged();

                }

            }

        });

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CharacteristicsViewModel.class);
        // TODO: Use the ViewModel
    }

}