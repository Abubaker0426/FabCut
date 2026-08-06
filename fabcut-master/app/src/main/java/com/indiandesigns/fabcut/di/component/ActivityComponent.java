package com.indiandesigns.fabcut.di.component;

import com.indiandesigns.fabcut.di.PerActivity;
import com.indiandesigns.fabcut.di.module.ActivityModule;
import com.indiandesigns.fabcut.ui.follower.FollowerActivity;
import com.indiandesigns.fabcut.ui.follower.job_detail.EndBitJobDetailFragment;
import com.indiandesigns.fabcut.ui.follower.job_detail.JobDetailFragment;
import com.indiandesigns.fabcut.ui.leader.LeaderActivity;
import com.indiandesigns.fabcut.ui.leader.follower.details.FollowerDetailsActivity;
import com.indiandesigns.fabcut.ui.leader.jobs.LeaderJobsActivity;
import com.indiandesigns.fabcut.ui.leader.jobs.edit.EditBundleDialog;
import com.indiandesigns.fabcut.ui.leader.jobs.edit.EditJobDialog;
import com.indiandesigns.fabcut.ui.leader.jobs.edit.EditEndBitJobDialog;
import com.indiandesigns.fabcut.ui.leader.jobs.parts.BundlePartsActivity;
import com.indiandesigns.fabcut.ui.main.MainActivity;
import com.indiandesigns.fabcut.ui.main.location_picker_dialog.LocationPickerDialog;
import com.indiandesigns.fabcut.ui.scanner.ScannerActivity;

import dagger.Component;

/**
 * Enables selected modules and uses them for performing dependency injection
 * Scope defined by {@link PerActivity}
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity activity);

    void inject(LocationPickerDialog locationPickerDialog);

    void inject(EditJobDialog editJobDialog);

    void inject(EditEndBitJobDialog editEndBitJobDialog);

    void inject(LeaderJobsActivity activity);

    void inject(LeaderActivity activity);

    void inject(FollowerActivity activity);

    void inject(ScannerActivity activity);

    void inject(FollowerDetailsActivity activity);

    void inject(JobDetailFragment fragment);

    void inject(EndBitJobDetailFragment fragment);

    void inject(EditBundleDialog editBundleDialog);

    void inject(BundlePartsActivity bundlePartsActivity);
}
