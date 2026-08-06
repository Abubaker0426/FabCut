package com.indiandesigns.fabcut.ui.follower;

import com.indiandesigns.fabcut.data.network.enums.JobType;
import com.indiandesigns.fabcut.ui.base.MvpPresenter;

/**
 * Implemented by {@link FollowerPresenter}
 */

public interface FollowerMvpPresenter<V extends FollowerMvpView> extends MvpPresenter<V> {

    void onViewPrepared();

    void completeJob(Integer jobId, Double assigned, Double cut, JobType jobType);

    void validatedAllPlies(Integer jobId, JobType jobType);

    void fetchEndBitJob();
}