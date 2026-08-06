package com.indiandesigns.fabcut.ui.leader.jobs.parts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.indiandesigns.fabcut.R;
import com.indiandesigns.fabcut.data.network.model.FetchPartsDetails;

import org.greenrobot.greendao.annotation.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BundlePartsAdapter extends RecyclerView.Adapter<BundlePartsAdapter.ViewHolder>{

    private final List<FetchPartsDetails> partsArrayList = new ArrayList<>();

    private Callback mCallback;
    public void setCallback(Callback callback){ mCallback = callback; }

    public void addBundleParts(List<FetchPartsDetails> bundleParts) {
        this.partsArrayList.clear();
        this.partsArrayList.addAll(bundleParts);
        notifyDataSetChanged();
    }
    public interface Callback {
        void setPartsDetails(Long partUnique,  boolean isChecked);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bundle_parts_row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FetchPartsDetails partsDetails = this.partsArrayList.get(position);
        int isCheckedValue = partsDetails.getIsSelected();
        boolean isSelected = false;
        if (isCheckedValue == 1) isSelected = true;

        holder.getPartsNameTextView().setText(partsDetails.getPart());
        holder.getPartsNameCheckBox().setOnCheckedChangeListener(null);
        holder.getPartsNameCheckBox().setChecked(isSelected);
        holder.getPartsNameCheckBox().setTag(partsDetails.getPartsUnique());
        holder.getPartsNameCheckBox().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Long partUnique = (Long) buttonView.getTag();
                mCallback.setPartsDetails(partUnique, isChecked);
            }
        });
    }


    @Override
    public int getItemCount() {
        return this.partsArrayList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView partsNameTextView;
        private final CheckBox partsNameCheckBox;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            partsNameTextView = itemView.findViewById(R.id.bundle_parts_name);
            partsNameCheckBox = itemView.findViewById(R.id.bundle_parts_checkBox);
        }

        public TextView getPartsNameTextView() { return partsNameTextView; }

        public CheckBox getPartsNameCheckBox() { return partsNameCheckBox; }

    }
}
