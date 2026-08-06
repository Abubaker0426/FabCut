package com.indiandesigns.fabcut.ui.leader.jobs.edit;

import android.view.View;
import android.widget.LinearLayout;

import com.indiandesigns.fabcut.data.network.model.Item;
import com.indiandesigns.fabcut.ui.base.MvpPresenter;

import java.util.List;

/**
 * Implemented by {@link EditJobDialogPresenter}
 */


public interface EditJobDialogMvpPresenter<V extends EditJobDialogMvpView> extends MvpPresenter<V> {

    void prepareJobData(LinearLayout sizeContainer, LinearLayout ratioContainer, LinearLayout quantityContainer, List<Item> itemList);
    void confirmDialogToRemoveJobItem(String itemCode);
}