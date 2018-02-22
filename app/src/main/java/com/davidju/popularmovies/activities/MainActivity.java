package com.davidju.popularmovies.activities;

import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.davidju.popularmovies.asynctasks.FetchMoviesTask;
import com.davidju.popularmovies.R;
import com.davidju.popularmovies.database.FavoritesContract;
import com.davidju.popularmovies.enums.SortType;
import com.davidju.popularmovies.fragments.MainActivityFragment;
import com.davidju.popularmovies.models.Movie;
import com.davidju.popularmovies.database.FavoritesContract.*;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override @SuppressWarnings("ConstantConditions")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            new FetchMoviesTask(MainActivity.this).execute(SortType.POPULAR);
            return true;
        } else if (id == R.id.sort_rating) {
            new FetchMoviesTask(MainActivity.this).execute(SortType.TOP_RATED);
            return true;
        } else if (id == R.id.favorites) {
            showFavorites();
        }

        return super.onOptionsItemSelected(item);
    }

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
                    movie.setRating(cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_RELEASE_DATE)));
                    movies.add(movie);
                } while (cursor.moveToNext());
            }
            cursor.close();

        }

        MainActivityFragment.moviesAdapter.updateMovieList(movies);
        MainActivityFragment.moviesAdapter.notifyDataSetChanged();

        MainActivityFragment.recyclerView.smoothScrollToPosition(0);
    }
}
