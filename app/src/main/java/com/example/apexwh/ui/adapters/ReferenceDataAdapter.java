package com.example.apexwh.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apexwh.R;
import com.example.apexwh.objects.Reference;

import java.util.ArrayList;
import java.util.List;

public class ReferenceDataAdapter extends RecyclerView.Adapter<ReferenceDataAdapter.ReferenceItemViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<Reference> references;
    private OnReferenceItemClickListener onReferenceItemClickListener;

    class ReferenceItemViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDescription;

        public ReferenceItemViewHolder(View itemView) {
            super(itemView);

            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Reference reference = references.get(getLayoutPosition());
                    onReferenceItemClickListener.onReferenceItemClick(reference);
                }
            });
        }

    }

    public ReferenceDataAdapter(Context context, ArrayList<Reference> references) {
        this.references = references;
        this.inflater = LayoutInflater.from(context);
    }

    public interface OnReferenceItemClickListener {
        void onReferenceItemClick(Reference reference);
    }

    public void setOnReferenceItemClickListener(OnReferenceItemClickListener onReferenceItemClickListener) {
        this.onReferenceItemClickListener = onReferenceItemClickListener;
    }

    @Override
    public ReferenceItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.reference_list_item, parent, false);
        return new ReferenceItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReferenceItemViewHolder holder, int position) {
        Reference reference = references.get(position);

        holder.tvDescription.setText(reference.description);

    }

    @Override
    public int getItemCount() {
        return references.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvDescription;
        ViewHolder(View view){
            super(view);
            tvDescription = (TextView) view.findViewById(R.id.tvDescription);
        }
    }
}

