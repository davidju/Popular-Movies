package com.davidju.popularmovies.asynctasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.davidju.popularmovies.BuildConfig;
import com.davidju.popularmovies.interfaces.AsyncResponse;
import com.davidju.popularmovies.models.Review;
import com.davidju.popularmovies.models.Trailer;

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

/** AsyncTask that fetches reviews for the specified movie from TMDB */
public class FetchReviewsTask extends AsyncTask<String, Void, String> {
    private final WeakReference<Context> contextReference;
    public AsyncResponse response = null;

    public FetchReviewsTask(Context context) {
        contextReference = new WeakReference<>(context);
    }

    @Override
    protected void onPreExecute() {
        if (!isNetworkAvailable()) {
            cancel(true);
            response.reportReviewsNetworkError();
        }
    }

    @Override @SuppressWarnings("ConstantConditions")
    protected String doInBackground(String... params) {
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
    private URL buildUrl(String movieId) {
        String url = "https://api.themoviedb.org/3/movie/";
        url += movieId + "/reviews?api_key=" + BuildConfig.TMDB_API_KEY;

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
        final String KEY_AUTHOR = "author";
        final String KEY_CONTENT = "content";

        List<Review> reviews = new ArrayList<>();
        try {
            JSONObject results = new JSONObject(json);
            JSONArray reviewsArr = results.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < reviewsArr.length(); i++) {
                JSONObject info = reviewsArr.getJSONObject(i);
                String author = info.optString(KEY_AUTHOR);
                String content = info.optString(KEY_CONTENT);
                reviews.add(new Review(author, content));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        response.processReviewResults(reviews);
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
