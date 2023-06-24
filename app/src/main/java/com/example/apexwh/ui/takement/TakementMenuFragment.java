package com.example.apexwh.ui.takement;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.apexwh.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TakementMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TakementMenuFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TakementMenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TakementMenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TakementMenuFragment newInstance(String param1, String param2) {
        TakementMenuFragment fragment = new TakementMenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_takement_menu, container, false);

        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);

        inflate.findViewById(R.id.btnContainerTakement).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                navController.navigate(R.id.nav_takementFragment, new Bundle());


            }
        });

        inflate.findViewById(R.id.btnContainerTakementList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                navController.navigate(R.id.nav_takementListFragment, new Bundle());


            }
        });

        inflate.findViewById(R.id.btnProductTakement).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                navController.navigate(R.id.nav_productTakementFragment, new Bundle());


            }
        });

        inflate.findViewById(R.id.btnProductTakementList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                navController.navigate(R.id.nav_productTakementListFragment, new Bundle());


            }
        });

        return inflate;
    }
}