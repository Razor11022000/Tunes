package com.example.itunes;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import static com.example.itunes.AlbumDetailsAdapter.albumFiles;
import static com.example.itunes.MainActivity.musicFiles;
import static com.example.itunes.MainActivity.repeatBoolean;
import static com.example.itunes.MainActivity.shuffleBoolean;
import static com.example.itunes.MusicAdapter.mFiles;

public class PlayerActivity extends AppCompatActivity
        implements MediaPlayer.OnCompletionListener, ActionPlaying, ServiceConnection {
    ImageButton btnNext, btnPrevious, btnShuffle, btnRepeat;
    FloatingActionButton btnPlayPause;
    TextView tvSongName, tvSongArtist, tvDurationPlayed, tvTotalDuration;
    SeekBar seekBar;
    ImageView coverArt;

    int position;
    private ArrayList<MusicFile> listSongs = new ArrayList<>();
    static MediaPlayer mediaPlayer;
    private final Handler handler = new Handler();
    private Uri uri;
    private Thread nextSongThread, prevSongThread, playSongThread;
    MusicService musicService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        listSongs = musicFiles;
        initView();
        mediaPlayer = new MediaPlayer();
        getIntentMethod();
//        position = getIntent().getIntExtra("position",-1);
//        Uri uri = listSongs.get(position).getUri();
//
//        Intent intent = new Intent(this, MusicService.class);
//        intent.putExtra("path",String.valueOf(uri));
//        if (isMyServiceRunning(MusicService.class)) {
//            stopService(intent);
//        }
//        startService(intent);
//        btnPlay.setImageResource(R.drawable.ic_pause);
//        musicService.startService(intent);
//        seekBar.setMax(musicService.mediaPlayerDuration());
        mediaPlayer.setOnCompletionListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(mediaPlayer!=null && b){
                    mediaPlayer.seekTo(i*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    int currentPlayerPosition = mediaPlayer.getCurrentPosition()/1000;
                    seekBar.setProgress(currentPlayerPosition);
                    tvDurationPlayed.setText(formattedTime(currentPlayerPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });
        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(shuffleBoolean){
                    shuffleBoolean = false;
                    btnShuffle.setImageResource(R.drawable.ic_shuffle);
                }
                else{
                    shuffleBoolean = true;
                    btnShuffle.setImageResource(R.drawable.ic_shuffle_on);
                }
            }
        });
        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(repeatBoolean){
                    repeatBoolean = false;
                    btnRepeat.setImageResource(R.drawable.ic_repeat);
                }
                else{
                    repeatBoolean = true;
                    btnRepeat.setImageResource(R.drawable.ic_repeat_on);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    private void playThreadBtn() {
        playSongThread = new Thread(){
            @Override
            public void run() {
                super.run();
                btnPlayPause.setOnClickListener(view -> playPauseBtnClicked());
            }
        };
        playSongThread.start();
    }

    public void playPauseBtnClicked() {
        if(mediaPlayer.isPlaying()){
            btnPlayPause.setImageResource(R.drawable.ic_play);
            mediaPlayer.pause();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int currentPlayerPosition = mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(currentPlayerPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        }
        else{
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int currentPlayerPosition = mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(currentPlayerPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        }
    }

    private void nextThreadBtn() {
        nextSongThread = new Thread(){
            @Override
            public void run() {
                super.run();
                btnNext.setOnClickListener(view -> nextBtnClicked());
            }
        };
        nextSongThread.start();
    }

    public void nextBtnClicked() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleBoolean && !repeatBoolean){
                position = getRandom(listSongs.size() - 1);
            }
            else if (!shuffleBoolean && !repeatBoolean){
                position = ((position+1) % listSongs.size());
            }
            uri = listSongs.get(position).getUri();
            mediaPlayer = MediaPlayer.create(this, uri);
            getSongDetails(position);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int currentPlayerPosition = mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(currentPlayerPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
        }
        else{
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleBoolean && !repeatBoolean){
                position = getRandom(listSongs.size() - 1);
            }
            else if (!shuffleBoolean && !repeatBoolean){
                position = ((position+1) % listSongs.size());
            }
            uri = listSongs.get(position).getUri();
            mediaPlayer = MediaPlayer.create(this, uri);
            getSongDetails(position);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int currentPlayerPosition = mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(currentPlayerPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            btnPlayPause.setImageResource(R.drawable.ic_play);
        }
    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

    private void prevThreadBtn() {
        prevSongThread = new Thread(){
            @Override
            public void run() {
                super.run();
                btnPrevious.setOnClickListener(view -> prevBtnClicked());
            }
        };
        prevSongThread.start();
    }

    public void prevBtnClicked() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleBoolean && !repeatBoolean){
                position = getRandom(listSongs.size() - 1);
            }
            else if (!shuffleBoolean && !repeatBoolean){
                position = ((position-1) < 0 ? (listSongs.size()-1) : (position-1));
            }
            uri = listSongs.get(position).getUri();
            mediaPlayer = MediaPlayer.create(this, uri);
            getSongDetails(position);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int currentPlayerPosition = mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(currentPlayerPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
        }
        else{
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleBoolean && !repeatBoolean){
                position = getRandom(listSongs.size() - 1);
            }
            else if (!shuffleBoolean && !repeatBoolean){
                position = ((position-1) < 0 ? (listSongs.size()-1) : (position-1));
            }
            uri = listSongs.get(position).getUri();
            mediaPlayer = MediaPlayer.create(this, uri);
            getSongDetails(position);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int currentPlayerPosition = mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(currentPlayerPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            btnPlayPause.setImageResource(R.drawable.ic_play);
        }
    }

    private String formattedTime(int currentPosition) {
        String totalOut;
        String totalNew;
        String seconds = String.valueOf(currentPosition % 60);
        String minutes = String.valueOf(currentPosition / 60);
        totalOut = minutes + ":" + seconds;
        totalNew = minutes + ":" + "0" + seconds;
        if(seconds.length() == 1) return totalNew;
        else return totalOut;
    }

    private void getIntentMethod() {
        position = getIntent().getIntExtra("position",-1);
        String sender = getIntent().getStringExtra("sender");

        if(sender != null && sender.equals("albumDetails")) listSongs = albumFiles;
        else listSongs = mFiles;

        if(listSongs != null) {
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            uri = listSongs.get(position).getUri();
        }
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
            } catch (Exception e) {
                Log.v("getIntentMethod", e.getMessage());
            }
        }
        else{
            try {
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
            } catch (Exception e) {
                Log.v("getIntentMethod", e.getMessage());
            }
        }
        seekBar.setMax(mediaPlayer.getDuration() / 1000);
        getSongDetails(position);
    }

    private void initView()   {
        btnNext = findViewById(R.id.buttonNext);
        btnPrevious = findViewById(R.id.buttonPrevious);
        btnRepeat = findViewById(R.id.buttonRepeat);
        btnShuffle = findViewById(R.id.buttonShuffle);

        btnPlayPause = findViewById(R.id.buttonPlay);

        seekBar = findViewById(R.id.playerSeekBar);

        tvSongName = findViewById(R.id.textTitle);
        tvSongArtist = findViewById(R.id.textArtist);
        tvDurationPlayed = findViewById(R.id.textCurrentTime);
        tvTotalDuration = findViewById(R.id.textTotalTime);

        coverArt = findViewById(R.id.imageAlbumArt);
    }

    private void getSongDetails(int position){
        tvTotalDuration.setText(formattedTime(mediaPlayer.getDuration() / 1000));
        tvSongName.setText(listSongs.get(position).getTitle());
        tvSongArtist.setText(listSongs.get(position).getArtist());
        Bitmap image = getArtistImage(listSongs.get(position).getAlbumId());
        if(image != null){
            ImageAnimation(this, coverArt, image);
        }
        else{
            Glide.with(this)
                    .load(R.drawable.default_songcover)
                    .into(coverArt);
        }
    }

    private Bitmap getArtistImage(String albumid) {
        Bitmap artwork = null;
        try {
            Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");
            Uri uri = ContentUris.withAppendedId(sArtworkUri,
                    Long.parseLong(albumid));
            ContentResolver res = this.getContentResolver();
            InputStream in = res.openInputStream(uri);
            artwork = BitmapFactory.decodeStream(in);

        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }
        return artwork;
    }
//    private boolean isMyServiceRunning(Class<?> serviceClass) {
//        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceClass.getName().equals(service.service.getClassName())) {
//                return true;
//            }
//        }
//        return false;
//    }
    public void ImageAnimation(Context context, ImageView imageView, Bitmap bitmap){
        Animation animOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        Animation animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).into(imageView);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageView.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animOut);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        nextBtnClicked();
        if(mediaPlayer!=null){
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        MusicService.MyBinder myBinder = (MusicService.MyBinder) iBinder;
        musicService = myBinder.getService();
        Toast.makeText(this, "Connected" + musicService, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        musicService = null;
    }
}