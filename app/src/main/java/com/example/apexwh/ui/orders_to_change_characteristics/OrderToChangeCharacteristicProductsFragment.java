package com.example.apexwh.ui.orders_to_change_characteristics;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.apexwh.DateStr;
import com.example.apexwh.HttpClient;
import com.example.apexwh.HttpRequestJsonObjectInterface;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.objects.DocumentLine;
import com.example.apexwh.objects.MoversService;
import com.example.apexwh.objects.OrderToChangeCharactericticLine;
import com.example.apexwh.ui.BundleMethodInterface;
import com.example.apexwh.ui.Dialogs;
import com.example.apexwh.ui.adapters.DataAdapter;
import com.example.apexwh.ui.adapters.DocumentLineAdapter;
import com.example.apexwh.ui.adapters.ListFragment;
import com.example.apexwh.ui.adapters.ScanProductsFragment;
import com.example.apexwh.ui.products.ProductsFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderToChangeCharacteristicProductsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderToChangeCharacteristicProductsFragment extends ScanProductsFragment<OrderToChangeCharactericticLine> {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListUpdater(new ListUpdater() {
            @Override
            public void update(String name, String ref, ArrayList lines, ProgressBar progressBar, DataAdapter adapter) {

                lines.clear();

                HttpClient httpClient = new HttpClient(getContext());

                httpClient.request_get("/hs/dta/obj?request=getOrderToChangeCharacteristic&name=" + name + "&id=" + ref, new HttpRequestJsonObjectInterface() {

                    @Override
                    public void setProgressVisibility(int visibility) {

                        progressBar.setVisibility(visibility);

                    }

                    @Override
                    public void processResponse(JSONObject jsonObjectResponse) {

                        JSONArray jsonArrayResponses = JsonProcs.getJsonArrayFromJsonObject(jsonObjectResponse, "responses");

                        JSONObject jsonObjectItem = JsonProcs.getItemJSONArray(jsonArrayResponses, 0);

                        JSONArray jsonArrayObjects = JsonProcs.getJsonArrayFromJsonObject(jsonObjectItem, "OrderToChangeCharacteristic");

                        JSONObject objectItem = JsonProcs.getItemJSONArray(jsonArrayObjects, 0);

                        JSONArray products = JsonProcs.getJsonArrayFromJsonObject(objectItem, "products");

                        for (int j = 0; j < products.length(); j++) {

                            JSONObject productItem = JsonProcs.getItemJSONArray(products, j);

                            lines.add(OrderToChangeCharactericticLine.FromJson(productItem));

                        }

                        adapter.notifyDataSetChanged();

                    }

                });


            }
        });

        setOnCreateViewElements(new ScanProductsFragment.OnCreateViewElements() {
            @Override
            public void execute(View root) {

                getAdapter().setInitViewsMaker(new DataAdapter.InitViewsMaker() {
                    @Override
                    public void init(View itemView, ArrayList<TextView> textViews) {

                        textViews.add(itemView.findViewById(R.id.tvProduct));
                        textViews.add(itemView.findViewById(R.id.tvShtrihCodes));
                        textViews.add(itemView.findViewById(R.id.tvScanned));
                    }
                });

                getAdapter().setDrawViewHolder(new DataAdapter.DrawViewHolder<OrderToChangeCharactericticLine>() {
                    @Override
                    public void draw(DataAdapter.ItemViewHolder holder, OrderToChangeCharactericticLine documentLine) {

                        ((TextView) holder.getTextViews().get(0)).setText(documentLine.productDescription
                                + (documentLine.characterDescription.isEmpty() || documentLine.characterDescription.equals("Основная характеристика") ? ""
                                    : ", " + documentLine.characterDescription));

                        String allSK = "";

                        for (String curSK:   documentLine.shtrihCodes          ) {

                            allSK = allSK + (allSK.isEmpty() ? "" : ", ") + curSK;

                        }

                        ((TextView) holder.getTextViews().get(1)).setText(allSK);

                        ((TextView) holder.getTextViews().get(2)).setText(documentLine.scanned.toString() + " из " + documentLine.number.toString());




                    }
                });

                getAdapter().setOnClickListener(new DataAdapter.OnClickListener<OrderToChangeCharactericticLine>() {
                   @Override
                   public void onItemClick(OrderToChangeCharactericticLine documentLine) {

                       Bundle bundle = new Bundle();
                       //bundle.putString("shtrihcode", documentLine.shtrihCodes.get(0));

                       Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), new BundleMethodInterface() {
                           @Override
                           public void callMethod(Bundle arguments) {

        //                       scanShtrihCode(arguments.getString("shtrihcode"), 1);

                               setShtrihCode("", documentLine, 1, new BundleMethodInterface() {
                                   @Override
                                   public void callMethod(Bundle arguments) {

                                       testForExecuted();


                                   }
                               });

                               //sendScanned(documentLine, 1);

                           }
                       }, bundle, "Ввести вручную "
                               + documentLine.productName
                               + (documentLine.characterName.equals("Основная характеристика") ? "" :
                               " (" + documentLine.characterName + ")" ) + " ?", "Ввод");


                   }
                });

//        adapter.setOnDocumentLineItemLongClickListener(new DocumentLineAdapter.OnDocumentLineItemLongClickListener() {
//            @Override
//            public void onDocumentLineItemLongClick(DocumentLine documentLine) {
//
//                Bundle bundle = new Bundle();
//                bundle.putString("shtrihcode", "");
//                bundle.putInt("toScan", documentLine.quantity - documentLine.scanned);
//                bundle.putString("productRef", documentLine.productRef);
//                bundle.putString("productName", documentLine.productName);
//                bundle.putString("characterRef", documentLine.characterRef);
//                bundle.putString("characterName", documentLine.characterName);
//
//                Dialogs.showProductMenu(getContext(), getActivity(), new BundleMethodInterface() {
//                    @Override
//                    public void callMethod(Bundle arguments) {
//
//                        if (arguments.getString("btn").equals("Foto")){
//
//                            Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.nav_gallery, arguments);
//
//                        } else if (arguments.getString("btn").equals("InputNumber")) {
//
//                            showInputNumber(documentLine);
//
//                        } else if (arguments.getString("btn").equals("ChangeCharcteristic")) {
//
//                            Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.nav_characteristics, arguments);
//
//                        }
//
//                    }
//                }, bundle, "Выберите", "Меню");
//
//
//            }
//        });



            }
        });



    }




}