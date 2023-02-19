package com.example.apexwh.ui.movers;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.apexwh.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MoversServiceRecordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoversServiceRecordFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MoversServiceRecordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MoversServiceRecordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MoversServiceRecordFragment newInstance(String param1, String param2) {
        MoversServiceRecordFragment fragment = new MoversServiceRecordFragment();
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
        View inflate = inflater.inflate(R.layout.fragment_movers_service_record, container, false);

        JSONArray record = null;

        try {
            record = new JSONArray(getArguments().getString("record"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        LinearLayout fields = inflate.findViewById(R.id.llFields);

        LinearLayout tr = (LinearLayout) inflater.inflate(R.layout.field_note_item, null);

        fields.addView(tr);



        return inflate;
    }
}