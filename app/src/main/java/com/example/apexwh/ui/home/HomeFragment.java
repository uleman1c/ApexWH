package com.example.apexwh.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.example.apexwh.DB;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.RequestToServer;
import com.example.apexwh.databinding.FragmentHomeBinding;
import com.example.apexwh.objects.MenuItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    protected FragmentHomeBinding binding;

    private String appId, id;
    NavController navController;

    private ArrayList<MenuItem> menuItems;

    private String parent;

    Bundle bundle;

    String nil = "00000000-0000-0000-0000-000000000000";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        HomeViewModel homeViewModel =
//                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        FragmentHomeBinding binding2 = binding;

        View root = binding.getRoot();

        parent = nil;

        menuItems = new ArrayList<>();

        bundle = getArguments();

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

            id = bundle.getString("id");
            db.updateConstant("userId", id);

            db.close();

            binding.tvName.setText(bundle.getString("name"));

            bundle.putString("appId", appId);
        }

        getMenuItems(inflater, binding2);


        return root;
    }

    private void getMenuItems(@NonNull LayoutInflater inflater, FragmentHomeBinding binding2) {

        menuItems.clear();

        binding2.llSettings.removeViews(1, binding2.llSettings.getChildCount() - 1);

        binding2.btnBack.setVisibility(parent == nil ? View.GONE : View.VISIBLE);

        RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladMenuSettings", "parent=" + parent, new JSONObject(), 1,
                new RequestToServer.ResponseResultInterface() {
                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray responseItems = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladMenuSettings");

                        for (int j = 0; j < responseItems.length(); j++) {

                            JSONObject objectItem = JsonProcs.getItemJSONArray(responseItems, j);

                            menuItems.add(MenuItem.FromJson(objectItem));

                        }

                        onGetMenuItems(inflater, binding2);

                    }
                });
    }

    public void onGetMenuItems(LayoutInflater inflater, FragmentHomeBinding binding){

        //FragmentHomeBinding binding = FragmentHomeBinding.inflate(inflater, container, false);

        if (menuItems.size() > 0){

            //for (int i = 0; i < binding.llSettings.getChildCount(); i++) {

            //}

            for (MenuItem menuItem: menuItems) {

                menuItem.button = (Button) inflater.inflate(R.layout.menu_button, null);
                binding.llSettings.addView(menuItem.button);
                menuItem.button.setText(menuItem.name);
                menuItem.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        for (MenuItem menuItem1: menuItems) {

                            if (menuItem1.button == view){

                                if (menuItem1.isGroup){

                                    parent = menuItem1.ref;

                                    getMenuItems(inflater, binding);

                                    break;

                                }

                            }

                        }

                    }
                });

            }


            binding.llStandart.setVisibility(View.GONE);

        }
        else {

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

                    navController.navigate(R.id.nav_placementMenuFragment, bundle);

                }
            });

            binding.btnTakement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    navController.navigate(R.id.nav_takementMenuFragment, bundle);

                }
            });

            binding.btnMoves.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    navController.navigate(R.id.movementListFragment, bundle);

                }
            });

            binding.btnProductCells.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    navController.navigate(R.id.nav_productCellsListFragment, bundle);

                }
            });

            binding.btnCellContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    navController.navigate(R.id.nav_cellContentListFragment, bundle);

                }
            });


            if (!DB.isSettingsExist(getContext())) {

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

                for (Button curBtn : buttons
                ) {

                    curBtn.setBackgroundColor(getResources().getColor(R.color.gray, null));
                    curBtn.setTextColor(getResources().getColor(R.color.light_gray, null));
                    curBtn.setClickable(false);
                }

            }

        }

//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);



    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}