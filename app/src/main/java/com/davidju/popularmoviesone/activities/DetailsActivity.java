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

/** Activity that controls view for movie details */
public class DetailsActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Set status bar color to black
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));
        }

        Movie movie = (Movie) getIntent().getSerializableExtra("movie");

        TextView title = findViewById(R.id.title);
        title.setText(movie.getTitle());

        ImageView poster = findViewById(R.id.poster);
        GlideApp.with(DetailsActivity.this)
                .load(Uri.parse(MoviesAdapter.baseUrl + movie.getPosterPath()))
                .fitCenter()
                .into(poster);

        TextView synopsis = findViewById(R.id.synopsis_content);
        synopsis.setText(movie.getSynopsis());

        TextView rating = findViewById(R.id.rating_content);
        rating.setText(movie.getRating());

        TextView releaseDate = findViewById(R.id.release_date_content);
        releaseDate.setText(movie.getReleaseDate());
    }
}
