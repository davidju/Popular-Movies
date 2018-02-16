package com.davidju.popularmoviesone;

import android.os.AsyncTask;

import com.davidju.popularmoviesone.interfaces.AsyncResponse;

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

public class FetchTrailersTask extends AsyncTask<String, Void, String> {
    public AsyncResponse response = null;
    @Override
    protected String doInBackground(String... params) {
        URL url = buildUrl(params[0]);
        try {
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
        return null;
    }

    @Override
    protected void onPostExecute(String json) {
        processJson(json);
    }

    private URL buildUrl(String movieId) {
        String url = "https://api.themoviedb.org/3/movie/";
        url += movieId + "/videos?api_key=" + BuildConfig.TMDB_API_KEY;

        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void processJson(String json) {
        final String KEY_RESULTS = "results";
        final String KEY_KEY = "key";

        List<String> trailers = new ArrayList<>();
        try {
            JSONObject results = new JSONObject(json);
            JSONArray trailersArr = results.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < trailersArr.length(); i++) {
                JSONObject info = trailersArr.getJSONObject(i);
                trailers.add(info.optString(KEY_KEY));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        response.processFinish(trailers);
    }
}
