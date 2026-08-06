package com.indiandesigns.fabcut.ui.leader;

import com.indiandesigns.fabcut.data.network.model.FetchFollowerEndBitJobResponse;
import com.indiandesigns.fabcut.data.network.model.FetchFollowerResponse;
import com.indiandesigns.fabcut.data.network.model.FetchPartsResponse;
import com.indiandesigns.fabcut.data.network.model.Follower;
import com.indiandesigns.fabcut.data.network.model.JobListResponse;
import com.indiandesigns.fabcut.data.network.model.Marker;
import com.indiandesigns.fabcut.data.network.model.ValidateOcAndFetchItemsData;
import com.indiandesigns.fabcut.ui.base.MvpView;
import com.indiandesigns.fabcut.ui.common.model.MarkerItem;
import com.indiandesigns.fabcut.ui.common.model.OcLay;
import com.indiandesigns.fabcut.ui.scanner.ScannerActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Implemented by {@link LeaderActivity} to perform actions
 */

public interface LeaderMvpView extends MvpView {

    void populateDropdownList(ArrayList<String> fitTypes);

    void clearDropDown();

    void updateFollowers(List<Follower> followers);

    void updateList(List<OcLay> response);

    void updateMarkerList(List<MarkerItem> markerItems);

    void openFollowerDetailActivity(FetchFollowerResponse response);

    void openFollowerDetailActivityForEndBit(FetchFollowerEndBitJobResponse response);

    void showBundleSplitDialog(ArrayList<String> countries, OcLay job);

   }