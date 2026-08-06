package com.indiandesigns.fabcut.ui.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.indiandesigns.fabcut.di.component.ActivityComponent;
import com.indiandesigns.fabcut.utils.CommonUtils;

import butterknife.Unbinder;

/**
 * Extended by all fragments
 * Provides common functionality
 */

public abstract class BaseFragment extends Fragment implements MvpView {

    private BaseActivity mActivity;
    private Unbinder mUnBinder;
    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUp(view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) context;
            this.mActivity = activity;
            activity.onFragmentAttached();
        }
    }

    /**
     * Shows the progress dialog from {@link CommonUtils}
     */

    @Override
    public void showLoading() {
        hideLoading();
        mProgressDialog = CommonUtils.showLoadingDialog(this.getContext(), false, 0);
    }

    /**
     * Shows the progress dialog with text from {@link CommonUtils}
     */

    @Override
    public void showLoading(int textId) {
        hideLoading();
        mProgressDialog = CommonUtils.showLoadingDialog(this.getContext(), true, textId);
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
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    /**
     * Shows a SnackBar with default error if input message is null
     * Else calls the showSnackBar method with the message provided
     *
     * @param message Text to be shown on SnackBar
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

    public ActivityComponent getActivityComponent() {
        if (mActivity != null) {
            return mActivity.getActivityComponent();
        }
        return null;
    }

    public BaseActivity getBaseActivity() {
        return mActivity;
    }

    public void setUnBinder(Unbinder unBinder) {
        mUnBinder = unBinder;
    }

    protected abstract void setUp(View view);

    @Override
    public void onDestroy() {
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
        super.onDestroy();
    }

    public interface Callback {

        void onFragmentAttached();

        void onFragmentDetached(String tag);
    }
}
