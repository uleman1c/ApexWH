package com.example.apexwh.ui.collects;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

import com.example.apexwh.HttpClient;
import com.example.apexwh.HttpRequestJsonObjectInterface;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.objects.DocumentLine;
import com.example.apexwh.ui.BundleMethodInterface;
import com.example.apexwh.ui.Dialogs;
import com.example.apexwh.ui.adapters.DocumentLineAdapter;
import com.example.apexwh.ui.products.ProductsFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CollectProductsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CollectProductsFragment extends ProductsFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListUpdater(new ListUpdater() {
            @Override
            public void update(String name, String ref, ArrayList<DocumentLine> lines, ProgressBar progressBar, DocumentLineAdapter adapter) {

                lines.clear();

                HttpClient httpClient = new HttpClient(getContext());

                httpClient.request_get("/hs/dta/obj?request=getLinesToTest&name=" + name + "&id=" + ref, new HttpRequestJsonObjectInterface() {

                    @Override
                    public void setProgressVisibility(int visibility) {

                        progressBar.setVisibility(visibility);

                    }

                    @Override
                    public void processResponse(JSONObject jsonObjectResponse) {

                        JSONArray jsonArrayResponses = JsonProcs.getJsonArrayFromJsonObject(jsonObjectResponse, "responses");

                        JSONObject jsonObjectItem = JsonProcs.getItemJSONArray(jsonArrayResponses, 0);

                        JSONArray jsonArrayObjects = JsonProcs.getJsonArrayFromJsonObject(jsonObjectItem, "LinesToTest");

                        for (int j = 0; j < jsonArrayObjects.length(); j++) {

                            JSONObject objectItem = JsonProcs.getItemJSONArray(jsonArrayObjects, j);

                            lines.add(DocumentLine.DocumentLineFromJson(objectItem));

                        }

                        adapter.notifyDataSetChanged();

                    }

                });



            }
        });

        setOnCreateViewElements(new OnCreateViewElements() {
            @Override
            public void execute(View root) {

                getAdapter().setonBindViewHolderI(new DocumentLineAdapter.onBindViewHolderI() {
                    @Override
                    public void OnBindViewHolder(DocumentLineAdapter.DocumentLineItemViewHolder holder, int position, ArrayList<DocumentLine> documentLines) {

                        DocumentLine documentLine = documentLines.get(position);

                        holder.tvArtikul.setText(documentLine.productArtikul);
                        holder.tvProduct.setText(documentLine.productName
                                + (documentLine.characterName.isEmpty() || documentLine.characterName.equals("Основная характеристика") ? "" : ", " + documentLine.characterName));

                        String allSK = "";

                        for (String curSK : documentLine.shtrihCodes) {

                            allSK = allSK + (allSK.isEmpty() ? "" : ", ") + curSK;

                        }

                        holder.tvShtrihCodes.setText(allSK);

                        holder.tvScanned.setText(documentLine.scanned.toString() + " из " + documentLine.quantity.toString());

                        if (documentLine.scanned == documentLine.quantity){

                            holder.llMain.setBackgroundColor(Color.parseColor("#00ff00"));

                        }


                    }
                });

                getAdapter().setOnDocumentLineItemClickListener(new DocumentLineAdapter.OnDocumentLineItemClickListener() {
                    @Override
                    public void onDocumentLineItemClick(DocumentLine documentLine) {

                        //curdocumentLine = documentLine;

                        Bundle bundle = new Bundle();
//                        bundle.putString("productRef", documentLine.productRef);
//                        bundle.putString("productName", documentLine.productName);
//                        bundle.putString("characterRef", documentLine.characterRef);
//                        bundle.putString("characterName", documentLine.characterName);

                        Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), new BundleMethodInterface() {
                            @Override
                            public void callMethod(Bundle arguments) {

                                sendScanned(documentLine, 1);

                            }
                        }, bundle, "Ввести вручную "
                                + documentLine.productName
                                + (documentLine.characterName.equals("Основная характеристика") ? "" :
                                    " (" + documentLine.characterName + ")" ) + " ?", "Ввод");


                    }
                });

                getAdapter().setOnDocumentLineItemLongClickListener(new DocumentLineAdapter.OnDocumentLineItemLongClickListener() {
                    @Override
                    public void onDocumentLineItemLongClick(DocumentLine documentLine) {

                        Bundle bundle = new Bundle();

                        Dialogs.showInputQuantity(getContext(), documentLine.quantity - documentLine.scanned, getActivity(), new BundleMethodInterface() {
                            @Override
                            public void callMethod(Bundle arguments) {

                                sendScanned(documentLine, arguments.getInt("quantity"));

                            }
                        }, bundle, "Ввести вручную "
                                + documentLine.productName
                                + (documentLine.characterName.equals("Основная характеристика") ? "" :
                                " (" + documentLine.characterName + ")" ) + " ?", "Ввод количества");



                    }
                });

            }
        });

    }

    private void sendScanned(DocumentLine documentLine, int quantity) {

        final HttpClient httpClient = new HttpClient(getContext());
        httpClient.addParam("id", UUID.randomUUID().toString());
        httpClient.addParam("shtrihCode", "");
        httpClient.addParam("appId", httpClient.getDbConstant("appId"));
        httpClient.addParam("quantity", quantity);
        httpClient.addParam("type1c", "doc");
        httpClient.addParam("name1c", name);
        httpClient.addParam("id1c", ref);
        httpClient.addParam("productRef", documentLine.productRef);
        httpClient.addParam("characterRef", documentLine.characterRef);
        httpClient.addParam("characterName", documentLine.characterName);
        httpClient.addParam("comment", "");

        httpClient.request_get("/hs/dta/obj", "setTestProduct", new HttpRequestJsonObjectInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

                progressBar.setVisibility(visibility);
            }

            @Override
            public void processResponse(JSONObject response) {

                setScanned(documentLine, quantity);

                if (allScanned()){

                    Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), new BundleMethodInterface() {
                        @Override
                        public void callMethod(Bundle arguments) {

                            setDocumentStatus();

                        }
                    }, new Bundle(), "Завершить проверку?", "Вопрос");

                }

            }
        });



    }

}