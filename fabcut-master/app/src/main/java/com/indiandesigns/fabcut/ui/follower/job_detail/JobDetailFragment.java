package com.indiandesigns.fabcut.ui.follower.job_detail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.indiandesigns.fabcut.R;
import com.indiandesigns.fabcut.data.network.enums.ListType;
import com.indiandesigns.fabcut.data.network.model.FetchFollowerResponse;
import com.indiandesigns.fabcut.data.network.model.FetchFollowerResponse.JobDetail;
import com.indiandesigns.fabcut.data.network.model.ScanBarcode;
import com.indiandesigns.fabcut.di.component.ActivityComponent;
import com.indiandesigns.fabcut.ui.base.BaseFragment;
import com.indiandesigns.fabcut.ui.follower.BarcodeAdapter;
import com.indiandesigns.fabcut.ui.scanner.ScannerActivity;
import com.indiandesigns.fabcut.utils.CommonUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.indiandesigns.fabcut.utils.AppConstants.SCANNED_CODE;


public class JobDetailFragment extends BaseFragment implements JobDetailMvpView, BarcodeAdapter.Callback {

    public static final String TAG = "JobDetailFragment";

    private static final String JOB_ID = "jobId";
    private static final String LAY_LENGTH = "layLength";
    private static final String OC_NUMBER = "ocNumber";
    private static final String JOB_DETAIL = "jobDetail";

    private Integer jobId;
    private Double layLength;
    private String ocNumber;
    private Integer sumOfRatios;
    private JobDetail jobDetail;

    @Inject
    BarcodeAdapter barcodeAdapter;

    @BindView(R.id.barcode_recycler_view)
    RecyclerView barcodeRecyclerView;

    @Inject
    LinearLayoutManager linearLayoutManager;

    @BindView(R.id.cut_quantity)
    TextView cutQuantityTextView;

    @BindView(R.id.assigned_quantity)
    TextView assignedQuantityTextView;

    @BindView(R.id.description)
    TextView descriptionTextView;

    @BindView(R.id.actual_plies)
    TextView actualPliesTextView;

    @BindView(R.id.follower_job_lay_umber)
    TextView jobLayNumber;

    @BindView(R.id.scan_barcode)
    ExtendedFloatingActionButton scanButton;

    private static final int SCAN_BARCODE = 2323;

    @BindView(R.id.lay_length)
    TextView layLengthTextView;

    @BindView(R.id.num_of_plies)
    TextView numOfPliesTextView;

    @BindView(R.id.size_container)
    LinearLayout sizeContainer;

    @BindView(R.id.oc_number)
    TextView ocNumberTextView;

    @BindView(R.id.size_quantity_container)
    LinearLayout sizeQuantitiesContainer;

    @Inject
    JobDetailMvpPresenter<JobDetailMvpView> mPresenter;

    public static JobDetailFragment newInstance(String ocNumber, Double layLength, Integer jobId, JobDetail jobDetail) {
        Bundle args = new Bundle();
        args.putSerializable(JOB_DETAIL, jobDetail);
        args.putInt(JOB_ID, jobId);
        args.putDouble(LAY_LENGTH, layLength);
        args.putString(OC_NUMBER, ocNumber);
        JobDetailFragment fragment = new JobDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_detail, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            setUnBinder(ButterKnife.bind(this, view));
            mPresenter.onAttach(this);
        }

        if(getArguments() != null){
            ocNumber = getArguments().getString(OC_NUMBER, "");
            jobDetail = (JobDetail) getArguments().getSerializable(JOB_DETAIL);
            layLength = getArguments().getDouble(LAY_LENGTH, 0.0);
            jobId = getArguments().getInt(JOB_ID, 0);
        }

        return view;
    }

    /**
     * Check for any invalid actual plies
     * Opens the scanner activity if all plies are valid
     * @param clicked View clicked
     */

    @OnClick(R.id.scan_barcode)
    public void scan(View clicked){
        if(!areActualPliesValidated()){
            onError(R.string.actual_plies_error);
            return;
        }
        Intent intent = ScannerActivity.getStartIntent(getContext());
        startActivityForResult(intent, SCAN_BARCODE);
    }

    /**
     * Validates is all actual plies are validated
     * @return Validity
     */

    private boolean areActualPliesValidated(){
        int childCount = barcodeRecyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = barcodeRecyclerView.getChildAt(i);
            if(checkForInvalidActualPlies(view)){
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if an actual plies entered by user is invalid
     * @param view Current view from recycler view
     * @return Invalidity of plies
     */

    private boolean checkForInvalidActualPlies(View view) {
        ImageView icon = (ImageView) view.findViewById(R.id.feedback_icon);
        if(icon.getVisibility() == View.INVISIBLE || icon.getDrawable() == getResources().getDrawable(R.drawable.ic_error)){
            return true;
        }
        return false;
    }

    /**
     * Fetches the unique device id
     * @return DeviceId
     */

    @Override
    public String getDeviceId() {
        return CommonUtils.getDeviceId(getContext());
    }

    /**
     * Sets up the barcode recycler view
     * Fetches the follower's job details
     */

    @Override
    protected void setUp(View view) {
        barcodeRecyclerView.setItemAnimator(new DefaultItemAnimator());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        barcodeRecyclerView.setLayoutManager(linearLayoutManager);
        barcodeRecyclerView.setAdapter(barcodeAdapter);
        barcodeAdapter.setCallback(this);

        mPresenter.onViewPrepared(ocNumber, layLength, jobId, jobDetail);
    }

    /**
     * Updates lay length and initializes data
     * @param layLength Lay length received from server
     * @param jobId Job Id
     * @param ocNumber Oc Number
     * @param sumOfRatios Sum of ratios
     * @param listData List data for Sizes/Quantity
     */

    @Override
    public void updateLayLength(String ocNumber, Double layLength, Integer jobId, Double quantity, String description, int sumOfRatios, List<List<String>> listData, int layNumber) {
        layLengthTextView.setText(String.valueOf(layLength));
        assignedQuantityTextView.setText(String.valueOf(quantity));
        descriptionTextView.setText(description);
        numOfPliesTextView.setText(String.valueOf(quantity/sumOfRatios));
        ocNumberTextView.setText(ocNumber);
        jobLayNumber.setText(String.valueOf(layNumber));
        this.sumOfRatios = sumOfRatios;
        addTextViewToLayout(sizeContainer, listData.get(ListType.SIZE.getValue()), getContext(), true);
        addTextViewToLayout(sizeQuantitiesContainer, listData.get(ListType.QUANTITY.getValue()), getContext(), true);
    }

    /**
     * Adds new barcode to the list and updates the UI
     * @param barcode New barcode
     */

    @Override
    public void updateBarcodeList(ScanBarcode barcode) {
        barcodeAdapter.addItem(barcode);
    }

    /**
     * Adds new barcodes to the list and updates the UI
     * @param scanBarcodeList New barcodes
     */

    @Override
    public void updateFromDatabase(List<ScanBarcode> scanBarcodeList) {
        barcodeAdapter.addItems(scanBarcodeList);
    }

    /**
     * Receives the barcode scanned
     * @param requestCode Request code
     * @param resultCode Result code
     * @param data Intent data
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SCAN_BARCODE) {
            if(resultCode == Activity.RESULT_OK){
                String code = data.getStringExtra(SCANNED_CODE);
                mPresenter.getBarcodeData(code, jobId, jobDetail.getItemCode());
            }
        }
    }

    /**
     * Updates the count for cut quantity on UI
     * @param actualPlies Sum of actual plies
     */

    @Override
    public boolean onItemAdded(Double actualPlies) {
        double cutQuantity = CommonUtils.getPrecisionDouble(actualPlies * sumOfRatios);
        Double assigned = Double.valueOf(assignedQuantityTextView.getText().toString());
        if(cutQuantity > assigned ){
            onError(R.string.cut_quantity_validation_error);
            return false;
        } else {
            cutQuantityTextView.setText(String.valueOf(cutQuantity));
            actualPliesTextView.setText(String.valueOf(CommonUtils.getPrecisionDouble(actualPlies)));

            Double cut = Double.valueOf(cutQuantityTextView.getText().toString());
            mPresenter.saveItemQuantityData(cut, assigned, jobDetail.getItemCode());
            return true;
        }

    }

    /**
     * Deletes barcode form the list
     * @param position Position to be removed
     */

    @Override
    public void delete(int position) {
        ScanBarcode barcode = barcodeAdapter.getItem(position);
        mPresenter.delete(barcode, jobId, position);
    }

    /**
     * Deletes barcode form the UI
     * @param position Position to be removed
     */

    @Override
    public void removeBarcodeFromList(int position) {
        barcodeAdapter.removeAt(position);
    }

    /**
     * Calls the validate api for actual plies entered by user
     * @param actualPlies Actual plies entered by user
     * @param barcode Barcode scanned
     * @param position Position of view
     */

    @Override
    public void validateActualPlies(Double actualPlies, String barcode, int position) {
        if(barcode == null || TextUtils.isEmpty(barcode)) return;
        mPresenter.validateActualPlies(actualPlies, barcode, jobId, position);
    }

    /**
     * Show error icon and hide progress bar
     * @param position View position
     */

    @Override
    public void pliesError(int position) {
        updateUIOnRecyclerView(position, R.drawable.ic_error);
        if(barcodeAdapter.getItemCount() == 0) return;
        ScanBarcode barcode = barcodeAdapter.getItem(position);
        barcode.setValidated(false);
        mPresenter.updateScanBarcode(barcode);
    }

    /**
     * Called when a reason is updated for a barcode
     * @param scanBarcode
     */

    @Override
    public void onReasonSelected(ScanBarcode scanBarcode) {
        mPresenter.updateScanBarcode(scanBarcode);
    }

    /**
     * Show check icon and hide progress bar
     * @param position View position
     */

    @Override
    public void pliesValidated(int position) {
        updateUIOnRecyclerView(position, R.drawable.ic_check);
        if(barcodeAdapter.getItemCount() == 0) return;
        ScanBarcode barcode = barcodeAdapter.getItem(position);
        barcode.setValidated(true);
        mPresenter.updateScanBarcode(barcode);
    }

    /**
     * Finds the view and sets visibility and icon
     * Finds and hides the progress bar
     * @param position View position
     * @param iconId Icon to show
     */

    private void updateUIOnRecyclerView(int position, int iconId){
        View view = barcodeRecyclerView.getChildAt(position);
        if(view != null) {
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.loader);
            ImageView icon = (ImageView) view.findViewById(R.id.feedback_icon);
            icon.setImageDrawable(getResources().getDrawable(iconId));
            icon.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    /***
     * For each layout, traverse the list of data and add TextView for each data object
     *
     * @param linearLayout Parent layout
     * @param list List of data to be shown for each TextView
     * @param context Context
     * @param bold Decides if the text is bold or not
     */

    private void addTextViewToLayout(LinearLayout linearLayout, List<String> list, Context context, boolean bold){
        for (String text:
                list) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            params.setMargins(0,4,0,4);
            TextView textView = new TextView(context);
            textView.setText(text);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(params);
            if(bold){
                textView.setTypeface(null, Typeface.BOLD);
            }
            textView.setTextColor(context.getResources().getColor(R.color.colorBlack));
            textView.setSingleLine();
            textView.setTextSize(13f);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            linearLayout.addView(textView);
        }
    }

    @Override
    public void onDestroyView() {
        mPresenter.onDetach();
        super.onDestroyView();
    }
}
