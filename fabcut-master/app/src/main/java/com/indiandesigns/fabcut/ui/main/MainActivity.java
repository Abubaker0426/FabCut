package com.indiandesigns.fabcut.ui.main;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import android.content.Intent;
import android.content.Context;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.storage.CredentialsManager;
import com.auth0.android.authentication.storage.SharedPreferencesStorage;
import com.auth0.android.authentication.storage.Storage;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.WebAuthProvider;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.indiandesigns.fabcut.BuildConfig;
import com.indiandesigns.fabcut.R;
import com.indiandesigns.fabcut.data.network.enums.Role;
import com.indiandesigns.fabcut.ui.base.BaseActivity;
import com.indiandesigns.fabcut.ui.follower.FollowerActivity;
import com.indiandesigns.fabcut.ui.leader.LeaderActivity;
import com.indiandesigns.fabcut.ui.main.location_picker_dialog.LocationPickerDialog;
import com.indiandesigns.fabcut.utils.AppUtils;
import com.indiandesigns.fabcut.utils.CommonUtils;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements MainMvpView {

    private String TAG = "MainActivity";

    @Inject
    MainPresenter<MainMvpView> mPresenter;

    @BindView(R.id.register_container)
    LinearLayout registerContainer;

    @BindView(R.id.unregister)
    Button unregisterButton;

    @BindView(R.id.role)
    RadioGroup roleRadioGroup;

    @BindView(R.id.table_number)
    EditText tableNumberEditText;

    CredentialsManager manager;
    Auth0 auth0Account;

    private String[] PERMISSIONSForAndroid13 = {
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
    };

    private String[] PERMISSIONSForAndroidBelow13 = {
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    private final int LOC_PERM_CODE = 125;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private AppUpdateManager mAppUpdateManager;
    public final int RC_APP_UPDATE = 444;

    /***
     * Returns an intent for launching the activity
     * @param context Context
     * @return Intent for activity
     */

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));
        mPresenter.onAttach(MainActivity.this);
        mAppUpdateManager = AppUpdateManagerFactory.create(this);
        setUp();
    }

    @Override
    protected void onDestroy() {
        if(fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
        mPresenter.onDetach();
        super.onDestroy();
    }

    /**
     * This function is used to initialize the setup for the app
     * Currently does the following:
     * - Request location permission
     * - Initialize Auth0 and CredentialsManager
     * - Start the auth flow by calling startAuthFlow of {@link MainPresenter}
     */

    @AfterPermissionGranted(LOC_PERM_CODE)
    @Override
    protected void setUp() {
     String[] PERMISSIONS= new String[0];
        double releaseVersion= Double.parseDouble(android.os.Build.VERSION.RELEASE);
     if(releaseVersion >= 13){
         PERMISSIONS = PERMISSIONSForAndroid13;
     }else{
         PERMISSIONS = PERMISSIONSForAndroidBelow13;
     }
        if (EasyPermissions.hasPermissions(this, PERMISSIONS)) {
            auth0Account = new Auth0(this);
            auth0Account.setOIDCConformant(true);

            AuthenticationAPIClient apiClient = new AuthenticationAPIClient(auth0Account);
            Storage sharedStoragePreferences = new SharedPreferencesStorage(this);
            manager = new CredentialsManager(apiClient, sharedStoragePreferences);

            mPresenter.setCredentialsManager(manager);

            roleRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                    tableNumberEditText.setText("0");
                    RadioButton checkedRadioButton = (RadioButton)radioGroup.findViewById(checkedId);
                    String tag = checkedRadioButton.getTag().toString().toLowerCase();
                    boolean isChecked = checkedRadioButton.isChecked();
                    if (isChecked)
                    {
                        if(tag.equals(Role.LEADER.getValue())){
                            tableNumberEditText.setVisibility(View.GONE);
                        } else if(tag.equals(Role.FOLLOWER.getValue())){
                            tableNumberEditText.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });

            startAuthFlow();

        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_ask),
                    LOC_PERM_CODE, PERMISSIONS);
        }
    }

    @Override
    public void startAuthFlow() {
        mPresenter.startAuthFlow(auth0Account);
    }

    /**
     * @return Package name of the app
     */

    @Override
    public String getCurrentPackageName() {
        return MainActivity.this.getPackageName();
    }

    /**
     * Calls openPlayStoreForApp method of {@link AppUtils}
     * to open the Play Store listing page
     */

    @Override
    public void openPlayStore() {
        AppUtils.openPlayStoreForApp(this);
    }

    /**
     * @return Current version
     * @throws PackageManager.NameNotFoundException If package name is not found
     */

    @Override
    public String getCurrentVersion() throws PackageManager.NameNotFoundException {
        return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
    }

    /**
     * Start the update flow
     *
     * @param appUpdateInfo App Update Info from @mAppUpdateManager
     * @throws IntentSender.SendIntentException
     */

    @Override
    public void startUpdateFlow(AppUpdateInfo appUpdateInfo) throws IntentSender.SendIntentException {
        mAppUpdateManager.startUpdateFlowForResult(
                appUpdateInfo, AppUpdateType.IMMEDIATE, this, RC_APP_UPDATE);

    }

    /**
     * Show error if update download failed
     *
     * @param requestCode Request code sent while starting activity for result
     * @param resultCode Result code for request
     * @param data Intent date sent with the result
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_APP_UPDATE) {
            if (resultCode != RESULT_OK) {
                onError(R.string.update_download_failed);
            }
        }
    }

    /**
     * Get update information and start update flow
     * if update is available and app is not in debug mode
     */

    @Override
    protected void onResume() {
        super.onResume();
        if (!BuildConfig.DEBUG) {
            mPresenter.checkForUpdate(mAppUpdateManager);
        }
    }

    @Override
    public void startLocationUpdates() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationUpdates();
    }

    private void getLocationUpdates(){
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    /**
     * Callback for location updates
     * <p>
     * Validates the location once location is fetched
     */

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if(locationResult.getLocations().size() > 0){
                Location location = locationResult.getLocations().get(0);
                mPresenter.doValidateLocationCall(location);
            } else {
                hideLoading();
                onError(R.string.location_error);
            }
        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
            if(!locationAvailability.isLocationAvailable()){
                hideLoading();
                onError(R.string.location_error);
            }
        }
    };

    /**
     * Calls the Auth0 API to start the authentication flow if no credentials are already stored
     * @param auth0Account The Auth0 account object
     * @param authCallback The callback function which will be called by the Auth0 service
     */
    public void startAuth(Auth0 auth0Account, AuthCallback authCallback) {
        WebAuthProvider.login(auth0Account)
                .withAudience(getString(R.string.auth0_audience))
                .withScheme(getString(R.string.auth0_scheme))
                .start(this, authCallback);
    }

    /**
     * Calls EasyPermissions to process permission result
     *
     * @param requestCode Request code for which the permission were asked
     * @param permissions Permissions which were asked
     * @param grantResults Results for each asked permission
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * Function that shows the {@link LocationPickerDialog}
     * @param locations The list of possible locations returned from the API
     */
    public void showLocationPickerDialog(ArrayList<String> locations) {
        LocationPickerDialog.newInstance().show(getSupportFragmentManager(), locations);
    }

    /***
     * Called when location picker is dismissed
     * @param tag TAG for Location Picker dialog
     */

    @Override
    public void onFragmentDetached(String tag) {
        super.onFragmentDetached(tag);
        mPresenter.isDeviceRegistered();
    }

    /**
     * Gets the Device ID from @{@link CommonUtils}
     *
     * @return Device ID
     */

    @Override
    public String getDeviceIdString() {
        return CommonUtils.getDeviceId(this);
    }

    /**
     * Shows the Register Container
     */

    @Override
    public void showRegisterButton() {
        registerContainer.setVisibility(View.VISIBLE);
    }

    /**
     * Shows the Unregister Container
     */

    @Override
    public void showUnRegisterButton() {
        unregisterButton.setVisibility(View.VISIBLE);
    }

    /**
     * Hides the Register Container
     */

    @Override
    public void hideRegisterButton() {
        registerContainer.setVisibility(View.GONE);
    }

    /**
     * Hides the Unregister Container
     */

    @Override
    public void hideUnRegisterButton() {
        unregisterButton.setVisibility(View.GONE);
    }

    /***
     * Registers the device when register button is clicked
     * @param view Clicked view
     */

    @OnClick(R.id.register)
    public void registerDevice(View view) {
        String role = ((RadioButton) findViewById(roleRadioGroup.getCheckedRadioButtonId())).getText().toString();
        String tableNumber = tableNumberEditText.getText().toString();
        if(tableNumber.isEmpty()){
            hideKeyboard();
            onError(R.string.table_number_error);
            return;
        }
        mPresenter.register(role, Integer.valueOf(tableNumber));
    }

    /***
     * Unregisters the device when unregister button is clicked
     * @param view Clicked view
     */

    @OnClick(R.id.unregister)
    public void unregisterDevice(View view) {
        mPresenter.unregister();
    }

    /***
     * Calls and checks if the location is valid
     */

    @Override
    public void isLocationValid() {
        runOnUiThread(new Runnable() {
            public void run() {
                mPresenter.isLocationValid();
            }
        });
    }

    /***
     * Open activity based on role
     * @param role Role of the device
     */

    @Override
    public void showUiBasedOnRole(String role) {
        if(role.equalsIgnoreCase(Role.LEADER.getValue())){
            startActivity(LeaderActivity.getStartIntent(this));
            finish();
        } else if (role.equalsIgnoreCase(Role.FOLLOWER.getValue())) {
            startActivity(FollowerActivity.getStartIntent(this));
            finish();
        }
    }
}