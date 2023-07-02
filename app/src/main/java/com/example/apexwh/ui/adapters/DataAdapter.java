package com.example.apexwh.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DataAdapter<T> extends RecyclerView.Adapter<DataAdapter<T>.ItemViewHolder> {

    private LayoutInflater inflater;
    private int layout;
    private ArrayList<T> items;
    private InitViewsMaker initViewsMaker;
    private DrawViewHolder drawViewHolder;
    private OnClickListener onClickListener;
    private OnLongClickListener onLongClickListener;

    public void setOnGetItemViewType(OnGetItemViewType onGetItemViewType) {
        this.onGetItemViewType = onGetItemViewType;
    }

    private OnGetItemViewType onGetItemViewType;

    public void setBeforeEndOnCreateViewHolder(BeforeEndOnCreateViewHolder beforeEndOnCreateViewHolder) {
        this.beforeEndOnCreateViewHolder = beforeEndOnCreateViewHolder;
    }

    private BeforeEndOnCreateViewHolder beforeEndOnCreateViewHolder;

    public class ItemViewHolder extends RecyclerView.ViewHolder {

         public final int getItemViewType(int position){

             return position;
         }

        public ArrayList<TextView> getTextViews() {
            return textViews;
        }

        private ArrayList<TextView> textViews;

        public ItemViewHolder(View itemView) {
            super(itemView);

            textViews = new ArrayList<>();

            initViewsMaker.init(itemView, textViews);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    T document = items.get(getLayoutPosition());
                    onClickListener.onItemClick(document);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    T document = items.get(getLayoutPosition());
                    onLongClickListener.onLongItemClick(document);
                    return true;
                }
            });
        }



    }

    public DataAdapter(Context context, ArrayList<T> items, int layout) {
        this.items = items;
        this.inflater = LayoutInflater.from(context);
        this.layout = layout;
    }

    public interface InitViewsMaker {

        void init(View itemView, ArrayList<TextView> textViews);
    }

    public void setInitViewsMaker(InitViewsMaker initViewsMaker) {
        this.initViewsMaker = initViewsMaker;
    }

    public interface DrawViewHolder<T> {

        void draw(DataAdapter<T>.ItemViewHolder holder, T document);
    }

    public void setDrawViewHolder(DrawViewHolder drawViewHolder) {
        this.drawViewHolder = drawViewHolder;
    }

    public interface OnClickListener<T> {
        void onItemClick(T document);
    }

    public interface OnLongClickListener<T> {
        void onLongItemClick(T document);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(layout, parent, false);

        if (beforeEndOnCreateViewHolder != null){

            view = beforeEndOnCreateViewHolder.Do(inflater, parent, viewType);

        }

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        this.drawViewHolder.draw(holder, items.get(position));

    }

    @Override
    public int getItemViewType(int position) {

        return onGetItemViewType == null ? 0 : onGetItemViewType.Do(position);

    }


    @Override
    public int getItemCount() {
        return items.size();
    }

}

