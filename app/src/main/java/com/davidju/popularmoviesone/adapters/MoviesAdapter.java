package com.davidju.popularmoviesone.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.davidju.popularmoviesone.R;
import com.davidju.popularmoviesone.models.Movie;

public class MoviesAdapter extends ArrayAdapter<Movie> {

    private String baseUrl = "http://image.tmdb.org/t/p/w185/";

    public MoviesAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_movie, parent, false);
            viewHolder.poster = convertView.findViewById(R.id.image_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        System.out.println(baseUrl + movie.getPosterPath());
        Glide.with(getContext()).load(Uri.parse(baseUrl + movie.getPosterPath())).into(viewHolder.poster);

        return convertView;
    }

    private class ViewHolder {
        ImageView poster;
    }
}
