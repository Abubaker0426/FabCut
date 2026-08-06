package com.indiandesigns.fabcut.ui.follower.job_detail;

import com.indiandesigns.fabcut.ui.base.MvpView;
import com.indiandesigns.fabcut.data.network.model.ScanBarcode;

import java.util.List;

public interface EndBitJobDetailMvpView extends MvpView{

    String getDeviceId();

    void updateLayLength(String ocNumber, Double layLength, Integer jobId, Double quantity, String description, int sumOfRatios, List<List<String>> listData);

    void updateBarcodeList(ScanBarcode barcode);

    void removeBarcodeFromList(int position);

    String getString(int id);

    void pliesValidated(int position);

    void pliesError(int position);

    void updateFromDatabase(List<ScanBarcode> scanBarcodeList);
}
