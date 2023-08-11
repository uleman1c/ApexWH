package com.example.apexwh.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
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
    private ArrayList<String> menuStack;

    private String parent;

    Bundle bundle;

    String nil = "00000000-0000-0000-0000-000000000000";

    protected Boolean isSettingsExist;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        HomeViewModel homeViewModel =
//                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        FragmentHomeBinding binding2 = binding;

        View root = binding.getRoot();

        parent = nil;

        menuItems = new ArrayList<>();
        menuStack = new ArrayList<>();

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

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                parent = menuStack.get(menuStack.size() - 1);

                menuStack.remove(menuStack.size() - 1);

                getMenuItems(inflater, binding);

            }
        });

        binding.llStandart.setVisibility(View.GONE);
        binding.llSettings.setVisibility(View.GONE);


        getMenuItems(inflater, binding2);


        return root;
    }

    private void getMenuItems(@NonNull LayoutInflater inflater, FragmentHomeBinding binding2) {

        menuItems.clear();

        binding2.llSettings.removeViews(1, binding2.llSettings.getChildCount() - 1);

        binding2.btnBack.setVisibility(parent == nil ? View.GONE : View.VISIBLE);

        isSettingsExist = DB.isSettingsExist(getContext());

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

            for (MenuItem menuItem: menuItems) {

                menuItem.button = (Button) inflater.inflate(R.layout.menu_button, null);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 0, 0, 20);
                menuItem.button.setLayoutParams(layoutParams);
                binding.llSettings.addView(menuItem.button);
                menuItem.button.setText(menuItem.name);
                menuItem.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        MenuItem foundMenuItem = null;
                        for (int i = 0; i < menuItems.size() && foundMenuItem == null; i++) {

                            MenuItem currentMenuItem = menuItems.get(i);

                            if (currentMenuItem.button == view) {

                                foundMenuItem = currentMenuItem;
                            }

                        }

                        if (foundMenuItem.isGroup){

                            menuStack.add(parent);

                            parent = foundMenuItem.ref;

                            getMenuItems(inflater, binding);

                        }
                        else {

                            if (foundMenuItem.navigation.equals("Acceptment")){

                                navController.navigate(R.id.nav_acceptmentFragment, bundle);

                            }
                            else if (foundMenuItem.navigation.equals("Shipment")){

                                navController.navigate(R.id.nav_BuierOrdersFragment, bundle);

                            }
                            else if (foundMenuItem.navigation.equals("Returns")){

                                navController.navigate(R.id.nav_returns, bundle);

                            }
                            else if (foundMenuItem.navigation.equals("Tests")){

                                navController.navigate(R.id.nav_tests, bundle);

                            }
                            else if (foundMenuItem.navigation.equals("ReturnsOfProducts")){

                                navController.navigate(R.id.nav_returnsOfProductsFragment, bundle);

                            }
                            else if (foundMenuItem.navigation.equals("OrdersToChangeCharacteristic")){

                                navController.navigate(R.id.nav_ordersToChangeCharacteristicFragment, bundle);

                            }
                            else if (foundMenuItem.navigation.equals("Placement")){

                                navController.navigate(R.id.nav_placementMenuFragment, bundle);

                            }
                            else if (foundMenuItem.navigation.equals("Takement")){

                                navController.navigate(R.id.nav_takementMenuFragment, bundle);

                            }
                            else if (foundMenuItem.navigation.equals("Collect")){

                                navController.navigate(R.id.nav_collectListFragment, bundle);

                            }
                            else if (foundMenuItem.navigation.equals("Movement")){

                                navController.navigate(R.id.nav_movementFragment, bundle);

                            }
                            else if (foundMenuItem.navigation.equals("Moves")){

                                navController.navigate(R.id.movementListFragment, bundle);

                            }
                            else if (foundMenuItem.navigation.equals("CellContent")){

                                navController.navigate(R.id.nav_cellContentListFragment, bundle);

                            }
                            else if (foundMenuItem.navigation.equals("ContainerContent")){

                                navController.navigate(R.id.nav_containerContentListFragment, bundle);

                            }
                            else if (foundMenuItem.navigation.equals("ProductCells")){

                                navController.navigate(R.id.nav_productCellsListFragment, bundle);

                            }
                            else if (foundMenuItem.navigation.equals("Invents")){

                                navController.navigate(R.id.nav_inventarizations, bundle);

                            }
                            else if (foundMenuItem.navigation.equals("ShtrihcodeProduct")){

                                navController.navigate(R.id.nav_shtrihcodeProductFragment, bundle);

                            }

                        }

                    }

                });

            }


            binding.llSettings.setVisibility(View.VISIBLE);

        }
        else {

            binding.llStandart.setVisibility(View.VISIBLE);

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

            binding.btnShtrihcodeProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    navController.navigate(R.id.nav_shtrihcodeProductFragment, bundle);

                }
            });

            if (!isSettingsExist) {

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

//                    curBtn.setBackgroundColor(getResources().getColor(R.color.gray));
//                    curBtn.setTextColor(getResources().getColor(R.color.light_gray));
//                    curBtn.setClickable(false);
                }

            }

        }

//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);



    }

    @Override
    public void onResume() {
        super.onResume();

        Bundle result = DB.getSettings(getContext());

        if (result.getString("warehouseId").equals(DB.nil)){

            Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.nav_settings);

        }


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();

        MenuHost menuHost = requireActivity();

        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                //menuInflater.inflate(R.menu.home_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull android.view.MenuItem menuItem) {

                boolean res = false;

                switch (menuItem.getItemId()) {

                    case R.id.miSettings:

                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.nav_settings);

                        res = true;

                    case R.id.miItem:
                        res = true;

                };

                return res;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}