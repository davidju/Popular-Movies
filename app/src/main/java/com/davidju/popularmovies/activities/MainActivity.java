package com.davidju.popularmovies.activities;

import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.davidju.popularmovies.adapters.MoviesAdapter;
import com.davidju.popularmovies.asynctasks.FetchMoviesTask;
import com.davidju.popularmovies.R;
import com.davidju.popularmovies.database.FavoritesContract;
import com.davidju.popularmovies.enums.SortType;
import com.davidju.popularmovies.interfaces.MainAsyncResponse;
import com.davidju.popularmovies.models.Movie;
import com.davidju.popularmovies.database.FavoritesContract.*;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainAsyncResponse {

    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.error_view) TextView errorMessage;
    MoviesAdapter moviesAdapter;
    SortType sortType;

    @Override @SuppressWarnings("ConstantConditions")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Set action bar color to black
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.black)));

        // Set status bar color to black
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));
        }

        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
        moviesAdapter = new MoviesAdapter();
        recyclerView.setAdapter(moviesAdapter);

        // Default to show popular movies upon entering app
        FetchMoviesTask moviesTask = new FetchMoviesTask(MainActivity.this);
        moviesTask.response = this;
        moviesTask.execute(SortType.POPULAR);
        sortType = SortType.POPULAR;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Handles case in which a move is removed from favorites; refreshes movie list so the
        // unselected movie no longer appears in the view
        if (sortType == SortType.FAVORITES) {
            showFavorites();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sort_popular) {
            FetchMoviesTask popularMoviesTask = new FetchMoviesTask(MainActivity.this);
            popularMoviesTask.response = this;
            popularMoviesTask.execute(SortType.POPULAR);
            sortType = SortType.POPULAR;
            return true;
        } else if (id == R.id.sort_rating) {
            FetchMoviesTask topRatedMoviesTask = new FetchMoviesTask(MainActivity.this);
            topRatedMoviesTask.response = this;
            topRatedMoviesTask.execute(SortType.TOP_RATED);
            sortType = SortType.TOP_RATED;
            return true;
        } else if (id == R.id.favorites) {
            showFavorites();
            sortType = SortType.FAVORITES;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void processMovieResults(List<Movie> results) {
        if (results.isEmpty()) {
            if (errorMessage.getVisibility() == View.INVISIBLE) {
                errorMessage.setText(getString(R.string.error_no_movies));
                errorMessage.setVisibility(View.VISIBLE);
            }
            if (recyclerView.getVisibility() == View.VISIBLE) {
                recyclerView.setVisibility(View.INVISIBLE);
            }
        } else {
            moviesAdapter.updateMovieList(results);
            moviesAdapter.notifyDataSetChanged();

            if (errorMessage.getVisibility() == View.VISIBLE) {
                errorMessage.setVisibility(View.INVISIBLE);
            }
            if (recyclerView.getVisibility() == View.INVISIBLE) {
                recyclerView.setVisibility(View.VISIBLE);
            }
            recyclerView.smoothScrollToPosition(0);
        }
    }

    /* Query and load user's favorite movies from local storage via Content Provider */
    private void showFavorites() {
        List<Movie> movies = new ArrayList<>();

        Cursor cursor = getContentResolver().query(FavoritesContract.FavoritesEntry.CONTENT_URI,
                null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Movie movie = new Movie();
                    movie.setId(cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_ID)));
                    movie.setTitle(cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_TITLE)));
                    movie.setPosterPath(cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_POSTER)));
                    movie.setSynopsis(cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_SYNOPSIS)));
                    movie.setRating(cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_RATING)));
                    movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_RELEASE_DATE)));
                    movies.add(movie);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        if (movies.isEmpty()) {
            if (errorMessage.getVisibility() == View.INVISIBLE) {
                errorMessage.setText(getString(R.string.error_no_favorites));
                errorMessage.setVisibility(View.VISIBLE);
            }
            if (recyclerView.getVisibility() == View.VISIBLE) {
                recyclerView.setVisibility(View.INVISIBLE);
            }
        } else {
            moviesAdapter.updateMovieList(movies);
            moviesAdapter.notifyDataSetChanged();

            if (errorMessage.getVisibility() == View.VISIBLE) {
                errorMessage.setVisibility(View.INVISIBLE);
            }
            if (recyclerView.getVisibility() == View.INVISIBLE) {
                recyclerView.setVisibility(View.VISIBLE);
            }
            recyclerView.smoothScrollToPosition(0);
        }
    }
}
