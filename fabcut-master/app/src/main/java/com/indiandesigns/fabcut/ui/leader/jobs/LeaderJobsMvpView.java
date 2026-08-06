package com.indiandesigns.fabcut.ui.leader.jobs;

import com.indiandesigns.fabcut.data.network.model.FetchFollowerEndBitJobResponse;
import com.indiandesigns.fabcut.data.network.model.FetchFollowerResponse;
import com.indiandesigns.fabcut.data.network.model.FetchPartsResponse;
import com.indiandesigns.fabcut.data.network.model.Follower;
import com.indiandesigns.fabcut.ui.base.MvpView;
import com.indiandesigns.fabcut.ui.common.model.OcLay;
import com.indiandesigns.fabcut.ui.leader.LeaderActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Implemented by {@link LeaderJobsActivity} to perform actions
 */

public interface LeaderJobsMvpView extends MvpView {

    void updateJobsList(HashMap<Integer, List<List<String>>> ratios);

    void updateFollowers(List<Follower> followers);

    String getString(int id);

    void openFollowerDetailActivity(FetchFollowerResponse response);

    void openFollowerDetailActivityForEndBit(FetchFollowerEndBitJobResponse response);

    void updateAssignedJobsList(List<OcLay> ocLays);

    void showBundleSplitDialog(ArrayList<String> countries, OcLay job);
}