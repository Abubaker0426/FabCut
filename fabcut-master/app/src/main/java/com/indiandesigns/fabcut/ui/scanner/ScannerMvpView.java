package com.indiandesigns.fabcut.ui.scanner;

import com.indiandesigns.fabcut.ui.base.MvpView;

/**
 * Implemented by {@link ScannerActivity} to perform actions
 */

public interface ScannerMvpView extends MvpView {

    void returnScannedCode(String barcode);

}