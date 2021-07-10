package com.example.itunes;

public class MusicFiles {
    private String path;
    private String title;
    private String artist;
    private String albumid;
    private String album;
    private String duration;

    public MusicFiles(String path, String title, String artist, String albumid, String album, String duration) {
        this.path = path;
        this.title = title;
        this.artist = artist;
        this.albumid = albumid;
        this.album = album;
        this.duration = duration;
    }

    public MusicFiles() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
