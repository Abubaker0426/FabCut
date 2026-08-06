package com.indiandesigns.fabcut.ui.follower.job_detail;

import com.indiandesigns.fabcut.data.network.model.FetchFollowerEndBitJobResponse;
import com.indiandesigns.fabcut.data.network.model.ScanBarcode;
import com.indiandesigns.fabcut.ui.base.MvpPresenter;


public interface EditEndBitJobDetailMvpPresenter<V extends EndBitJobDetailMvpView> extends MvpPresenter<V> {

    void onViewPrepared(String ocNumber, Double layLength, Integer jobId, String itemCode, String itemDesc, FetchFollowerEndBitJobResponse.JobDetail jobDetail);

    void getBarcodeData(String code, Integer jobId, String partName);

    void delete(ScanBarcode barcode, Integer jobId, String partName, int position);

    void validateActualPlies(Double actualPlies, String barcode, Integer jobId, String partName, int position);

    void deleteBarcode(ScanBarcode item);

    void updateScanBarcode(ScanBarcode item);

    void saveItemQuantityData(Double cut, Double assigned, String itemCode);
}
