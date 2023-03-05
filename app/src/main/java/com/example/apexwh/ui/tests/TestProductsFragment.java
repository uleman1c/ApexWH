package com.example.apexwh.ui.tests;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.apexwh.R;
import com.example.apexwh.objects.DocumentLine;
import com.example.apexwh.ui.adapters.DocumentLineAdapter;
import com.example.apexwh.ui.products.ProductsFragment;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TestProductsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestProductsFragment extends ProductsFragment {


    public TestProductsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListUpdater(new ListUpdater() {
            @Override
            public void update(String name, String ref, ArrayList<DocumentLine> lines, ProgressBar progressBar, DocumentLineAdapter adapter) {

            }
        });

    }

}