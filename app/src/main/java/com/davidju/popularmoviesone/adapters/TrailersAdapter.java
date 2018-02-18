package com.davidju.popularmoviesone.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.davidju.popularmoviesone.R;

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
        viewHolder.trailerCount.setText(String.valueOf(viewHolder.getAdapterPosition() + 1));
        viewHolder.trailerCount.setOnClickListener(view -> {
            Context context = viewHolder.trailerCount.getContext();
            String url = "http://youtube.com/watch?v=" + trailers.get(viewHolder.getAdapterPosition());
            // Alternative is to use YoutubePlayerApi from Google, but that requires registering
            // this application to obtain a key. For now, launch and play trailer using the
            // following intent.
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
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
