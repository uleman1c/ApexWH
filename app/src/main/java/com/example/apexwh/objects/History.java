package com.example.apexwh.objects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.apexwh.DB;
import com.example.apexwh.DateStr;
import com.example.apexwh.R;
import com.example.apexwh.ui.adapters.BeforeEndOnCreateViewHolder;
import com.example.apexwh.ui.adapters.DataAdapter;
import com.example.apexwh.ui.adapters.OnGetItemViewType;

import java.util.ArrayList;

public class History {
    public DataAdapter<HistoryRecord> getAdapter() {
        return adapter;
    }

    public String getType() {
        return type;
    }

    public ArrayList<HistoryRecord> getItems() {
        return items;
    }

    private final DataAdapter<HistoryRecord> adapter;

    private String type, userId;

    private ArrayList<HistoryRecord> items;

    public History(Context context, String type) {

        this.type = type;

        DB db = new DB(context);
        db.open();

        this.userId = db.getConstant("userId");

        db.close();

        items = new ArrayList<>();

        adapter = new DataAdapter<HistoryRecord>(context, items, R.layout.history_record_list_item);

        getAdapter().setOnGetItemViewType(new OnGetItemViewType() {
            @Override
            public int Do(int position) {
                return ((HistoryRecord)items.get(position)).mode;
            }
        });

        getAdapter().setBeforeEndOnCreateViewHolder(new BeforeEndOnCreateViewHolder() {
            @Override
            public View Do(LayoutInflater inflater, ViewGroup parent, int viewType) {

                View view = null;

                if (viewType == 0) {

                    view = inflater.inflate(R.layout.history_record_list_item, parent, false);
                }
                else if (viewType == 1) {

                    view = inflater.inflate(R.layout.history_record_red_list_item, parent, false);
                }

                return view;

            }
        });


        getAdapter().setInitViewsMaker(new DataAdapter.InitViewsMaker() {
            @Override
            public void init(View itemView, ArrayList<TextView> textViews) {

                textViews.add(itemView.findViewById(R.id.tvDate));
                textViews.add(itemView.findViewById(R.id.tvData));
                textViews.add(itemView.findViewById(R.id.tvComment));
            }
        });

        getAdapter().setDrawViewHolder(new DataAdapter.DrawViewHolder<HistoryRecord>() {
            @Override
            public void draw(DataAdapter.ItemViewHolder holder, HistoryRecord document) {

                ((TextView) holder.getTextViews().get(0)).setText(DateStr.FromYmdhmsToDmyhms(document.date));
                ((TextView) holder.getTextViews().get(1)).setText(document.data);
                ((TextView) holder.getTextViews().get(2)).setText(document.comment);
            }
        });

        getAdapter().setOnClickListener(document -> {});

        getAdapter().setOnLongClickListener(document -> {});



    }

    public void AddHistoryRecord(String data){

        items.add(0, new HistoryRecord(DateStr.NowYmdhms(), getType(), userId, data));

        getAdapter().notifyDataSetChanged();

    }

    public void SetLastRecordMode(int mode){

        items.get(0).mode = mode;

        getAdapter().notifyDataSetChanged();

    }

    public void SetLastRecordComment(String comment){

        items.get(0).comment = comment;

        getAdapter().notifyDataSetChanged();

    }

}
