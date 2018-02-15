package com.davidju.popularmoviesone.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.davidju.popularmoviesone.GlideApp;
import com.davidju.popularmoviesone.R;
import com.davidju.popularmoviesone.activities.DetailsActivity;
import com.davidju.popularmoviesone.models.Movie;

public class MoviesAdapter extends ArrayAdapter<Movie> {

    public static final String baseUrl = "http://image.tmdb.org/t/p/w185/";

    public MoviesAdapter(Context context) {
        super(context, 0);
    }

    @Override @NonNull @SuppressWarnings("ConstantConditions")
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final Movie movie = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_movie, parent, false);
            viewHolder.poster = convertView.findViewById(R.id.image_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        GlideApp.with(getContext())
                .load(Uri.parse(baseUrl + movie.getPosterPath()))
                .fitCenter()
                .into(viewHolder.poster);
        viewHolder.poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DetailsActivity.class);
                intent.putExtra("movie", movie);
                getContext().startActivity(intent);
            }
        });

        return convertView;
    }

    private class ViewHolder {
        ImageView poster;
    }
}
