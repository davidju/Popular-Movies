package com.davidju.popularmoviesone.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.davidju.popularmoviesone.R;
import com.davidju.popularmoviesone.activities.TrailerActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder> {

    private List<String> trailers;

    public void updateTrailers(List<String> trailers) {
        this.trailers = trailers;
        notifyDataSetChanged();
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trailer, parent, false);
        TrailerViewHolder viewHolder = new TrailerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final TrailerViewHolder viewHolder, final int position) {
        viewHolder.trailerCount.setText(String.valueOf(position + 1));
        viewHolder.trailerCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = viewHolder.trailerCount.getContext();
                Intent intent = new Intent(context, TrailerActivity.class);
                intent.putExtra(TrailerActivity.TRAILER_KEY, trailers.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.trailer_count) TextView trailerCount;

        public TrailerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
