package com.davidju.popularmoviesone;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import com.davidju.popularmoviesone.enums.SortType;
import com.davidju.popularmoviesone.fragments.MainActivityFragment;
import com.davidju.popularmoviesone.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/** AsyncTask that fetches movies from TMDB based on specified search criteria */
public class FetchMoviesTask extends AsyncTask<SortType, Void, String> {
    private final WeakReference<Context> contextReference;

    public FetchMoviesTask(Context context) {
        contextReference = new WeakReference<>(context);
        if (!isNetworkAvailable()) {
            cancel(true);
            Toast.makeText(context, context.getString(R.string.toast_no_network), Toast.LENGTH_LONG).show();
        }
    }

    @Override @SuppressWarnings("ConstantConditions")
    protected String doInBackground(SortType... params) {
        if (!isCancelled()) {
            try {
                URL url = buildUrl(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder buffer = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                    buffer.append("\n");
                }

                inputStream.close();
                reader.close();

                return buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String json) {
        processJson(json);
    }

    /* Create URL for HTTP request */
    private URL buildUrl(SortType sortType) {
        String url = "http://api.themoviedb.org/3/movie/";
        if (sortType == SortType.POPULAR) {
            url += "popular";
        } else if (sortType == SortType.TOP_RATED) {
            url += "top_rated";
        }
        Context context = contextReference.get();
        url += "?api_key=" + context.getString(R.string.tmdb_api_key);

        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /* Parse JSON results */
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

        MainActivityFragment.moviesAdapter.clear();
        MainActivityFragment.moviesAdapter.addAll(movies);
        MainActivityFragment.moviesAdapter.notifyDataSetChanged();

        MainActivityFragment.gridView.smoothScrollToPosition(0);
    }

    /* Check if device currently has network has network access */
    private boolean isNetworkAvailable() {
        Context context = contextReference.get();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnectedOrConnecting();
        }
        return false;
    }
}