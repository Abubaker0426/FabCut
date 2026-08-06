package com.indiandesigns.fabcut.ui.leader.jobs.parts;

import com.indiandesigns.fabcut.data.network.model.FetchPartsDetails;
import com.indiandesigns.fabcut.ui.base.MvpView;

import java.util.List;

public interface BundlePartsMvpView extends MvpView {

    void updatePartsResponse(List<FetchPartsDetails> bundlePartsList);
    void showErrorIconForOCNumber();
    void closeBundlePartsActivity();
}
