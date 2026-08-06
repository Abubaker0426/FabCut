package com.indiandesigns.fabcut.ui.base;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Extended by all ViewHolders
 *
 * Opens a getCurrentPosition() method that
 * returns the current position in list
 */

public abstract class BaseViewHolder extends RecyclerView.ViewHolder {

    private int mCurrentPosition;

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    protected abstract void clear();

    public void onBind(int position) {
        mCurrentPosition = position;
        clear();
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }
}
