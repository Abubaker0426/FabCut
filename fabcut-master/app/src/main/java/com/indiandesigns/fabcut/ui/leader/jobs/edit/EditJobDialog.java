package com.indiandesigns.fabcut.ui.leader.jobs.edit;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.indiandesigns.fabcut.R;
import com.indiandesigns.fabcut.data.network.enums.ListType;
import com.indiandesigns.fabcut.data.network.model.AssignJobRequest;
import com.indiandesigns.fabcut.data.network.model.Item;
import com.indiandesigns.fabcut.di.component.ActivityComponent;
import com.indiandesigns.fabcut.ui.base.BaseDialog;
import com.indiandesigns.fabcut.utils.CommonUtils;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.indiandesigns.fabcut.utils.AppConstants.ITEM_PATTERN_TAG;
import static com.indiandesigns.fabcut.utils.AppConstants.ITEM_SHADE_TAG;
import static com.indiandesigns.fabcut.utils.AppConstants.ITEM_SHRINKAGE_TAG;
import static com.indiandesigns.fabcut.utils.AppConstants.LAY_LENGTH_TAG;
import static com.indiandesigns.fabcut.utils.AppConstants.QUANTITY_GROUP_TAG;
import static com.indiandesigns.fabcut.utils.AppConstants.QUANTITY_TAG;
import static com.indiandesigns.fabcut.utils.AppConstants.RATIO_MATCH;
import static com.indiandesigns.fabcut.utils.AppConstants.SHADE_GROUP_TAG;
import static com.indiandesigns.fabcut.utils.AppConstants.TOTAL_TAG;
import static com.indiandesigns.fabcut.utils.AppConstants.RATIO_TAG;

public class EditJobDialog extends BaseDialog implements EditJobDialogMvpView {

    private static final String TAG = "RateUsDialog";
    private static final int EDITTEXT_CHAR_LENGTH = 7;

    @Inject
    EditJobDialogMvpPresenter<EditJobDialogMvpView> mPresenter;

    private List<List<String>> dataList;
    private List<Item> itemList;

    @BindView(R.id.size_container)
    LinearLayout sizeContainer;

    @BindView(R.id.quantities_container)
    LinearLayout quantitiesContainer;

    @BindView(R.id.ratio_container)
    LinearLayout ratioContainer;

    int ratioPivot = 0;
    EditJobDialogResult mDialogResult;

    /**
     * Gets an new instance of the dialog
     * @return EditJobDialog instance
     */

    public static EditJobDialog newInstance() {
        EditJobDialog fragment = new EditJobDialog();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.edit_job_dialog, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            setUnBinder(ButterKnife.bind(this, view));
            mPresenter.onAttach(this);
        }

        return view;
    }

    /**
     * Initializes the data for the popup and
     * Shows the dialog
     * @param fragmentManager Fragment Manager
     * @param lists List of data for the job
     * @param items
     */

    public void show(FragmentManager fragmentManager, List<List<String>> lists, List<Item> items) {
        super.show(fragmentManager, TAG);
        dataList = lists;
        itemList = items;
    }

    /**
     * Traverses through the data list and adds required views
     * @param view EditDialogView
     */

    @Override
    protected void setUp(View view) {
        if(dataList == null || dataList.size() == 0){
            return;
        }

        for (int i = 0; i < dataList.get(ListType.RATIO.getValue()).size(); i++) {
            String ratio = dataList.get(ListType.RATIO.getValue()).get(i);
            if(ratio.equals(RATIO_MATCH)){
                ratioPivot = Integer.valueOf(dataList.get(ListType.QUANTITY.getValue()).get(i));
            }
        }

        // Adds EditTexts for quantity and TextViews for Ratio and Size
        addEditTextToLayout(quantitiesContainer, dataList.get(ListType.QUANTITY.getValue()), getContext(), false);
        addRatioEditTextToLayout(ratioContainer, dataList.get(ListType.RATIO.getValue()), getContext());
        addTextViewToLayout(sizeContainer, dataList.get(ListType.SIZE.getValue()), getContext(), true);
    }

    private void addRatioEditTextToLayout(LinearLayout linearLayout, List<String> list, Context context){
        for (int i = 0; i < list.size(); i++)
        {
            String text = list.get(i);
            if(i < list.size() - 2){
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                params.setMargins(0,4,0,4);
                EditText editText = new EditText(context);
                editText.setText(text);
                editText.setGravity(Gravity.CENTER);
                editText.setLayoutParams(params);
                editText.setTextColor(context.getResources().getColor(R.color.colorBlack));
                editText.setSingleLine();
                editText.setTextSize(13f);
                editText.setEllipsize(TextUtils.TruncateAt.END);
                editText.setTag(RATIO_TAG+i);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);

                setEdittextCharLength(editText);

                linearLayout.addView(editText);
                editText.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        editText.onTouchEvent(motionEvent);
                        editText.setSelection(editText.getText().length());
                        return true;
                    }
                });
            } else {
                addTextViewToLayout(linearLayout, text, context, false, "", false);
            }
        }
    }


    /**
     * Adds TextViews to a given layout with property bold with tags
     * @param linearLayout Layout to contain the TextViews
     * @param list List of text to show with TextViews
     * @param context Context
     * @param bold Bold the text
     */

    private void addTextViewToLayout(LinearLayout linearLayout, List<String> list, Context context, boolean bold){
        for (int i = 0; i < list.size(); i++)
        {
            String text = list.get(i);
            String tag = "";
            addTextViewToLayout(linearLayout, text, context, bold, tag, false);
        }
    }

    /**
     * Adds EditText to a given layout with tag and adds listener
     * @param quantitiesLayout Layout to add EditTexts
     * @param list List of data to traverse through
     * @param context Context
     * @param bold Text bold property
     */

    private void addEditTextToLayout(LinearLayout quantitiesLayout, List<String> list, Context context, boolean bold){

        for (int item = 0; item < itemList.size(); item++) {

            LinearLayout.LayoutParams parentParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setLayoutParams(parentParams);
            linearLayout.setTag(QUANTITY_GROUP_TAG);

            TextView textView = createItemDescriptionTextView(parentParams,context,itemList.get(item).getItemCode(),itemList.get(item).getItemDesc());
            ImageView clearImageView = createImageViewToClearItem(context, itemList.get(item).getItemCode());

            quantitiesContainer.addView(textView);
            quantitiesContainer.addView(clearImageView);

            quantitiesContainer.addView(addShadeEditTextViewToLayout(parentParams, context));




            for (int i = 0; i < list.size(); i++)
            {
                String text = list.get(i);

                // Last item would be a TextView for total
                if(i == list.size() - 1){
                    addTextViewToLayout(linearLayout, text, context, bold, TOTAL_TAG, false);
                } else {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                    params.setMargins(0,2,0,4);
                    EditText editText = new EditText(context);
                    editText.setText(text);
                    editText.setGravity(Gravity.CENTER);
                    editText.setLayoutParams(params);
                    editText.setTextColor(context.getResources().getColor(R.color.colorBlack));
                    editText.setSingleLine();
                    editText.setTextSize(13f);
                    editText.setEllipsize(TextUtils.TruncateAt.END);

                    setEdittextCharLength(editText);

                    editText.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            editText.onTouchEvent(motionEvent);
                            editText.setSelection(editText.getText().length());
                            return true;
                        }
                    });

                    // Second last item would be lay length - TAGS identify each view differently
                    if(i == list.size() - 2){
                        editText.setTag(LAY_LENGTH_TAG);
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                for(int index = 0; index < quantitiesContainer.getChildCount(); index++){
                                    if(quantitiesContainer.getChildAt(index).getTag() == QUANTITY_GROUP_TAG){
                                        LinearLayout quantityContainer = (LinearLayout) quantitiesContainer.getChildAt(index);
                                        for(int childIndex = 0; childIndex < quantityContainer.getChildCount(); childIndex++ ){
                                            View v = quantityContainer.getChildAt(childIndex);
                                            if(v.getTag().toString().startsWith(LAY_LENGTH_TAG)){
                                                EditText et = (EditText) v;
                                                if(!et.getText().toString().equals(charSequence.toString()))
                                                    et.setText(charSequence);
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });
                    } else {
                        editText.setTag(QUANTITY_TAG+i);
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int k, int i1, int i2) {
                                int sum = 0;
                                final int childCount = linearLayout.getChildCount();
                                for (int count = 0; count < childCount; count++) {
                                    View v = linearLayout.getChildAt(count);
                                    if(v.getTag().toString().startsWith(QUANTITY_TAG)){
                                        EditText et = (EditText) v;
                                        if(!et.getText().toString().equals("")){
                                            sum = sum + Integer.valueOf(et.getText().toString());
                                        }
                                    }
                                }

                                // Update total in real time
                                TextView total = (TextView) linearLayout.findViewWithTag(TOTAL_TAG);
                                total.setText(String.valueOf(sum));
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });
                    }

                    linearLayout.addView(editText);
                }
            }

            quantitiesLayout.addView(linearLayout);
        }
    }

      LinearLayout addShadeEditTextViewToLayout(LinearLayout.LayoutParams parentParams, Context context){

        LinearLayout linearLayoutForShade = new LinearLayout(getContext());
        linearLayoutForShade.setLayoutParams(parentParams);
        linearLayoutForShade.setTag(SHADE_GROUP_TAG);

        LinearLayout.LayoutParams paramsForTextview = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsForTextview.setMargins(0,0,6,0);

        LinearLayout.LayoutParams paramsForShade = new LinearLayout.LayoutParams(150, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsForShade.setMargins(0,1,10,1);

        LinearLayout.LayoutParams paramsForShrinkage = new LinearLayout.LayoutParams(80, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsForShrinkage.setMargins(0,1,10,1);

        TextView textViewForShade = new TextView(context);
        textViewForShade.setText("Shade");
        textViewForShade.setLayoutParams(paramsForTextview);
        textViewForShade.setTextColor(context.getResources().getColor(R.color.colorBlack));
        textViewForShade.setTextSize(13f);
        textViewForShade.setTypeface(null, Typeface.BOLD);
        textViewForShade.setSingleLine();
        linearLayoutForShade.addView(textViewForShade);

        EditText editTextForShade = new EditText(context);
        editTextForShade.setGravity(Gravity.NO_GRAVITY);
        editTextForShade.setLayoutParams(paramsForShade);
        editTextForShade.setTextColor(context.getResources().getColor(R.color.colorBlack));
        editTextForShade.setSingleLine();
        editTextForShade.setTextSize(13f);
        editTextForShade.setTag(ITEM_SHADE_TAG);
        editTextForShade.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        linearLayoutForShade.addView(editTextForShade);

        TextView textViewForShrinkage = new TextView(context);
        textViewForShrinkage.setText("Shrinkage");
        textViewForShrinkage.setLayoutParams(paramsForTextview);
        textViewForShrinkage.setTextColor(context.getResources().getColor(R.color.colorBlack));
        textViewForShrinkage.setTextSize(13f);
        textViewForShrinkage.setTypeface(null, Typeface.BOLD);
        textViewForShrinkage.setSingleLine();
        linearLayoutForShade.addView(textViewForShrinkage);

        EditText editTextForShrinkage = new EditText(context);
        editTextForShrinkage.setWidth(10);
        editTextForShrinkage.setGravity(Gravity.CENTER);
        editTextForShrinkage.setLayoutParams(paramsForShrinkage);
        editTextForShrinkage.setTextColor(context.getResources().getColor(R.color.colorBlack));
        editTextForShrinkage.setSingleLine();
        editTextForShrinkage.setTextSize(13f);
        editTextForShrinkage.setTag(ITEM_SHRINKAGE_TAG);
        editTextForShrinkage.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        linearLayoutForShade.addView(editTextForShrinkage);

          TextView textViewForPattern = new TextView(context);
          textViewForPattern.setText("Pattern");
          textViewForPattern.setLayoutParams(paramsForTextview);
          textViewForPattern.setTextColor(context.getResources().getColor(R.color.colorBlack));
          textViewForPattern.setTextSize(13f);
          textViewForPattern.setTypeface(null, Typeface.BOLD);
          textViewForPattern.setSingleLine();
          linearLayoutForShade.addView(textViewForPattern);

          EditText editTextForPattern = new EditText(context);
          editTextForPattern.setWidth(10);
          editTextForPattern.setGravity(Gravity.CENTER);
          editTextForPattern.setLayoutParams(paramsForShrinkage);
          editTextForPattern.setTextColor(context.getResources().getColor(R.color.colorBlack));
          editTextForPattern.setSingleLine();
          editTextForPattern.setTextSize(13f);
          editTextForPattern.setTag(ITEM_PATTERN_TAG);
          editTextForPattern.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
          linearLayoutForShade.addView(editTextForPattern);

        return linearLayoutForShade;

    }
    /**
     * Adds TextViews to a given layout
     * @param linearLayout Layout to add TextViews
     * @param text Text for TextView
     * @param context Context
     * @param bold Text bold property
     * @param tag Tag
     */

    private void addTextViewToLayout(LinearLayout linearLayout, String text, Context context, boolean bold, String tag, boolean scrolling){
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
        textView.setTextSize(13f);
        textView.setSingleLine();
        if(tag != null) textView.setTag(tag);
        if(scrolling) {
            textView.setLines(1);
            textView.setHorizontallyScrolling(true);
            textView.setMarqueeRepeatLimit(-1);
            textView.setSelected(true);
            textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        } else {
            textView.setEllipsize(TextUtils.TruncateAt.END);
        }
        linearLayout.addView(textView);
    }

    /**
     * Sets a string length limit for an EditText through creating an InputFilter
     * @param editText for Editext
     */
    private void setEdittextCharLength(EditText editText) {
        if(editText != null) {
            InputFilter[] inputFilters = new InputFilter[1];
            inputFilters[0] = new InputFilter.LengthFilter(EDITTEXT_CHAR_LENGTH);
            editText.setFilters(inputFilters);
        }
    }

    /**
     * Dismiss the dialog
     */

    @Override
    public void dismissDialog() {
        super.dismissDialog(TAG);

    }

    /**
     * Called when submit is clicked
     * Prepares the data to assign to a follower
     * Validates the data, only proceeds if no error
     * @param view View clicked
     */

    @OnClick(R.id.submit)
    public void submit(View view){
        hideKeyboard();
        validateValues();

        List<Boolean> quantityValidations = new ArrayList<>();

        if(quantitiesContainer.getChildCount() == 0){
            showMessage(R.string.job_item_quantity_error);
            return;
        }

        for(int i = 0; i < quantitiesContainer.getChildCount(); i++){
            if(quantitiesContainer.getChildAt(i).getTag() == QUANTITY_GROUP_TAG){
                LinearLayout quantityContainer = (LinearLayout) quantitiesContainer.getChildAt(i);
                final int childCount = quantityContainer.getChildCount();
                boolean allQuantitiesZero = true;
                for (int count = 0; count < childCount; count++) {
                    View q = quantityContainer.getChildAt(count);
                    if(q.getTag().toString().startsWith(QUANTITY_TAG)){
                        EditText qt = (EditText) q;
                        if(qt.getError() != null || qt.getText().length() == 0) {
                            return;
                        }
                        if(Integer.valueOf(qt.getText().toString()) != 0) {
                            allQuantitiesZero = false;
                        }
                    }
                }
                quantityValidations.add(allQuantitiesZero);
            }
        }

        boolean allRatiosZero = true;
        for (int count = 0; count < ratioContainer.getChildCount(); count++) {
            View r = ratioContainer.getChildAt(count);
            if(r.getTag().toString().startsWith(RATIO_TAG)){
                EditText rt = (EditText) r;
                if(rt.getError() != null || rt.getText().length() == 0) {
                    break;
                }
                if(Integer.valueOf(rt.getText().toString()) != 0) {
                    allRatiosZero = false;
                }
            }
        }

        if(!areQuantitiesValid(quantityValidations) || allRatiosZero) {
            showMessage(R.string.zero_quantity_ratio_error);
            return;
        }

        mPresenter.prepareJobData(sizeContainer, ratioContainer, quantitiesContainer, itemList);
    }

    private boolean areQuantitiesValid(List<Boolean> quantitiesValidations){
        for (Boolean validation: quantitiesValidations){
            if(validation){
                return false;
            }
        }
        return true;
    }

    /**
     * create TextView for item code and item description
     * @param parentParams Layout params
     * @param itemDesc The item description
     * @param context Context
     * @param itemCode The item code
     * @@return TextView The combination of item code and item description
     */
    private TextView createItemDescriptionTextView(LinearLayout.LayoutParams parentParams, Context context, String itemCode, String itemDesc){
        TextView textView = new TextView(context);
        parentParams.setMargins(0,12,0,0);
        textView.setLayoutParams(parentParams);
        textView.setText(CommonUtils.getItemDescription(itemCode, itemDesc));
        textView.setGravity(Gravity.START);
        textView.setLayoutParams(parentParams);
        textView.setTextColor(context.getResources().getColor(R.color.colorBlack));
        textView.setTextSize(13f);
        return textView;
    }

    /**
     * create ImageView to clear item
     * @param context Context
     * @param itemCode Job item Code
     * @@return ImageView
     */
    private ImageView createImageViewToClearItem(Context context, String itemCode){
        RelativeLayout.LayoutParams imageViewParams;
        imageViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        imageViewParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        ImageView clearImageView = new ImageView(context);
        clearImageView.setImageResource(R.drawable.ic_clear);
        clearImageView.setLayoutParams(imageViewParams);
        clearImageView.setColorFilter(context.getResources().getColor(R.color.colorRed));

        clearImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.confirmDialogToRemoveJobItem(itemCode);
            }
        });

        return clearImageView;
    }
    /**
     * Validate ratios and quantities
     */

    private void validateValues() {

        int smallestRatio = 0;
        List<Integer> ratios = new ArrayList<>();

        for(int i = 0; i < quantitiesContainer.getChildCount(); i++){
            int smallestQuantity = 0;
            boolean quantityError = false;
            List<Integer> quantities = new ArrayList<>();
            if(quantitiesContainer.getChildAt(i).getTag() == QUANTITY_GROUP_TAG){
                LinearLayout quantityContainer = (LinearLayout) quantitiesContainer.getChildAt(i);

                final int childCount = quantityContainer.getChildCount();
                for (int count = 0; count < childCount; count++) {
                    View r = ratioContainer.getChildAt(count);
                    View q = quantityContainer.getChildAt(count);
                    if(q.getTag().toString().startsWith(QUANTITY_TAG)){
                        EditText qt = (EditText) q;
                        EditText rt = (EditText) r;
                        if(qt.getText().toString().equals("") ||
                                rt.getText().toString().equals("")) {
                            qt.setError(getString(R.string.invalid));
                            rt.setError(getString(R.string.invalid));
                            quantityError = true;
                        } else {
                            qt.setError(null);
                            rt.setError(null);
                            int quantity = Integer.valueOf(qt.getText().toString());
                            int ratio = Integer.valueOf(rt.getText().toString());
                            if(quantity > 0 && ratio > 0) {
                                quantities.add(quantity);
                                ratios.add(ratio);
                            }
                            if(quantity > 0 && ratio == 0){
                                rt.setError(getString(R.string.invalid));
                                return;
                            }
                        }
                    }
                }

                if(!quantityError) {

                    if(quantities.size() == 0 || ratios.size() == 0) {
                        return;
                    }

                    smallestRatio = Collections.min(ratios);
                    int smallestRatioIndex = ratios.indexOf(smallestRatio);
                    smallestQuantity = quantities.get(smallestRatioIndex);

                    if(smallestRatio == 0) return;

                    double pivot = (double)smallestQuantity / (double)smallestRatio;

                    for (int count = 0; count < childCount; count++) {
                        View r = ratioContainer.getChildAt(count);
                        View q = quantityContainer.getChildAt(count);
                        if(r.getTag().toString().startsWith(RATIO_TAG)){
                            EditText rt = (EditText) r;
                            EditText qt = (EditText) q;
                            if(rt.getText().toString().equals("")) {
                                rt.setError(getString(R.string.invalid));
                            } else {
                                rt.setError(null);
                                int ratio = Integer.valueOf(rt.getText().toString());
                                int quantity = Integer.valueOf(qt.getText().toString());
                                double actualQuantity = ratio * pivot;
                                if(ratio == 0 && quantity != 0) {
                                    rt.setError(getString(R.string.invalid));
                                    qt.setError(getString(R.string.invalid));
                                } else if (ratio != 0 && quantity == 0) {
                                    rt.setError(getString(R.string.invalid));
                                    qt.setError(getString(R.string.invalid));
                                } else {
                                    if(quantity != (int) actualQuantity) {
                                        rt.setError(getString(R.string.invalid));
                                        qt.setError(getString(R.string.invalid));
                                    } else if (!((pivot == Math.floor(pivot)) && !Double.isInfinite(pivot))) {
                                        if(!(ratio == 0 && quantity == 0)){
                                            rt.setError(getString(R.string.invalid));
                                            qt.setError(getString(R.string.invalid));
                                        }
                                    } else {
                                        rt.setError(null);
                                        qt.setError(null);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Assigns the interface to instance variable
     * @param dialogResult Interface
     */

    public void setEditJobDialogResult(EditJobDialogResult dialogResult){
        mDialogResult = dialogResult;
    }

    /**
     * Interface for callback when processing is done
     */

    public interface EditJobDialogResult{
        void submit(AssignJobRequest request);
    }

    /**
     * Called when data is modeled
     * Calls the submit method of parent activity
     * @param request Data model to be sent to server
     */

    @Override
    public void assignJob(AssignJobRequest request) {
        if( mDialogResult != null ){
            mDialogResult.submit(request);
        }
        dismissDialog();
    }

    /**
     * This function is responsible to remove itemCode, clear ImageView and quantity container from job(quantitiesContainer)
     * @param itemCode Job item code
     */
    @Override
    public void removeItemFromJob(String itemCode) {
        for(int i = 0; i < quantitiesContainer.getChildCount(); i++){
            View view = quantitiesContainer.getChildAt(i);
            if(view instanceof TextView){
                TextView itemCodeTextView = (TextView) view;
                String[] splitItemCode = itemCodeTextView.getText().toString().split(",");
                if(itemCode.equals(splitItemCode[0])){
                    quantitiesContainer.removeViewAt(i+3);
                    quantitiesContainer.removeViewAt(i+2);
                    quantitiesContainer.removeViewAt(i+1);
                    quantitiesContainer.removeViewAt(i);
                    quantitiesContainer.refreshDrawableState();
                    return;
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        mPresenter.onDetach();
        super.onDestroyView();
    }
}