package com.indiandesigns.fabcut.ui.leader.jobs;

import com.indiandesigns.fabcut.data.network.model.AssignEndBitJobRequest;
import com.indiandesigns.fabcut.data.network.model.AssignJobRequest;
import com.indiandesigns.fabcut.data.network.model.Follower;
import com.indiandesigns.fabcut.data.network.model.SizeDetail;
import com.indiandesigns.fabcut.ui.base.MvpPresenter;
import com.indiandesigns.fabcut.ui.common.model.MarkerItem;
import com.indiandesigns.fabcut.ui.common.model.OcLay;
import com.indiandesigns.fabcut.ui.common.model.OcLayDetail;
import com.indiandesigns.fabcut.ui.leader.LeaderPresenter;

import java.util.List;

/**
 * Implemented by {@link LeaderJobsPresenter}
 */

public interface LeaderJobsMvpPresenter<V extends LeaderJobsMvpView> extends MvpPresenter<V> {

    void fetchJobDetails(String marker);

    void fetchFollowers();

    void assignJob(AssignJobRequest request, Follower follower);

    void assignEndBitJob(AssignEndBitJobRequest request, Follower follower);

    void getFollowerJob(Follower follower);

    void fetchFollowerJob(Follower follower);

    void fetchFollowerEndBitJob(Follower follower);

    void fetchAssignedJobs();

    void getFollowerEndBitJob(Follower follower);

    void getCountriesList(OcLay job);
}