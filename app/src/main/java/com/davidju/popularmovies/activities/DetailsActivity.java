package com.davidju.popularmovies.activities;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.davidju.popularmovies.FetchTrailersTask;
import com.davidju.popularmovies.GlideApp;
import com.davidju.popularmovies.R;
import com.davidju.popularmovies.adapters.MoviesAdapter;
import com.davidju.popularmovies.adapters.TrailersAdapter;
import com.davidju.popularmovies.interfaces.AsyncResponse;
import com.davidju.popularmovies.models.Movie;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/** Activity that controls view for movie details */
public class DetailsActivity extends Activity implements AsyncResponse{

    @BindView(R.id.title) TextView title;
    @BindView(R.id.poster) ImageView poster;
    @BindView(R.id.synopsis_content) TextView synopsis;
    @BindView(R.id.rating_content) TextView rating;
    @BindView(R.id.release_date_content) TextView releaseDate;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        // Set status bar color to black
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));
        }

        Movie movie = getIntent().getParcelableExtra("movie");

        title.setText(movie.getTitle());
        GlideApp.with(DetailsActivity.this)
                .load(Uri.parse(MoviesAdapter.baseUrl + movie.getPosterPath()))
                .fitCenter()
                .into(poster);
        synopsis.setText(movie.getSynopsis());
        rating.setText(movie.getRating());
        releaseDate.setText(movie.getReleaseDate());

        FetchTrailersTask trailersTask = new FetchTrailersTask();
        trailersTask.response = this;
        trailersTask.execute(movie.getId());
    }

    @Override
    public void processFinish(List<String> results) {
        recyclerView.setLayoutManager(new GridLayoutManager(DetailsActivity.this, 3));
        TrailersAdapter adapter = new TrailersAdapter();
        adapter.updateTrailers(results);
        System.out.println(results.size());
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
    }
}
