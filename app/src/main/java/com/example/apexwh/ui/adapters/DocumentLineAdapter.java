package com.example.apexwh.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apexwh.R;
import com.example.apexwh.objects.Document;
import com.example.apexwh.objects.DocumentLine;

import java.util.ArrayList;

public class DocumentLineAdapter extends RecyclerView.Adapter<DocumentLineAdapter.DocumentLineItemViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<DocumentLine> documentLines;
    private OnDocumentLineItemClickListener onDocumentLineItemClickListener;

    class DocumentLineItemViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNumberDate;
        private TextView tvDescription;

        public DocumentLineItemViewHolder(View itemView) {
            super(itemView);

            tvNumberDate = (TextView) itemView.findViewById(R.id.tvNumberDate);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DocumentLine documentLine = documentLines.get(getLayoutPosition());
                    onDocumentLineItemClickListener.onDocumentLineItemClick(documentLine);
                }
            });
        }

    }

    public DocumentLineAdapter(Context context, ArrayList<DocumentLine> documentLines) {
        this.documentLines = documentLines;
        this.inflater = LayoutInflater.from(context);
    }

    public interface OnDocumentLineItemClickListener {
        void onDocumentLineItemClick(DocumentLine documentLine);
    }

    public void setOnDocumentLineItemClickListener(OnDocumentLineItemClickListener onDocumentLineItemClickListener) {
        this.onDocumentLineItemClickListener = onDocumentLineItemClickListener;
    }

    @Override
    public DocumentLineItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.document_list_item, parent, false);
        return new DocumentLineItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentLineItemViewHolder holder, int position) {
        DocumentLine documentLine = documentLines.get(position);

        holder.tvNumberDate.setText(documentLine.productName);
        holder.tvDescription.setText(documentLine.characterName);

    }

    @Override
    public int getItemCount() {
        return documentLines.size();
    }

}

