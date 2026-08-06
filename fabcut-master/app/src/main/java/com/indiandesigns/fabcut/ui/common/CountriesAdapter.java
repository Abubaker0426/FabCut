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
 * Adapter for showing the list of countries in spinner
 * Extends {@link BaseViewHolder}
 */
public class CountriesAdapter extends ArrayAdapter<String> {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final List<String> items;
    private final int mResource;

    public CountriesAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects) {
        super(context, resource, 0, objects);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        objects.add(0, "");
        items = objects;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
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

        TextView country = (TextView) view.findViewById(R.id.selected_country);

        String item = items.get(position);
        country.setText(item);

        return view;
    }
}
