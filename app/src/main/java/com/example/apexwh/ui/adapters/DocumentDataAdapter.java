package com.example.apexwh.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apexwh.R;
import com.example.apexwh.objects.Document;
import com.example.apexwh.objects.Reference;

import java.util.ArrayList;

public class DocumentDataAdapter extends RecyclerView.Adapter<DocumentDataAdapter.DocumentItemViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<Document> documents;
    private OnDocumentItemClickListener onDocumentItemClickListener;
    private OnDocumentItemLongClickListener onDocumentItemLongClickListener;

    class DocumentItemViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNumberDate;
        private TextView tvDescription;
        private TextView tvStatus;

        public DocumentItemViewHolder(View itemView) {
            super(itemView);

            tvNumberDate = (TextView) itemView.findViewById(R.id.tvNumberDate);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            tvStatus = (TextView) itemView.findViewById(R.id.tvStatus);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Document document = documents.get(getLayoutPosition());
                    onDocumentItemClickListener.onDocumentItemClick(document);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Document document = documents.get(getLayoutPosition());
                    onDocumentItemLongClickListener.onDocumentLongItemClick(document);
                    return true;
                }
            });
        }

    }

    public DocumentDataAdapter(Context context, ArrayList<Document> documents) {
        this.documents = documents;
        this.inflater = LayoutInflater.from(context);
    }

    public interface OnDocumentItemClickListener {
        void onDocumentItemClick(Document document);
    }

    public interface OnDocumentItemLongClickListener {
        void onDocumentLongItemClick(Document document);
    }

    public void setOnDocumentItemClickListener(OnDocumentItemClickListener onDocumentItemClickListener) {
        this.onDocumentItemClickListener = onDocumentItemClickListener;
    }

    public void setOnDocumentItemLongClickListener(OnDocumentItemLongClickListener onDocumentItemLongClickListener) {
        this.onDocumentItemLongClickListener = onDocumentItemLongClickListener;
    }

    @Override
    public DocumentItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.document_list_item, parent, false);
        return new DocumentItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentItemViewHolder holder, int position) {
        Document document = documents.get(position);

        holder.tvNumberDate.setText(document.nameStr + " № " + document.number + " от " + document.date);
        holder.tvDescription.setText(document.description);

        if (document.status.isEmpty()){

            holder.tvStatus.setText("Новый");
            holder.tvStatus.setBackgroundColor(Color.parseColor("#ffffff"));


        } else if (document.status.equals("closed")){

            holder.tvStatus.setText("Закрыт");
            holder.tvStatus.setBackgroundColor(Color.parseColor("#00ff00"));
        }


    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

}

