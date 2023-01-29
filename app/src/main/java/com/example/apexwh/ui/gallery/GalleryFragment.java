package com.example.apexwh.ui.gallery;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.apexwh.Connections;
import com.example.apexwh.HttpClient;
import com.example.apexwh.HttpRequestInterface;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.databinding.FragmentGalleryBinding;
import com.example.apexwh.objects.Document;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;

    private ProgressBar progressBar;
    private ImageView imageView;
    private String ref;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        Bundle arguments = getArguments();

        ref = arguments.getString("productRef");

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        progressBar = root.findViewById(R.id.progressBar);
        imageView = root.findViewById(R.id.imageView);

        updateList();

//        final TextView textView = binding.textGallery;
//        galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateList() {

        HttpClient httpClient = new HttpClient(getContext());

        httpClient.request_get_apx("/files?type=goods&id=" + ref, new HttpRequestInterface() {
            @Override
            public void setProgressVisibility(int visibility) {

                progressBar.setVisibility(visibility);

            }

            @Override
            public void processResponse(String response) {

                JSONObject jsonObjectResponse = JsonProcs.getJSONObjectFromString(response);

                if (JsonProcs.getBooleanFromJSON(jsonObjectResponse, "success")){

                    JSONArray jsonArrayResult = JsonProcs.getJsonArrayFromJsonObject(jsonObjectResponse, "result");

                    for (int j = 0; j < jsonArrayResult.length(); j++) {

                        JSONObject objectItem = JsonProcs.getItemJSONArray(jsonArrayResult, j);

                        String version_id = JsonProcs.getStringFromJSON(objectItem,"version_id");
                        String ext = JsonProcs.getStringFromJSON(objectItem,"ext");
                        String name = JsonProcs.getStringFromJSON(objectItem,"name");

                        Uri uri = Uri.parse(Connections.addrApx + "/getatt/?in_t=0&in_a=1&id=" + version_id + "&ext=" + ext + "&full_name=" + name + "&u=undefined&disp=1");

                        Picasso.get().load(uri.toString()).into(imageView);


//                        imageView.setImageBitmap(getImageBitmap(uri.toString()));

//    /getatt/?in_t=0&in_a=1&id=" + version_id + "&ext=" + ext + "&full_name=" + name + "&u=undefined

                        Integer i = 1;
//                        returns.add(Document.DocumentFromJson(objectItem));

                    }

//                    adapter.notifyDataSetChanged();

                }

            }

        });

    }


}