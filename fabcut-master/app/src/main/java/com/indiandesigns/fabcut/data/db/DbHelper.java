package com.indiandesigns.fabcut.data.db;

import com.indiandesigns.fabcut.data.network.model.ScanBarcode;

import java.util.List;

import io.reactivex.Observable;

/**
 * Interface for CRUD operation with requests
 */
public interface DbHelper {

    Observable<Long> saveScanBarcode(ScanBarcode barcode);

    Observable<List<ScanBarcode>> getScanBarcodes(int jobId, String itemCode);

    Observable<List<ScanBarcode>> getScanBarcodes(int jobId);

    Observable<Boolean> deleteScanBarcodes(int jobId);

    Observable<Boolean> deleteScanBarcode(ScanBarcode barcode);

    Observable<Boolean> updateScanBarcode(ScanBarcode barcode);

}
