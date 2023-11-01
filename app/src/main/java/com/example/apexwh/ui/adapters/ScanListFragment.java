package com.example.apexwh.ui.adapters;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.example.apexwh.DB;
import com.example.apexwh.R;
import com.example.apexwh.ui.ShtrihCodeInput;

public class ScanListFragment<T> extends ListFragment {

    protected String scanned;

    protected ShtrihCodeInput shtrihCodeInput;
    protected Boolean askQuantityAfterProductScan, showScannedProducts, barсodeEqualsCharacteristic;


    public ScanListFragment(int fragmentLayout, int itemLayout) {
        super(fragmentLayout, itemLayout);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(fragmentLayout, container, false);

        arguments = getArguments();

        Bundle settings = DB.getSettings(getContext());

        warehouseId = settings.getString("warehouseId");
        askQuantityAfterProductScan = settings.getString("askQuantityAfterProductScan").equals("1");
        showScannedProducts = settings.getString("showScannedProducts").equals("1");
        barсodeEqualsCharacteristic = settings.getString("barсodeEqualsCharacteristic").equals("1");

        progressBar = root.findViewById(R.id.progressBar);

        adapter = new DataAdapter<T>(getContext(), items, itemLayout);

        recyclerView = root.findViewById(R.id.list);
        recyclerView.setAdapter(adapter);

        shtrihCodeInput = new ShtrihCodeInput(getActivity(), root, R.id.actvShtrihCode, R.id.ibKeyboard, new ShtrihCodeInput.AfterScanShtrih() {
            @Override
            public void Scan(String shtrihcode) {

                updateList(shtrihcode);

            }
        });

        if (onCreateViewElements != null){

            onCreateViewElements.execute(root, Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main));

        }


        //updateList(etFilter.getText().toString());

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        shtrihCodeInput.RegisterReceiver(getActivity());

    }

    @Override
    public void onPause() {
        super.onPause();

        shtrihCodeInput.UnRegisterReceiver(getActivity());

    }
}
