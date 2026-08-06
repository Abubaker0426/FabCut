package com.indiandesigns.fabcut.ui.leader.jobs;

import android.os.Build;
import android.os.Bundle;

import javax.inject.Inject;

import biz.laenger.android.vpbs.BottomSheetUtils;
import biz.laenger.android.vpbs.ViewPagerBottomSheetBehavior;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.indiandesigns.fabcut.R;
import com.indiandesigns.fabcut.data.network.model.AssignEndBitJobRequest;
import com.indiandesigns.fabcut.data.network.model.AssignJobRequest;
import com.indiandesigns.fabcut.data.network.model.FetchFollowerEndBitJobResponse;
import com.indiandesigns.fabcut.data.network.model.FetchFollowerResponse;
import com.indiandesigns.fabcut.data.network.model.Follower;
import com.indiandesigns.fabcut.ui.base.BaseActivity;
import com.indiandesigns.fabcut.ui.common.AssignedJobListAdapter;
import com.indiandesigns.fabcut.ui.common.CustomViewPager;
import com.indiandesigns.fabcut.ui.common.model.MarkerItem;
import com.indiandesigns.fabcut.ui.common.model.OcLay;
import com.indiandesigns.fabcut.ui.leader.follower.details.FollowerDetailsActivity;
import com.indiandesigns.fabcut.ui.leader.jobs.edit.EditBundleDialog;
import com.indiandesigns.fabcut.ui.leader.jobs.edit.EditJobDialog;
import com.indiandesigns.fabcut.ui.leader.jobs.edit.EditEndBitJobDialog;
import com.indiandesigns.fabcut.utils.CommonUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class LeaderJobsActivity extends BaseActivity implements LeaderJobsMvpView, JobListAdapter.Callback, FollowerAdapter.Callback, AssignedJobListAdapter.Callback {

    private final static String MARKER_ITEM = "markerItem";

    private MarkerItem markerItem;

    private List<List<String>> endBitRatioList;

    @Inject
    JobListAdapter jobListAdapter;

    @Inject
    FollowerAdapter followerAdapter;

    @Inject
    LinearLayoutManager linearLayoutManager;

    @BindView(R.id.ratio_jobs_recycler_view)
    RecyclerView jobsRecyclerView;

    @BindView(R.id.bottom_sheet)
    CoordinatorLayout bottomSheet;

    @BindView(R.id.followers_recycler_view)
    RecyclerView followersRecyclerView;

    private ViewPagerBottomSheetBehavior sheetBehavior;
    private AssignJobRequest assignJobRequest;
    private AssignEndBitJobRequest assignEndBitJobRequest;

    @Inject
    LeaderJobsPresenter<LeaderJobsMvpView> mPresenter;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.view_pager)
    CustomViewPager viewPager;

    @Inject
    AssignedJobListAdapter assignedJobListAdapter;

    @BindView(R.id.jobs_recycler_view)
    RecyclerView assignedJobsRecyclerView;

    @BindView(R.id.tabs)
    TabLayout mTabLayout;

    private TextView showFollowers;

    private ListPagerAdapter pagerAdapter;

    public void setEndBitRatioList(List<List<String>> endBitRatioList) {
        this.endBitRatioList = endBitRatioList;
    }

    /***
     * Returns an intent for launching the activity
     * @param context Context
     * @return Intent for activity
     */

    public static Intent getStartIntent(Context context, MarkerItem markerItem) {
        Intent intent = new Intent(context, LeaderJobsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MARKER_ITEM, markerItem);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_jobs);

        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));
        mPresenter.onAttach(LeaderJobsActivity.this);

        if(getIntent().getExtras() != null){
            markerItem = (MarkerItem) getIntent().getSerializableExtra(MARKER_ITEM);
        }

        setUp();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDetach();
        super.onDestroy();
    }

    /***
     * Sets up the job and follower recycler views
     *
     * Fetches jobs for given marker
     */

    @Override
    protected void setUp() {

        pagerAdapter = new ListPagerAdapter();
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        BottomSheetUtils.setupViewPager(viewPager);

        mTabLayout.setupWithViewPager(viewPager, true);

        assignedJobsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager jobsLinearLayoutManager = new LinearLayoutManager(this);
        jobsLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        assignedJobsRecyclerView.setLayoutManager(jobsLinearLayoutManager);
        assignedJobsRecyclerView.setAdapter(assignedJobListAdapter);
        assignedJobListAdapter.setCallback(this);

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
                if(newState == BottomSheetBehavior.STATE_COLLAPSED){
                    followerAdapter.setSelectionMode(false);
                    mTabLayout.setVisibility(View.VISIBLE);
                    viewPager.setPagingEnabled(true);
                    assignJobRequest = null;
                }
                if(newState == BottomSheetBehavior.STATE_EXPANDED || newState ==  BottomSheetBehavior.STATE_HALF_EXPANDED){
                    mPresenter.fetchFollowers();
                    mPresenter.fetchAssignedJobs();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        jobsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        jobsRecyclerView.setLayoutManager(linearLayoutManager);
        jobsRecyclerView.setAdapter(jobListAdapter);
        jobListAdapter.setCallback(this);

        int mNoOfColumns = CommonUtils.calculateNoOfColumns(this, 180);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, mNoOfColumns);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        followersRecyclerView.setLayoutManager(gridLayoutManager);
        followersRecyclerView.setAdapter(followerAdapter);
        followerAdapter.setCallback(this);

        mPresenter.fetchJobDetails(markerItem.getMarkerUnique());

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.fetchJobDetails(markerItem.getMarkerUnique());
            }
        });

        Button end_bit_button = (Button) findViewById(R.id.end_bit);
        if(markerItem.getItems().size() == 1){
            end_bit_button.setVisibility(View.VISIBLE);
        }else{
            end_bit_button.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Show refresh spinner
     */

    @Override
    public void showLoading() {
        if(refreshLayout != null && !refreshLayout.isRefreshing())
            refreshLayout.setRefreshing(true);
    }

    /**
     * Hide refresh spinner
     */

    @Override
    public void hideLoading() {
        super.hideLoading();
        if(refreshLayout != null && refreshLayout.isRefreshing())
            refreshLayout.setRefreshing(false);
    }

    /***
     * Updates follower recycler view with new followers list
     *
     * @param followers Follower list returned from server
     */

    @Override
    public void updateFollowers(List<Follower> followers) {
        followerAdapter.addItems(followers);
    }

    /***
     * Updates jobs recycler view with new jobs list
     *
     * @param ratios Jobs list returned from server after processing
     */

    @Override
    public void updateJobsList(HashMap<Integer, List<List<String>>> ratios) {
        jobListAdapter.addItems(ratios);
        if (ratios != null && ratios.size() >= 1) {
            setEndBitRatioList(ratios.get(ratios.size())); // get the last element of the hashmap which start from 1
        }
    }

    /**
     * Called when a job is clicked
     * shows the Followers bottom sheet
     *
     * @param ratioNumber Ratio number of job clicked
     * @param lists ratio list
     */

    @Override
    public void onItemClickListener(int ratioNumber, List<List<String>> lists) {
        if(isBottomSheetVisible()){
            return;
        }
        EditJobDialog editJobDialog = EditJobDialog.newInstance();
        editJobDialog.setEditJobDialogResult(new EditJobDialog.EditJobDialogResult() {
            @Override
            public void submit(AssignJobRequest request) {
                assignJobRequest = request;
                followerAdapter.setSelectionMode(true);
                mTabLayout.setVisibility(View.GONE);
                viewPager.setPagingEnabled(false);
                sheetBehavior.setState(ViewPagerBottomSheetBehavior.STATE_EXPANDED);
            }
        });
        editJobDialog.show(getSupportFragmentManager(), lists, markerItem.getItems());
    }

    @OnClick(R.id.end_bit)
    public void  onEndBitButtonClick(){
        EditEndBitJobDialog editEndBitJobDialog = EditEndBitJobDialog.newInstance();
        editEndBitJobDialog.setEditEndBitJobDialogResult(new EditEndBitJobDialog.EditEndBitJobDialogResult() {
            @Override
            public void submit(AssignEndBitJobRequest request) {
                assignEndBitJobRequest = request;
                followerAdapter.setSelectionMode(true);
                mTabLayout.setVisibility(View.GONE);
                viewPager.setPagingEnabled(false);
                sheetBehavior.setState(ViewPagerBottomSheetBehavior.STATE_EXPANDED);
            }
        });
        editEndBitJobDialog.show(getSupportFragmentManager(), endBitRatioList, markerItem);
    }

    /***
     * Called when a follower is clicked
     *
     * Collapses the bottom sheet
     * Assigns the job to the follower
     *
     * @param follower Follower clicked
     */

    @Override
    public void onItemClickListener(Follower follower) {
        if (isBottomSheetVisible()) {
            sheetBehavior.setState(ViewPagerBottomSheetBehavior.STATE_COLLAPSED);
        }
        if(assignJobRequest != null && assignJobRequest.getJobDetails() != null){
            assignJobRequest.setMarkerUnique(markerItem.getMarkerUnique());
            mPresenter.assignJob(assignJobRequest, follower);
        }else{
            if (assignEndBitJobRequest != null && assignEndBitJobRequest.getJobDetails() != null){
                assignEndBitJobRequest.setMarkerId(markerItem.getMarkerUnique());
                mPresenter.assignEndBitJob(assignEndBitJobRequest, follower);
            }else{
                mPresenter.fetchFollowerJob(follower);
            }
        }


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
     * Hides bottom sheet
     */

    private void hideBottomSheet(){
        sheetBehavior.setState(ViewPagerBottomSheetBehavior.STATE_COLLAPSED);
    }

    /***
     * Collapse if bottom sheet is visible
     *
     * Go back if bottom sheet is not collapsed
     */

    @Override
    public void onBackPressed() {
        if (isBottomSheetVisible()) {
            sheetBehavior.setState(ViewPagerBottomSheetBehavior.STATE_COLLAPSED);
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

    @Override
    public void openFollowerDetailActivity(FetchFollowerResponse response) {
        startActivity(FollowerDetailsActivity.getStartIntent(this, response));
    }

    @Override
    public void openFollowerDetailActivityForEndBit(FetchFollowerEndBitJobResponse response){
        startActivity(FollowerDetailsActivity.getStartIntentForEndBit(this, response));
    }

    /**
     * Delete file if exists
     * @param path File path
     * @return Delete response
     */

    @Override
    public boolean deleteFile(String path) {
        File fileToDelete = new File(path);
        if(fileToDelete.exists()){
            return fileToDelete.delete();
        } else return false;
    }

    @Override
    public void updateAssignedJobsList(List<OcLay> ocLays) {
        assignedJobListAdapter.addItems(ocLays);
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
     * Call API to fetch countries before launching bundle split dialog
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void bundleSplitDialog(OcLay job) {
        mPresenter.getCountriesList(job);
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