package com.indiandesigns.fabcut.ui.leader.jobs.parts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.indiandesigns.fabcut.R;
import com.indiandesigns.fabcut.data.network.model.FetchPartsDetails;
import com.indiandesigns.fabcut.databinding.ActivityBundlePartsBinding;
import com.indiandesigns.fabcut.ui.base.BaseActivity;

import java.util.List;

import javax.inject.Inject;

public class BundlePartsActivity extends BaseActivity implements BundlePartsMvpView, EditText.OnFocusChangeListener, TextView.OnEditorActionListener, BundlePartsAdapter.Callback {

    private static String TAG = "BundlePartsActivity";


    @Inject
    BundlePartsPresenter<BundlePartsMvpView> mPresenter;

    private ActivityBundlePartsBinding activityBundlePartsBinding;

    private TextView ocNumberText;
    private ImageView validateIcon;
    private Button bundlePartsSubmitButton;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshActivityBundleParts;
    private MaterialToolbar bundlePartsToolbar;

    private BundlePartsAdapter bundlePartsAdapter;
    private LinearLayoutManager linearLayoutManager;
    private String ocNumber;
    private List<FetchPartsDetails> partsDetailList;


    /***
     * Returns an intent for launching the activity
     * @param context Context
     * @return Intent for activity
     */

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, BundlePartsActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityBundlePartsBinding = ActivityBundlePartsBinding.inflate(getLayoutInflater());
        View view = activityBundlePartsBinding.getRoot();
        setContentView(view);

        getActivityComponent().inject(this);
        mPresenter.onAttach(BundlePartsActivity.this);

        this.setUp();
    }


    @Override
    protected void setUp() {
        this.ocNumberText = activityBundlePartsBinding.bundlePartsOcNumber;
        this.validateIcon = activityBundlePartsBinding.bundlePartsValidateIcon;
        this.bundlePartsSubmitButton = activityBundlePartsBinding.bundlePartsSubmit;
        this.recyclerView = activityBundlePartsBinding.bundlePartsRecyclerView;
        this.refreshActivityBundleParts = activityBundlePartsBinding.refreshActivityBundleParts;
        this.bundlePartsToolbar = activityBundlePartsBinding.activityBundlePartsToolbar;
        this.bundlePartsSubmitButton = activityBundlePartsBinding.bundlePartsSubmit;

        this.ocNumberText.setOnFocusChangeListener(this);
        this.ocNumberText.setOnEditorActionListener(this);

        this.linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        this.linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        this.recyclerView.setLayoutManager(this.linearLayoutManager);
        this.bundlePartsAdapter = new BundlePartsAdapter();
        this.recyclerView.setAdapter(bundlePartsAdapter);
        this.bundlePartsAdapter.setCallback(this);

        this.bundlePartsSubmitButton.setOnClickListener(submitButtonClickListener);
        this.refreshActivityBundleParts.setOnRefreshListener(refreshRecycleView);
        this.bundlePartsToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24);
        this.bundlePartsToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });
    }

    /**
     * Validates OC number and populates list of parts
     * @param view View changing focus
     * @param hasFocus Is focused currently or not
     */
    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if(!hasFocus){
            ocNumber = this.ocNumberText.getText().toString();
            hideKeyboard();
            if(TextUtils.isEmpty(ocNumber)){
                validateIcon.setVisibility(View.VISIBLE);
                validateIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_error));
                onError(R.string.enter_oc_number_error);
                return;
            }
            mPresenter.validateOcNumberAndGetParts(ocNumber);
        }
    }

    /***
     * This function is responsible to click out of OC Number EditText to validate entered OC number
     * @param textView
     * @param actionId
     * @param event
     * @return
     */

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
            hideKeyboard();
            this.ocNumberText.clearFocus();
        }
        return true;
    }

    @Override
    public void updatePartsResponse(List<FetchPartsDetails> bundlePartsList) {
        this.partsDetailList = bundlePartsList;
        validateIcon.setVisibility(View.VISIBLE);
        validateIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
        this.bundlePartsAdapter.addBundleParts(bundlePartsList);
    }

    @Override
    public void showErrorIconForOCNumber() {
        validateIcon.setVisibility(View.VISIBLE);
        validateIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_error));
    }

    @Override
    public void closeBundlePartsActivity() {
        finish();
    }


    private SwipeRefreshLayout.OnRefreshListener refreshRecycleView = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            validateIcon.setVisibility(View.INVISIBLE);
            mPresenter.validateOcNumberAndGetParts(ocNumber);
            refreshActivityBundleParts.setRefreshing(false);
        }
    };

    /***
     * This function responsible to update isSelected in list of fetchPartsDetails
     * @param partUnique Unique number for part
     * @param isChecked Status of checkbox
     * @return
     */
    @Override
    public void setPartsDetails(Long partUnique,  boolean isChecked) {

        for (int index = 0; index < this.partsDetailList.size(); index++) {
            FetchPartsDetails partsDetails = this.partsDetailList.get(index);
            if(partsDetails.getPartsUnique() == partUnique){
                partsDetails.setIsSelected(0);
                if(isChecked) { partsDetails.setIsSelected(1); }
                this.partsDetailList.set(index, partsDetails);
                break;
            }

        }
        this.bundlePartsAdapter.addBundleParts(this.partsDetailList);
    }

    /**
     * This function validates input and calls API to update bundle parts
     * @return
     */

    private View.OnClickListener submitButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!checkIfEntriesAreValid() || !mPresenter.validateSelectedPartsList(BundlePartsActivity.this.partsDetailList)) {
                onError(R.string.bundle_parts_validate_error);
                return;
            }
            bundlePartsSubmitButton.setEnabled(false);
            mPresenter.callPutSelectedBundleParts(BundlePartsActivity.this.ocNumber, BundlePartsActivity.this.partsDetailList);
        }
    };

    /**
     * Checks if the inputs entry by user is valid
     * @return Boolean
     */
    private boolean checkIfEntriesAreValid() {
        String ocNumber = this.ocNumberText.getText().toString();
        if (TextUtils.isEmpty(ocNumber)) {
            return false;
        }
        return true;
    }

}