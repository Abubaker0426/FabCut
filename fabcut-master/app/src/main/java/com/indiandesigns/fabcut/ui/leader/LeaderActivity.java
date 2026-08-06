package com.indiandesigns.fabcut.ui.leader;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.indiandesigns.fabcut.R;
import com.indiandesigns.fabcut.data.network.model.FetchFollowerEndBitJobResponse;
import com.indiandesigns.fabcut.data.network.model.FetchFollowerResponse;
import com.indiandesigns.fabcut.data.network.model.Follower;
import com.indiandesigns.fabcut.ui.base.BaseActivity;
import com.indiandesigns.fabcut.ui.common.AssignedJobListAdapter;
import com.indiandesigns.fabcut.ui.common.CustomViewPager;
import com.indiandesigns.fabcut.ui.common.FitAdapter;
import com.indiandesigns.fabcut.ui.common.OverFlowMenu;
import com.indiandesigns.fabcut.ui.common.model.MarkerItem;
import com.indiandesigns.fabcut.ui.common.model.OcLay;
import com.indiandesigns.fabcut.ui.leader.follower.details.FollowerDetailsActivity;
import com.indiandesigns.fabcut.ui.leader.jobs.FollowerAdapter;
import com.indiandesigns.fabcut.ui.leader.jobs.LeaderJobsActivity;
import com.indiandesigns.fabcut.ui.leader.jobs.edit.EditBundleDialog;
import com.indiandesigns.fabcut.ui.leader.jobs.parts.BundlePartsActivity;
import com.indiandesigns.fabcut.utils.CommonUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import biz.laenger.android.vpbs.BottomSheetUtils;
import biz.laenger.android.vpbs.ViewPagerBottomSheetBehavior;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LeaderActivity extends BaseActivity implements LeaderMvpView, EditText.OnFocusChangeListener, TextView.OnEditorActionListener,
        AdapterView.OnItemSelectedListener, FollowerAdapter.Callback, AssignedJobListAdapter.Callback, MarkerAdapter.Callback, OverFlowMenu.Callback {

    private static String TAG = "LeaderActivity";
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";


    @Inject
    LeaderPresenter<LeaderMvpView> mPresenter;
    @Inject
    OverFlowMenu overFlowMenu;

    @BindView(R.id.oc_no_edit)
    EditText ocNoText;

    @BindView(R.id.fit_type_spinner)
    Spinner fitDropdown;

    @BindView(R.id.submit_oc_style_button)
    Button submitButton;

    @BindView(R.id.feedback_icon)
    ImageView feedback;

    @BindView(R.id.activity_leader_toolbar)
    MaterialToolbar toolbar;

    private String ocNo;
    private String selectedFit;
    private FitAdapter fitAdapter;

    @BindView(R.id.bottom_sheet)
    CoordinatorLayout bottomSheet;

    private ViewPagerBottomSheetBehavior sheetBehavior;

    @BindView(R.id.view_pager)
    CustomViewPager viewPager;

    @Inject
    FollowerAdapter followerAdapter;

    @BindView(R.id.followers_recycler_view)
    RecyclerView followersRecyclerView;

    @Inject
    AssignedJobListAdapter jobListAdapter;

    @Inject
    LinearLayoutManager linearLayoutManager;

    @BindView(R.id.jobs_recycler_view)
    RecyclerView jobsRecyclerView;

    @BindView(R.id.tabs)
    TabLayout mTabLayout;

    private TextView showFollowers;

    @Inject
    MarkerAdapter markerAdapter;

    @BindView(R.id.marker_recycler_view)
    RecyclerView markerRecyclerView;

    @BindView(R.id.main_container)
    LinearLayout mainContainer;

    @BindView(R.id.markers_container)
    LinearLayout markerContainer;

    /***
     * Returns an intent for launching the activity
     * @param context Context
     * @return Intent for activity
     */

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, LeaderActivity.class);
        return intent;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader);

        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));
        mPresenter.onAttach(LeaderActivity.this);

        overFlowMenu.setOverFlowMenuVariables(this.toolbar, this);
        overFlowMenu.setCallback(this);
        overFlowMenu.setOverflowMenu();

        setUp();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDetach();
        super.onDestroy();
    }

    /***
     * Sets listener for EditText and Spinners
     * Sets up the follower/jobs recycler view and bottom sheet
     * Sets up the view pager
     */

    @Override
    protected void setUp() {

        ListPagerAdapter pagerAdapter = new ListPagerAdapter();
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        BottomSheetUtils.setupViewPager(viewPager);

        mTabLayout.setupWithViewPager(viewPager, true);

        int mNoOfColumns = CommonUtils.calculateNoOfColumns(this, 180);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, mNoOfColumns);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        followersRecyclerView.setLayoutManager(gridLayoutManager);
        followersRecyclerView.setAdapter(followerAdapter);
        followerAdapter.setCallback(this);

        jobsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        jobsRecyclerView.setLayoutManager(linearLayoutManager);
        jobsRecyclerView.setAdapter(jobListAdapter);
        jobListAdapter.setCallback(this);

        markerRecyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager markerLinearLayoutManager = new LinearLayoutManager(this);
        markerLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        markerRecyclerView.setLayoutManager(markerLinearLayoutManager);
        markerRecyclerView.setAdapter(markerAdapter);
        markerAdapter.setCallback(this);

        ocNoText.setOnFocusChangeListener(this);
        ocNoText.setOnEditorActionListener(this);
        fitDropdown.setOnItemSelectedListener(this);

        sheetBehavior = ViewPagerBottomSheetBehavior.from(bottomSheet);

        showFollowers = (TextView) bottomSheet.findViewById(R.id.show_followers);
        showFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sheetBehavior.setState(ViewPagerBottomSheetBehavior.STATE_EXPANDED);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    showFollowers.setText(R.string.followers);
                } else if(position == 1){
                    showFollowers.setText(R.string.jobs_assigned);
                } else return;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        sheetBehavior.setBottomSheetCallback(new ViewPagerBottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_EXPANDED){
                    mPresenter.fetchFollowers();
                    mPresenter.fetchAssignedJobs();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    /**
     * Invoked when user clicks back button
     */

    @Override
    public void onBackPressed() {
        if (isBottomSheetVisible()) {
            sheetBehavior.setState(ViewPagerBottomSheetBehavior.STATE_COLLAPSED);
            return;
        }

        if(markerContainer.getVisibility() == View.VISIBLE) {
            markerContainer.setVisibility(View.GONE);
            mainContainer.setVisibility(View.VISIBLE);
            return;
        }
        super.onBackPressed();
    }

    /**
     * Checks whether the bottom sheet is visible or not
     * @return Visibility of bottom sheet
     */

    private boolean isBottomSheetVisible(){
        return (sheetBehavior.getState() == ViewPagerBottomSheetBehavior.STATE_EXPANDED);
    }

    /***
     * Populates the spinner with fit types
     * Sets icon for valid OC Number
     *
     * @param fitTypes List of new item codes fetched from server for OC Number
     */

    @Override
    public void populateDropdownList(ArrayList<String> fitTypes) {
        feedback.setVisibility(View.VISIBLE);
        feedback.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
        fitAdapter = new FitAdapter(this, R.layout.spinner_row_select_fit, fitTypes);
        fitDropdown.setAdapter(fitAdapter);
    }

    /***
     * Clear focus if uses tabs out of EditText for OC Number
     * @param textView
     * @param actionId
     * @param keyEvent
     * @return
     */

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
            hideKeyboard();
            ocNoText.clearFocus();
        }
        return true;
    }

    /**
     * Validates and populates item code spinner when OC Number EditText loses focus
     * @param view View changing focus
     * @param hasFocus Is focused currently or not
     */

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if(!hasFocus){
            ocNo = ocNoText.getText().toString();
            hideKeyboard();
            mPresenter.validateOcNumber(ocNo);
        }
    }

    /***
     * Clears drop down and sets error icon for OC Number
     * Clears selected fit
     */

    @Override
    public void clearDropDown() {
        feedback.setVisibility(View.VISIBLE);
        fitDropdown.setAdapter(null);
        selectedFit = "";
        feedback.setImageDrawable(getResources().getDrawable(R.drawable.ic_error));
    }

    /***
     * Called when an item is selected in spinner
     * @param adapterView AdapterView
     * @param view Spinner View
     * @param position Position selected
     * @param l long
     */

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        selectedFit = fitAdapter.getItemData(position);
    }

    /**
     * Updates marker list
     * @param markerItems Marker items
     */

    @Override
    public void updateMarkerList(List<MarkerItem> markerItems) {
        markerAdapter.addItems(markerItems);
        mainContainer.setVisibility(View.GONE);
        markerContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClickListener(MarkerItem markerItem) {
        startActivity(LeaderJobsActivity.getStartIntent(this, markerItem));
    }

    /***
     * Override the method when there's nothing selected in the spinner
     * @param adapterView
     */

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /***
     * Called when submit button is clicked
     *
     * Checks if OC Number and Fit type are present
     * Launches Leader Jobs Activity
     * @param view
     */

    @OnClick(R.id.submit_oc_style_button)
    public void click(View view){
        hideKeyboard();
        if(TextUtils.isEmpty(ocNo)){
            onError(R.string.enter_oc_number_error);
            return;
        }
        if(TextUtils.isEmpty(selectedFit)){
            onError(R.string.select_fir_error);
            return;
        }
        mPresenter.fetchMarkers(ocNo, selectedFit);
    }

    /**
     * Update the followers recyclerview with response received from server
     * @param followers List of new followers
     */

    @Override
    public void updateFollowers(List<Follower> followers) {
        followerAdapter.addItems(followers);
    }

    /**
     * Called when a specific follower is clicked
     * @param follower Follower clicked
     */

    @Override
    public void onItemClickListener(Follower follower) {
        mPresenter.fetchFollowerJob(follower);
    }

    @Override
    public void openFollowerDetailActivity(FetchFollowerResponse response) {
        startActivity(FollowerDetailsActivity.getStartIntent(this, response));
    }

    @Override
    public void openFollowerDetailActivityForEndBit(FetchFollowerEndBitJobResponse response) {
        startActivity(FollowerDetailsActivity.getStartIntentForEndBit(this, response));
    }

    /**
     * Launch bundle split dialog
     */
    @Override
    public void showBundleSplitDialog(ArrayList<String> countries, OcLay job) {
        EditBundleDialog editBundleDialog = EditBundleDialog.newInstance();
        editBundleDialog.show(getSupportFragmentManager(), job, countries, this, this);
    }

    /**
     * Called when delete pressed on bottom sheet
     * @param follower Long pressed follower
     */

    @Override
    public void onDelete(Follower follower) {
        mPresenter.getFollowerJob(follower);
    }

    /**
     * Update the jobs recyclerview with response received from server
     * @param response List of assigned jobs
     */

    @Override
    public void updateList(List<OcLay> response) {
        jobListAdapter.addItems(response);
    }

    /**
     * Call API to fetch countries before launching bundle split dialog
     */

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void bundleSplitDialog(OcLay job) {
        mPresenter.getCountriesList(job);
    }

    /**
     * Deletes a file by path
     * @param path Path of file
     * @return true if file is deleted else false
     */

    @Override
    public boolean deleteFile(String path) {
        File fileToDelete = new File(path);
        if(fileToDelete.exists()){
            return fileToDelete.delete();
        } else return false;
    }

    /***
     * launching the bundle parts activity
     */

    @Override
    public void launchBundlePartsActivity() {
        startActivity(BundlePartsActivity.getStartIntent(this));
    }

    /**
     * Viewpager adapter for showing lists of Followers/Jobs
     */

    class ListPagerAdapter extends PagerAdapter {

        public Object instantiateItem(ViewGroup collection, int position) {

            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.id.followers_recycler_view;
                    break;
                case 1:
                    resId = R.id.jobs_recycler_view;
                    break;
            }
            return findViewById(resId);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override public void destroyItem(ViewGroup container, int position, Object object) {
            // No super
        }
    }


}