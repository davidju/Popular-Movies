package com.davidju.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.davidju.popularmovies.GlideApp;
import com.davidju.popularmovies.R;
import com.davidju.popularmovies.activities.DetailsActivity;
import com.davidju.popularmovies.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/** Adapter for GridView that displays list of movies */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    public static final String baseUrl = "http://image.tmdb.org/t/p/w185/";
    private List<Movie> movies;

    public MoviesAdapter() {
        movies = new ArrayList<>();
    }

    public void updateMovieList(List<Movie> movies) {
        this.movies.clear();
        this.movies.addAll(movies);
        notifyDataSetChanged();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        MovieViewHolder viewHolder = new MovieViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder viewHolder, int position) {
        final Movie movie = movies.get(position);
        final Context context = viewHolder.poster.getContext();
        Picasso.with(context)
                .load(baseUrl + movie.getPosterPath())
                .into(viewHolder.poster);
        viewHolder.poster.setOnClickListener(view -> {
            Intent intent = new Intent(context, DetailsActivity.class);
            intent.putExtra("movie", movie);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_view) ImageView poster;

        MovieViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
