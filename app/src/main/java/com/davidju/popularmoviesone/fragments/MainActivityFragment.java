package com.davidju.popularmoviesone.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.davidju.popularmoviesone.FetchMoviesTask;
import com.davidju.popularmoviesone.R;
import com.davidju.popularmoviesone.adapters.MoviesAdapter;
import com.davidju.popularmoviesone.enums.SortType;

/** Fragment that controls main view that displays grid of popular movies */
public class MainActivityFragment extends Fragment {

    public static RecyclerView recyclerView;
    public static MoviesAdapter moviesAdapter;

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        moviesAdapter = new MoviesAdapter();
        recyclerView.setAdapter(moviesAdapter);

        new FetchMoviesTask(getContext()).execute(SortType.POPULAR);

        return rootView;
    }
}
