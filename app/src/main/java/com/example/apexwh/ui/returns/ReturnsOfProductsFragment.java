package com.example.apexwh.ui.returns;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apexwh.Connections;
import com.example.apexwh.DB;
import com.example.apexwh.DateStr;
import com.example.apexwh.GetFoto;
import com.example.apexwh.HttpClient;
import com.example.apexwh.HttpRequestInterface;
import com.example.apexwh.HttpRequestJsonObjectInterface;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.RequestToServer;
import com.example.apexwh.VolleyMultipartRequest;
import com.example.apexwh.objects.Acceptment;
import com.example.apexwh.objects.Document;
import com.example.apexwh.objects.Return;
import com.example.apexwh.objects.ReturnOfProducts;
import com.example.apexwh.ui.BundleMethodInterface;
import com.example.apexwh.ui.Dialogs;
import com.example.apexwh.ui.adapters.DataAdapter;
import com.example.apexwh.ui.adapters.DocumentDataAdapter;
import com.example.apexwh.ui.adapters.ListFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.FileEntity;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;

public class ReturnsOfProductsFragment extends ListFragment<ReturnOfProducts> {

    Button btnUpdate;

    public static final int CAMERA_REQUEST_FOTO = 0, CAMERA_REQUEST_ADAPTER = 1;

    GetFoto getFoto;
    private String name = "";
    private String ref = "";

    ActivityResultLauncher<Intent> startCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {

                        sendPhoto();


                    }
                }
            }
    );

    private ActivityResultLauncher<String[]> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
        @Override
        public void onActivityResult(Map<String, Boolean> result) {
            Log.e("activityResultLauncher", "" + result.toString());
            Boolean areAllGranted = true;
            for (String perm : result.keySet()) {
                areAllGranted = ActivityCompat.checkSelfPermission(getContext(), perm) == PackageManager.PERMISSION_GRANTED && areAllGranted;
            }

            //startLocationManager();

            if (areAllGranted) {


            }
        }
    });


    public ReturnsOfProductsFragment() {

        super(R.layout.fragment_filter_btn_list, R.layout.outcome_list_item);

        setListUpdater(new ListUpdater() {
            @Override
            public void update(ArrayList items, ProgressBar progressBar, DataAdapter adapter, String filter) {

                items.clear();

                HttpClient httpClient = new HttpClient(getContext());

                httpClient.request_get("/hs/dta/obj?request=getReturns&warehouseId=" + getWarehouseId() + "&filter=" + filter, new HttpRequestJsonObjectInterface() {
                    @Override
                    public void setProgressVisibility(int visibility) {

                        progressBar.setVisibility(visibility);

                    }

                    @Override
                    public void processResponse(JSONObject jsonObjectResponse) {

                        JSONArray jsonArrayResponses = JsonProcs.getJsonArrayFromJsonObject(jsonObjectResponse, "responses");

                        JSONObject jsonObjectItem = JsonProcs.getItemJSONArray(jsonArrayResponses, 0);

                        JSONArray jsonArrayObjects = JsonProcs.getJsonArrayFromJsonObject(jsonObjectItem, "Returns");

                        for (int j = 0; j < jsonArrayObjects.length(); j++) {

                            JSONObject objectItem = JsonProcs.getItemJSONArray(jsonArrayObjects, j);

                            items.add(ReturnOfProducts.FromJson(objectItem));

                        }

                        adapter.notifyDataSetChanged();

                    }

                });



            }
        });

        setOnCreateViewElements(new OnCreateViewElements() {
            @Override
            public void execute(View root, NavController navController) {

                getFoto = new GetFoto(getContext());

                getAdapter().setInitViewsMaker(new DataAdapter.InitViewsMaker() {
                    @Override
                    public void init(View itemView, ArrayList<TextView> textViews) {

                        textViews.add(itemView.findViewById(R.id.tvNumberDate));
                        textViews.add(itemView.findViewById(R.id.tvDescription));
                        textViews.add(itemView.findViewById(R.id.tvComment));
                    }
                });

                getAdapter().setDrawViewHolder(new DataAdapter.DrawViewHolder<ReturnOfProducts>() {
                    @Override
                    public void draw(DataAdapter.ItemViewHolder holder, ReturnOfProducts document) {

                        ((TextView) holder.getTextViews().get(0)).setText(document.description);
                        ((TextView) holder.getTextViews().get(1)).setText(document.contractor + ", " + document.incomeNumber + " от " + DateStr.FromYmdhmsToDmy(document.incomeDate));
                        ((TextView) holder.getTextViews().get(2)).setText(document.comment);
                    }
                });

                getAdapter().setOnClickListener(new DataAdapter.OnClickListener<ReturnOfProducts>() {
                    @Override
                    public void onItemClick(ReturnOfProducts document) {

                        JSONObject jsonObject = new JSONObject();

                        JsonProcs.putToJsonObject(jsonObject, "id", UUID.randomUUID().toString());
                        JsonProcs.putToJsonObject(jsonObject, "baseId", document.ref);
                        JsonProcs.putToJsonObject(jsonObject, "baseName", document.name);

                        Bundle bundle = new Bundle();
                        bundle.putString("selected", jsonObject.toString());

                        Dialogs.showReturnOfProductsMenu(getContext(), getActivity(), new BundleMethodInterface() {
                            @Override
                            public void callMethod(Bundle arguments) {

                                JSONObject selected = JsonProcs.getJSONObjectFromString(arguments.getString("selected"));

                                if (arguments.getString("btn").equals("OrderToChangeCharacteristic")){

                                    HttpClient httpClient = new HttpClient(getContext());

                                    httpClient.request_get("/hs/dta/obj?request=getOrderToChangeCharacteristic"
                                            + "&id=" + JsonProcs.getStringFromJSON(selected, "id")
                                            + "&baseId=" + JsonProcs.getStringFromJSON(selected, "baseId")
                                            + "&baseName=" + JsonProcs.getStringFromJSON(selected, "baseName"), new HttpRequestJsonObjectInterface() {
                                        @Override
                                        public void setProgressVisibility(int visibility) {

                                            //progressBar.setVisibility(visibility);

                                        }

                                        @Override
                                        public void processResponse(JSONObject jsonObjectResponse) {

                                            JSONArray jsonArrayResponses = JsonProcs.getJsonArrayFromJsonObject(jsonObjectResponse, "responses");

                                            JSONObject jsonObjectItem = JsonProcs.getItemJSONArray(jsonArrayResponses, 0);

                                            JSONArray jsonArrayObjects = JsonProcs.getJsonArrayFromJsonObject(jsonObjectItem, "OrderToChangeCharacteristic");

                                            JSONObject objectItem = JsonProcs.getItemJSONArray(jsonArrayObjects, 0);

                                            navController.popBackStack();

                                            Bundle bundle1 = new Bundle();
                                            bundle1.putString("ref", JsonProcs.getStringFromJSON(objectItem, "ref"));
                                            bundle1.putString("name", "ЗаявкаНаКорректировкуТоваров");


                                            navController.navigate(R.id.nav_orderToChangeCharacteristicProductsFragment, bundle1);

                                        }

                                    });


                                }

                            }
                        }, bundle, "Выберите", "Меню");

//                        Bundle result = getArguments();
//                        result.putString("selected", jsonObject.toString());
//                        getParentFragmentManager().setFragmentResult("acceptment_order_selected", result);


                    }
                });

                getAdapter().setOnLongClickListener(new DataAdapter.OnLongClickListener<ReturnOfProducts>() {
                    @Override
                    public void onLongItemClick(ReturnOfProducts document) {

                        name = document.name;
                        ref = document.ref;

                                if (getFoto.intent != null){

//                                    startActivityForResult(getFoto.intent, CAMERA_REQUEST_FOTO);
                                    startCamera.launch(getFoto.intent); // VERY NEW WAY

                                }






                    }
                });

                btnUpdate = root.findViewById(R.id.btnAction);

                btnUpdate.setText("Обновить");

                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        updateList("");

                    }
                });

            }
        });



    }

    public void sendPhoto() {

        String arFileNameWithExt = getFoto.file.getName();
        String arFileName = UUID.randomUUID().toString();

        HashMap headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/octet-stream");
        headers.put("type1c", "doc");
        headers.put("name1c", Uri.encode(name));
        headers.put("id1c", ref);
        headers.put("owner_name", Uri.encode(name));
        headers.put("owner_id", ref);
        headers.put("user", "mihail.u");
        headers.put("id", arFileName);
        headers.put("part", "1");
        headers.put("filename", Uri.encode(arFileNameWithExt));
        headers.put("size", String.valueOf(getFoto.file.length()));

        int size = (int) getFoto.file.length();
        byte[] bytes = new byte[size];
        BufferedInputStream buf = null;
        try {
            buf = new BufferedInputStream(new FileInputStream(getFoto.file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            buf.read(bytes, 0, bytes.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            buf.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        RequestToServer.uploadByteArrayMultiPart(getContext(), Connections.fileAddr, headers, bytes, response -> {

        });



        }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String[] appPerms;
        appPerms = new String[] {
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                Manifest.permission.INTERNET
        };

        this.activityResultLauncher.launch(appPerms);

    }




}