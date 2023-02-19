package com.example.apexwh.ui.movers;

import android.app.DatePickerDialog;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

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

    JSONArray record;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_movers_service_record, container, false);

        record = JsonProcs.getJsonArrayFromString(getArguments().getString("record"));

        LinearLayout fields = inflate.findViewById(R.id.llFields);

        int curViewPos = 0;

        for (int i = 0; i < record.length(); i++) {

            JSONObject fd = JsonProcs.getItemJSONArray(record, i);

            String name = fd.keys().next();

            JSONObject field = JsonProcs.getJsonObjectFromJsonObject(fd, name);

            Boolean visible = JsonProcs.getStringFromJSON(field, "visible").equals("true");
            Boolean editable = JsonProcs.getStringFromJSON(field, "editable").equals("true");


            if (visible){

                String value = JsonProcs.getStringFromJSON(field, "value");

                String type = JsonProcs.getStringFromJSON(field, "type");

                if (type.equals("date")){

                    if (!value.isEmpty()){

                        value = value.substring(6, 8) + "." + value.substring(4, 6) + "." + value.substring(0, 4)
                                + " " + value.substring(8, 10) + ":" + value.substring(10, 12) + ":" + value.substring(12, 14);
                    }


                }



                int field_layout = R.layout.field_note_item;

                if (editable) {

                    if (type.equals("date")){
                        field_layout = R.layout.field_editdate_item;
                    } else {
                        field_layout = R.layout.field_edittext_item;
                    }



                }
                LinearLayout tr = (LinearLayout) inflater.inflate(field_layout, null);

                ((TextView) tr.getChildAt(0)).setText(JsonProcs.getStringFromJSON(field, "alias"));

                View input = tr.getChildAt(1);
                input.setId(View.generateViewId());

                JsonProcs.putToJsonObject(field, "input", input.getId());



                ((TextView) input).setText(value);

                if (editable) {

                    if (type.equals("date")){

                        View btn = tr.getChildAt(2);
                        btn.setId(View.generateViewId());

                        JsonProcs.putToJsonObject(field, "btn", btn.getId());

                        TimeZone timeZone = TimeZone.getTimeZone("Europe/Moscow");

                        Calendar calendar = new GregorianCalendar();
                        calendar.roll(Calendar.HOUR_OF_DAY, timeZone.getRawOffset() / (3600 * 1000));

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");


                        if (!value.isEmpty()){

                            calendar.set(Calendar.YEAR, Integer.valueOf(value.substring(0, 4)));
                            calendar.set(Calendar.MONTH, Integer.valueOf(value.substring(4, 6)) - 1);
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(value.substring(6, 8)));
                            calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(value.substring(8, 10)));
                            calendar.set(Calendar.MINUTE, Integer.valueOf(value.substring(10, 12)));
                            calendar.set(Calendar.SECOND, Integer.valueOf(value.substring(12, 14)));

                        }

                        DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                                dateAndTime.set(Calendar.YEAR, year);
//                                dateAndTime.set(Calendar.MONTH, monthOfYear);
//                                dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                                setInitialDateTime();
                            }
                        };


//                        tr.getChildAt(2).setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//
//                                new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
//                                    @Override
//                                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
//
//                                    }
//                                },
////                                        dateAndTime.get(Calendar.YEAR),
////                                        dateAndTime.get(Calendar.MONTH),
////                                        dateAndTime.get(Calendar.DAY_OF_MONTH))
//                                        .show();
//
////                                DatePicker datePicker =  new DatePicker();
//
//                            }
//                        });

                    }

                }

                fields.addView(tr, curViewPos);

                curViewPos = curViewPos + 1;

            }



        }



        return inflate;
    }
}