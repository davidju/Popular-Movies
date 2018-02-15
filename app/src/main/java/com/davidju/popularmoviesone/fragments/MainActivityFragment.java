package com.davidju.popularmoviesone.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.davidju.popularmoviesone.FetchMoviesTask;
import com.davidju.popularmoviesone.R;
import com.davidju.popularmoviesone.adapters.MoviesAdapter;
import com.davidju.popularmoviesone.enums.SortType;


public class MainActivityFragment extends Fragment {

    public static GridView gridView;
    public static MoviesAdapter moviesAdapter;

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        gridView = rootView.findViewById(R.id.grid_view);
        moviesAdapter = new MoviesAdapter(getContext());
        gridView.setAdapter(moviesAdapter);

        new FetchMoviesTask(getContext()).execute(SortType.POPULAR);

        return rootView;
    }
}
