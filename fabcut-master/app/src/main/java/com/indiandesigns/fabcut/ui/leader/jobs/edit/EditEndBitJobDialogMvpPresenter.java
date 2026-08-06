package com.indiandesigns.fabcut.ui.leader.jobs.edit;

import android.widget.LinearLayout;

import com.indiandesigns.fabcut.data.network.model.Item;
import com.indiandesigns.fabcut.ui.base.MvpPresenter;
import com.indiandesigns.fabcut.ui.common.model.MarkerItem;

import java.util.List;

/**
 * Implemented by {@link EditEndBitJobDialogPresenter}
 */


public interface EditEndBitJobDialogMvpPresenter<V extends EditEndBitJobDialogMvpView> extends MvpPresenter<V> {

    void prepareJobData(LinearLayout sizeContainer, LinearLayout quantityContainer, List<Item> itemList, String selectedLay, List<String> selectedParts);

    void fetchLayNumbers(MarkerItem markerItem);

    void fetchPartNames(String ocNum);
}