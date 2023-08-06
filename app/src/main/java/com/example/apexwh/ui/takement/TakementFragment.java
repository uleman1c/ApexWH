package com.example.apexwh.ui.takement;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.example.apexwh.DB;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.RequestToServer;
import com.example.apexwh.objects.History;
import com.example.apexwh.ui.Dialogs;
import com.example.apexwh.ui.ScanShtrihcodeFragment;
import com.example.apexwh.ui.adapters.ScanCodeSetter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;

public class TakementFragment extends ScanShtrihcodeFragment {


    public TakementFragment() {
        super(R.layout.fragment_scan_cell_container_take);
    }

    private TextView tvCell, tvContent, tvContainer, tvContainerCell;

    private String cellRef, containerRef;

    private History history;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cellRef = DB.nil;
        containerRef = DB.nil;

        history = new History(getContext(), "takement");

        setOnCreateViewElements(new OnCreateViewElements() {
            @Override
            public void execute(View root) {

                tvCell = root.findViewById(R.id.tvCell);
                tvContainerCell = root.findViewById(R.id.tvContainerCell);
                tvContent = root.findViewById(R.id.tvContent);
                tvContainer = root.findViewById(R.id.tvContainer);
                root.findViewById(R.id.llContainer).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!containerRef.isEmpty()) {

                            askFortakement(containerRef);
                        }

                    }
                });

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


        RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladCellContainer", "filter=" + strCatName, new JSONObject(),
                RequestToServer.TypeOfResponse.JsonObjectWithArray, response -> {

            JSONArray cells = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladCellContainer");

            if (cells.length() == 1){

                JSONObject cell = JsonProcs.getItemJSONArray(cells, 0);

                if (JsonProcs.getStringFromJSON(cell, "type").equals("Ячейка")){

                    tvCell.setText(strCatName);

                    cellRef = JsonProcs.getStringFromJSON(cell, "ref");
                    tvCell.setText(JsonProcs.getStringFromJSON(cell, "name"));

                    containerRef = JsonProcs.getStringFromJSON(cell, "containerCellRef");

                    tvContainerCell.setText("Контейнер: " + JsonProcs.getStringFromJSON(cell, "containerCell"));

                    tvContent.setText(JsonProcs.getStringFromJSON(cell, "product")
                            + ", " + JsonProcs.getIntegerFromJSON(cell, "quantity")
                            + " (" + JsonProcs.getIntegerFromJSON(cell, "placeQuantity") + ")");

                    tvContainer.setText("");

                    if(saveToHistory){

                        history.SetLastRecordComment("Найдена ячейка");
                    }


                }
                else {

                    tvContainer.setText(JsonProcs.getStringFromJSON(cell, "name"));

                    containerRef = JsonProcs.getStringFromJSON(cell, "ref");

                    if (saveToHistory){

                        history.SetLastRecordComment("Найден контейнер");
                    }


                    askFortakement(strCatName);

                }

            }
            else {

                history.SetLastRecordMode(1);
                history.SetLastRecordComment("не найдено");

            }

        });
    }

    private void askFortakement(String strCatName) {

        RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladContainerCell",
                "container=" + strCatName + "&cell=" + cellRef, new JSONObject(),
                RequestToServer.TypeOfResponse.JsonObjectWithArray, response -> {

                    JSONArray containers = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladContainerCell");

                    if (containers.length() > 0){

                        JSONObject container = JsonProcs.getItemJSONArray(containers, 0);

                        if (cellRef.equals(JsonProcs.getStringFromJSON(container, "cell"))) {

                            containerRef = JsonProcs.getStringFromJSON(container, "ref");
                            tvContainer.setText(JsonProcs.getStringFromJSON(container, "name")
                                + ", остаток: " + JsonProcs.getIntegerFromJSON(container, "number"));

                            Bundle args = new Bundle();
                            args.putString("cellRef", cellRef);
                            args.putString("containerRef", containerRef);
                            args.putString("containerName", tvContainer.getText().toString());

                            Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), arguments -> {

                                JSONObject jsonObject = new JSONObject();
                                JsonProcs.putToJsonObject(jsonObject, "ref", UUID.randomUUID().toString());
                                JsonProcs.putToJsonObject(jsonObject, "cellRef", arguments.getString("cellRef"));
                                JsonProcs.putToJsonObject(jsonObject, "containerRef", arguments.getString("containerRef"));
                                JsonProcs.putToJsonObject(jsonObject, "containerName", arguments.getString("containerName"));

                                RequestToServer.executeRequestBodyUW(getContext(), Request.Method.POST, "setErpSkladTakement", jsonObject,
                                        RequestToServer.TypeOfResponse.JsonObject, response1 -> {

                                            if (!JsonProcs.getStringFromJSON(response1, "ref").isEmpty()) {

                                                history.AddHistoryRecord("Создано взятие контейнера "
                                                 + tvContainer.getText().toString() + " из ячейки "
                                                        + tvCell.getText().toString());

                                                tvContainer.setText("");

                                                scanCellContainer(tvCell.getText().toString(), false);

                                            }


                                        });


                            }, args, "Взять контейнер " + tvContainer.getText().toString() + " из ячейки " + tvCell.getText().toString() + " ?", "Взятие");

                        }
                        else {
                            history.SetLastRecordMode(1);
                            history.AddLastRecordComment("не найден контейнер в ячейке");
                        }

                    }

                });
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