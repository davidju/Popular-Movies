package com.davidju.popularmoviesone.activities;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.davidju.popularmoviesone.GlideApp;
import com.davidju.popularmoviesone.R;
import com.davidju.popularmoviesone.adapters.MoviesAdapter;
import com.davidju.popularmoviesone.models.Movie;

import butterknife.BindView;
import butterknife.ButterKnife;

/** Activity that controls view for movie details */
public class DetailsActivity extends Activity {

    @BindView(R.id.title) TextView title;
    @BindView(R.id.poster) ImageView poster;
    @BindView(R.id.synopsis_content) TextView synopsis;
    @BindView(R.id.rating_content) TextView rating;
    @BindView(R.id.release_date_content) TextView releaseDate;

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

        Movie movie = (Movie) getIntent().getParcelableExtra("movie");

        title.setText(movie.getTitle());
        GlideApp.with(DetailsActivity.this)
                .load(Uri.parse(MoviesAdapter.baseUrl + movie.getPosterPath()))
                .fitCenter()
                .into(poster);
        synopsis.setText(movie.getSynopsis());
        rating.setText(movie.getRating());
        releaseDate.setText(movie.getReleaseDate());
    }
}
