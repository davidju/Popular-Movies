package com.davidju.popularmovies.interfaces;

import com.davidju.popularmovies.models.Trailer;

import java.util.List;

public interface AsyncResponse {
    void processFinish(List<Trailer> results);
}
