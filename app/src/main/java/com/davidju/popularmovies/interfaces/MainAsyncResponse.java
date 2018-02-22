package com.davidju.popularmovies.interfaces;

import com.davidju.popularmovies.models.Movie;

import java.util.List;

/** Interface used to get callback from FetchMovies AsyncTask */
public interface MainAsyncResponse {
    void processMovieResults(List<Movie> results);
}
