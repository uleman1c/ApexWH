package com.example.apexwh.ui.menu;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.example.apexwh.DB;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.MainActivity;
import com.example.apexwh.R;
import com.example.apexwh.RequestToServer;
import com.example.apexwh.objects.MenuItem;
import com.example.apexwh.objects.NavigationNames;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SubMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubMenuFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "parent";
    private static final String ARG_PARAM2 = "parentName";

    // TODO: Rename and change types of parameters
    private String parent, parentName;
    private ArrayList<MenuItem> menuItems;
    private NavController navController;

    public SubMenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param parent Parameter 1.
     * @return A new instance of fragment SubMenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SubMenuFragment newInstance(String parent) {
        SubMenuFragment fragment = new SubMenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, parent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parent = DB.nil;
        parentName = "Подменю";

        menuItems = new ArrayList<>();

        if (getArguments() != null) {
            parent = getArguments().getString(ARG_PARAM1);
            parentName = getArguments().getString(ARG_PARAM2);

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_sub_menu, container, false);

        MainActivity activity = (MainActivity) getActivity();
        activity.binding.appBarMain.toolbar.setTitle(parentName);

        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);

        LinearLayout llButtons = inflate.findViewById(R.id.llButtons);

        llButtons.removeAllViews();

        menuItems.clear();

        RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladMenuSettings", "parent=" + parent, new JSONObject(), 1,
                new RequestToServer.ResponseResultInterface() {
                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray responseItems = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladMenuSettings");

                        for (int j = 0; j < responseItems.length(); j++) {

                            JSONObject objectItem = JsonProcs.getItemJSONArray(responseItems, j);

                            menuItems.add(MenuItem.FromJson(objectItem));

                        }

                        for (MenuItem menuItem: menuItems) {

                            menuItem.button = (Button) inflater.inflate(R.layout.menu_button, null);

                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            layoutParams.setMargins(0, 0, 0, 20);
                            menuItem.button.setLayoutParams(layoutParams);
                            llButtons.addView(menuItem.button);

                            menuItem.button.setText(menuItem.name);
                            menuItem.button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Bundle bundle = new Bundle();

                                    MenuItem foundMenuItem = null;
                                    for (int i = 0; i < menuItems.size() && foundMenuItem == null; i++) {

                                        MenuItem currentMenuItem = menuItems.get(i);

                                        if (currentMenuItem.button == view) {

                                            foundMenuItem = currentMenuItem;
                                        }

                                    }

                                    if (foundMenuItem.isGroup){

                                        bundle.putString("parent", foundMenuItem.ref);

                                        navController.navigate(R.id.nav_subMenuFragment, bundle);

                                    }
                                    else {

                                        int curNavId = NavigationNames.getIdFromName(foundMenuItem.navigation);
                                        if (curNavId != 0){

                                            navController.navigate(curNavId, bundle);
                                        }

                                    }

                                }

                            });

                        }
                    }
                });







        return inflate;
    }
}