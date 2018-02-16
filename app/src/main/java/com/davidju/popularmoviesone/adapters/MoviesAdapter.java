package com.davidju.popularmoviesone.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.davidju.popularmoviesone.GlideApp;
import com.davidju.popularmoviesone.R;
import com.davidju.popularmoviesone.activities.DetailsActivity;
import com.davidju.popularmoviesone.models.Movie;

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
        GlideApp.with(context)
                .load(Uri.parse(baseUrl + movie.getPosterPath()))
                .fitCenter()
                .into(viewHolder.poster);
        viewHolder.poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("movie", movie);
                context.startActivity(intent);
            }
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
