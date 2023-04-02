package com.example.apexwh.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.apexwh.DB;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.databinding.FragmentHomeBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private String appId, id;
    NavController navController;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        HomeViewModel homeViewModel =
//                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Bundle bundle = getArguments();

        if (bundle != null){

            navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);

            JSONArray warehouses = JsonProcs.getJsonArrayFromString(bundle.getString("warehouses"));

            DB db = new DB(getContext());
            db.open();
            appId = db.getConstant("appId");

            String warehouseId = db.getConstant("warehouseId");

            if (warehouseId == null && warehouses.length() == 1){

                JSONObject warehouse = JsonProcs.getItemJSONArray(warehouses, 0);

                db.updateConstant("warehouseId", JsonProcs.getStringFromJSON(warehouse, "ref"));
                db.updateConstant("warehouseDescription", JsonProcs.getStringFromJSON(warehouse,"name"));

            }

            db.close();

            id = bundle.getString("id");
            binding.tvName.setText(bundle.getString("name"));

            bundle.putString("appId", appId);
        }


        binding.btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                navController.navigate(R.id.nav_settings, bundle);

            }
        });

        binding.btnAcceptment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                navController.navigate(R.id.nav_acceptmentFragment, bundle);

            }
        });

        binding.btnShipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                navController.navigate(R.id.nav_BuierOrdersFragment, bundle);

            }
        });

        binding.btnReturns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                navController.navigate(R.id.nav_returns, bundle);

            }
        });

        binding.btnReturnsOfProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                navController.navigate(R.id.nav_returnsOfProductsFragment, bundle);

            }
        });

        binding.btnInvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                navController.navigate(R.id.nav_inventarizations, bundle);

            }
        });

        binding.btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                navController.navigate(R.id.nav_tests, bundle);

            }
        });

        binding.btnMovers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                navController.navigate(R.id.nav_movers, bundle);

            }
        });

        binding.btnOrderToChangeCharacteristic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                navController.navigate(R.id.nav_ordersToChangeCharacteristicFragment, bundle);

            }
        });

        binding.btnPlacement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                navController.navigate(R.id.nav_placementListFragment, bundle);

            }
        });

        binding.btnTakement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                navController.navigate(R.id.nav_takementListFragment, bundle);

            }
        });


        if(!DB.isSettingsExist(getContext())){

            ArrayList<Button> buttons = new ArrayList<>();
            buttons.add(binding.btnAcceptment);
            buttons.add(binding.btnShipment);
            buttons.add(binding.btnReturns);
            buttons.add(binding.btnTest);
            buttons.add(binding.btnReturnsOfProducts);
            buttons.add(binding.btnMovers);
            buttons.add(binding.btnOrderToChangeCharacteristic);
            buttons.add(binding.btnPlacement);
            buttons.add(binding.btnTakement);
            buttons.add(binding.btnInvents);

            for (Button curBtn: buttons
                 ) {

                curBtn.setBackgroundColor(getResources().getColor(R.color.gray, null));
                curBtn.setTextColor(getResources().getColor(R.color.light_gray, null));
                curBtn.setClickable(false);
            }

        }


//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}