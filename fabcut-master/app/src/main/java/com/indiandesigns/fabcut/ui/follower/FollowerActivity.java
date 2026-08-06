package com.indiandesigns.fabcut.ui.follower;

import android.os.Bundle;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.indiandesigns.fabcut.R;
import com.indiandesigns.fabcut.data.network.enums.JobType;
import com.indiandesigns.fabcut.data.network.model.FetchFollowerEndBitJobResponse;
import com.indiandesigns.fabcut.data.network.model.FetchFollowerResponse;
import com.indiandesigns.fabcut.ui.base.BaseActivity;
import com.indiandesigns.fabcut.ui.common.NonSwipeableViewPager;
import com.indiandesigns.fabcut.ui.follower.job_detail.EndBitJobDetailFragment;
import com.indiandesigns.fabcut.ui.follower.job_detail.EndBitJobDetailPagerAdapter;
import com.indiandesigns.fabcut.ui.follower.job_detail.JobDetailFragment;
import com.indiandesigns.fabcut.ui.follower.job_detail.JobDetailPagerAdapter;
import com.indiandesigns.fabcut.utils.CommonUtils;

import java.util.List;

public class FollowerActivity extends BaseActivity implements FollowerMvpView {

    @Inject
    FollowerPresenter<FollowerMvpView> mPresenter;

    @BindView(R.id.submit)
    ExtendedFloatingActionButton submitButton;

    @BindView(R.id.no_job_layout)
    LinearLayout noJobLayout;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.viewpager)
    NonSwipeableViewPager mViewPager;

    @Inject
    JobDetailPagerAdapter adapter;

    @Inject
    EndBitJobDetailPagerAdapter endBitAdapter;

    @BindView(R.id.tabs)
    TabLayout mTabs;

    private Integer jobId;

    private JobType jobType;

    /***
     * Returns an intent for launching the activity
     * @param context Context
     * @return Intent for activity
     */

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, FollowerActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);

        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));
        mPresenter.onAttach(FollowerActivity.this);

        setUp();
    }

    /**
     * Add a new view for each job detail object
     * @param fragment Job detail fragment instance
     * @param title Item code
     */

    private void addTab(JobDetailFragment fragment, String title) {
        adapter.addFragment(fragment, title);
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDetach();
        super.onDestroy();
    }

    /**
     * Fetches the unique device id
     * @return DeviceId
     */

    @Override
    public String getDeviceIdString() {
        return CommonUtils.getDeviceId(this);
    }

    private void notifyAdapter() {
        adapter.notifyDataSetChanged();
        mViewPager.setOffscreenPageLimit(adapter.getCount());
        mTabs.setupWithViewPager(mViewPager);
    }

    private void notifyEndBitAdapter(){
        endBitAdapter.notifyDataSetChanged();
        mViewPager.setOffscreenPageLimit(endBitAdapter.getCount());
        mTabs.setupWithViewPager(mViewPager);
    }

    /**
     * Create a view pager from the response
     * @param jobId Job Id
     * @param layLength Lay length
     * @param ocNumber Oc Number
     * @param jobDetails Job details list
     */

    @Override
    public void updateViewPager(Integer jobId, Double layLength, String ocNumber, List<FetchFollowerResponse.JobDetail> jobDetails) {
        this.jobId = jobId;
        jobType = JobType.NORMAL;
        mViewPager.setAdapter(adapter);
        for(FetchFollowerResponse.JobDetail jobDetail: jobDetails) {
            addTab(JobDetailFragment.newInstance(ocNumber, layLength, jobId, jobDetail), jobDetail.getItemCode());
        }
        notifyAdapter();
    }

    /**
     * Create a view pager for end bits from the response
     * @param jobId End bit job Id
     * @param ocNumber Oc Number
     * @param jobDetails Job details list
     */

    @Override
    public void updateViewEndBitPager(Integer jobId, String ocNumber, String itemCode, String itemDesc, List<FetchFollowerEndBitJobResponse.JobDetail> jobDetails){
        this.jobId = jobId;
        jobType = JobType.END_BIT;
        mViewPager.setAdapter(endBitAdapter);
        for(FetchFollowerEndBitJobResponse.JobDetail jobDetail: jobDetails) {
            addEndBitTab(EndBitJobDetailFragment.newInstance(ocNumber, jobDetail.getLayLength(), jobId, itemCode, itemDesc, jobDetail), jobDetail.getPartName());
        }
        notifyEndBitAdapter();
    }


    /**
     * Add a new view for each end bit job detail object
     * @param fragment End bit job detail fragment instance
     * @param title Item code
     */

    private void addEndBitTab(EndBitJobDetailFragment fragment, String title) {
        endBitAdapter.addFragment(fragment, title);
    }

    /**
     * Sets up the barcode recycler view
     * Fetches the follower's job details
     */

    @Override
    protected void setUp() {
        jobType = JobType.NORMAL;
        mPresenter.onViewPrepared();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    /**
     * Checks is all actual plies have values and submit the job
     * Else shows an error
     * @param view View clicked
     */

    @OnClick(R.id.submit)
    public void submit(View view){
        mPresenter.validatedAllPlies(jobId, jobType);
    }

    /**
     * Hides the no job TextView and show the Main layout
     * and action buttons
     */

    @Override
    public void showMainLayout() {
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        submitButton.setVisibility(View.VISIBLE);
        noJobLayout.setVisibility(View.GONE);
    }

    /**
     * Shows the no job TextView and hides the Main layout
     * And action buttons
     */

    @Override
    public void hideMainLayout() {
        swipeRefreshLayout.setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);
        noJobLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Clear adapters and fetch job data
     */

    @OnClick(R.id.refresh)
    public void refresh(){
        adapter.clear();
        endBitAdapter.clear();
        mPresenter.onViewPrepared();
    }

    @Override
    public void hideLoading() {
        super.hideLoading();
        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}