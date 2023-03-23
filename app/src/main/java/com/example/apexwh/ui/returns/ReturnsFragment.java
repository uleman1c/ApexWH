package com.example.apexwh.ui.returns;

import static android.app.Activity.RESULT_OK;
import static android.os.Environment.DIRECTORY_PICTURES;

import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apexwh.Connections;
import com.example.apexwh.DB;
import com.example.apexwh.GetFoto;
import com.example.apexwh.HttpClient;
import com.example.apexwh.HttpRequestInterface;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.objects.Document;
import com.example.apexwh.objects.Reference;
import com.example.apexwh.objects.Return;
import com.example.apexwh.objects.Warehouse;
import com.example.apexwh.ui.adapters.DocumentDataAdapter;
import com.example.apexwh.ui.adapters.ReferenceDataAdapter;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.FileEntity;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;

public class ReturnsFragment extends Fragment {

    private ReturnsViewModel mViewModel;

    public static ReturnsFragment newInstance() {
        return new ReturnsFragment();
    }

    private ProgressBar progressBar;
    private ArrayList<Document> returns;

    private DocumentDataAdapter adapter;
    private RecyclerView recyclerView;
    private EditText etFilter;
    private InputMethodManager imm;

    private String warehouseId;

    private Uri outputFileUri;
    private static final int CAMERA_REQUEST = 20;
    public static final int CAMERA_REQUEST_FOTO = 0, CAMERA_REQUEST_ADAPTER = 1;

    GetFoto getFoto;

    private File photo;

    private String currentPhotoPath;

    private String name = "";
    private String ref = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_returns, container, false);

        Bundle settings = DB.getSettings(getContext());

        warehouseId = settings.getString("warehouseId");

        progressBar = root.findViewById(R.id.progressBar);

        returns = new ArrayList<>();

        getFoto = new GetFoto(getContext());

        adapter = new DocumentDataAdapter(getContext(), returns);
        adapter.setOnDocumentItemClickListener(new DocumentDataAdapter.OnDocumentItemClickListener() {
            @Override
            public void onDocumentItemClick(Document document) {

                Bundle bundle = new Bundle();
                bundle.putString("ref", document.ref);
                bundle.putString("name", document.name);
                bundle.putString("nameStr", document.nameStr);
                bundle.putString("number", document.number);
                bundle.putString("date", document.date);
                bundle.putString("description", document.description); // args.getString("nameStr") + " № " + args.getString("number") + " от " + args.getString("date")
                bundle.putString("mode", "return");

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.nav_returnProductsFragment, bundle);

            }

        });
        adapter.setOnDocumentItemLongClickListener(new DocumentDataAdapter.OnDocumentItemLongClickListener() {
            @Override
            public void onDocumentLongItemClick(Document document) {

                name = document.name;
                ref = document.ref;

                if (getFoto.intent != null){

                    startActivityForResult(getFoto.intent, CAMERA_REQUEST_FOTO);

                }



            }
        });

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


        updateList(etFilter.getText().toString());

        return root;
    }

    private void updateList(String filter) {

        returns.clear();

        HttpClient httpClient = new HttpClient(getContext());

        httpClient.request_get("/hs/dta/obj?request=getReturnsToAccept&warehouseId=" + warehouseId + "&filter=" + filter, new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

                progressBar.setVisibility(visibility);

            }

            @Override
            public void processResponse(String response) {

                JSONObject jsonObjectResponse = JsonProcs.getJSONObjectFromString(response);

                if (JsonProcs.getBooleanFromJSON(jsonObjectResponse, "success")){

                    JSONArray jsonArrayResponses = JsonProcs.getJsonArrayFromJsonObject(jsonObjectResponse, "responses");

                    JSONObject jsonObjectItem = JsonProcs.getItemJSONArray(jsonArrayResponses, 0);

                    JSONArray jsonArrayObjects = JsonProcs.getJsonArrayFromJsonObject(jsonObjectItem, "ReturnsToAccept");

                    for (int j = 0; j < jsonArrayObjects.length(); j++) {

                        JSONObject objectItem = JsonProcs.getItemJSONArray(jsonArrayObjects, j);

                        returns.add(Document.DocumentFromJson(objectItem));

                    }

                    adapter.notifyDataSetChanged();

                }

            }

        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ReturnsViewModel.class);
        // TODO: Use the ViewModel
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_FOTO) {

            String arFileNameWithExt = getFoto.file.getName();
            String arFileName = UUID.randomUUID().toString();

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            /* example for setting a HttpMultipartMode */
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            /* example for adding an image part */
            FileBody fileBody = new FileBody(getFoto.file); //image should be a String
            builder.addPart("icon", fileBody);

            HttpEntity httpEntity = new FileEntity(getFoto.file, ContentType.IMAGE_JPEG);

            HttpClient httpClient = new HttpClient(getActivity(), Connections.fileAddr, "", "");

            httpClient.addHeader("type1c", "doc");
            httpClient.addHeader("name1c", Uri.encode(name));
            httpClient.addHeader("id1c", ref);
            httpClient.addHeader("id", arFileName);
            httpClient.addHeader("part", "1");
            httpClient.addHeader("filename", Uri.encode(arFileNameWithExt));
            httpClient.addHeader("size", String.valueOf(getFoto.file.length()));

            httpClient.postBinary("/upfa/?ak=" + Connections.fileAccessKey, httpEntity, new HttpRequestInterface() {
                @Override
                public void setProgressVisibility(int visibility) {

                    progressBar.setVisibility(visibility);

                }

                @Override
                public void processResponse(String response) {

                    JSONObject jsonObjectResponse = JsonProcs.getJSONObjectFromString(response);

                    if(JsonProcs.getBooleanFromJSON(jsonObjectResponse, "success")){

                        Toast.makeText(getContext(), "Фото сохранено", Toast.LENGTH_SHORT).show();

                    }


                }
            });

        }

    }


}