package com.example.apexwh.ui.moves;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

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

public class MovementFragment extends ScanShtrihcodeFragment {


    public MovementFragment() {
        super(R.layout.fragment_scan_container_cell);
    }

    private TextView tvCell, tvContent, tvSource;

    private String cellRef, cellName, containerRef, containerName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setOnCreateViewElements(new OnCreateViewElements() {
            @Override
            public void execute(View root) {

                tvCell = root.findViewById(R.id.tvCell);
                tvContent = root.findViewById(R.id.tvContent);
                tvSource = root.findViewById(R.id.tvSource);

            }
        });

        setScanCodeSetter(new ScanCodeSetter() {
            @Override
            public void setScanCode(String strCatName, int pos, int quantity) {

                if (tvSource.getText().toString().isEmpty()){

                    RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladCellsContainers", "filter=" + strCatName, new JSONObject(),
                            RequestToServer.TypeOfResponse.JsonObjectWithArray, response -> {

                        JSONArray cells = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladCellsContainers");

                        if (cells.length() == 1){

                            JSONObject cell = JsonProcs.getItemJSONArray(cells, 0);

                            String type = JsonProcs.getStringFromJSON(cell, "type");

                            if (type.equals("Ячейка")){

                                cellRef = JsonProcs.getStringFromJSON(cell, "ref");
                                cellName = JsonProcs.getStringFromJSON(cell, "name");

                                containerRef = JsonProcs.getStringFromJSON(cell, "containerCellRef");
                                containerName = JsonProcs.getStringFromJSON(cell, "containerCell");

                            } else {

                                containerRef = JsonProcs.getStringFromJSON(cell, "ref");
                                containerName = JsonProcs.getStringFromJSON(cell, "name");

                                cellRef = JsonProcs.getStringFromJSON(cell, "containerCellRef");
                                cellName = JsonProcs.getStringFromJSON(cell, "containerCell");


                            }

                            tvSource.setText("Ячейка: " + cellName + ", контейнер: " + containerName);
//                            tvContent.setText(JsonProcs.getStringFromJSON(cell, "container")
//                                    + ", " + JsonProcs.getStringFromJSON(cell, "product")
//                                    + ", " + JsonProcs.getIntegerFromJSON(cell, "quantity")
//                                    + " (" + JsonProcs.getIntegerFromJSON(cell, "placeQuantity") + ")" );

                        }

                    });

                } else {

                    RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladCells", "filter=" + strCatName, new JSONObject(),
                            RequestToServer.TypeOfResponse.JsonObjectWithArray, response -> {

                        JSONArray containers = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladCells");

                        if (containers.length() == 1){

                            JSONObject container = JsonProcs.getItemJSONArray(containers, 0);

                            String cellDestinationRef = JsonProcs.getStringFromJSON(container, "ref");
                            tvCell.setText(JsonProcs.getStringFromJSON(container, "name"));

                            Bundle args = new Bundle();
                            args.putString("cellRef", cellRef);
                            args.putString("cellDestinationRef", cellDestinationRef);
                            args.putString("containerRef", containerRef);
                            args.putString("containerName", tvSource.getText().toString());

                            Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), arguments -> {

                                JSONObject jsonObject = new JSONObject();
                                JsonProcs.putToJsonObject(jsonObject,"ref", UUID.randomUUID().toString());
                                JsonProcs.putToJsonObject(jsonObject,"cellRef", arguments.getString("cellRef"));
                                JsonProcs.putToJsonObject(jsonObject,"cellDestinationRef", arguments.getString("cellDestinationRef"));
                                JsonProcs.putToJsonObject(jsonObject,"containerRef", arguments.getString("containerRef"));
                                JsonProcs.putToJsonObject(jsonObject,"containerName", arguments.getString("containerName"));

                                RequestToServer.executeRequestBodyUW(getContext(), Request.Method.POST, "setErpSkladMovement", jsonObject,
                                        RequestToServer.TypeOfResponse.JsonObject, response1 -> {

                                    if (!JsonProcs.getStringFromJSON(response1, "ref").isEmpty()){
                                        navController.popBackStack();
                                    }



                                        });


                            },  args, "Переместить контейнер " + containerName + " в ячейку " + tvCell.getText().toString() + " ?", "Перемещение");

                        }

                    });


                }



            }
        });
    }
}