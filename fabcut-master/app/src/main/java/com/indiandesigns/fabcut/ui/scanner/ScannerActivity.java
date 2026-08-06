package com.indiandesigns.fabcut.ui.scanner;

import android.app.Activity;
import android.os.Bundle;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Intent;
import android.content.Context;

import com.indiandesigns.fabcut.R;
import com.indiandesigns.fabcut.ui.base.BaseActivity;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import static com.indiandesigns.fabcut.utils.AppConstants.SCANNED_CODE;

public class ScannerActivity extends BaseActivity implements ScannerMvpView {

    @Inject
    ScannerPresenter<ScannerMvpView> mPresenter;

    @BindView(R.id.barcode_scanner_view)
    DecoratedBarcodeView scannerView;

    /***
     * Returns an intent for launching the activity
     * @param context Context
     * @return Intent for activity
     */

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, ScannerActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));
        mPresenter.onAttach(ScannerActivity.this);

        setUp();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDetach();
        super.onDestroy();
    }

    /**
     * Sets up listener for the scanner view
     * Hides the informational text for scanner view
     */

    @Override
    protected void setUp() {
        scannerView.setStatusText("");
        mPresenter.onViewPrepared(scannerView);
    }

    /**
     * Resumes the scanner
     */

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.resume();
    }

    /**
     * Pauses the scanner
     */

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.pause();
    }

    /**
     * Closes the scanner activity and returns the scanned barcode
     * @param barcode Scanned barcode
     */

    @Override
    public void returnScannedCode(String barcode) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(SCANNED_CODE, barcode);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
}