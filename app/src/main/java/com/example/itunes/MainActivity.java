package com.example.itunes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //Used to store music information
    public static final int REQUEST_CODE = 1;
    static ArrayList<MusicFiles> musicFiles;
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permission();

    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void permission() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
        else{
            musicFiles = getAllAudio(this);
            initViewPager();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //Do whatever you want permission related
                musicFiles = getAllAudio(this);
                initViewPager();
            }
            else{
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }
    }

    private void initViewPager() {
        ViewPager2 viewPager2 = findViewById(R.id.viewpager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPagerAdapter.addFragment(new SongsFragment(), "Songs");
        viewPagerAdapter.addFragment(new AlbumFragment(), "Albums");
        viewPager2.setAdapter(viewPagerAdapter);
        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> tab.setText(viewPagerAdapter.titles.get(position))
        ).attach();
    }

    public  static class ViewPagerAdapter extends FragmentStateAdapter{

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }
        void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public static ArrayList<MusicFiles> getAllAudio(Context context){
        ArrayList<MusicFiles> tempAudioList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST
        };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if(cursor != null){
            while(cursor.moveToNext()){
                String album = cursor.getString(0);
                String albumid = cursor.getString(1);
                String title = cursor.getString(2);
                String duration = cursor.getString(3);
                String path = cursor.getString(4);
                String artist = cursor.getString(5);

                MusicFiles musicFiles = new MusicFiles(path, title, artist, albumid, album, duration);
                tempAudioList.add(musicFiles);
            }
            cursor.close();
        }
        return tempAudioList;
    }
}