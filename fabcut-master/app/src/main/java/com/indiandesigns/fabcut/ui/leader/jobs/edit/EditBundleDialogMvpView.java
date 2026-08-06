package com.indiandesigns.fabcut.ui.leader.jobs.edit;

import com.indiandesigns.fabcut.data.network.model.FetchPartsResponse;
import com.indiandesigns.fabcut.ui.base.MvpView;
import com.indiandesigns.fabcut.ui.common.model.OcLay;

public interface EditBundleDialogMvpView extends MvpView {
    void dismissDialog();
    void createBarcodes(OcLay ocLay, FetchPartsResponse response, String fileName);
    void printBarcodes(OcLay ocLay, FetchPartsResponse response, String filename);
}
