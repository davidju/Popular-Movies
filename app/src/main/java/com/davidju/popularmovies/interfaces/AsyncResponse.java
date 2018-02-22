package com.davidju.popularmovies.interfaces;

import com.davidju.popularmovies.models.Review;
import com.davidju.popularmovies.models.Trailer;

import java.util.List;

public interface AsyncResponse {
    void processTrailerResults(List<Trailer> results);
    void processReviewResults(List<Review> results);
    void reportTrailersNetworkError();
    void reportReviewsNetworkError();
}
