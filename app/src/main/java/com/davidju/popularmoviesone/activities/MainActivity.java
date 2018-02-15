package com.davidju.popularmoviesone.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.davidju.popularmoviesone.FetchMoviesTask;
import com.davidju.popularmoviesone.R;
import com.davidju.popularmoviesone.enums.SortType;
import com.davidju.popularmoviesone.fragments.MainActivityFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        }

        return super.onOptionsItemSelected(item);
    }
}
