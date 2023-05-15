package com.example.apexwh.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apexwh.R;
import com.example.apexwh.objects.Document;
import com.example.apexwh.objects.DocumentLine;
import com.example.apexwh.ui.products.ProductsFragment;

import java.util.ArrayList;

public class DocumentLineAdapter extends RecyclerView.Adapter<DocumentLineAdapter.DocumentLineItemViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<DocumentLine> documentLines;
    private OnDocumentLineItemClickListener onDocumentLineItemClickListener;
    private OnDocumentLineItemLongClickListener onDocumentLineItemLongClickListener;



    public interface onBindViewHolderI  {

        void OnBindViewHolder(DocumentLineItemViewHolder holder, int position, ArrayList<DocumentLine> documentLines);

    }

    private onBindViewHolderI onBindViewHolderI;

    public void setonBindViewHolderI(onBindViewHolderI onBindViewHolderI){

        this.onBindViewHolderI = onBindViewHolderI;

    }


    public class DocumentLineItemViewHolder extends RecyclerView.ViewHolder {

        public TextView tvArtikul;
        public TextView tvProduct;
        public TextView tvShtrihCodes;
        public TextView tvScanned;

        public LinearLayout llMain;

        public DocumentLineItemViewHolder(View itemView) {
            super(itemView);

            tvArtikul = (TextView) itemView.findViewById(R.id.tvArtikul);
            tvProduct = (TextView) itemView.findViewById(R.id.tvProduct);
            tvShtrihCodes = (TextView) itemView.findViewById(R.id.tvShtrihCodes);
            tvScanned = (TextView) itemView.findViewById(R.id.tvScanned);
            llMain = (LinearLayout) itemView.findViewById(R.id.llMain);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DocumentLine documentLine = documentLines.get(getLayoutPosition());
                    onDocumentLineItemClickListener.onDocumentLineItemClick(documentLine);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    DocumentLine documentLine = documentLines.get(getLayoutPosition());
                    onDocumentLineItemLongClickListener.onDocumentLineItemLongClick(documentLine);
                    return false;
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

    public interface OnDocumentLineItemLongClickListener {
        void onDocumentLineItemLongClick(DocumentLine documentLine);
    }
    public void setOnDocumentLineItemClickListener(OnDocumentLineItemClickListener onDocumentLineItemClickListener) {
        this.onDocumentLineItemClickListener = onDocumentLineItemClickListener;
    }

    public void setOnDocumentLineItemLongClickListener(OnDocumentLineItemLongClickListener onDocumentLineItemLongClickListener) {
        this.onDocumentLineItemLongClickListener = onDocumentLineItemLongClickListener;
    }

    @Override
    public DocumentLineItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.document_line_list_item, parent, false);

        return new DocumentLineItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentLineItemViewHolder holder, int position) {

        if (onBindViewHolderI != null){

            onBindViewHolderI.OnBindViewHolder(holder, position, documentLines);

        }
        else {
            DocumentLine documentLine = documentLines.get(position);

            holder.tvProduct.setText(documentLine.productName
                    + (documentLine.characterName.isEmpty() || documentLine.characterName.equals("Основная характеристика") ? "" : ", " + documentLine.characterName));

            String allSK = "";

            for (String curSK : documentLine.shtrihCodes) {

                allSK = allSK + (allSK.isEmpty() ? "" : ", ") + curSK;

            }

            holder.tvShtrihCodes.setText(allSK);

            holder.tvScanned.setText(documentLine.scanned.toString() + " из " + documentLine.quantity.toString());
        }
    }

    @Override
    public int getItemCount() {
        return documentLines.size();
    }

}

