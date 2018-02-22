package com.davidju.popularmovies.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.davidju.popularmovies.GlideApp;
import com.davidju.popularmovies.R;
import com.davidju.popularmovies.adapters.MoviesAdapter;
import com.davidju.popularmovies.asynctasks.FetchReviewsTask;
import com.davidju.popularmovies.asynctasks.FetchTrailersTask;
import com.davidju.popularmovies.database.FavoritesContract.FavoritesEntry;
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
    @BindView(R.id.favorite_title) TextView favoriteTitle;
    @BindView(R.id.icon_favorite) ImageView favoritesButton;
    @BindView(R.id.synopsis_content) TextView synopsis;
    @BindView(R.id.rating_content) TextView rating;
    @BindView(R.id.release_date_content) TextView releaseDate;
    @BindView(R.id.trailers) LinearLayout trailers;
    @BindView(R.id.reviews) LinearLayout reviews;

    private boolean isFavorite;

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

        isFavorite = isFavorite(movie.getId());
        toggleFavoriteButton(isFavorite);

        favoritesButton.setOnClickListener(view -> {
            if (!isFavorite) {
                insertFavorite(movie);
                isFavorite = true;
                toggleFavoriteButton(true);
            } else {
                removeFavorite(movie);
                isFavorite = false;
                toggleFavoriteButton(false);
            }
        });

        FetchTrailersTask trailersTask = new FetchTrailersTask(this);
        trailersTask.response = this;
        trailersTask.execute(movie.getId());

        FetchReviewsTask reviewsTask = new FetchReviewsTask(this);
        reviewsTask.response = this;
        reviewsTask.execute(movie.getId());
    }

    @Override
    public void processTrailerResults(List<Trailer> results) {
        if (!results.isEmpty()) {
            for (Trailer trailer : results) {
                View item = getLayoutInflater().inflate(R.layout.item_trailer, trailers, false);

                TextView name = item.findViewById(R.id.trailer_name);
                name.setText(trailer.getName());

                ImageView play = item.findViewById(R.id.trailer_play);
                play.setOnClickListener(view -> {
                    String link = "https://www.youtube.com/watch?v=" + trailer.getKey();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    startActivity(intent);
                });

                trailers.addView(item);
            }
        } else {
            TextView item = (TextView) getLayoutInflater().inflate(R.layout.item_error, trailers, false);
            item.setText(getString(R.string.error_no_trailers));
            trailers.addView(item);
        }
    }

    @Override
    public void processReviewResults(List<Review> results) {
        if (!results.isEmpty()) {
            for (Review review : results) {
                View item = getLayoutInflater().inflate(R.layout.item_review, reviews, false);

                TextView content = item.findViewById(R.id.content);
                content.setText(review.getContent());

                TextView author = item.findViewById(R.id.author);
                author.setText(getString(R.string.detail_reviews_author, review.getAuthor()));

                reviews.addView(item);
            }
        } else {
            TextView item = (TextView) getLayoutInflater().inflate(R.layout.item_error, trailers, false);
            item.setText(getString(R.string.error_no_reviews));
            reviews.addView(item);
        }
    }

    @Override
    public void reportTrailersNetworkError() {
        TextView item = (TextView) getLayoutInflater().inflate(R.layout.item_error, trailers, false);
        item.setText(getString(R.string.error_no_network_trailers));
        trailers.addView(item);
    }

    @Override
    public void reportReviewsNetworkError() {
        TextView item = (TextView) getLayoutInflater().inflate(R.layout.item_error, trailers, false);
        item.setText(getString(R.string.error_no_network_reviews));
        reviews.addView(item);
    }

    /* Toggles favorite icon and messages from selected to unselected state or vice versa */
    private void toggleFavoriteButton(boolean isFavorite) {
        if (isFavorite) {
            favoriteTitle.setText(getString(R.string.details_favorite_selected_title));
            favoritesButton.setImageDrawable(ContextCompat.getDrawable(DetailsActivity.this, R.drawable.favorite_icon_selected));
        } else {
            favoriteTitle.setText(getString(R.string.details_favorite_unselected_title));
            favoritesButton.setImageDrawable(ContextCompat.getDrawable(DetailsActivity.this, R.drawable.favorite_icon_unselected));
        }
    }

    /* Check if current movie being shown is included in the user's favorite list */
    private boolean isFavorite(String id) {
        Cursor cursor = getContentResolver().query(FavoritesEntry.CONTENT_URI, null,
                FavoritesEntry.COLUMN_ID + " = ?", new String[]{id}, null);
        boolean isFavorite = cursor != null && cursor.moveToFirst();
        cursor.close();
        return isFavorite;
    }

    /* Add the current movie to the user's favorite list */
    private void insertFavorite(Movie movie) {
        ContentValues values = new ContentValues();
        values.put(FavoritesEntry.COLUMN_ID, movie.getId());
        values.put(FavoritesEntry.COLUMN_TITLE, movie.getTitle());
        values.put(FavoritesEntry.COLUMN_POSTER, movie.getPosterPath());
        values.put(FavoritesEntry.COLUMN_SYNOPSIS, movie.getSynopsis());
        values.put(FavoritesEntry.COLUMN_RATING, movie.getRating());
        values.put(FavoritesEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        getContentResolver().insert(FavoritesEntry.CONTENT_URI, values);
    }

    /* Remove the current movie from the user's favorite list */
    private void removeFavorite(Movie movie) {
        getContentResolver().delete(FavoritesEntry.CONTENT_URI, FavoritesEntry.COLUMN_ID + " = ?", new String[]{movie.getId()});
    }
}
