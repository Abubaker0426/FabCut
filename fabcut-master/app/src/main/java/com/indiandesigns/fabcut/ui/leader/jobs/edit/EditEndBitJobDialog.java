package com.indiandesigns.fabcut.ui.leader.jobs.edit;

import static com.indiandesigns.fabcut.utils.AppConstants.LAY_LENGTH_TAG;
import static com.indiandesigns.fabcut.utils.AppConstants.QUANTITY_GROUP_TAG;
import static com.indiandesigns.fabcut.utils.AppConstants.QUANTITY_TAG;
import static com.indiandesigns.fabcut.utils.AppConstants.RATIO_MATCH;
import static com.indiandesigns.fabcut.utils.AppConstants.RATIO_TAG;
import static com.indiandesigns.fabcut.utils.AppConstants.TOTAL_TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.indiandesigns.fabcut.R;
import com.indiandesigns.fabcut.data.network.enums.ListType;
import com.indiandesigns.fabcut.data.network.model.AssignEndBitJobRequest;
import com.indiandesigns.fabcut.data.network.model.FetchPartsDetails;
import com.indiandesigns.fabcut.data.network.model.Item;
import com.indiandesigns.fabcut.di.component.ActivityComponent;
import com.indiandesigns.fabcut.ui.base.BaseDialog;
import com.indiandesigns.fabcut.ui.common.model.MarkerItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditEndBitJobDialog extends BaseDialog implements EditEndBitJobDialogMvpView, AdapterView.OnItemSelectedListener {

    private static final String TAG = "RateUsDialog";

    @Inject
    EditEndBitJobDialogMvpPresenter<EditEndBitJobDialogMvpView> mPresenter;

    private MarkerItem markerItem;


    private List<List<String>> dataList;
    private List<Item> itemList;
    private List<String> layNumbersList = new ArrayList<String>();
    private String selectedLayNumber = "0";

    boolean[] booleanParts;
    ArrayList<Integer> partList =  new ArrayList<>();
    String[] partsDataArray = new String[0];

    @BindView(R.id.lay_numbers_drop_down)
    Spinner layNumbersSpinner;

    @BindView(R.id.parts_drop_down)
    TextView partDropDown;

    @BindView(R.id.size_container)
    LinearLayout sizeContainer;

    @BindView(R.id.quantities_container)
    LinearLayout quantitiesContainer;

    int ratioPivot = 0;
    EditEndBitJobDialogResult mDialogResult;

    /**
     * Gets an new instance of the dialog
     * @return EditEndBitJobDialog instance
     */

    public static EditEndBitJobDialog newInstance() {
        EditEndBitJobDialog fragment = new EditEndBitJobDialog();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.edit_end_bit_job_dialog, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            setUnBinder(ButterKnife.bind(this, view));
            mPresenter.onAttach(this);
        }

        return view;
    }

    public String getSelectedLayNumber() {
        return selectedLayNumber;
    }

    public void setSelectedLayNumber(String selectedLayNumber) {
        this.selectedLayNumber = selectedLayNumber;
    }

    /**
     * Initializes the data for the popup and
     * Shows the dialog
     * @param fragmentManager Fragment Manager
     * @param lists List of data for the job
     * @param marker
     */

    public void show(FragmentManager fragmentManager, List<List<String>> lists, MarkerItem marker) {
        super.show(fragmentManager, TAG);
        dataList = lists;
        markerItem = marker;
        itemList = markerItem.getItems();
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

        mPresenter.fetchLayNumbers(markerItem);
        mPresenter.fetchPartNames(markerItem.getOcNumber());

        //lay number drop down
        if(layNumbersList.size() == 0){
            layNumbersList.add("0");
        }
        layNumbersList.add("Select Lay"); // drop down place holder

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, layNumbersList) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setTextSize(15f);
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }else{
                    ((TextView)v.findViewById(android.R.id.text1)).setText("Lay " + getItem(position));
                    ((TextView)v.findViewById(android.R.id.text1)).setTextSize(16f);
                }

                return v;
            }

            @Override
            public int getCount() {
                return super.getCount()-1; // do not display last item. It is used as hint.
            }

        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        layNumbersSpinner.setAdapter(adapter);
        layNumbersSpinner.setSelection(adapter.getCount()); //set the hint the default selection so it appears on launch.
        layNumbersSpinner.setOnItemSelectedListener(this);

        // parts drop down
        booleanParts = new boolean[partsDataArray.length];
        partDropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Select parts");
                builder.setCancelable(false);
                builder.setMultiChoiceItems(partsDataArray, booleanParts, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        if(b){
                            partList.add(i);
                            Collections.sort(partList);
                        }else{
                            for(int j=0;j<partList.size(); j++){
                                if(partList.get(j)==i){
                                    partList.remove(j);
                                }
                            }
                        }
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for(int j=0; j<partList.size(); j++){
                            stringBuilder.append(partsDataArray[partList.get(j)]);
                            if(j != partList.size()-1){
                                stringBuilder.append(", ");
                            }
                        }
                        partDropDown.setText(stringBuilder.toString());
                        setQuantitiesContainer();
                    }
                });

                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for(int j =0; j< booleanParts.length; j++){
                            booleanParts[j] = false;
                            partList.clear();
                            partDropDown.setText("");
                        }
                        setQuantitiesContainer();
                    }
                });
                builder.show();
            }
        });


        // Adds EditTexts for quantity and TextViews for Ratio and Size
        addTextViewToLayout(sizeContainer, dataList.get(ListType.SIZE.getValue()), getContext(), true);
    }

    public void setQuantitiesContainer(){
        quantitiesContainer.removeAllViews();
        for(Integer index: partList){
            String partName = partsDataArray[index];
            addEditTextToLayout(quantitiesContainer, dataList.get(ListType.QUANTITY.getValue()), getContext(), false, partName);
        }

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

    private void addEditTextToLayout(LinearLayout quantitiesLayout, List<String> list, Context context, boolean bold, String part){

        for (int item = 0; item < itemList.size(); item++) {

            LinearLayout.LayoutParams parentParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setLayoutParams(parentParams);
            linearLayout.setTag(QUANTITY_GROUP_TAG);

            TextView textView = new TextView(context);
            parentParams.setMargins(0,12,0,0);
            textView.setLayoutParams(parentParams);
            textView.setText(part);
            textView.setGravity(Gravity.START);
            textView.setLayoutParams(parentParams);
            textView.setTextColor(context.getResources().getColor(R.color.colorBlack));
            textView.setTextSize(13f);

            quantitiesContainer.addView(textView);

            for (int i = 0; i < list.size(); i++)
            {
                String text = list.get(i);

                // Last item would be a TextView for total
                if(i == list.size() - 1){
                    addTextViewToLayout(linearLayout, text, context, bold, TOTAL_TAG, false);
                } else {
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

        List<Boolean> quantityValidations = new ArrayList<>();

        String selectedLay = getSelectedLayNumber();
        if(selectedLay.equals("0")){
            showMessage(R.string.select_lay);
            return;
        }

        if(partList.size() == 0){
            showMessage(R.string.select_parts);
            return;
        }
        List<String> selectedParts = new ArrayList<>();
        for(int j=0;j<partList.size();j++){
            selectedParts.add(partsDataArray[partList.get(j)]);
        }

        for(int i = 0; i < quantitiesContainer.getChildCount(); i++){
            if(quantitiesContainer.getChildAt(i).getTag() != null){
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

        if(!areQuantitiesValid(quantityValidations)) {
            showMessage(R.string.zero_quantity_ratio_error);
            return;
        }

        mPresenter.prepareJobData(sizeContainer, quantitiesContainer, itemList, selectedLay, selectedParts);
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
     * Assigns the interface to instance variable
     * @param dialogResult Interface
     */

    public void setEditEndBitJobDialogResult(EditEndBitJobDialogResult dialogResult){
        mDialogResult = dialogResult;
    }

    /**
     * Updates lay numbers list
     * @param lays List of lay numbers received from server
     */

    @Override
    public void updateLayNumbersList(List<Number> lays){
        layNumbersList.clear();
        for(Number lay: lays){
            layNumbersList.add(String.valueOf(lay));
        }
        if(layNumbersList.size() == 0){
            layNumbersList.add("0");
            showMessage(R.string.no_lays_error);
        }
        layNumbersList.add("Select Lay"); // drop down place holder
        layNumbersSpinner.setSelection(layNumbersList.size()-1);
    }

    /**
     * Updates part names list
     * @param parts list of part names received from server
     */
    @Override
    public void updatePartNamesList(List<FetchPartsDetails> parts){
        List<String> PartList = new ArrayList<>();

        for(FetchPartsDetails partsDetails: parts){
            PartList.add(partsDetails.getPart());
        }
        this.partsDataArray = PartList.toArray(new String[parts.size()]);
        this.booleanParts = new boolean[partsDataArray.length];
        if(partsDataArray.length == 0){
            showMessage(R.string.no_parts_error);
        }
    }

    /**
     * Function called for   spinner on item select
     * @param adapterView
     * @param view
     * @param pos
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
        String item = adapterView.getItemAtPosition(pos).toString();
        if(item.equals("Select Lay") == false){
            setSelectedLayNumber(item);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // TODO Auto-generated method stub
    }

    /**
     * Interface for callback when processing is done
     */

    public interface EditEndBitJobDialogResult{
        void submit(AssignEndBitJobRequest request);
    }

    /**
     * Called when data is modeled
     * Calls the submit method of parent activity
     * @param request Data model to be sent to server
     */

    @Override
    public void assignJob(AssignEndBitJobRequest request) {
        if( mDialogResult != null ){
            mDialogResult.submit(request);
        }
        dismissDialog();
    }

    @Override
    public void onDestroyView() {
        mPresenter.onDetach();
        super.onDestroyView();
        if(layNumbersList.size() > 0){
            layNumbersList.remove(layNumbersList.size()-1); // remove the last element used for hint
        }
    }
}