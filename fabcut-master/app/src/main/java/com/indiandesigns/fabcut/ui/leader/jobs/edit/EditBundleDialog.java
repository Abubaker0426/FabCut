package com.indiandesigns.fabcut.ui.leader.jobs.edit;

import static android.content.Context.RECEIVER_EXPORTED;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;

import com.indiandesigns.fabcut.R;
import com.indiandesigns.fabcut.data.network.model.FetchPartsDetails;
import com.indiandesigns.fabcut.data.network.model.FetchPartsResponse;
import com.indiandesigns.fabcut.data.network.model.SizeDetail;
import com.indiandesigns.fabcut.di.component.ActivityComponent;
import com.indiandesigns.fabcut.ui.base.BaseDialog;
import com.indiandesigns.fabcut.ui.common.CountriesAdapter;
import com.indiandesigns.fabcut.ui.common.PrintManager;
import com.indiandesigns.fabcut.ui.common.model.BundleDetail;
import com.indiandesigns.fabcut.ui.common.model.OcLay;
import com.indiandesigns.fabcut.ui.common.model.OcLayDetail;
import com.indiandesigns.fabcut.utils.AppConstants;
import com.zebra.sdk.printer.discovery.DiscoveredPrinter;
import com.zebra.sdk.printer.discovery.DiscoveredPrinterUsb;
import com.zebra.sdk.printer.discovery.DiscoveryHandler;
import com.zebra.sdk.printer.discovery.UsbDiscoverer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditBundleDialog extends BaseDialog implements EditBundleDialogMvpView, PrintManager.PrintManagerListener{
    private static final String TAG = "EditBundleDialog";
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    @Inject
    EditBundleDialogMvpPresenter<EditBundleDialogMvpView> mPresenter;

    @Inject
    PrintManager printManager;

    @BindView(R.id.bundle_row_list)
    LinearLayout bundleRowContainer;

    @BindView(R.id.bundle_add)
    Button addBundleRowButton;

    @BindView(R.id.bundle_print)
    Button bundlePrintButton;

    @BindView(R.id.allowed_quantity)
    TextView allowedQuantityTextview;

    private OcLay ocLay;
    private Activity activity;
    private DiscoveredPrinterUsb discoveredPrinterUsb;
    private CountriesAdapter countriesAdapter;
    private List<OcLayDetail> augmentedSizeList;
    private List<SizeDetail> sizeDetailList;

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            mPresenter.fetchParts(ocLay);
                        }
                    }else{
                        showError(R.string.zebra_printer_connection_denied);
                    }
                }
            }
        }
    };

    /**
     * Gets an new instance of the dialog
     * @return EditBundleDialog instance
     */

    public static EditBundleDialog newInstance() {
        EditBundleDialog fragment = new EditBundleDialog();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_bundle_dialog, container, false);
        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            setUnBinder(ButterKnife.bind(this, view));
            mPresenter.onAttach(this);
        }
        addBundleRowButton.setOnClickListener(addBundleRowButtonClickListener);

        registerUsbBroadcastReceiver();

        return view;
    }

    private void registerUsbBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter(ACTION_USB_PERMISSION);
//        this.activity.registerReceiver(usbReceiver, intentFilter);
        this.activity.registerReceiver(usbReceiver, intentFilter, RECEIVER_EXPORTED);
    }

    private View.OnClickListener addBundleRowButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addBundleRow();
        }
    };

    private void addBundleRow() {
        View bundleRowView = getLayoutInflater().inflate(R.layout.edit_bundle_row_item, null, false);
        ImageView removeBundleRow = (ImageView)bundleRowView.findViewById(R.id.bundle_item_delete);
        populateCountriesDropDown(bundleRowView);
        removeBundleRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeBundleView(bundleRowView);
            }
        });
        bundleRowContainer.addView(bundleRowView);
    }
    /***
     * Populates the spinner with countries
     * @param bundleRowView the view of bundle row
     */

    private void populateCountriesDropDown(View bundleRowView) {
        Spinner countriesDropDown = (Spinner)bundleRowView.findViewById(R.id.country_drop_down);
        countriesDropDown.setAdapter(countriesAdapter);
        countriesDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               TextView countryTextview = bundleRowView.findViewById(R.id.country_drop_down_text);
               String countryItemText = countriesAdapter.getItemData(position);
                countryTextview.setText(countryItemText);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

    }


    private void removeBundleView(View bundleRowView) {
        bundleRowContainer.removeView(bundleRowView);
    }

    @Override
    protected void setUp(View view) {
        int allowedQuantity = mPresenter.getBundleSplitAllowedQuantity(ocLay.getSizeList());
        printManager.setup(this.activity, this);
        allowedQuantityTextview.setText(String.valueOf(allowedQuantity));
        addBundleRow();
    }

    @Override
    public void dismissDialog() {
        super.dismissDialog(TAG);
    }

    @Override
    public void onDestroy() {
        this.activity.unregisterReceiver(usbReceiver);
        super.onDestroy();
    }

    /**
     * Create barcodes from parts and job information
     * Invoke print method
     * @param ocLay Job info
     * @param response Parts info
     */

    @Override
    public void createBarcodes(OcLay ocLay, FetchPartsResponse response, String fileName) {
        ArrayList<BundleDetail> bundleDetails = getBundleInputList();
        if(bundleDetails.size() > 1){

            List<FetchPartsDetails> partsList = response.getParts();
            if (partsList.size() == 0) {
                onError(R.string.no_parts_error);
                this.dismissDialog();
                return;
            }

        boolean validateBundlePartsResult = mPresenter.validateBundlePartsDetailList(response.getParts());
        if(!validateBundlePartsResult){
            onError(R.string.edit_bundle_parts_validate_error);
            this.dismissDialog();
            return;
        }}

        List<FetchPartsDetails> partsList = response.getParts();
        this.augmentedSizeList = mPresenter.addAugmentedSizeToOcLayDetailList(ocLay.getSizeList(),  bundleDetails);
        this.sizeDetailList = mPresenter.createSizeDetailsList(this.augmentedSizeList, partsList);
        mPresenter.saveParts(ocLay, this.sizeDetailList, response, fileName);
    }

    @Override
    public void printBarcodes(OcLay ocLay, FetchPartsResponse response, String filename) {
        printManager.print(ocLay, response, filename, discoveredPrinterUsb, this.augmentedSizeList, this.sizeDetailList);
    }

    /**
     * This method will get the quantity & country for each bundle
     * @return
     */
    private ArrayList<BundleDetail> getBundleInputList() {
        ArrayList<BundleDetail> countryAndQuantityList = new ArrayList<>();
        for(int i = 0; i < bundleRowContainer.getChildCount(); i++) {
            View bundleRowView = bundleRowContainer.getChildAt(i);
            EditText quantityTextEditText = bundleRowView.findViewById(R.id.edit_bundle_quantity);
            EditText countryTextEditText = bundleRowView.findViewById(R.id.country_drop_down_text);
            String quantityText = quantityTextEditText.getText().toString();
            String countryText = countryTextEditText.getText().toString();
            if (isInputTextValid(quantityText, countryText)) {
                BundleDetail bundleDetail = mPresenter.getBundleDetail(Integer.parseInt(quantityText), countryText);
                countryAndQuantityList.add(bundleDetail);
            }
        }
        return countryAndQuantityList;
    }

    /**
     * Initializes the data for the popup and
     * Shows the dialog
     * @param fragmentManager Fragment Manager
     * @param job Lay job details
     * @param activity Activity that method called
     */

    public void show(FragmentManager fragmentManager,  OcLay job, ArrayList<String> countries,  Activity activity, Context context) {
        this.activity = activity;
        this.ocLay = job;
        countriesAdapter = new CountriesAdapter(context, R.layout.spinner_row_select_country, countries);
        super.show(fragmentManager, TAG);
    }

    @Override
    public void onDestroyView() {
        mPresenter.onDetach();
        super.onDestroyView();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @OnClick(R.id.bundle_print)
    public void printBundleStickers() {
        InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);

        if (!checkIfEntriesAreValid()) {
            showError(R.string.bundle_input_blank_validation_error);
            return;
        }

        if (!checkQuantityTotalAgainstAllowedQuantity()) {
            showError(R.string.bundle_allowed_quantity_mismatch_error);
            return;
        }
        disableDialogButtons();
        checkForZebraPrinter();
    }

    /**
     * This function is responsible to disable edit bundle dialog buttons.
     */
    private void disableDialogButtons(){
        addBundleRowButton.setEnabled(false);
        bundlePrintButton.setEnabled(false);
    }

    /**
     * A utility method that will determine whether the text entered in the quantity field and country selected
     * for split bundle quantities is valid or not.
     * @param quantityText The text entered in the split quantity field
     * @param country The country selected for split quantity field
     * @return
     */
    private boolean isInputTextValid(String quantityText, String country) {
        if (quantityText.equals("") || Integer.parseInt(quantityText) == 0 || TextUtils.isEmpty(country)) {
            return false;
        }
        return true;
    }

    /**
     * Checks if the inputs by the user are valid for split bundle quantities
     * @return
     */
    private boolean checkIfEntriesAreValid() {
        for (int i = 0; i < bundleRowContainer.getChildCount(); i++) {
            View bundleRowView = bundleRowContainer.getChildAt(i);
            EditText quantityTextEditText = bundleRowView.findViewById(R.id.edit_bundle_quantity);
            EditText countryTextEditText = bundleRowView.findViewById(R.id.country_drop_down_text);
            String quantityText = quantityTextEditText.getText().toString();
            String countryText = countryTextEditText.getText().toString();
            if (!isInputTextValid(quantityText, countryText)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the quantity entered is equal to the total quantity
     * @return
     */
    private boolean checkQuantityTotalAgainstAllowedQuantity() {
        int quantityTotal = 0;
        for(int i = 0; i < bundleRowContainer.getChildCount(); i++) {
            View bundleRowView = bundleRowContainer.getChildAt(i);
            EditText quantityTextEditText = bundleRowView.findViewById(R.id.edit_bundle_quantity);
            EditText countryTextEditText = bundleRowView.findViewById(R.id.country_drop_down_text);
            String quantityText = quantityTextEditText.getText().toString();
            String countryText = countryTextEditText.getText().toString();
            if (isInputTextValid(quantityText, countryText)) {
                quantityTotal += Integer.parseInt(quantityText);
            }
        }
        int allowedQuantity = mPresenter.getBundleSplitAllowedQuantity(ocLay.getSizeList());
        return quantityTotal == allowedQuantity;
    }

    @Override
    public void showError(int messageId) {
        hideLoading();
        onError(messageId);
    }

    /**
     * This method will check if there is a Zebra printer connected via USB
     * To find more documentation for this method, look at
     * https://link-os.github.io/android/v2.9.2275/documentation/com/zebra/sdk/printer/discovery/UsbDiscoverer.html
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void checkForZebraPrinter() {
        UsbManager usbManager = (UsbManager) this.activity.getSystemService(Context.USB_SERVICE);

        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        for (UsbDevice value : deviceList.values()) {
            String manufacturerName = value.getManufacturerName();
            if (manufacturerName.equals(AppConstants.PRINTER_MANUFACTURER_NAME)) {
                requestConnectionToZebraPrinter();
                return;
            }
        }
        showMessage("No Zebra printer was detected");
        mPresenter.fetchParts(ocLay);
    }

    /**
     * This method will make an attempt to connect to the printer connected via USB
     * The discoverer will run on a separate thread
     */
    private void requestConnectionToZebraPrinter() {
        UsbManager usbManager = (UsbManager) this.activity.getSystemService(Context.USB_SERVICE);
//        PendingIntent permissionIntent = PendingIntent.getBroadcast(getContext(), 0, new Intent(ACTION_USB_PERMISSION), 0,);
        PendingIntent permissionIntent = PendingIntent.getBroadcast(getContext(), 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_ALLOW_UNSAFE_IMPLICIT_INTENT | PendingIntent.FLAG_MUTABLE );

        new Thread(new Runnable() {
            @Override
            public void run() {
                UsbDiscoverer.findPrinters(getContext(), new DiscoveryHandler() {
                    @Override
                    public void foundPrinter(DiscoveredPrinter discoveredPrinter) {
                        discoveredPrinterUsb = (DiscoveredPrinterUsb) discoveredPrinter;
                        usbManager.requestPermission(discoveredPrinterUsb.device, permissionIntent);
                    }


                    @Override
                    public void discoveryFinished() {
                    }

                    @Override
                    public void discoveryError(String s) {
                        onError(s);
                    }
                });
            }
        }).start();
    }
}
