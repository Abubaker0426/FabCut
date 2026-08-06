package com.indiandesigns.fabcut.ui.main.location_picker_dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.indiandesigns.fabcut.R;
import com.indiandesigns.fabcut.di.component.ActivityComponent;
import com.indiandesigns.fabcut.ui.base.BaseDialog;
import com.indiandesigns.fabcut.ui.common.ListAdapter;
import com.indiandesigns.fabcut.ui.common.RecyclerItemClickListener;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static java.security.AccessController.getContext;

/**
 * Show the location picker dialog and open respective activity
 */

public class LocationPickerDialog extends BaseDialog implements LocationPickerDialogMvpView {

    private static final String TAG = "RateUsDialog";

    @Inject
    ListAdapter mLocationAdapter;

    @Inject
    LinearLayoutManager mLayoutManager;

    @BindView(R.id.location_recycler_view)
    RecyclerView mRecyclerView;

    private List<String> mList;

    @Inject
    LocationPickerDialogMvpPresenter<LocationPickerDialogMvpView> mPresenter;

    /**
     * Returns a new instance of the Dialog
     *
     * @return Location picker dialog
     */

    public static LocationPickerDialog newInstance() {
        LocationPickerDialog fragment = new LocationPickerDialog();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_location_picker, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            setUnBinder(ButterKnife.bind(this, view));
            mPresenter.onAttach(this);
        }

        return view;
    }

    /**
     * Shows the dialog and set's the value for instance variables
     *
     * @param fragmentManager FragmentManager instance from where it is being called
     * @param list List of locations for the user to select from
     */

    public void show(FragmentManager fragmentManager, List<String> list) {
        super.show(fragmentManager, TAG);
        mList = list;
    }

    /**
     * Sets up initial values and views
     *
     * Sets the on click listener on RecyclerView item using {@link RecyclerItemClickListener}
     * which updates the location for the session
     *
     * @param view View of the dialog
     */

    @Override
    protected void setUp(View view) {
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mLocationAdapter);

        mLocationAdapter.addItems(mList);

        mRecyclerView.addOnItemTouchListener(
            new RecyclerItemClickListener(getContext(), mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                @Override public void onItemClick(View view, int position) {
                    mPresenter.updateLocation(mList.get(position));
                }

                @Override public void onLongItemClick(View view, int position) {
                }
            })
        );
    }

    /**
     * Dismisses the dialog with custom TAG
     */

    @Override
    public void dismissDialog() {
        super.dismissDialog(TAG);

    }

    @Override
    public void onDestroyView() {
        mPresenter.onDetach();
        super.onDestroyView();
    }
}