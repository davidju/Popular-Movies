package com.davidju.popularmovies.interfaces;

import com.davidju.popularmovies.models.Review;
import com.davidju.popularmovies.models.Trailer;

import java.util.List;

/** Interface used to get callback from FetchTrailers and FetchReviews AsyncTask */
public interface AsyncResponse {
    void processTrailerResults(List<Trailer> results);
    void processReviewResults(List<Review> results);
    void reportTrailersNetworkError();
    void reportReviewsNetworkError();
}
