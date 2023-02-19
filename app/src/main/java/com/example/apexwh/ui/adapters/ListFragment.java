package com.example.apexwh.ui.adapters;

import android.content.Context;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apexwh.DB;
import com.example.apexwh.R;

import java.util.ArrayList;

public class ListFragment<T> extends Fragment {

    public ListFragment(int fragmentLayout, int itemLayout) {

        this.fragmentLayout = fragmentLayout;
        this.itemLayout = itemLayout;

        items = new ArrayList<>();

    }

    private int fragmentLayout;
    private int itemLayout;

    private ProgressBar progressBar;
    private ArrayList<T> items;

    public DataAdapter getAdapter() {
        return adapter;
    }

    private DataAdapter adapter;
    private RecyclerView recyclerView;
    private EditText etFilter;
    private InputMethodManager imm;

    public String getWarehouseId() {
        return warehouseId;
    }

    private String warehouseId;

    public void setListUpdater(ListUpdater listUpdater) {
        this.listUpdater = listUpdater;
    }

    private ListUpdater listUpdater;

    private Bundle arguments;

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

    private OnCreateViewElements onCreateViewElements;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(fragmentLayout, container, false);

        arguments = getArguments();

        Bundle settings = DB.getSettings(getContext());

        warehouseId = settings.getString("warehouseId");

        progressBar = root.findViewById(R.id.progressBar);

        adapter = new DataAdapter<T>(getContext(), items, itemLayout);

//        adapter.setInitViewsMaker(initViewsMaker);
//
//        adapter.setDrawViewHolder(drawViewHolder);

//        adapter.setOnItemClickListener(new DocumentDataAdapter.OnDocumentItemClickListener() {
//            @Override
//            public void onDocumentItemClick(Document document) {
//
//                Bundle bundle = new Bundle();
//                bundle.putString("ref", document.ref);
//                bundle.putString("name", document.name);
//                bundle.putString("nameStr", document.nameStr);
//                bundle.putString("number", document.number);
//                bundle.putString("date", document.date);
//                bundle.putString("description", document.description);
//                bundle.putString("mode", "return");
//
//                Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.nav_products, bundle);
//
//            }
//
//        });
//        adapter.setOnDocumentItemLongClickListener(new DocumentDataAdapter.OnDocumentItemLongClickListener() {
//            @Override
//            public void onDocumentLongItemClick(Document document) {
//
//                name = document.name;
//                ref = document.ref;
//
//                if (getFoto.intent != null){
//
//                    startActivityForResult(getFoto.intent, CAMERA_REQUEST_FOTO);
//
//                }
//
//
//
//            }
//        });
//
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

    private void updateList(String filter) {

        listUpdater.update(items, progressBar, adapter, filter);

    }



}