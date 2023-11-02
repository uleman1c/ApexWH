package com.example.apexwh.ui.adapters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apexwh.DB;
import com.example.apexwh.R;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class ListFragment<T> extends Fragment {

    public ListFragment(int fragmentLayout, int itemLayout) {

        this.fragmentLayout = fragmentLayout;
        this.itemLayout = itemLayout;

        items = new ArrayList<>();

    }

    protected int fragmentLayout;
    protected int itemLayout;

    protected ProgressBar progressBar;
    protected ArrayList<T> items;

    public DataAdapter getAdapter() {
        return adapter;
    }



    protected DataAdapter adapter;

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    protected RecyclerView recyclerView;
    protected EditText etFilter;
    protected InputMethodManager imm;

    public String getWarehouseId() {
        return warehouseId;
    }

    protected String warehouseId;

    public void setListUpdater(ListUpdater listUpdater) {
        this.listUpdater = listUpdater;
    }

    protected ListUpdater listUpdater;

    protected Bundle arguments;

    public BroadcastReceiver broadcastReceiver;

//    public void setInitViewsMaker(DataAdapter.InitViewsMaker initViewsMaker) {
//        this.initViewsMaker = initViewsMaker;
//    }
//
//    private DataAdapter.InitViewsMaker initViewsMaker;

//    public void setDrawViewHolder(DataAdapter.DrawViewHolder drawViewHolder) {
//        this.drawViewHolder = drawViewHolder;
//    }
//
//    private DataAdapter.DrawViewHolder drawViewHolder;

    public interface OnCreateViewElements{

        void execute(View root, NavController navController);

    }


    public void setOnCreateViewElements(OnCreateViewElements onCreateViewElements) {
        this.onCreateViewElements = onCreateViewElements;
    }

    protected OnCreateViewElements onCreateViewElements;

    @Override
    public void onResume() {
        super.onResume();

//        if (etFilter != null) {
//
//            updateList(etFilter.getText().toString());
//        }


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(fragmentLayout, container, false);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String strCatName = intent.getStringExtra("EXTRA_BARCODE_DECODING_DATA");

                updateList(strCatName.replaceAll("\n", ""));

            }
        };

        arguments = getArguments();

        Bundle settings = DB.getSettings(getContext());

        warehouseId = settings.getString("warehouseId");

        progressBar = root.findViewById(R.id.progressBar);

        adapter = new DataAdapter<T>(getContext(), items, itemLayout);

        recyclerView = root.findViewById(R.id.list);
        recyclerView.setAdapter(adapter);

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        etFilter = root.findViewById(R.id.etFilter);
        etFilter.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    String strCatName = etFilter.getText().toString();

                    imm.hideSoftInputFromWindow(etFilter.getWindowToken(), 0);

                    updateList(strCatName);

                    return true;
                }

                return false;
            }
        });

        root.findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                etFilter.setText("");
                updateList(etFilter.getText().toString());

            }
        });

        if (onCreateViewElements != null){

            onCreateViewElements.execute(root, Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main));

        }


        updateList(etFilter.getText().toString());

        return root;
    }

    public interface ListUpdater<T>{

        void update(ArrayList<T> items, ProgressBar progressBar, DataAdapter<T> adapter, String filter);

    }

    public void updateList(String filter) {

        listUpdater.update(items, progressBar, adapter, filter);

    }

    public void RegisterReceiver(FragmentActivity fragmentActivity){

        fragmentActivity.registerReceiver(broadcastReceiver, new IntentFilter("com.xcheng.scanner.action.BARCODE_DECODING_BROADCAST"));

    }

    public void UnRegisterReceiver(FragmentActivity fragmentActivity){

        fragmentActivity.unregisterReceiver(broadcastReceiver);

    }


}