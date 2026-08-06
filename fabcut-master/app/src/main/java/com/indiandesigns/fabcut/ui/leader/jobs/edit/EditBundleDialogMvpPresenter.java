package com.indiandesigns.fabcut.ui.leader.jobs.edit;

import com.indiandesigns.fabcut.data.network.model.FetchPartsDetails;
import com.indiandesigns.fabcut.data.network.model.FetchPartsResponse;
import com.indiandesigns.fabcut.data.network.model.SizeDetail;
import com.indiandesigns.fabcut.ui.base.MvpPresenter;
import com.indiandesigns.fabcut.ui.common.model.BundleDetail;
import com.indiandesigns.fabcut.ui.common.model.OcLay;
import com.indiandesigns.fabcut.ui.common.model.OcLayDetail;

import java.util.ArrayList;
import java.util.List;

public interface EditBundleDialogMvpPresenter<V extends EditBundleDialogMvpView> extends MvpPresenter<V> {
    void fetchParts(OcLay ocLay);

    void saveParts(OcLay ocLay, List<SizeDetail> sizeDetails, FetchPartsResponse response, String fileName);

    int getBundleSplitAllowedQuantity(List<OcLayDetail> sizeList);

    BundleDetail getBundleDetail(int bundleQuantity, String bundleCountry);

    boolean validateBundlePartsDetailList(List<FetchPartsDetails> partsDetails);

    List<OcLayDetail> addAugmentedSizeToOcLayDetailList(List<OcLayDetail> ocLayDetailsList, ArrayList<BundleDetail> countryAndQuantityList);

    List<SizeDetail> createSizeDetailsList(List<OcLayDetail> augmentedSizeList, List<FetchPartsDetails> partsList);
}