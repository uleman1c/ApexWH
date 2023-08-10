package com.example.apexwh.ui.info;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.example.apexwh.DateStr;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.RequestToServer;
import com.example.apexwh.SpanText;
import com.example.apexwh.objects.ProductCellContainerOutcome;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderInfoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrderInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderInfoFragment newInstance(String param1, String param2) {
        OrderInfoFragment fragment = new OrderInfoFragment();
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

    String ref, name;

    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_order_info, container, false);

        ref = getArguments().getString("ref");
        name = getArguments().getString("name");

        progressBar = inflate.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);

        RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladOrderInfo",
                "name=" + name + "&ref=" + ref, new JSONObject(), 1,
                new RequestToServer.ResponseResultInterface() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progressBar.setVisibility(View.GONE);

                        JSONObject orderInfo = JsonProcs.getJsonObjectFromJsonObject(response, "ErpSkladOrderInfo");

                        SpanText spanText = new SpanText();
                        spanText.AppendBold("Документ: ");
                        spanText.Append(JsonProcs.getStringFromJSON(orderInfo, "Представление"));
                        spanText.AppendBold("\n№ ");
                        spanText.Append(JsonProcs.getStringFromJSON(orderInfo, "НомерПоДаннымКлиента"));
                        spanText.AppendBold(" от ");
                        spanText.Append(DateStr.FromYmdhmsToDmyhms(JsonProcs.getStringFromJSON(orderInfo, "ДатаПоДаннымКлиента")));
                        spanText.AppendBold("\nКонтрагент: ");
                        spanText.Append(JsonProcs.getStringFromJSON(orderInfo, "Контрагент"));
                        spanText.AppendBold("\nМенеджер: ");
                        spanText.Append(JsonProcs.getStringFromJSON(orderInfo, "Менеджер"));
                        spanText.AppendBold("\nВес: ");
                        spanText.Append(JsonProcs.getIntegerFromJSON(orderInfo, "Вес").toString());

                        if (JsonProcs.getBooleanFromJSON(orderInfo,"ОтгрузкаСОтветственногоХранения")){
                            spanText.AppendBold("\n\nОтгрузка с ответственного хранения\n");

                        }

                        spanText.AppendBold("\nКомментарий: ");
                        spanText.Append(JsonProcs.getStringFromJSON(orderInfo, "Комментарий"));

                        ((TextView)inflate.findViewById(R.id.tvInfo)).setText(spanText.GetSpannableString());


//                        Результат.Вставить("Менеджер", Строка(ТекДок.Менеджер));
//                        Результат.Вставить("Вес", ТекДок.Вес);
//                        Результат.Вставить("ОтгрузкаСОтветственногоХранения", ТекДок.ОтгрузкаСОтветственногоХранения);
//                        Результат.Вставить("Комментарий", ТекДок.Комментарий);
//                        Результат.Вставить("НомерПоДаннымКлиента", ТекДок.НомерПоДаннымКлиента);
//                        Результат.Вставить("ДатаПоДаннымКлиента", Формат(ТекДок.ДатаПоДаннымКлиента, "ДФ=yyyyMMddhhmmss"));






                    }
                });



        return inflate;
    }
}