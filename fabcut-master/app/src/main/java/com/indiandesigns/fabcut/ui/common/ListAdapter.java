package com.indiandesigns.fabcut.ui.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.indiandesigns.fabcut.R;
import com.indiandesigns.fabcut.ui.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter for showing the list of String items
 * Extends {@link BaseViewHolder}
 */

public class ListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    public static final int VIEW_TYPE_NORMAL = 1;
    private ArrayList<String> mList;

    public ListAdapter(ArrayList<String> list) {
        mList = list;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void addItems(List<String> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void addItem(String item) {
        mList.add(item);
        notifyDataSetChanged();
    }

    public void addUniqueItem(String item) {
        if(mList.contains(item)){
            return;
        }
        mList.add(item);
        notifyDataSetChanged();
    }

    public List<String> getList(){
        return mList;
    }

    public boolean isUniqueItem(String item){
        return !mList.contains(item);
    }

    public interface Callback {
        void onEmptyViewRetryClick();
    }

    public class ViewHolder extends BaseViewHolder {

        @BindView(R.id.item_text_view)
        TextView itemTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        protected void clear() {
            itemTextView.setText("");
        }

        public void onBind(int position) {
            super.onBind(position);

            final String code = mList.get(position);

            if (code != null) {
                itemTextView.setText(code);
            }
        }
    }
}
