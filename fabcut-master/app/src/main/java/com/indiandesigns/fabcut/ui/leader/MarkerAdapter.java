package com.indiandesigns.fabcut.ui.leader;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.indiandesigns.fabcut.R;
import com.indiandesigns.fabcut.data.network.enums.Status;
import com.indiandesigns.fabcut.data.network.model.Follower;
import com.indiandesigns.fabcut.data.network.model.Item;
import com.indiandesigns.fabcut.ui.base.BaseViewHolder;
import com.indiandesigns.fabcut.ui.common.model.MarkerItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter for showing the list of followers
 * Extends {@link BaseViewHolder}
 */

public class MarkerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<MarkerItem> markerItemList;

    public MarkerAdapter(List<MarkerItem> markerItems) {
        this.markerItemList = markerItems;
    }

    private Callback mCallback;
    public void setCallback(Callback callback) {
        mCallback = callback;
    }


    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.marker_item, parent, false));
    }

    @Override
    public int getItemCount() {
        return markerItemList.size();
    }

    public void addItems(List<MarkerItem> markerItems) {
        markerItemList.clear();
        markerItemList.addAll(markerItems);
        notifyDataSetChanged();
    }

    public interface Callback {
        void onItemClickListener(MarkerItem markerItem);
    }

    public class ViewHolder extends BaseViewHolder {

        @BindView(R.id.oc_number)
        TextView ocNumberTextView;

        @BindView(R.id.item_code)
        TextView itemCodeTextView;

        @BindView(R.id.item_description)
        TextView itemDescriptionTextView;

        @BindView(R.id.shrinkage)
        TextView shrinkageTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        protected void clear() {
            ocNumberTextView.setText("");
            itemCodeTextView.setText("");
            itemDescriptionTextView.setText("");
            shrinkageTextView.setText("");
        }

        public void onBind(int position) {
            super.onBind(position);

            MarkerItem markerItem = markerItemList.get(position);
            Context context = itemView.getContext();

            ocNumberTextView.setText(markerItem.getOcNumber());
            List<String> itemCodes = new ArrayList<>();
            List<String> itemDescriptions = new ArrayList<>();
            for (Item item:
                 markerItem.getItems()) {
                itemCodes.add(item.getItemCode());
                itemDescriptions.add(item.getItemDesc());
            }
            itemCodeTextView.setText(TextUtils.join("/", itemCodes));
            itemDescriptionTextView.setText(TextUtils.join("/", itemDescriptions));
            shrinkageTextView.setText(markerItem.getShrinkage());

            itemView.setOnClickListener(view -> {
                mCallback.onItemClickListener(markerItem);
            });

        }
    }

}
