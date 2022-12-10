package com.example.apexwh.ui.products;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.apexwh.R;

public class ProductsFragment extends Fragment {

    private ProductsViewModel mViewModel;

    public static ProductsFragment newInstance() {
        return new ProductsFragment();
    }

    private ProgressBar progressBar;
    private String ref;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_products, container, false);

        Bundle args = getArguments();

        ref = args.getString("ref");

        ((TextView) root.findViewById(R.id.tvHeader)).setText(args.getString("name") + " № " + args.getString("number") + " от " + args.getString("date"));

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProductsViewModel.class);
        // TODO: Use the ViewModel
    }

}