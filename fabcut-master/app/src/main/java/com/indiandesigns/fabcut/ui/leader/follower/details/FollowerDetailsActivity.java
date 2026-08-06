package com.indiandesigns.fabcut.ui.leader.follower.details;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.indiandesigns.fabcut.R;
import com.indiandesigns.fabcut.data.network.enums.JobType;
import com.indiandesigns.fabcut.data.network.enums.ListType;
import com.indiandesigns.fabcut.data.network.model.FetchFollowerEndBitJobResponse;
import com.indiandesigns.fabcut.data.network.model.FetchFollowerResponse;
import com.indiandesigns.fabcut.ui.base.BaseActivity;

import java.util.List;

public class FollowerDetailsActivity extends BaseActivity implements FollowerDetailsMvpView {

    @Inject
    FollowerDetailsPresenter<FollowerDetailsMvpView> mPresenter;

    private static String FOLLOWER_RESPONSE = "followerResponse";
    private FetchFollowerResponse response;

    private static String FOLLOWER_END_BIT_RESPONSE = "followerEndBitResponse";
    private FetchFollowerEndBitJobResponse endBitJobResponse;

    private static String JOB_TYPE = "jobType";
    private JobType jobType;

    @BindView(R.id.details_container)
    LinearLayout detailsContainer;

    public static Intent getStartIntent(Context context, FetchFollowerResponse fetchFollowerResponse) {
        Intent intent = new Intent(context, FollowerDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(FOLLOWER_RESPONSE, fetchFollowerResponse);
        bundle.putSerializable(JOB_TYPE, JobType.NORMAL);
        intent.putExtras(bundle);

        return intent;
    }

    public static Intent getStartIntentForEndBit(Context context, FetchFollowerEndBitJobResponse endBitJobResponse) {
        Intent intent = new Intent(context, FollowerDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(FOLLOWER_END_BIT_RESPONSE, endBitJobResponse);
        bundle.putSerializable(JOB_TYPE, JobType.END_BIT);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower_details);

        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));
        mPresenter.onAttach(FollowerDetailsActivity.this);

        response = (FetchFollowerResponse) getIntent().getExtras().getSerializable(FOLLOWER_RESPONSE);
        endBitJobResponse = (FetchFollowerEndBitJobResponse) getIntent().getExtras().getSerializable(FOLLOWER_END_BIT_RESPONSE);
        jobType = (JobType) getIntent().getExtras().getSerializable(JOB_TYPE);
        setUp();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDetach();
        super.onDestroy();
    }

    @OnClick(R.id.go_back)
    public void back(){
        finish();
    }

    /**
     * Update view with item description
     * Update view with size wise quantities
     * @param itemDesc Item Description
     * @param listData Parsed list Data
     */

    @Override
    public void updateView(String itemDesc, List<List<String>> listData) {
        LayoutInflater factory = LayoutInflater.from(this);
        View view = factory.inflate(R.layout.follower_deatil_item, null);
        LinearLayout parentLayout = (LinearLayout) view;

        TextView descriptionTextView = (TextView) parentLayout.findViewById(R.id.description);
        LinearLayout sizeContainer = (LinearLayout) parentLayout.findViewById(R.id.size_container);
        LinearLayout sizeQuantitiesContainer = (LinearLayout) parentLayout.findViewById(R.id.size_quantity_container);

        descriptionTextView.setText(itemDesc);
        addTextViewToLayout(sizeContainer, listData.get(ListType.SIZE.getValue()), this, true);
        addTextViewToLayout(sizeQuantitiesContainer, listData.get(ListType.QUANTITY.getValue()), this, true);

        detailsContainer.addView(parentLayout);
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
    protected void setUp() {
        if(jobType == JobType.END_BIT) {
            mPresenter.parseEndBitDetails(endBitJobResponse);
            return;
        }
        mPresenter.parseDetails(response);
    }
}