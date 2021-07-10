package com.example.itunes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import static com.example.itunes.MainActivity.musicFiles;

public class PlayerActivity extends AppCompatActivity {
    ImageButton btnNext, btnPrevious, btnShuffle, btnRepeat;
    FloatingActionButton btnPlay;
    TextView tvSongName, tvSongArtist, tvDurationPlayed, tvTotalDuration;
    SeekBar seekBar;
    ImageView coverArt;

    int position;
    static ArrayList<MusicFiles> listSongs = new ArrayList<>(musicFiles);
    static Uri uri;
    static MediaPlayer mediaPlayer;
//    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initView();
        getIntentMethod(this);
    }

//    private String formatedTime(int currentPosition) {
//        String totalOut = "";
//        String totalNew = "";
//        String seconds = String.valueOf(currentPosition % 60);
//        String minutes = String.valueOf(currentPosition / 60);
//        totalOut = minutes + ":" + seconds;
//        totalNew = minutes + ":" + "0" + seconds;
//        if(seconds.length() == 1) return totalNew;
//        else return totalOut;
//    }

    private void getIntentMethod(Context context) {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        position = bundle.getInt("position", -1);
//        listSongs = musicFiles;
        if(listSongs != null){
            btnPlay.setImageResource(R.drawable.ic_pause);
            uri = Uri.parse(listSongs.get(position).getPath());
        }
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(context, uri);
        mediaPlayer.start();
//        seekBar.setMax(mediaPlayer.getDuration() / 1000);
    }

    private void initView() {
        btnNext = findViewById(R.id.buttonNext);
        btnPrevious = findViewById(R.id.buttonPrevious);
        btnRepeat = findViewById(R.id.buttonRepeat);
        btnShuffle = findViewById(R.id.buttonShuffle);

        btnPlay = findViewById(R.id.buttonPlay);

        seekBar = findViewById(R.id.playerSeekBar);

        tvSongName = findViewById(R.id.textTitle);
        tvSongArtist = findViewById(R.id.textArtist);
        tvDurationPlayed = findViewById(R.id.textCurrentTime);
        tvTotalDuration = findViewById(R.id.textTotalTime);

        coverArt = findViewById(R.id.imageAlbumArt);
    }
}