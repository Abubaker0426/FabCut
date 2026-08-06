package com.indiandesigns.fabcut.ui.follower;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.indiandesigns.fabcut.R;
import com.indiandesigns.fabcut.data.network.model.ScanBarcode;
import com.indiandesigns.fabcut.ui.base.BaseViewHolder;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter for showing the list of barcodes for a job
 * Extends {@link BaseViewHolder}
 */

public class BarcodeAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<ScanBarcode> barcodeList;

    public BarcodeAdapter(List<ScanBarcode> barcodeList) {
        this.barcodeList = barcodeList;
    }

    private Callback mCallback;
    public void setCallback(Callback callback) {
        mCallback = callback;
    }


    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.follower_job_row_item, parent, false));
    }

    @Override
    public int getItemCount() {
        return barcodeList.size();
    }

    public void addItem(ScanBarcode barcode) {
        barcodeList.add(barcode);
        mCallback.onItemAdded(getActualPliesSum());
        notifyDataSetChanged();
    }

    public void addItems(List<ScanBarcode> barcodes) {
        barcodeList.addAll(barcodes);
        mCallback.onItemAdded(getActualPliesSum());
        notifyDataSetChanged();
    }

    void clear(){
        barcodeList.clear();
        notifyDataSetChanged();
    }

    public interface Callback {
        boolean onItemAdded(Double actualPlies);
        void delete(int position);
        void hideKeyboard();
        void validateActualPlies(Double actualPlies, String barcode, int position);
        void onReasonSelected(ScanBarcode scanBarcode);
    }

    public ScanBarcode getItem(int position){
        return barcodeList.get(position);
    }

    public void removeAt(int position) {
        barcodeList.remove(position);
        mCallback.onItemAdded(getActualPliesSum());
        notifyDataSetChanged();
    }

    public class ViewHolder extends BaseViewHolder {

        @BindView(R.id.barcode)
        TextView barcodeTextView;

        @BindView(R.id.expected_plies)
        TextView expectedTextView;

        @BindView(R.id.actual_plies)
        EditText actualEditText;

        @BindView(R.id.reason_spinner)
        Spinner reasonSpinner;

        @BindView(R.id.delete)
        ImageView delete;

        @BindView(R.id.feedback_icon)
        ImageView feedbackIcon;

        @BindView(R.id.loader)
        ProgressBar loader;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        protected void clear() {
            barcodeTextView.setText("");
            expectedTextView.setText("");
            actualEditText.setText("");
            reasonSpinner.setSelection(0);
        }

        public void onBind(int position) {
            super.onBind(position);

            ScanBarcode barcode = barcodeList.get(position);
            Context context = itemView.getContext();
            String[] reasons = context.getResources().getStringArray(R.array.reason_options);

            if(barcode.getBarcode() != null){
                barcodeTextView.setText(barcode.getBarcode());
            }

            if(barcode.getExpectedPlies() != null){
                expectedTextView.setText(String.valueOf(barcode.getExpectedPlies()));
            }

            actualEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if(!hasFocus){
                        if(!barcodeList.contains(barcode)) return;
                        if(actualEditText.getText().toString().equals("")){
                            actualEditText.setText("0.0");
                        }

                        loader.setVisibility(View.VISIBLE);
                        barcode.setActualPlies(Double.valueOf(actualEditText.getText().toString()));
                        boolean isCutGreaterThanActual = mCallback.onItemAdded(getActualPliesSum());
                        if(!isCutGreaterThanActual){
                            actualEditText.setText("0.0");
                            barcode.setActualPlies(Double.valueOf(actualEditText.getText().toString()));
                        } else {
                            mCallback.validateActualPlies(Double.valueOf(actualEditText.getText().toString()), barcode.getBarcode(), position);
                        }
                        } else {
                        actualEditText.requestFocus();
                    }
                }
            });

            actualEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                        mCallback.hideKeyboard();
                        actualEditText.clearFocus();
                    }
                    return true;
                }
            });

            if(barcode.getActualPlies() != null){
                actualEditText.setText(String.valueOf(barcode.getActualPlies()));
            } else {
                actualEditText.setText("0");
            }

            reasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if(i == 0){
                        barcode.setReason("");
                        return;
                    }
                    barcode.setReason(reasons[i]);
                    mCallback.onReasonSelected(barcode);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            if(barcode.getReason() != null){
                int index = Arrays.asList(reasons).indexOf(barcode.getReason());
                reasonSpinner.setSelection(index);
            } else {
                reasonSpinner.setSelection(0);
            }

            if(barcode.getValidated() == null){
                feedbackIcon.setImageDrawable(null);
                feedbackIcon.setVisibility(View.INVISIBLE);
            } else if(barcode.getValidated()){
                feedbackIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check));
                feedbackIcon.setVisibility(View.VISIBLE);
            } else {
                feedbackIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_error));
                feedbackIcon.setVisibility(View.VISIBLE);
            }

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.delete(position);
                }
            });


        }
    }

    /**
     * Checks if all actual plies have values
     * @return boolean
     */

    public boolean areActualPliesValid(){
        for (ScanBarcode code:
             barcodeList) {
            if(code.getActualPlies() == null || code.getActualPlies() == 0.0){
                return false;
            }
        }

        return true;
    }

    /**
     * Calculates the sum of all the actual plies entered
     * @return Sum
     */

    private double getActualPliesSum() {
        double sum = 0;
        for (int i = 0; i < barcodeList.size(); i++){
            ScanBarcode value = barcodeList.get(i);
            if(value.getActualPlies() != 0){
                sum = sum + value.getActualPlies();
            }
        }
        return sum;
    }

}
