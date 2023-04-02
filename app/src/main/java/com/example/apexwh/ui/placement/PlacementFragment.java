package com.example.apexwh.ui.placement;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.RequestToServer;
import com.example.apexwh.ui.Dialogs;
import com.example.apexwh.ui.ScanShtrihcodeFragment;
import com.example.apexwh.ui.adapters.ScanCodeSetter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;

public class PlacementFragment extends ScanShtrihcodeFragment {


    public PlacementFragment() {
        super(R.layout.fragment_scan_cell_container);
    }

    private TextView tvCell, tvContainer;

    private String cellRef, containerRef;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setOnCreateViewElements(new OnCreateViewElements() {
            @Override
            public void execute(View root) {

                tvCell = root.findViewById(R.id.tvCell);
                tvContainer = root.findViewById(R.id.tvContainer);

            }
        });

        setScanCodeSetter(new ScanCodeSetter() {
            @Override
            public void setScanCode(String strCatName, int pos, int quantity) {

                if (tvCell.getText().toString().isEmpty()){

                    RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladCells", "filter=" + strCatName, new JSONObject(),
                            RequestToServer.TypeOfResponse.JsonObjectWithArray, response -> {

                        JSONArray cells = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladCells");

                        if (cells.length() == 1){

                            JSONObject cell = JsonProcs.getItemJSONArray(cells, 0);

                            cellRef = JsonProcs.getStringFromJSON(cell, "ref");
                            tvCell.setText(JsonProcs.getStringFromJSON(cell, "name"));

                        }

                    });

                } else {

                    RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladContainers", "filter=" + strCatName, new JSONObject(),
                            RequestToServer.TypeOfResponse.JsonObjectWithArray, response -> {

                        JSONArray containers = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladContainers");

                        if (containers.length() == 1){

                            JSONObject container = JsonProcs.getItemJSONArray(containers, 0);

                            containerRef = JsonProcs.getStringFromJSON(container, "ref");
                            tvContainer.setText(JsonProcs.getStringFromJSON(container, "name"));

                            Bundle args = new Bundle();
                            args.putString("cellRef", cellRef);
                            args.putString("containerRef", containerRef);
                            args.putString("containerName", tvContainer.getText().toString());

                            Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), arguments -> {

                                JSONObject jsonObject = new JSONObject();
                                JsonProcs.putToJsonObject(jsonObject,"ref", UUID.randomUUID().toString());
                                JsonProcs.putToJsonObject(jsonObject,"cellRef", arguments.getString("cellRef"));
                                JsonProcs.putToJsonObject(jsonObject,"containerRef", arguments.getString("containerRef"));
                                JsonProcs.putToJsonObject(jsonObject,"containerName", arguments.getString("containerName"));

                                RequestToServer.executeRequestBodyUW(getContext(), Request.Method.POST, "setErpSkladPlacement", jsonObject,
                                        RequestToServer.TypeOfResponse.JsonObjectWithArray, response1 -> {

                                            navController.popBackStack();

                                        });


                            },  args, "Разместить контейнер " + tvContainer.getText().toString() + " в ячейку " + tvCell.getText().toString() + " ?", "Размещение");

                        }

                    });


                }



            }
        });
    }
}