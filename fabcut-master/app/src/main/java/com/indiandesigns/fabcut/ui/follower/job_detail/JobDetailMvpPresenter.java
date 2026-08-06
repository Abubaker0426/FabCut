package com.indiandesigns.fabcut.ui.follower.job_detail;

import com.indiandesigns.fabcut.data.network.model.FetchFollowerResponse.JobDetail;
import com.indiandesigns.fabcut.data.network.model.ScanBarcode;
import com.indiandesigns.fabcut.ui.base.MvpPresenter;

public interface JobDetailMvpPresenter<V extends JobDetailMvpView> extends MvpPresenter<V> {

    void onViewPrepared(String ocNumber, Double layLength, Integer jobId, JobDetail jobDetail);

    void getBarcodeData(String code, Integer jobId, String itemCode);

    void delete(ScanBarcode barcode, Integer jobId, int position);

    void validateActualPlies(Double actualPlies, String barcode, Integer jobId, int position);

    void deleteBarcode(ScanBarcode item);

    void updateScanBarcode(ScanBarcode item);

    void saveItemQuantityData(Double cut, Double assigned, String itemCode);
}
