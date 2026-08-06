package com.indiandesigns.fabcut.ui.base;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.indiandesigns.fabcut.utils.AppLogger;
import com.indiandesigns.fabcut.utils.CommonUtils;
import com.indiandesigns.fabcut.di.component.ActivityComponent;

import butterknife.Unbinder;

/**
 * Extended by all dialogs
 * Provides common functionality
 */

public abstract class BaseDialog extends DialogFragment implements DialogMvpView {

    private BaseActivity mActivity;
    private Unbinder mUnBinder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            BaseActivity mActivity = (BaseActivity) context;
            this.mActivity = mActivity;
            mActivity.onFragmentAttached();
        }
    }

    /**
     * Shows the progress dialog from {@link CommonUtils}
     */

    @Override
    public void showLoading() {
        if (mActivity != null) {
            mActivity.showLoading();
        }
    }

    /**
     * Shows the progress dialog with text from {@link CommonUtils}
     */

    @Override
    public void showLoading(int textId) {
        if (mActivity != null) {
            mActivity.showLoading(textId);
        }
    }

    /**
     * Shows the alert dialog from {@link CommonUtils} with custom message
     * and dialog click listener
     */

    @Override
    public void showAlertDialog(String message, DialogInterface.OnClickListener listener) {
        if (mActivity != null) {
            mActivity.showAlertDialog(message, listener);
        }
    }

    /**
     * Shows the alert dialog from {@link CommonUtils} with custom message,
     * and dismiss button
     */

    @Override
    public void showAlertDialog(int message) {
        if (mActivity != null) {
            mActivity.showAlertDialog(message);
        }
    }

    /**
     * Shows the alert dialog from {@link CommonUtils} with custom message,
     * and dismiss button
     */

    @Override
    public void showConfirmationDialog(int message) {
        if (mActivity != null) {
            mActivity.showConfirmationDialog(message);
        }
    }

    /**
     * Shows the alert dialog from {@link CommonUtils} with custom message
     * custom positive & negative button text
     * and dialog click listener
     */

    @Override
    public void showAlertDialog(int positiveText, int negativeText, String message, DialogInterface.OnClickListener listener) {
        if (mActivity != null) {
            mActivity.showAlertDialog(positiveText, negativeText, message, listener);
        }
    }

    /**
     * Shows the alert dialog from {@link CommonUtils} with custom message
     * and dialog click listener
     */

    @Override
    public void showAlertDialog(int message, DialogInterface.OnClickListener listener) {
        if (mActivity != null) {
            mActivity.showAlertDialog(message, listener);
        }
    }

    /**
     * Hides the progress dialog from {@link CommonUtils}
     */

    @Override
    public void hideLoading() {
        if (mActivity != null) {
            mActivity.hideLoading();
        }
    }

    /**
     * Shows a Toast with default error if input message is null
     * Else shows a Toast with the message provided
     *
     * @param message Text to be shown on Toast
     */

    @Override
    public void onError(String message) {
        if (mActivity != null) {
            mActivity.onError(message);
        }
    }

    /**
     * Gets the string from the resource id and shows the SnackBar
     *
     * @param resId Resource id of the string
     */

    @Override
    public void onError(@StringRes int resId) {
        if (mActivity != null) {
            mActivity.onError(resId);
        }
    }

    /**
     * Shows a Toast with default error if input message is null
     * Else shows a Toast with the message provided
     *
     * @param message Text to be shown on Toast
     */

    @Override
    public void showMessage(String message) {
        if (mActivity != null) {
            mActivity.showMessage(message);
        }
    }

    /**
     * Gets the string from the resource id and shows the Toast
     *
     * @param resId Resource id of the string
     */

    @Override
    public void showMessage(@StringRes int resId) {
        if (mActivity != null) {
            mActivity.showMessage(resId);
        }
    }

    /**
     * Checks for the network connectivity
     *
     * @return The status of the network connectivity
     */

    @Override
    public boolean isNetworkConnected() {
        if (mActivity != null) {
            return mActivity.isNetworkConnected();
        }
        return false;
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    @Override
    public void hideKeyboard() {
        if (mActivity != null) {
            mActivity.hideKeyboard();
        }
    }

    public BaseActivity getBaseActivity() {
        return mActivity;
    }

    public ActivityComponent getActivityComponent() {
        if (mActivity != null) {
            return mActivity.getActivityComponent();
        }
        return null;
    }

    public void setUnBinder(Unbinder unBinder) {
        mUnBinder = unBinder;
    }

    protected abstract void setUp(View view);

    /**
     * Creates and returns a new Dialog
     *
     * @return Dialog
     */

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUp(view);
    }

    /**
     * To show the fragment by TAG
     *
     * @param tag Custom tag
     */

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            AppLogger.e("Dialog exception", e.toString());
        }
    }

    /**
     * Dismisses the dialog with a custom tag
     *
     * @param tag Custom tag
     */

    @Override
    public void dismissDialog(String tag) {
        dismiss();
        getBaseActivity().onFragmentDetached(tag);
    }

    @Override
    public void onDestroy() {
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
        super.onDestroy();
    }
}