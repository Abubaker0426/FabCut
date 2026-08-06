package com.indiandesigns.fabcut.ui.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;

import com.indiandesigns.fabcut.R;

import javax.inject.Inject;

public class OverFlowMenu {

    private Toolbar mToolbar;
    private Context context;
    private Callback mCallback;
    @Inject
    public OverFlowMenu() {

    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }


    public void setOverFlowMenuVariables(Toolbar toolbar, Context context) {
        this.mToolbar = toolbar;
        this.context = context;
    }

    public interface Callback {
        void launchBundlePartsActivity();
    }
    /**
     * This method is to display the overflow menu on the toolbar.
     * overflow menu has the option to:
     * select parts to print in bundle
     */
    public void setOverflowMenu(){
        mToolbar.inflateMenu(R.menu.overflow_menu);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_bundle_parts:
                        callBundlePartsActivity();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }

    /** This function calls batchResourceActivity on click of resource entry from menu
     * */
    public void callBundlePartsActivity(){
        mCallback.launchBundlePartsActivity();
    }

}
