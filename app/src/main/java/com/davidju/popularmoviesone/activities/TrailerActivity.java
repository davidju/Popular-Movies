package com.davidju.popularmoviesone.activities;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.VideoView;

import com.davidju.popularmoviesone.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrailerActivity extends Activity {

    public static final String TRAILER_KEY = "trailer_key";
    private static final String BASE_URL = "http://youtube.com/watch?v=";

    @BindView(R.id.video_view) VideoView trailer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer);
        ButterKnife.bind(this);

        String videoId = getIntent().getStringExtra(TRAILER_KEY);
        trailer.setVideoURI(Uri.parse(BASE_URL + videoId));
        trailer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                trailer.start();
            }
        });
    }
}
