package com.indiandesigns.fabcut.ui.base;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.StringRes;

/**
 * Interface extended by all views
 *
 * Contains common functionality for views
 */

public interface MvpView {

    void showLoading();

    void showLoading(int textId);

    void showConfirmationDialog(int message);

    void showAlertDialog(String message, DialogInterface.OnClickListener dialogClickListener);

    void showAlertDialog(int positiveText, int negativeText, String message, DialogInterface.OnClickListener dialogClickListener);

    void showAlertDialog(int message, DialogInterface.OnClickListener dialogClickListener);

    void showAlertDialog(int message);

    void hideLoading();

    void onError(@StringRes int resId);

    void onError(String message);

    void showMessage(String message);

    void showMessage(@StringRes int resId);

    boolean isNetworkConnected();

    void hideKeyboard();

    String getString(int resId);

}
