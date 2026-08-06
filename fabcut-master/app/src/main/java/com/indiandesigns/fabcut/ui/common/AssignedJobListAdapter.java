package com.indiandesigns.fabcut.ui.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.indiandesigns.fabcut.R;
import com.indiandesigns.fabcut.ui.base.BaseViewHolder;
import com.indiandesigns.fabcut.ui.common.model.OcLay;
import com.indiandesigns.fabcut.ui.common.model.OcLayDetail;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter for showing the list of jobs assigned
 * Extends {@link BaseViewHolder}
 */

public class AssignedJobListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<OcLay> jobList;

    public static final int VIEW_TYPE_ROW = 0;
    public static final int VIEW_TYPE_HEADER = 1;

    public AssignedJobListAdapter(List<OcLay> jobList) {
        this.jobList = jobList;
    }

    private Callback mCallback;
    public void setCallback(Callback callback) {
        mCallback = callback;
    }


    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if(position > 0){
            holder.onBind(position - 1);
        } else {
            holder.onBind(position);
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                return new HeaderViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.job_list_header_item, parent, false));
            case VIEW_TYPE_ROW:
            default:
                return new RowViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.job_list_row_item, parent, false));
        }
    }

    @Override
    public int getItemCount() {
        return jobList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        } else {
            return VIEW_TYPE_ROW;
        }
    }

    public void addItems(List<OcLay> jobs) {
        jobList.clear();
        jobList.addAll(jobs);
        notifyDataSetChanged();
    }

    public interface Callback {
        void bundleSplitDialog(OcLay job);
    }

    public class HeaderViewHolder extends BaseViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        protected void clear() {
        }

        public void onBind(int position) {
            super.onBind(position);
        }
    }

    public class RowViewHolder extends BaseViewHolder {

        @BindView(R.id.oc_number)
        TextView ocNumberTextView;

        @BindView(R.id.lay_number)
        TextView layNumberTextView;

        @BindView(R.id.follower_name)
        TextView followerNameTextView;

        @BindView(R.id.Bundling)
        TextView Bundling;

        @BindView(R.id.job_list_row_itemDescription)
        TextView textViewItemDescription;

        public RowViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        protected void clear() {
            ocNumberTextView.setText("");
            layNumberTextView.setText("");
            followerNameTextView.setText("");
        }

        public void onBind(int position) {
            super.onBind(position);

            OcLay job = jobList.get(position);
            Context context = itemView.getContext();

            String itemDescription = null;
            List<OcLayDetail> SizeList = job.getSizeList();
            if(SizeList.size() > 0 ) {
                OcLayDetail ocLayDetail = SizeList.get(0);
                itemDescription =  ocLayDetail.getItemDesc();
            }

            ocNumberTextView.setText(job.getOcNo());
            layNumberTextView.setText(String.valueOf(job.getLay()));
            followerNameTextView.setText(String.valueOf(job.getTableNum()));
            textViewItemDescription.setText(itemDescription);

            Bundling.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.bundleSplitDialog(job);
                }
            });

        }
    }

}
