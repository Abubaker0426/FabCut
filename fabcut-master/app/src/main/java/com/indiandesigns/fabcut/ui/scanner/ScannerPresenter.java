package com.indiandesigns.fabcut.ui.scanner;

import com.google.zxing.ResultPoint;
import com.indiandesigns.fabcut.data.DataManager;
import com.indiandesigns.fabcut.ui.base.BasePresenter;
import com.indiandesigns.fabcut.utils.AppLogger;
import com.indiandesigns.fabcut.utils.rx.SchedulerProvider;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

import javax.inject.Inject;

public class ScannerPresenter<V extends ScannerMvpView> extends BasePresenter<V> implements ScannerMvpPresenter<V> {

    private static final String TAG = "ScannerPresenter";

    /**
     * Parameterized Constructor
     *
     * Instantiates data operator classes
     *
     * @param dataManager Injected with Dagger
     * @param schedulerProvider Injected with Dagger
     * @param compositeDisposable Injected with Dagger
     */

    @Inject
    public ScannerPresenter(DataManager dataManager,
                            SchedulerProvider schedulerProvider,
                            CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    /**
     * Instantiates the scanner view and returns the code
     * when a barcode is scanned successfully
     *
     * @param scannerView Scanner View
     */

    @Override
    public void onViewPrepared(DecoratedBarcodeView scannerView) {
        scannerView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                getMvpView().returnScannedCode(result.getText());
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {}
        });
    }
}