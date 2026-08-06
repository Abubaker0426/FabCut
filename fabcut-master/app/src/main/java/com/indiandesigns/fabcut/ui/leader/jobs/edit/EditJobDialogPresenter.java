package com.indiandesigns.fabcut.ui.leader.jobs.edit;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.indiandesigns.fabcut.R;
import com.indiandesigns.fabcut.data.DataManager;
import com.indiandesigns.fabcut.data.network.model.AssignJobRequest;
import com.indiandesigns.fabcut.data.network.model.Item;
import com.indiandesigns.fabcut.ui.base.BasePresenter;
import com.indiandesigns.fabcut.utils.rx.SchedulerProvider;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

import static com.indiandesigns.fabcut.utils.AppConstants.ITEM_PATTERN_TAG;
import static com.indiandesigns.fabcut.utils.AppConstants.ITEM_SHADE_TAG;
import static com.indiandesigns.fabcut.utils.AppConstants.ITEM_SHRINKAGE_TAG;
import static com.indiandesigns.fabcut.utils.AppConstants.LAY_LENGTH_TAG;
import static com.indiandesigns.fabcut.utils.AppConstants.QUANTITY_GROUP_TAG;
import static com.indiandesigns.fabcut.utils.AppConstants.SHADE_GROUP_TAG;

public class EditJobDialogPresenter<V extends EditJobDialogMvpView> extends BasePresenter<V> implements EditJobDialogMvpPresenter<V> {

    private static final String TAG = "EditJobDialogPresenter";

    /**
     * Parameterized Constructor
     *
     * Instantiates data operator classes
     *
     * @param dataManager Injected with Dagger
     * @param schedulerProvider Injected with Dagger
     * @param compositeDisposable Injected with Dagger
     */

    @Inject
    public EditJobDialogPresenter(DataManager dataManager,
                                  SchedulerProvider schedulerProvider,
                                  CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    /**
     * Prepares data model to be assigned to a follower
     * @param sizeContainer Size values layout
     * @param ratioContainer Ratio values layout
     * @param quantitiesContainer Quantity values layout
     * @param itemList Item list
     * @param itemList
     */

    @Override
    public void prepareJobData(LinearLayout sizeContainer, LinearLayout ratioContainer, LinearLayout quantitiesContainer, List<Item> itemList) {

        AssignJobRequest request = new AssignJobRequest();
        request.setLocation(getDataManager().getLocation());

        List<AssignJobRequest.JobDetail> jobDetails = new ArrayList<AssignJobRequest.JobDetail>();

        // Traverses the array of TextViews and EditTexts and prepares the data model

        final int childCount = sizeContainer.getChildCount();
        String jobItemCode = new String();
        String jobItemDescription = new String();
        String itemShade = new String();
        String itemShrinkage = new String();
        String itemPattern = new String();

        for(int itemIndex = 0; itemIndex < quantitiesContainer.getChildCount(); itemIndex++){

            View view = quantitiesContainer.getChildAt(itemIndex);
            if(view instanceof TextView){
                TextView itemCodeTextView = (TextView) view;
                String[] splitItemCode = itemCodeTextView.getText().toString().split(",");
                for (Item item : itemList) {
                    String itemCode = item.getItemCode();
                    if(itemCode.equals(splitItemCode[0])){
                        jobItemCode = item.getItemCode();
                        jobItemDescription = item.getItemDesc();
                    }
                }
            }

            if(quantitiesContainer.getChildAt(itemIndex).getTag() == SHADE_GROUP_TAG){
                LinearLayout quantityContainer = (LinearLayout) quantitiesContainer.getChildAt(itemIndex);

                String itemShadeValue = ((TextView) quantityContainer.findViewWithTag(ITEM_SHADE_TAG)).getText().toString();
                String itemShrinkageValue = ((TextView) quantityContainer.findViewWithTag(ITEM_SHRINKAGE_TAG)).getText().toString();
                String itemPatternValue = ((TextView) quantityContainer.findViewWithTag(ITEM_PATTERN_TAG)).getText().toString();

                if(TextUtils.isEmpty(itemShadeValue)){
                    getMvpView().showMessage(R.string.item_shade_error);
                    return;
                }

                if(TextUtils.isEmpty(itemShrinkageValue)){
                    getMvpView().showMessage(R.string.item_shrinkage_error);
                    return;
                }

                if(TextUtils.isEmpty(itemPatternValue)){
                    getMvpView().showMessage(R.string.item_pattern_error);
                    return;
                }

                itemShade = itemShadeValue;
                itemShrinkage = itemShrinkageValue;
                itemPattern = itemPatternValue;


            }

            if(quantitiesContainer.getChildAt(itemIndex).getTag() == QUANTITY_GROUP_TAG){
                LinearLayout quantityContainer = (LinearLayout) quantitiesContainer.getChildAt(itemIndex);
                AssignJobRequest.JobDetail jobDetail = request.new JobDetail();
                List<AssignJobRequest.RatioDetail> ratioDetailList = new ArrayList<>();

                String layLength = ((TextView) quantityContainer.findViewWithTag(LAY_LENGTH_TAG)).getText().toString();
                if(TextUtils.isEmpty(layLength)){
                    getMvpView().showMessage(R.string.lay_length_error);
                    return;
                }

                request.setLayLength(Double.valueOf(layLength));

                for (int index = 0; index < childCount - 2; index++) {
                    AssignJobRequest.RatioDetail ratioDetail = request.new RatioDetail();

                    String size = ((TextView) sizeContainer.getChildAt(index)).getText().toString();
                    String quantity = ((EditText) quantityContainer.getChildAt(index)).getText().toString();
                    String ratio = ((EditText) ratioContainer.getChildAt(index)).getText().toString();

                    ratioDetail.setQuantity(Integer.valueOf(quantity));
                    ratioDetail.setSize(size);
                    ratioDetail.setRatio(Integer.valueOf(ratio));

                    ratioDetailList.add(ratioDetail);
                }
                jobDetail.setItemCode(jobItemCode);
                jobDetail.setItemDesc(jobItemDescription);
                jobDetail.setShade(itemShade);
                jobDetail.setShrinkage(itemShrinkage);
                jobDetail.setPattern(itemPattern);
                jobDetail.setRatioDetails(ratioDetailList);
                jobDetails.add(jobDetail);
            }
        }

        request.setJobDetails(jobDetails);
        getMvpView().assignJob(request);
    }

    /**
     * Shows confirmation message to remove job item
     * @param itemCode Job item Code
     */
    @Override
    public void confirmDialogToRemoveJobItem(String itemCode) {
        getMvpView().showAlertDialog(R.string.jobAssign_confirm_dialog_message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(i == DialogInterface.BUTTON_NEGATIVE){
                    dialog.dismiss();
                }else {
                    getMvpView().removeItemFromJob(itemCode);
                }
            }
        });
    }
}