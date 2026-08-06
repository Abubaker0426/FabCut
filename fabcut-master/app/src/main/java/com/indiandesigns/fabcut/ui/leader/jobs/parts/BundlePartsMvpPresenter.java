package com.indiandesigns.fabcut.ui.leader.jobs.parts;

import com.indiandesigns.fabcut.data.network.model.FetchPartsDetails;
import com.indiandesigns.fabcut.ui.base.MvpPresenter;

import java.util.List;

public interface BundlePartsMvpPresenter<V extends BundlePartsMvpView> extends MvpPresenter<V> {

        void validateOcNumberAndGetParts(String ocNo);
        void callPutSelectedBundleParts(String ocNo, List<FetchPartsDetails> PartsDetailList);
        boolean validateSelectedPartsList(List<FetchPartsDetails> PartsDetailList);

}
