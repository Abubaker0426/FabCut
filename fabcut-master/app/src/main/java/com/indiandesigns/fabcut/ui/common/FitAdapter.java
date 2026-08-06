package com.indiandesigns.fabcut.ui.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.indiandesigns.fabcut.R;
import com.indiandesigns.fabcut.ui.base.BaseViewHolder;

import java.util.List;

/**
 * Adapter for showing the list of fit types in spinner
 * Extends {@link BaseViewHolder}
 */

public class FitAdapter extends ArrayAdapter<String>{

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final List<String> items;
    private final int mResource;

    public FitAdapter(@NonNull Context context, @LayoutRes int resource,
                      @NonNull List objects) {
        super(context, resource, 0, objects);

        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        items = objects;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    public void updateList(List<String> markerList){
        items.clear();
        items.addAll(markerList);
        notifyDataSetChanged();
    }

    public String getItemData(int position){
        return items.get(position);
    }

    @Override
    public @NonNull View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent){
        final View view = mInflater.inflate(mResource, parent, false);

        TextView marker = (TextView) view.findViewById(R.id.fit_type);

        String item = items.get(position);
        marker.setText(item);

        return view;
    }
}