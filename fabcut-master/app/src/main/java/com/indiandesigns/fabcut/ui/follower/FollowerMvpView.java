package com.indiandesigns.fabcut.ui.follower;

import com.indiandesigns.fabcut.data.network.model.FetchFollowerEndBitJobResponse;
import com.indiandesigns.fabcut.data.network.model.FetchFollowerResponse;
import com.indiandesigns.fabcut.ui.base.MvpView;

import java.util.List;

/**
 * Implemented by {@link FollowerActivity} to perform actions
 */

public interface FollowerMvpView extends MvpView {

    String getDeviceIdString();

    String getString(int id);

    void showMainLayout();

    void hideMainLayout();

    void updateViewPager(Integer jobId, Double layLength, String ocNumber, List<FetchFollowerResponse.JobDetail> jobDetails);

    void updateViewEndBitPager(Integer jobId, String ocNumber, String itemCode, String itemDesc, List<FetchFollowerEndBitJobResponse.JobDetail> jobDetails);
}