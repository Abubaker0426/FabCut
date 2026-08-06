package com.indiandesigns.fabcut.ui.scanner;

import com.indiandesigns.fabcut.ui.base.MvpPresenter;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

/**
 * Implemented by {@link ScannerPresenter}
 */

public interface ScannerMvpPresenter<V extends ScannerMvpView> extends MvpPresenter<V> {

    void onViewPrepared(DecoratedBarcodeView scannerView);
}