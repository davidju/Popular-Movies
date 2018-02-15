package com.davidju.popularmoviesone.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.davidju.popularmoviesone.R;
import com.davidju.popularmoviesone.adapters.MoviesAdapter;
import com.davidju.popularmoviesone.enums.SortType;
import com.davidju.popularmoviesone.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivityFragment extends Fragment {

    private GridView gridView;
    private MoviesAdapter moviesAdapter;

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        gridView = rootView.findViewById(R.id.grid_view);
        moviesAdapter = new MoviesAdapter(getContext());
        gridView.setAdapter(moviesAdapter);

        new FetchMoviesTask().execute(SortType.POPULAR);

        return rootView;
    }

    private class FetchMoviesTask extends AsyncTask<SortType, Void, String> {
        @Override
        protected String doInBackground(SortType... params) {
            try {
                URL url = buildUrl(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                inputStream.close();
                reader.close();

                return buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String json) {
            processJson(json);
        }
    }

    private URL buildUrl(SortType sortType) {
        String url = "http://api.themoviedb.org/3/movie/";
        if (sortType == SortType.POPULAR) {
            url += "popular";
        } else if (sortType == SortType.TOP_RATED) {
            url += "top_rated";
        }
        url += "?api_key=" + getString(R.string.tmdb_api_key);

        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void processJson(String json) {
        final String KEY_RESULTS = "results";
        final String KEY_TITLE = "original_title";
        final String KEY_POSTER_PATH = "poster_path";
        final String KEY_OVERVIEW = "overview";
        final String KEY_RATING = "vote_average";
        final String KEY_RELEASE_DATE = "release_date";


        List<Movie> movies = new ArrayList<>();

        try {
            JSONObject results = new JSONObject(json);
            JSONArray moviesArr = results.getJSONArray(KEY_RESULTS);

            for (int i = 0; i < moviesArr.length(); i++) {
                JSONObject info = moviesArr.getJSONObject(i);
                Movie movie = new Movie();

                movie.setTitle(info.optString(KEY_TITLE));
                movie.setPosterPath(info.optString(KEY_POSTER_PATH));
                movie.setSynopsis(info.optString(KEY_OVERVIEW));
                movie.setRating(info.optString(KEY_RATING));
                movie.setReleaseDate(info.optString(KEY_RELEASE_DATE));

                movies.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        moviesAdapter.clear();
        moviesAdapter.addAll(movies);
        moviesAdapter.notifyDataSetChanged();
    }
}
