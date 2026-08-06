package com.indiandesigns.fabcut.di.module;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.indiandesigns.fabcut.data.network.model.Follower;
import com.indiandesigns.fabcut.data.network.model.ScanBarcode;
import com.indiandesigns.fabcut.di.ActivityContext;
import com.indiandesigns.fabcut.di.PerActivity;
import com.indiandesigns.fabcut.ui.common.ListAdapter;
import com.indiandesigns.fabcut.ui.common.PrintManager;
import com.indiandesigns.fabcut.ui.follower.BarcodeAdapter;
import com.indiandesigns.fabcut.ui.follower.FollowerMvpPresenter;
import com.indiandesigns.fabcut.ui.follower.FollowerMvpView;
import com.indiandesigns.fabcut.ui.follower.FollowerPresenter;
import com.indiandesigns.fabcut.ui.follower.job_detail.EditEndBitJobDetailMvpPresenter;
import com.indiandesigns.fabcut.ui.follower.job_detail.EndBitJobDetailMvpView;
import com.indiandesigns.fabcut.ui.follower.job_detail.EndBitJobDetailPagerAdapter;
import com.indiandesigns.fabcut.ui.follower.job_detail.EndBitJobDetailPresenter;
import com.indiandesigns.fabcut.ui.follower.job_detail.JobDetailMvpPresenter;
import com.indiandesigns.fabcut.ui.follower.job_detail.JobDetailMvpView;
import com.indiandesigns.fabcut.ui.follower.job_detail.JobDetailPagerAdapter;
import com.indiandesigns.fabcut.ui.follower.job_detail.JobDetailPresenter;
import com.indiandesigns.fabcut.ui.leader.LeaderMvpPresenter;
import com.indiandesigns.fabcut.ui.leader.LeaderMvpView;
import com.indiandesigns.fabcut.ui.leader.LeaderPresenter;
import com.indiandesigns.fabcut.ui.leader.follower.details.FollowerDetailsMvpPresenter;
import com.indiandesigns.fabcut.ui.leader.follower.details.FollowerDetailsMvpView;
import com.indiandesigns.fabcut.ui.leader.follower.details.FollowerDetailsPresenter;
import com.indiandesigns.fabcut.ui.leader.jobs.FollowerAdapter;
import com.indiandesigns.fabcut.ui.leader.jobs.JobListAdapter;
import com.indiandesigns.fabcut.ui.leader.jobs.LeaderJobsMvpPresenter;
import com.indiandesigns.fabcut.ui.leader.jobs.LeaderJobsMvpView;
import com.indiandesigns.fabcut.ui.leader.jobs.LeaderJobsPresenter;
import com.indiandesigns.fabcut.ui.leader.jobs.edit.EditBundleDialogMvpPresenter;
import com.indiandesigns.fabcut.ui.leader.jobs.edit.EditBundleDialogMvpView;
import com.indiandesigns.fabcut.ui.leader.jobs.edit.EditBundleDialogPresenter;
import com.indiandesigns.fabcut.ui.leader.jobs.edit.EditEndBitJobDialog;
import com.indiandesigns.fabcut.ui.leader.jobs.edit.EditJobDialogMvpPresenter;
import com.indiandesigns.fabcut.ui.leader.jobs.edit.EditJobDialogMvpView;
import com.indiandesigns.fabcut.ui.leader.jobs.edit.EditJobDialogPresenter;
import com.indiandesigns.fabcut.ui.leader.jobs.edit.EditEndBitJobDialogMvpPresenter;
import com.indiandesigns.fabcut.ui.leader.jobs.edit.EditEndBitJobDialogMvpView;
import com.indiandesigns.fabcut.ui.leader.jobs.edit.EditEndBitJobDialogPresenter;
import com.indiandesigns.fabcut.ui.common.AssignedJobListAdapter;
import com.indiandesigns.fabcut.ui.leader.MarkerAdapter;
import com.indiandesigns.fabcut.ui.main.MainMvpPresenter;
import com.indiandesigns.fabcut.ui.main.MainMvpView;
import com.indiandesigns.fabcut.ui.main.MainPresenter;
import com.indiandesigns.fabcut.ui.main.location_picker_dialog.LocationPickerDialogMvpPresenter;
import com.indiandesigns.fabcut.ui.main.location_picker_dialog.LocationPickerDialogMvpView;
import com.indiandesigns.fabcut.ui.main.location_picker_dialog.LocationPickerDialogPresenter;
import com.indiandesigns.fabcut.ui.scanner.ScannerMvpPresenter;
import com.indiandesigns.fabcut.ui.scanner.ScannerMvpView;
import com.indiandesigns.fabcut.ui.scanner.ScannerPresenter;
import com.indiandesigns.fabcut.utils.rx.AppSchedulerProvider;
import com.indiandesigns.fabcut.utils.rx.SchedulerProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Define classes and methods which provide dependencies
 */

@Module
public class ActivityModule {

    private AppCompatActivity mActivity;

    public ActivityModule(AppCompatActivity activity) {
        this.mActivity = activity;
    }

    @Provides
    @ActivityContext
    Context provideContext() {
        return mActivity;
    }

    @Provides
    AppCompatActivity provideActivity() {
        return mActivity;
    }

    @Provides
    CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    SchedulerProvider provideSchedulerProvider() {
        return new AppSchedulerProvider();
    }

    @Provides
    @PerActivity
    MainMvpPresenter<MainMvpView> provideMainPresenter(
            MainPresenter<MainMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    FollowerDetailsMvpPresenter<FollowerDetailsMvpView> provideFollowerDetailsPresenter(
            FollowerDetailsPresenter<FollowerDetailsMvpView> presenter) {
        return presenter;
    }


    @Provides
    @PerActivity
    LeaderMvpPresenter<LeaderMvpView> providesLeaderPresenter(
            LeaderPresenter<LeaderMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    FollowerMvpPresenter<FollowerMvpView> providesFollowerPresenter(
            FollowerPresenter<FollowerMvpView> presenter) {
        return presenter;
    }

    @Provides
    LocationPickerDialogMvpPresenter<LocationPickerDialogMvpView> provideLocationPickerPresenter(
                    LocationPickerDialogPresenter<LocationPickerDialogMvpView> presenter) {
        return presenter;
    }

    @Provides
    EditJobDialogMvpPresenter<EditJobDialogMvpView> provideEditJobPresenter(
            EditJobDialogPresenter<EditJobDialogMvpView> presenter) {
        return presenter;
    }

    @Provides
    EditEndBitJobDialogMvpPresenter<EditEndBitJobDialogMvpView> provideEditEndBitJobPresenter(
            EditEndBitJobDialogPresenter<EditEndBitJobDialogMvpView> presenter) {
        return presenter;
    }

    @Provides
    EditBundleDialogMvpPresenter<EditBundleDialogMvpView> provideEditBundleDialogMvpPresenter(
            EditBundleDialogPresenter<EditBundleDialogMvpView> presenter) {
        return presenter;
    }

    @Provides
    LeaderJobsMvpPresenter<LeaderJobsMvpView> provideLeaderJobsPresenter(
            LeaderJobsPresenter<LeaderJobsMvpView> presenter) {
        return presenter;
    }

    @Provides
    ScannerMvpPresenter<ScannerMvpView> provideScannerPresenter(
            ScannerPresenter<ScannerMvpView> presenter) {
        return presenter;
    }

    @Provides
    JobDetailMvpPresenter<JobDetailMvpView> provideJobDetailPresenter(
            JobDetailPresenter<JobDetailMvpView> presenter) {
        return presenter;
    }

    @Provides
    EditEndBitJobDetailMvpPresenter<EndBitJobDetailMvpView> provideEndBitJobDetailPresenter(
            EndBitJobDetailPresenter<EndBitJobDetailMvpView> presenter) {
        return presenter;
    }

    @Provides
    LinearLayoutManager provideLinearLayoutManager(AppCompatActivity activity) {
        return new LinearLayoutManager(activity);
    }

    @Provides
    JobListAdapter provideJobListAdapter(){
        return new JobListAdapter(new HashMap<Integer, List<List<String>>>());
    }

    @Provides
    AssignedJobListAdapter provideAssignedJobListAdapter(){
        return new AssignedJobListAdapter(new ArrayList<>());
    }

    @Provides
    FollowerAdapter provideFollowerAdapter(){
        return new FollowerAdapter(new ArrayList<Follower>());
    }

    @Provides
    BarcodeAdapter provideBarcodeAdapter(){
        return new BarcodeAdapter(new ArrayList<ScanBarcode>());
    }

    @Provides
    MarkerAdapter provideMarkerAdapter(){
        return new MarkerAdapter(new ArrayList<>());
    }

    @Provides
    PrintManager providePrintManager(){
        return new PrintManager();
    }

    @Provides
    ListAdapter provideListAdapter() {
        return new ListAdapter(new ArrayList<String>());
    }

    @Provides
    JobDetailPagerAdapter provideJobDetailPagerAdapter(AppCompatActivity activity){
        return new JobDetailPagerAdapter(activity.getSupportFragmentManager());
    }

    @Provides
    EndBitJobDetailPagerAdapter provideEndBitJobDetailPagerAdapter(AppCompatActivity activity){
        return new EndBitJobDetailPagerAdapter(activity.getSupportFragmentManager());
    }
}
