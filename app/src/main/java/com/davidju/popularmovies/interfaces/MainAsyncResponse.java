package com.davidju.popularmovies.interfaces;

import com.davidju.popularmovies.models.Movie;

import java.util.List;

public interface MainAsyncResponse {
    void processMovieResults(List<Movie> results);
}
