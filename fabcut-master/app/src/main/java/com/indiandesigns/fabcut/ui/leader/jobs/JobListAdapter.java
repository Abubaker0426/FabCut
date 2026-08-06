package com.indiandesigns.fabcut.ui.leader.jobs;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.indiandesigns.fabcut.R;
import com.indiandesigns.fabcut.data.network.enums.ListType;
import com.indiandesigns.fabcut.ui.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter for showing the list of jobs
 * Extends {@link BaseViewHolder}
 */

public class JobListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Map<Integer, List<List<String>>> ratioList;

    public static final int VIEW_TYPE_ROW = 0;
    public static final int VIEW_TYPE_HEADER = 1;

    public JobListAdapter(Map<Integer, List<List<String>>> ratioList) {
        this.ratioList = ratioList;
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
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.job_header_item, parent, false));
            case VIEW_TYPE_ROW:
            default:
                return new RowViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.job_row_item, parent, false));
        }
    }

    @Override
    public int getItemCount() {
        return ratioList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        } else {
            return VIEW_TYPE_ROW;
        }
    }

    public void addItems(Map<Integer, List<List<String>>> ratios) {
        ratioList.clear();
        ratioList.putAll(ratios);
        notifyDataSetChanged();
    }

    public interface Callback {
        void onItemClickListener(int ratioNumber, List<List<String>> item);
    }

    public class HeaderViewHolder extends BaseViewHolder {

        @BindView(R.id.size_container)
        LinearLayout sizeContainer;

        @BindView(R.id.size_quantity_container)
        LinearLayout sizeQuantitiesContainer;

        @BindView(R.id.size_completed_quantity_container)
        LinearLayout sizeCompleteQuantityContainer;

        @BindView(R.id.size_available_quantity_container)
        LinearLayout sizeAvailableQuantityContainer;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        protected void clear() {
            sizeContainer.removeAllViews();
            sizeQuantitiesContainer.removeAllViews();
            sizeCompleteQuantityContainer.removeAllViews();
            sizeAvailableQuantityContainer.removeAllViews();
        }

        public void onBind(int position) {
            super.onBind(position);

            if(ratioList.size() == 0){
                return;
            }

            Object key = ratioList.keySet().toArray()[position];
            if(key == null) { return; }
            final List<List<String>> item = ratioList.get(key);

            if(item == null || item.size() == 0){
                return;
            }
            Context context = itemView.getContext();

            addTextViewToLayout(sizeContainer, item.get(ListType.SIZE.getValue()), context, true, R.color.colorBlack);
            addTextViewToLayout(sizeQuantitiesContainer, item.get(ListType.SIZE_QUANTITY.getValue()), context, true, R.color.colorGreenPastel);
            addTextViewToLayout(sizeCompleteQuantityContainer, item.get(ListType.SIZE_COMPLETED_QUANTITY.getValue()), context, true, R.color.colorRedPastel);
            addTextViewToLayout(sizeAvailableQuantityContainer, item.get(ListType.SIZE_AVAILABLE_QUANTITY.getValue()), context, true, R.color.colorBlack);
        }
    }

    public class RowViewHolder extends BaseViewHolder {

        @BindView(R.id.quantity_container)
        LinearLayout quantityContainer;

        @BindView(R.id.ratio_container)
        LinearLayout ratioContainer;

        public RowViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        protected void clear() {
            quantityContainer.removeAllViews();
            ratioContainer.removeAllViews();
        }

        public void onBind(int position) {
            super.onBind(position);

            if(ratioList.size() == 0){
                return;
            }

            Object key = ratioList.keySet().toArray()[position];
            if(key == null) { return; }

            // get the list of three level lists for each ratioNumber(key)
            final List<List<String>> item = ratioList.get(key);

            if(item == null || item.size() == 0){
                return;
            }
            Context context = itemView.getContext();

            addTextViewToLayout(quantityContainer, item.get(ListType.QUANTITY.getValue()), context, false, R.color.colorBlack);
            addTextViewToLayout(ratioContainer, item.get(ListType.RATIO.getValue()), context, false, R.color.colorBlack);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.onItemClickListener((int) key, item);
                }
            });
        }
    }

    /***
     * For each layout, traverse the list of data and add TextView for each data object
     *
     * @param linearLayout Parent layout
     * @param list List of data to be shown for each TextView
     * @param context Context
     * @param bold Decides if the text is bold or not
     */

    private void addTextViewToLayout(LinearLayout linearLayout, List<String> list, Context context, boolean bold, int color){
        for (String text:
             list) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            params.setMargins(0,4,0,4);
            TextView textView = new TextView(context);
            textView.setText(text);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(params);
            if(bold){
                textView.setTypeface(null, Typeface.BOLD);
            }
            textView.setTextColor(context.getResources().getColor(color));
            textView.setSingleLine();
            textView.setTextSize(13f);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            linearLayout.addView(textView);
        }
    }

}
