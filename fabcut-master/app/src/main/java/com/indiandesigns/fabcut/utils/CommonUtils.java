package com.indiandesigns.fabcut.utils;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import com.indiandesigns.fabcut.R;
import com.indiandesigns.fabcut.data.network.model.Item;
import com.indiandesigns.fabcut.data.network.model.JobListResponse;
import com.indiandesigns.fabcut.ui.common.model.CommonUtilsJobUnique;
import com.indiandesigns.fabcut.ui.common.model.OcLay;
import com.indiandesigns.fabcut.ui.common.model.OcLayDetail;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Utilities to be provided application wide
 */

public final class CommonUtils {

    private static final String TAG = "CommonUtils";

    private CommonUtils() {
        // This utility class is not publicly instantiable
    }

    /***
     * Calculates the number of columns for a specified column width
     * @param context Context
     * @param columnWidthDp desired column width
     * @return Number of columns
     */
    public static int calculateNoOfColumns(Context context, float columnWidthDp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (screenWidthDp / columnWidthDp + 0.5);
        return noOfColumns;
    }

    public static String getFileName(OcLay ocLay){
        try {
            return ocLay.getOcNo() + "-" + ocLay.getFitType() + "-" + ocLay.getSizeList().get(0).getItemCode() + "-" + ocLay.getLay();
        } catch (Exception ex){
            return ocLay.getOcNo() + "-" + ocLay.getFitType() + "-" + ocLay.getLay();
        }
    }

    /**
     * From the given list, generates a unique list oc-lay
     *
     * @param response List of jobs from server
     * @return Unique combinations of oc-lay with size/quantity array
     */

    public static List<OcLay> getUniqueOcLay(List<JobListResponse> response) {
        List<OcLay> ocLays = new ArrayList<>();
        HashMap<Long, ArrayList<CommonUtilsJobUnique>> uniqueJobMap = new HashMap<>();
        for(JobListResponse job: response) {
            CommonUtilsJobUnique commonUtilsJobUnique = new CommonUtilsJobUnique(job.getOcNo(), job.getLay(), job.getFitType(), job.getItemCode());
            boolean isCommonUtilsJobUniqueExist = validateCommonUtilsJobUniqueExist(uniqueJobMap, commonUtilsJobUnique, job.getJobId());
            if(isCommonUtilsJobUniqueExist == true){
                OcLay ocLay = findOcLay(commonUtilsJobUnique, ocLays, job.getItemCode());
                if(ocLay != null) {
                    if(job.getQuantity() > 0){
                        OcLayDetail ocLayDetail = new OcLayDetail();
                        ocLayDetail.setSize(job.getSize());
                        ocLayDetail.setQuantity(job.getQuantity());
                        ocLayDetail.setRatio(job.getRatio());
                        ocLayDetail.setFitType(job.getFitType());
                        ocLayDetail.setItemDesc(job.getItemDesc());
                        ocLayDetail.setItemCode(job.getItemCode());

                        ocLay.getSizeList().add(ocLayDetail);
                    }
                }
            } else {
                OcLay ocLay = new OcLay();
                ocLay.setJobId(job.getJobId());
                ocLay.setTableNum(job.getTableNum());
                ocLay.setOcNo(job.getOcNo());
                ocLay.setLay(job.getLay());
                ocLay.setFitType(job.getFitType());
                ocLay.setSizeList(new ArrayList<>());

                ocLays.add(ocLay);
                if(job.getQuantity() > 0){
                    OcLayDetail ocLayDetail = new OcLayDetail();
                    ocLayDetail.setSize(job.getSize());
                    ocLayDetail.setQuantity(job.getQuantity());
                    ocLayDetail.setRatio(job.getRatio());
                    ocLayDetail.setFitType(job.getFitType());
                    ocLayDetail.setItemDesc(job.getItemDesc());
                    ocLayDetail.setItemCode(job.getItemCode());

                    ocLay.getSizeList().add(ocLayDetail);
                }

                ArrayList<CommonUtilsJobUnique> commonUtilsJobUniquesList;
                if(uniqueJobMap.containsKey(job.getJobId())){
                    commonUtilsJobUniquesList = uniqueJobMap.get(job.getJobId());
                }else{
                    commonUtilsJobUniquesList = new ArrayList<CommonUtilsJobUnique>();
                }
                commonUtilsJobUniquesList.add(commonUtilsJobUnique);
                uniqueJobMap.put(job.getJobId(), commonUtilsJobUniquesList);
            }
        }
        return ocLays;
    }

    /**
     * Checks if a specific ocLay exists within the list
     * @param commonUtilsJobUnique Oc number, itemCode, lay number, fitType to match
     * @param ocLays List of ocLays
     * @return
     */
    private static OcLay findOcLay(CommonUtilsJobUnique commonUtilsJobUnique, List<OcLay> ocLays, String itemCode) {
        for(OcLay ocLay: ocLays) {
            List<OcLayDetail> SizeList = ocLay.getSizeList();
            String actualItemCode = itemCode;
            if(SizeList.size() > 0 ) {
                OcLayDetail ocLayDetail = SizeList.get(0);
                actualItemCode = ocLayDetail.getItemCode();
            }
            CommonUtilsJobUnique jobUnique = new CommonUtilsJobUnique(ocLay.getOcNo(), ocLay.getLay(), ocLay.getFitType(), actualItemCode);
             if(validateJobUniqueObjects(jobUnique, commonUtilsJobUnique)) {
                return ocLay;
            }
        }
        return null;
    }

    /**
     * Checks if a specific ocLay exists within the list
     * @param uniqueJobMap Hash map for unique job match
     * @param commonUtilsJobUnique Job unique object to compare
     * @param jobId Job id to get commonUtilsJobUnique list
     * @return
     */
    private static boolean validateCommonUtilsJobUniqueExist(HashMap<Long, ArrayList<CommonUtilsJobUnique>> uniqueJobMap, CommonUtilsJobUnique commonUtilsJobUnique, Long jobId) {
        if(uniqueJobMap.containsKey(jobId)) {
            ArrayList<CommonUtilsJobUnique> commonUtilsJobUniquesList = uniqueJobMap.get(jobId);
            for (CommonUtilsJobUnique jobUnique : commonUtilsJobUniquesList) {
                if (validateJobUniqueObjects(jobUnique, commonUtilsJobUnique)) {
                    return true;
                }
            }
        }
        return false;
    }


    private static boolean validateJobUniqueObjects(CommonUtilsJobUnique jobUnique, CommonUtilsJobUnique commonUtilsJobUnique) {
    if(jobUnique.getOcNo().equals(commonUtilsJobUnique.getOcNo()) &&
            jobUnique.getItemCode().equals(commonUtilsJobUnique.getItemCode()) &&
            jobUnique.getFitType().equals(commonUtilsJobUnique.getFitType()) &&
    jobUnique.getLay().equals(commonUtilsJobUnique.getLay())){
        return true;
    }
    return false;
    }


    /**
     * Provides a progress dialog
     *
     * @param context Context from where it is being called
     * @param showText Whether to show text on dialog or not
     * @param textId Resource id for text
     * @return ProgressDialog
     */

    public static ProgressDialog showLoadingDialog(Context context, boolean showText, int textId) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        if(showText){
            View view = progressDialog.getWindow().getDecorView();
            TextView additionalText = view.findViewById(R.id.additional_text);
            additionalText.setVisibility(View.VISIBLE);
            additionalText.setText(textId);
        }
        return progressDialog;
    }

    /**
     * Get a formatted description for number of items
     *
     * @param items List of items
     * @return Formatted description
     */

    public static String getItemDescription(List<Item> items){
        String description = "";
        if(items == null || items.size() == 0) return "";
        for(int i = 0; i < items.size(); i++){
            if(i == items.size() - 1){
                description = description + items.get(i).getItemCode() + ", " + items.get(i).getItemDesc();
            } else {
                description = description + items.get(i).getItemCode() + ", " + items.get(i).getItemDesc() + ",";
            }
        }
        return description;
    }

    /**
     * Get formatted string for item detail
     * @param itemCode Item code
     * @param itemDescription Item description
     * @return Formatted item description
     */

    public static String getItemDescription(String itemCode, String itemDescription){
        return itemCode + ", " +  itemDescription;
    }

    /**
     * Gets the Device ID via context passed
     *
     * @param context Context from where the function is executed
     * @return Device ID
     */

    @SuppressLint("all")
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Provides a Alert Dialog
     *
     * @param context             Context from where it is being called
     * @param message             Message to be shown on the AlertDialog
     * @param dialogClickListener Listener to handle the click events
     */

    public static void showAlertDialog(Context context, String message, DialogInterface.OnClickListener dialogClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
        builder.setMessage(message).setPositiveButton(R.string.dialog_ok, dialogClickListener)
                .setNegativeButton(R.string.dialog_cancel, dialogClickListener).show();
    }

    /**
     * Provides a Alert Dialog with dismiss button
     *
     * @param context             Context from where it is being called
     * @param message             Message to be shown on the AlertDialog
     */

    public static void showAlertDialog(Context context, int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
        builder.setMessage(message).setPositiveButton(R.string.dialog_dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    /**
     * Provides a Alert Dialog
     *
     * @param context             Context from where it is being called
     * @param message             Message to be shown on the AlertDialog
     */

    public static void showConfirmationDialog(Context context, int message) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(message).setPositiveButton(R.string.dialog_dismiss, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
    }

    /**
     * Provides a Alert Dialog
     *
     * @param context             Context from where it is being called
     * @param positiveText        Text for positive button
     * @param negativeText        Text for negative button
     * @param message             Message to be shown on the AlertDialog
     * @param dialogClickListener Listener to handle the click events
     */

    public static void showAlertDialog(Context context, int positiveText, int negativeText, String message, DialogInterface.OnClickListener dialogClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setPositiveButton(positiveText, dialogClickListener)
                .setNegativeButton(negativeText, dialogClickListener).show();
    }

    /**
     * Truncates additional decimal places from double
     * @param toBeTruncated Value to be trucated
     * @return two decimal point double
     */

    public static Double getPrecisionDouble(Double toBeTruncated){
        Double truncatedDouble = BigDecimal.valueOf(toBeTruncated)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
        return truncatedDouble;
    }

    /**
     * Provides a Alert Dialog
     *
     * @param context             Context from where it is being called
     * @param message             Resource ID of string to be shown on the AlertDialog
     * @param dialogClickListener Listener to handle the click events
     */

    public static void showAlertDialog(Context context, int message, DialogInterface.OnClickListener dialogClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setPositiveButton(R.string.dialog_ok, dialogClickListener)
                .setNegativeButton(R.string.dialog_cancel, dialogClickListener).show();
    }
}
