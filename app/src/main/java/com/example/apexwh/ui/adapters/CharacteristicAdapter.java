package com.example.apexwh.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apexwh.R;
import com.example.apexwh.objects.Characteristic;
import com.example.apexwh.objects.Document;

import java.util.ArrayList;

public class CharacteristicAdapter extends RecyclerView.Adapter<CharacteristicAdapter.ItemViewHolder> {


    private LayoutInflater inflater;
    private ArrayList<Characteristic> items;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;


    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDescription;

        public ItemViewHolder(View itemView) {
            super(itemView);

            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Characteristic document = items.get(getLayoutPosition());
                    onItemClickListener.onItemClick(document);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Characteristic document = items.get(getLayoutPosition());
                    onItemLongClickListener.onItemLongClick(document);
                    return true;
                }
            });
        }

    }

    public CharacteristicAdapter(Context context, ArrayList<Characteristic> items) {
        this.items = items;
        this.inflater = LayoutInflater.from(context);
    }

    public interface OnItemClickListener {
        void onItemClick(Characteristic document);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Characteristic document);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.characteristic_list_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Characteristic document = items.get(position);

        holder.tvDescription.setText(document.description);
//
//        if (document.status.isEmpty()){
//
//            holder.tvStatus.setText("Новый");
//            holder.tvStatus.setBackgroundColor(Color.parseColor("#ffffff"));
//
//
//        } else if (document.status.equals("closed")){
//
//            holder.tvStatus.setText("Закрыт");
//            holder.tvStatus.setBackgroundColor(Color.parseColor("#00ff00"));
//        }


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}

