package com.example.itunes;

import android.net.Uri;

public class MusicFile {
    private Uri uri;
    private String title;
    private String artist;
    private String albumid;
    private String album;
    private String duration;

    public MusicFile(Uri uri, String title, String artist, String albumid, String album, String duration) {
        this.uri = uri;
        this.title = title;
        this.artist = artist;
        this.albumid = albumid;
        this.album = album;
        this.duration = duration;
    }

    public MusicFile() {
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbumId() {
        return albumid;
    }

    public void setAlbumId(String albumid) {
        this.albumid = albumid;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
