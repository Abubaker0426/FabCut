package com.indiandesigns.fabcut.ui.base;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.indiandesigns.fabcut.MvpApp;
import com.indiandesigns.fabcut.R;
import com.indiandesigns.fabcut.di.component.ActivityComponent;
import com.indiandesigns.fabcut.di.component.DaggerActivityComponent;
import com.indiandesigns.fabcut.di.module.ActivityModule;
import com.indiandesigns.fabcut.utils.CommonUtils;
import com.indiandesigns.fabcut.utils.NetworkUtils;

import butterknife.Unbinder;

/**
 * Extended by all activities
 * Provides common functionality
 * Initializes ActivityComponent (prefixed with Dagger now) for dependency injection
 */

public abstract class BaseActivity extends AppCompatActivity
        implements MvpView, BaseFragment.Callback {

    private ProgressDialog mProgressDialog;

    private ActivityComponent mActivityComponent;

    private Unbinder mUnBinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            mActivityComponent = DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .applicationComponent(((MvpApp) getApplication()).getComponent())
                .build();

    }

    public ActivityComponent getActivityComponent() {
        return mActivityComponent;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

    /**
     * Asks for permissions if android version is greater than Marshmallow
     *
     * @param permissions Arrays of permission required
     * @param requestCode Request code for activity result
     */

    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissionsSafely(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    /**
     * Shows the progress dialog from {@link CommonUtils}
     */

    @Override
    public void showLoading() {
        hideLoading();
        mProgressDialog = CommonUtils.showLoadingDialog(this, false, 0);
    }

    /**
     * Shows the progress dialog with text from {@link CommonUtils}
     */

    @Override
    public void showLoading(int textId) {
        hideLoading();
        mProgressDialog = CommonUtils.showLoadingDialog(this, true, textId);
    }

    /**
     * Shows the alert dialog from {@link CommonUtils} with custom message
     * and dialog click listener
     */

    @Override
    public void showAlertDialog(String message, DialogInterface.OnClickListener dialogClickListener) {
        CommonUtils.showAlertDialog(this, message, dialogClickListener);
    }

    /**
     * Shows the alert dialog from {@link CommonUtils} with custom message,
     * custom positive & negative button text
     * and dialog click listener
     */

    @Override
    public void showAlertDialog(int positiveText, int negativeText, String message, DialogInterface.OnClickListener dialogClickListener) {
        CommonUtils.showAlertDialog(this, positiveText, negativeText, message, dialogClickListener);
    }

    /**
     * Shows the alert dialog from {@link CommonUtils} with custom message,
     * and dismiss button
     */

    @Override
    public void showAlertDialog(int message) {
        CommonUtils.showAlertDialog(this, message);
    }

    /**
     * Shows the alert dialog from {@link CommonUtils} with custom message,
     * and dismiss button
     */

    @Override
    public void showConfirmationDialog(int message) {
        CommonUtils.showConfirmationDialog(this, message);
    }

    /**
     * Shows the alert dialog from {@link CommonUtils} with custom message
     * and dialog click listener
     */

    @Override
    public void showAlertDialog(int message, DialogInterface.OnClickListener dialogClickListener) {
        CommonUtils.showAlertDialog(this, message, dialogClickListener);
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
     * Shows a SnackBar with the message provided
     *
     * @param message To be shown on the SnackBar
     */

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBlack));
        TextView textView = (TextView) sbView
                .findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
        snackbar.show();
    }

    /**
     * Shows a SnackBar with default error if input message is null
     * Else calls the showSnackBar method with the message provided
     *
     * @param message Text to be shown on SnackBar
     */

    @Override
    public void onError(String message) {
        if (message != null) {
            showSnackBar(message);
        } else {
            showSnackBar(getString(R.string.some_error));
        }
    }

    /**
     * Gets the string from the resource id and shows the SnackBar
     *
     * @param resId Resource id of the string
     */

    @Override
    public void onError(@StringRes int resId) {
        onError(getString(resId));
    }

    /**
     * Shows a Toast with default error if input message is null
     * Else shows a Toast with the message provided
     *
     * @param message Text to be shown on Toast
     */

    @Override
    public void showMessage(String message) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.some_error), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Gets the string from the resource id and shows the Toast
     *
     * @param resId Resource id of the string
     */

    @Override
    public void showMessage(@StringRes int resId) {
        showMessage(getString(resId));
    }

    /**
     * Checks for the network connectivity
     *
     * @return The status of the network connectivity
     */

    @Override
    public boolean isNetworkConnected() {
        return NetworkUtils.isNetworkConnected(getApplicationContext());
    }

    @Override
    public void onFragmentAttached() {

    }

    @Override
    public void onFragmentDetached(String tag) {

    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void setUnBinder(Unbinder unBinder) {
        mUnBinder = unBinder;
    }

    @Override
    protected void onDestroy() {

        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
        super.onDestroy();
    }

    protected abstract void setUp();
}
