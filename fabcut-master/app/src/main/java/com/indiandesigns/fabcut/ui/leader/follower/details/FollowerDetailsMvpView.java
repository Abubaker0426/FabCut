package com.indiandesigns.fabcut.ui.leader.follower.details;

import com.indiandesigns.fabcut.ui.base.MvpView;

import java.util.List;

public interface FollowerDetailsMvpView extends MvpView {
    void updateView(String itemDesc, List<List<String>> ratioListData);
}