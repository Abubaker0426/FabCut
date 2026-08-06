package com.indiandesigns.fabcut.ui.leader.follower.details;

import com.indiandesigns.fabcut.data.network.model.FetchFollowerEndBitJobResponse;
import com.indiandesigns.fabcut.data.network.model.FetchFollowerResponse;
import com.indiandesigns.fabcut.ui.base.MvpPresenter;

public interface FollowerDetailsMvpPresenter<V extends FollowerDetailsMvpView> extends MvpPresenter<V> {

    void parseDetails(FetchFollowerResponse response);

    void parseEndBitDetails(FetchFollowerEndBitJobResponse response);
}