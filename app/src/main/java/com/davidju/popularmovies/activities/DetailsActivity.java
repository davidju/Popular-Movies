package com.davidju.popularmovies.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.davidju.popularmovies.asynctasks.FetchReviewsTask;
import com.davidju.popularmovies.asynctasks.FetchTrailersTask;
import com.davidju.popularmovies.GlideApp;
import com.davidju.popularmovies.R;
import com.davidju.popularmovies.adapters.MoviesAdapter;
import com.davidju.popularmovies.interfaces.AsyncResponse;
import com.davidju.popularmovies.models.Movie;
import com.davidju.popularmovies.models.Review;
import com.davidju.popularmovies.models.Trailer;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/** Activity that controls view for movie details */
public class DetailsActivity extends Activity implements AsyncResponse {

    @BindView(R.id.title) TextView title;
    @BindView(R.id.poster) ImageView poster;
    @BindView(R.id.synopsis_content) TextView synopsis;
    @BindView(R.id.rating_content) TextView rating;
    @BindView(R.id.release_date_content) TextView releaseDate;
    @BindView(R.id.trailers) LinearLayout trailers;
    @BindView(R.id.reviews) LinearLayout reviews;

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

        FetchReviewsTask reviewsTask = new FetchReviewsTask();
        reviewsTask.response = this;
        reviewsTask.execute(movie.getId());
    }

    @Override
    public void processTrailerResults(List<Trailer> results) {
        for (Trailer trailer : results) {
            ConstraintLayout item = (ConstraintLayout) getLayoutInflater().inflate(R.layout.item_trailer, trailers, false);
            TextView name = item.findViewById(R.id.trailer_name);
            name.setText(trailer.getName());
            TextView play = item.findViewById(R.id.trailer_play);
            play.setOnClickListener(view -> {
                String link = "https://www.youtube.com/watch?v=" + trailer.getKey();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(intent);
            });
            trailers.addView(item);
        }
    }

    @Override
    public void processReviewResults(List<Review> results) {
        for (Review review : results) {
            TextView item = new TextView(DetailsActivity.this);
            item.setText(review.getContent());
            item.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
            reviews.addView(item);
        }
    }
}
