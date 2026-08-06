package com.indiandesigns.fabcut.ui.leader.jobs.edit;

import android.view.View;

import com.indiandesigns.fabcut.data.network.model.AssignJobRequest;
import com.indiandesigns.fabcut.ui.base.MvpView;

/**
 * Implemented by {@link EditJobDialog} to perform actions
 */

public interface EditJobDialogMvpView extends MvpView {
    void dismissDialog();
    void assignJob(AssignJobRequest request);
    void removeItemFromJob(String itemCode);
}