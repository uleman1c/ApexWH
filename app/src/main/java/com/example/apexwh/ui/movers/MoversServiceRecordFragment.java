package com.example.apexwh.ui.movers;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

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

                    if (type.equals("date")
                    || type.equals("ref")){
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

                        setDatePicker(inflate, tr);

                    } else if (type.equals("ref")){

                        View btn = tr.getChildAt(2);
                        btn.setId(View.generateViewId());

                        JsonProcs.putToJsonObject(field, "btn", btn.getId());

                        setRefChoiser(inflate, tr);

                    }

                }

                fields.addView(tr, curViewPos);

                curViewPos = curViewPos + 1;

            }



        }

        inflate.findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Boolean allRequired = true;
                for (int i = 0; i < record.length(); i++) {

                    JSONObject jf = JsonProcs.getItemJSONArray(record, i);

                    String name = jf.keys().next();

                    JSONObject field = JsonProcs.getJsonObjectFromJsonObject(jf, name);

                    if (JsonProcs.getStringFromJSON(field,"required").equals("true")){

                        String type = JsonProcs.getStringFromJSON(field,"type");

                        String value = JsonProcs.getStringFromJSON(field,"value");

                        Boolean curRequired = false;
                        if(type.equals("date")){

                            curRequired = value.isEmpty();

                        } else if(type.equals("integer")){

                            curRequired = value.equals("0");

                        } else {

                            curRequired = value.isEmpty();

                        }

                        allRequired = curRequired ? false : allRequired;

                        if (curRequired){

                            inflate.findViewById(Integer.valueOf(JsonProcs.getStringFromJSON(field,"input"))).setBackgroundColor(Color.parseColor("#FF0000"));
                        }

                    }

                }

                if(allRequired){



                }


            }
        });

        getParentFragmentManager().setFragmentResultListener("selected", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {

                int btnId = bundle.getInt("id");

                JSONArray containers = JsonProcs.getJsonArrayFromString(bundle.getString("selected"));

                JSONObject field = getFieldByViewId(btnId);

                try {
                    field.put("containers", containers);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                String strContainers = "";

                for (int i = 0; i < containers.length(); i++) {

                    JSONObject jsonObject = JsonProcs.getItemJSONArray(containers, i);

                    strContainers = strContainers + (strContainers.isEmpty() ? "" : ", ") + JsonProcs.getStringFromJSON(jsonObject, "name");

                }

                JsonProcs.putToJsonObject(field,"value", strContainers);

                View view = inflate.findViewById(JsonProcs.getIntegerFromJSON(field, "input"));
                view.setBackgroundColor(Color.parseColor("#FFFFFF"));
                ((TextView) view).setText(strContainers);

            }
        });


        return inflate;
    }

    private void setDatePicker(View inflate, LinearLayout tr) {
        tr.getChildAt(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JSONObject field = getFieldByViewId(view.getId());
                String value = JsonProcs.getStringFromJSON(field, "value");

                TimeZone timeZone = TimeZone.getTimeZone("Europe/Moscow");

                Calendar calendar = new GregorianCalendar();
                calendar.roll(Calendar.HOUR_OF_DAY, timeZone.getRawOffset() / (3600 * 1000));

                if (!value.isEmpty()){

                    calendar.set(Calendar.YEAR, Integer.valueOf(value.substring(0, 4)));
                    calendar.set(Calendar.MONTH, Integer.valueOf(value.substring(4, 6)) - 1);
                    calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(value.substring(6, 8)));
                    calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(value.substring(8, 10)));
                    calendar.set(Calendar.MINUTE, Integer.valueOf(value.substring(10, 12)));
                    calendar.set(Calendar.SECOND, Integer.valueOf(value.substring(12, 14)));

                }

                JSONObject finalField = field;
                new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                        new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {

                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, month);
                                calendar.set(Calendar.DAY_OF_MONTH, day);
                                calendar.set(Calendar.HOUR_OF_DAY, hour);
                                calendar.set(Calendar.MINUTE, minute);
                                calendar.set(Calendar.SECOND, 0);

                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

                                String value = simpleDateFormat.format(calendar.getTime());

                                JsonProcs.putToJsonObject(finalField, "value", value);

                                value = value.substring(6, 8) + "." + value.substring(4, 6) + "." + value.substring(0, 4)
                                        + " " + value.substring(8, 10) + ":" + value.substring(10, 12) + ":" + value.substring(12, 14);

                                int curInput = JsonProcs.getIntegerFromJSON(finalField, "input");

                                inflate.findViewById(curInput).setBackgroundColor(Color.parseColor("#FFFFFF"));

                                ((TextView) inflate.findViewById(curInput)).setText(value);


                            }
                        },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE), true).show();

                    }
                },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();


            }
        });
    }

    private void setRefChoiser(View inflate, LinearLayout tr) {
        tr.getChildAt(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JSONObject field = getFieldByViewId(view.getId());
                String value = JsonProcs.getStringFromJSON(field, "value");

                Bundle bundle = new Bundle();
                bundle.putInt("id", view.getId());
                bundle.putString("value", value);

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main)
                        .navigate(Integer.valueOf(JsonProcs.getStringFromJSON(field, "fragment")), bundle);

//                TimeZone timeZone = TimeZone.getTimeZone("Europe/Moscow");
//
//                Calendar calendar = new GregorianCalendar();
//                calendar.roll(Calendar.HOUR_OF_DAY, timeZone.getRawOffset() / (3600 * 1000));
//
//                if (!value.isEmpty()){
//
//                    calendar.set(Calendar.YEAR, Integer.valueOf(value.substring(0, 4)));
//                    calendar.set(Calendar.MONTH, Integer.valueOf(value.substring(4, 6)) - 1);
//                    calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(value.substring(6, 8)));
//                    calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(value.substring(8, 10)));
//                    calendar.set(Calendar.MINUTE, Integer.valueOf(value.substring(10, 12)));
//                    calendar.set(Calendar.SECOND, Integer.valueOf(value.substring(12, 14)));
//
//                }
//
//                JSONObject finalField = field;
//                new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
//
//                        new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
//                            @Override
//                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
//
//                                calendar.set(Calendar.YEAR, year);
//                                calendar.set(Calendar.MONTH, month);
//                                calendar.set(Calendar.DAY_OF_MONTH, day);
//                                calendar.set(Calendar.HOUR_OF_DAY, hour);
//                                calendar.set(Calendar.MINUTE, minute);
//                                calendar.set(Calendar.SECOND, 0);
//
//                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
//
//                                String value = simpleDateFormat.format(calendar.getTime());
//
//                                JsonProcs.putToJsonObject(finalField, "value", value);
//
//                                value = value.substring(6, 8) + "." + value.substring(4, 6) + "." + value.substring(0, 4)
//                                        + " " + value.substring(8, 10) + ":" + value.substring(10, 12) + ":" + value.substring(12, 14);
//
//                                int curInput = JsonProcs.getIntegerFromJSON(finalField, "input");
//
//                                ((TextView) inflate.findViewById(curInput)).setText(value);
//
//
//                            }
//                        },
//                                calendar.get(Calendar.HOUR_OF_DAY),
//                                calendar.get(Calendar.MINUTE), true).show();
//
//                    }
//                },
//                        calendar.get(Calendar.YEAR),
//                        calendar.get(Calendar.MONTH),
//                        calendar.get(Calendar.DAY_OF_MONTH))
//                        .show();


            }
        });
    }

    @Nullable
    private JSONObject getFieldByViewId(int viewId) {

        JSONObject field = null;
        String value = null;
        for (int j = 0; j < record.length() && value == null; j++) {

            JSONObject fd = JsonProcs.getItemJSONArray(record, j);

            String name = fd.keys().next();

            field = JsonProcs.getJsonObjectFromJsonObject(fd, name);

            int curBtn = JsonProcs.getIntegerFromJSON(field, "btn");
            if (viewId == curBtn){

                value = JsonProcs.getStringFromJSON(field, "value");

            }

        }
        return field;
    }


}