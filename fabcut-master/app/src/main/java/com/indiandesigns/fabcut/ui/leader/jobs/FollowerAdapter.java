package com.indiandesigns.fabcut.ui.leader.jobs;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.indiandesigns.fabcut.R;
import com.indiandesigns.fabcut.data.network.enums.Status;
import com.indiandesigns.fabcut.data.network.model.Follower;
import com.indiandesigns.fabcut.ui.base.BaseViewHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter for showing the list of followers
 * Extends {@link BaseViewHolder}
 */

public class FollowerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<Follower> followerList;
    private boolean selectionMode = false;

    public FollowerAdapter(List<Follower> followerList) {
        this.followerList = followerList;
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
                LayoutInflater.from(parent.getContext()).inflate(R.layout.follower_item, parent, false));
    }

    public void setSelectionMode(boolean selectionMode) {
        this.selectionMode = selectionMode;
    }

    @Override
    public int getItemCount() {
        return followerList.size();
    }

    public void addItems(List<Follower> followers) {
        followerList.clear();
        followerList.addAll(followers);
        notifyDataSetChanged();
    }

    public interface Callback {
        void onItemClickListener(Follower follower);
        void onDelete(Follower follower);
    }

    public class ViewHolder extends BaseViewHolder {

        @BindView(R.id.status)
        TextView statusTextView;

        @BindView(R.id.table_number)
        TextView tableNumberTextView;

        @BindView(R.id.follower_card)
        CardView followerCard;

        @BindView(R.id.delete)
        ImageView deleteJob;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        protected void clear() {
            statusTextView.setText("");
            tableNumberTextView.setText("");
        }

        public void onBind(int position) {
            super.onBind(position);

            Follower follower = followerList.get(position);
            Context context = itemView.getContext();

            if(follower.getStatus() != null){
                Status status = follower.getStatus();
                // apply item click listener if status is IDLE
                if(selectionMode && status.equals(Status.BUSY)){
                    itemView.setOnClickListener(view -> {});
                } else {
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mCallback.onItemClickListener(follower);
                        }
                    });
                }

                deleteJob.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCallback.onDelete(follower);
                    }
                });

                statusTextView.setText(status.getValue());
                tableNumberTextView.setText(String.valueOf(follower.getTableNumber()));
            }

            if(selectionMode) {
                deleteJob.setVisibility(View.GONE);
                Status followerStatus = follower.getStatus();
                if (followerStatus.equals(Status.IDLE)) {
                    followerCard.setBackgroundColor(context.getResources().getColor(R.color.colorGreenPastel));
                } else if (followerStatus.equals(Status.BUSY)) {
                    followerCard.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                }
            } else {
                deleteJob.setVisibility(View.VISIBLE);
                followerCard.setBackgroundColor(context.getResources().getColor(R.color.colorLightBlue));
            }

        }
    }

}
