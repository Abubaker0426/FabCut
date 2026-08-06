package com.indiandesigns.fabcut.ui.leader;

import com.indiandesigns.fabcut.data.network.model.Follower;
import com.indiandesigns.fabcut.ui.base.MvpPresenter;
import com.indiandesigns.fabcut.ui.common.model.OcLay;

/**
 * Implemented by {@link LeaderPresenter}
 */

public interface LeaderMvpPresenter<V extends LeaderMvpView> extends MvpPresenter<V> {

    void validateOcNumber(String ocNo);

    void fetchFollowers();

    void fetchAssignedJobs();

    void getFollowerJob(Follower follower);

    void fetchMarkers(String ocNo, String selectedFit);

    void fetchFollowerJob(Follower follower);

    void fetchFollowerEndBitJob(Follower follower);

    void getFollowerEndBitJob(Follower follower);

    void getCountriesList(OcLay job);
}