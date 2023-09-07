package com.example.apexwh.ui.filter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.example.apexwh.JsonProcs;
import com.example.apexwh.R;
import com.example.apexwh.RequestToServer;
import com.example.apexwh.objects.Cell;
import com.example.apexwh.objects.ProductCellContainerOutcome;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "name";
    private static final String ARG_PARAM2 = "ref";

    // TODO: Rename and change types of parameters
    private String name;
    private String ref;

    private ProgressBar progressBar;

    private ArrayList<String> lines, sections, racks, levels, positions;
    private Spinner spLines, spSections, spRacks, spLevels, spPositions;

    private ArrayList<Cell> cells;

    private Button btnApply;
    private TextView tvCellNumber;

    public FilterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FilterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FilterFragment newInstance(String param1, String param2) {
        FilterFragment fragment = new FilterFragment();
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
            name = getArguments().getString(ARG_PARAM1);
            ref = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate;

        inflate = inflater.inflate(R.layout.fragment_filter, container, false);

        spSections = inflate.findViewById(R.id.spSections);
        spLines = inflate.findViewById(R.id.spLines);
        spRacks = inflate.findViewById(R.id.spRacks);
        spLevels = inflate.findViewById(R.id.spLevels);
        spPositions = inflate.findViewById(R.id.spPositions);

        tvCellNumber = inflate.findViewById(R.id.tvCellNumber);
        btnApply = inflate.findViewById(R.id.btnApply);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item = (String)parent.getItemAtPosition(position);

                String curSection = spSections.getItemAtPosition(spSections.getSelectedItemPosition()).toString();
                String curLine = spLines.getItemAtPosition(spLines.getSelectedItemPosition()).toString();
                String curRack = spRacks.getItemAtPosition(spRacks.getSelectedItemPosition()).toString();
                String curLevel = spLevels.getItemAtPosition(spLevels.getSelectedItemPosition()).toString();
                String curPosition = spPositions.getItemAtPosition(spPositions.getSelectedItemPosition()).toString();

                int cellCount = 0;
                for (Cell cell: cells) {

                    if (
                            (cell.section.equals(curSection) || curSection.isEmpty())
                                    && (cell.line.equals(curLine) || curLine.isEmpty())
                                    && (cell.rack.equals(curRack) || curRack.isEmpty())
                                    && (cell.level.equals(curLevel) || curLevel.isEmpty())
                                    && (cell.position.equals(curPosition) || curPosition.isEmpty())
                    ){
                        cellCount++;
                    }


                }

                tvCellNumber.setText("Количество: " + String.valueOf(cellCount));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spSections.setOnItemSelectedListener(itemSelectedListener);
        spLines.setOnItemSelectedListener(itemSelectedListener);
        spRacks.setOnItemSelectedListener(itemSelectedListener);
        spLevels.setOnItemSelectedListener(itemSelectedListener);
        spPositions.setOnItemSelectedListener(itemSelectedListener);

        cells = new ArrayList<>();

        lines = new ArrayList<>();
        lines.add("");
        sections = new ArrayList<>();
        sections.add("");
        racks = new ArrayList<>();
        racks.add("");
        levels = new ArrayList<>();
        levels.add("");
        positions = new ArrayList<>();
        positions.add("");



        progressBar = inflate.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);

        RequestToServer.executeRequestUW(getContext(), Request.Method.GET, "getErpSkladProductsToOutcome",
                "name=" + name + "&ref=" + ref, new JSONObject(), 1,
                new RequestToServer.ResponseResultInterface() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progressBar.setVisibility(View.GONE);

                        JSONArray responseItems = JsonProcs.getJsonArrayFromJsonObject(response, "ErpSkladProductsToOutcome");

                        for (int j = 0; j < responseItems.length(); j++) {

                            JSONObject objectItem = JsonProcs.getItemJSONArray(responseItems, j);

                            ProductCellContainerOutcome productCellContainerOutcome = ProductCellContainerOutcome.FromJson(objectItem);

                            Cell cell = productCellContainerOutcome.cell;
                            cells.add(cell);


                            if (lines.indexOf(cell.line) < 0) {

                                lines.add(cell.line);
                            }

                            if (sections.indexOf(cell.section) < 0) {

                                sections.add(cell.section);
                            }

                            if (racks.indexOf(cell.rack) < 0) {

                                racks.add(cell.rack);
                            }

                            if (levels.indexOf(cell.level) < 0) {

                                levels.add(cell.level);
                            }

                            if (positions.indexOf(cell.position) < 0) {

                                positions.add(cell.position);
                            }

                        }


                        setAdapterToSinner(spLines, lines);
                        setAdapterToSinner(spSections, sections);
                        setAdapterToSinner(spRacks, racks);
                        setAdapterToSinner(spLevels, levels);
                        setAdapterToSinner(spPositions, positions);


//                        adapter.notifyItemRemoved(removed);
//
//                        adapter.notifyItemInserted(items.size());
//
//                        if (items.size() == 0){
//
//                            Dialogs.showQuestionYesNoCancel(getContext(), getActivity(), new BundleMethodInterface() {
//                                @Override
//                                public void callMethod(Bundle arguments) {
//
//                                    setDocumentStatus("toTest");
//
//                                }
//                            }, new Bundle(), "Завершить документ?", "Завершить");
//                        } else {
//
//
//                            items.sort(new Comparator() {
//                                @Override
//                                public int compare(Object o, Object t1) {
//
//                                    // Ладожский  ZB-ZA-ZE-ZH-ZK-ZC-ZD-ZF-ZI-ZL
//
//                                    ProductCellContainerOutcome pcco1 = ((ProductCellContainerOutcome) o);
//                                    ProductCellContainerOutcome pcco2 = ((ProductCellContainerOutcome) t1);
//
//                                    String l1 = pcco1.cell.level;
//                                    String l2 = pcco2.cell.level;
//                                    String cl1 = pcco1.cell.order + (pcco1.cell.section + " ").substring(0, 1) + (l1.equals("P") ? "0" : "") + l1 + pcco1.cell.name;
//                                    String cl2 = pcco2.cell.order + (pcco2.cell.section + " ").substring(0, 1) + (l2.equals("P") ? "0" : "") + l2 + pcco2.cell.name;
//
//                                    return cl1.compareTo(cl2);
//                                }
//                            });
//
//                            adapter.notifyDataSetChanged();
//
//                            if (shtrih != null && !shtrih.isEmpty()) {
//
//                                searchShtrih(items, shtrih);
//
//                            }
//                        }




                    }
                });





//        getParentFragmentManager().setFragmentResult("setFilter", bundle);
//
//        NavHostFragment.findNavController(ProductListFragment.this).popBackStack();



        return inflate;
    }

    private void setAdapterToSinner(Spinner spinner, ArrayList<String> arrayList) {

        if (arrayList.size() == 2){
            arrayList.remove(0);
        }

        arrayList.sort(new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {

                return s.compareTo(t1);
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, arrayList);

        adapter.setDropDownViewResource(R.layout.filter_spinner_drop_down_item);

        spinner.setAdapter(adapter);
    }
}