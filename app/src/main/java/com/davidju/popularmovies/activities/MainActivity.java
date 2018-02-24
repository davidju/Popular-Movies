package com.davidju.popularmovies.activities;

import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
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

    private static final String KEY_LAYOUT_STATE = "layout_state";

    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.error_view) TextView errorMessage;
    MoviesAdapter moviesAdapter;

    static SortType sortType = SortType.POPULAR;
    Parcelable layoutState;

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

        // Get LayoutManager state during onCreate because AsyncTask to fetch movie data is also executed
        // during onCreate. onRestoreInstanceState only called after onStart(), so this is to avoid the possibility
        // that the state is restored during onRestoreInstanceState before AsyncTask finishes retrieving
        // the data. LayoutManager state is stored in a local variable and only restored once the adapter has
        // been populated with data.
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_LAYOUT_STATE)) {
            layoutState = savedInstanceState.getParcelable(KEY_LAYOUT_STATE);
        }

        if (sortType != SortType.FAVORITES) {
            FetchMoviesTask moviesTask = new FetchMoviesTask(MainActivity.this);
            moviesTask.response = this;
            moviesTask.execute(sortType);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Handles case in which a movie is removed from favorites; refreshes movie list so the
        // unselected movie no longer appears in the view
        if (sortType == SortType.FAVORITES) {
            showFavorites();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_LAYOUT_STATE, recyclerView.getLayoutManager().onSaveInstanceState());
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
            restoreLayoutState();

            moviesAdapter.updateMovieList(results);
            moviesAdapter.notifyDataSetChanged();

            if (errorMessage.getVisibility() == View.VISIBLE) {
                errorMessage.setVisibility(View.INVISIBLE);
            }
            if (recyclerView.getVisibility() == View.INVISIBLE) {
                recyclerView.setVisibility(View.VISIBLE);
            }
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
            restoreLayoutState();

            moviesAdapter.updateMovieList(movies);
            moviesAdapter.notifyDataSetChanged();

            if (errorMessage.getVisibility() == View.VISIBLE) {
                errorMessage.setVisibility(View.INVISIBLE);
            }
            if (recyclerView.getVisibility() == View.INVISIBLE) {
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    /** Restore previous LayoutManager state if it exists */
    private void restoreLayoutState() {
        // Set listener to detect when the adapter has updated the RecyclerView with new data
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                recyclerView.removeOnLayoutChangeListener(this);

                // Best solution to restore scroll state in a RecyclerView I could find so far. Using a
                // Runnable is required to make this work. Traditional ways of doing so do not work -
                // I suspect it has something to do with the fact that the movie
                // poster images are being loaded asynchronously, but not sure what I can do resolve it.
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (layoutState != null) {
                            recyclerView.getLayoutManager().onRestoreInstanceState(layoutState);
                            layoutState = null;
                        }
                    }
                }, 20);
            }
        });
    }
}
