package com.example.apexwh.ui.placement;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.example.apexwh.DB;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.RequestToServer;
import com.example.apexwh.objects.History;
import com.example.apexwh.ui.BundleMethodInterface;
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

    private TextView tvCell, tvContent, tvContainer, tvContainerContent;

    private String cellRef, containerRef, cellName;

    private History history;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        history = new History(getContext(), "placement");

        cellRef = DB.nil;
        cellName = "";

        setOnCreateViewElements(new OnCreateViewElements() {
            @Override
            public void execute(View root) {

                tvCell = root.findViewById(R.id.tvCell);
                tvContent = root.findViewById(R.id.tvContent);
                tvContainer = root.findViewById(R.id.tvContainer);
                tvContainerContent = root.findViewById(R.id.tvContainerContent);

                RecyclerView rvHistory = root.findViewById(R.id.rvHistory);
                rvHistory.setAdapter(history.getAdapter());



            }
        });

        setScanCodeSetter(new ScanCodeSetter() {
            @Override
            public void setScanCode(String strCatName, int pos, int quantity) {

                scanCellContainer(strCatName, true);

            }
        });
    }

    private void scanCellContainer(String strCatName, boolean saveToHistory) {

        if (saveToHistory){

            history.AddHistoryRecord(strCatName);
        }

        RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladCellContainerProduct", "filter=" + strCatName, new JSONObject(),
                RequestToServer.TypeOfResponse.JsonObjectWithArray, response -> {

                    JSONArray cells = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladCellContainerProduct");

                    if (cells.length() == 1){

                        JSONObject cell = JsonProcs.getItemJSONArray(cells, 0);

                        String type = JsonProcs.getStringFromJSON(cell, "type");

                        if (type.equals("Ячейка")){

                            tvCell.setText(strCatName);

                            cellRef = JsonProcs.getStringFromJSON(cell, "ref");
                            cellName = JsonProcs.getStringFromJSON(cell, "name");
                            tvCell.setText(cellName);

                            containerRef = JsonProcs.getStringFromJSON(cell, "containerCellRef");

                            tvContent.setText("Контейнер: " + JsonProcs.getStringFromJSON(cell, "containerCell")
                                    + ", " + JsonProcs.getStringFromJSON(cell, "product")
                                    + ", " + JsonProcs.getIntegerFromJSON(cell, "quantity")
                                    + " (" + JsonProcs.getIntegerFromJSON(cell, "placeQuantity") + ")");

                            tvContainer.setText("");
                            tvContainerContent.setText("");

                            if(saveToHistory){

                                history.SetLastRecordComment("Найдена ячейка");
                            }


                        }
                        else if (type.equals("Номенклатура")) {

                            if (saveToHistory){

                                history.SetLastRecordComment("Найдена номенклатура");
                            }

                            if (!cellRef.equals(DB.nil)){

                                askForPlacementProductToCell(strCatName);

                            }

                        }
                        else if (type.equals("Контейнер")) {

                                tvContainer.setText(JsonProcs.getStringFromJSON(cell, "name"));
                            tvContainerContent.setText(JsonProcs.getStringFromJSON(cell, "product")
                                    + ", " + JsonProcs.getIntegerFromJSON(cell, "quantity")
                                    + " (" + JsonProcs.getIntegerFromJSON(cell, "placeQuantity") + ")");

                            containerRef = JsonProcs.getStringFromJSON(cell, "ref");

                            if (saveToHistory){

                                history.SetLastRecordComment("Найден контейнер");
                            }


                            askForPlacement(strCatName);

                        }

                    }
                    else {

                        history.SetLastRecordMode(1);
                        history.SetLastRecordComment("не найдено");

                    }

                });
    }

    private void askForPlacementProductToCell(String productRef) {
//        Bundle args = new Bundle();
//        args.putString("cellRef", cellRef);
//
//        args.putString("productRef", productRef);
//
//        Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), arguments -> {
//
//            JSONObject jsonObject = new JSONObject();
//            JsonProcs.putToJsonObject(jsonObject,"ref", UUID.randomUUID().toString());
//            JsonProcs.putToJsonObject(jsonObject,"cellRef", arguments.getString("cellRef"));
//            JsonProcs.putToJsonObject(jsonObject,"containerRef", arguments.getString("containerRef"));
//            JsonProcs.putToJsonObject(jsonObject,"containerName", arguments.getString("containerName"));
//
//            RequestToServer.executeRequestBodyUW(getContext(), Request.Method.POST, "setErpSkladPlacement", jsonObject,
//                    RequestToServer.TypeOfResponse.JsonObject, response1 -> {
//
//                        if (!JsonProcs.getStringFromJSON(response1, "ref").isEmpty()){
//
//                            history.AddHistoryRecord("Создано размещение контейнера "
//                                    + tvContainer.getText().toString() + " в ячейку "
//                                    + tvCell.getText().toString());
//
//                            tvContainer.setText("");
//
//                            scanCellContainer(tvCell.getText().toString(), false);
//
//                        }
//
//
//
//                    });
//
//
//        },  args,
//                "Разместить номенклатуру " + tvContainer.getText().toString()
//                + " (" + tvContainerContent.getText().toString() +")"
//                + " в ячейку " + cellName + " ?", "Размещение");

    }

    private void askForPlacement(String strCatName) {

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
                    RequestToServer.TypeOfResponse.JsonObject, response1 -> {

                        if (!JsonProcs.getStringFromJSON(response1, "ref").isEmpty()){

                            history.AddHistoryRecord("Создано размещение контейнера "
                                    + tvContainer.getText().toString() + " в ячейку "
                                    + tvCell.getText().toString());

                            tvContainer.setText("");

                            scanCellContainer(tvCell.getText().toString(), false);

                        }



                    });


        },  args, "Разместить контейнер " + tvContainer.getText().toString()
                + " (" + tvContainerContent.getText().toString() +")"
                + " в ячейку " + tvCell.getText().toString() + " ?", "Размещение");

    }


    @Override
    public void onResume() {
        super.onResume();

        RegisterReceiver(getActivity());

    }

    @Override
    public void onPause() {
        super.onPause();

        UnRegisterReceiver(getActivity());

    }
}