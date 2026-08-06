package com.indiandesigns.fabcut.ui.leader.jobs.edit;

import com.indiandesigns.fabcut.data.network.model.AssignEndBitJobRequest;
import com.indiandesigns.fabcut.data.network.model.FetchPartsDetails;
import com.indiandesigns.fabcut.ui.base.MvpView;

import java.util.List;

/**
 * Implemented by {@link EditEndBitJobDialog} to perform actions
 */

public interface EditEndBitJobDialogMvpView extends MvpView {

    void dismissDialog();

    void assignJob(AssignEndBitJobRequest request);

    void updateLayNumbersList(List<Number> lays);

    void updatePartNamesList(List<FetchPartsDetails> parts);
}