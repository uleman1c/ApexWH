package com.example.apexwh.ui.settings;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;

import com.example.apexwh.DB;
import com.example.apexwh.R;

public class SettingsFragment extends Fragment {

    private SettingsViewModel mViewModel;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        Bundle settings = DB.getSettings(getContext());

        TextView tvWarehouse = root.findViewById(R.id.tvWarehouse);
        String warehouseDescription = settings.getString("warehouseDescription");
        tvWarehouse.setText("Склад: " + warehouseDescription);

        TextView tvAppId = root.findViewById(R.id.tvAppId);
        tvAppId.setText("appId: " + settings.getString("appId"));

        Switch askQuantityAfterProductScan = root.findViewById(R.id.askQuantityAfterProductScan);
        askQuantityAfterProductScan.setChecked(settings.getString("askQuantityAfterProductScan").equals("1"));
        askQuantityAfterProductScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DB db = new DB(getContext());
                db.open();
                db.updateConstant("askQuantityAfterProductScan", askQuantityAfterProductScan.isChecked() ? "1" : "0");
                db.close();

            }
        });
        Switch showScannedProducts = root.findViewById(R.id.showScannedProducts);
        showScannedProducts.setChecked(settings.getString("showScannedProducts").equals("1"));
        showScannedProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DB db = new DB(getContext());
                db.open();
                db.updateConstant("showScannedProducts", showScannedProducts.isChecked() ? "1" : "0");
                db.close();

            }
        });

        Switch barсodeEqualsCharacteristic = root.findViewById(R.id.barсodeEqualsCharacteristic);
        barсodeEqualsCharacteristic.setChecked(settings.getString("barсodeEqualsCharacteristic").equals("1"));
        barсodeEqualsCharacteristic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DB db = new DB(getContext());
                db.open();
                db.updateConstant("barсodeEqualsCharacteristic", barсodeEqualsCharacteristic.isChecked() ? "1" : "0");
                db.close();

            }
        });

        Switch sortByStrong = root.findViewById(R.id.sortByStrong);
        sortByStrong.setChecked(settings.getString("sortByStrong").equals("1"));
        sortByStrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DB db = new DB(getContext());
                db.open();
                db.updateConstant("sortByStrong", sortByStrong.isChecked() ? "1" : "0");
                db.close();

            }
        });

        Switch useLocalServer = root.findViewById(R.id.useLocalServer);
        useLocalServer.setChecked(settings.getString("useLocalServer").equals("1"));
        useLocalServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DB db = new DB(getContext());
                db.open();
                db.updateConstant("useLocalServer", useLocalServer.isChecked() ? "1" : "0");
                db.close();

            }
        });

        root.findViewById(R.id.btnSelectWarehouse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString("mode", "selectWarehouseSetting");

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.nav_warehouses, bundle);

            }
        });



        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        // TODO: Use the ViewModel
    }

}