package com.example.musicplayer;

import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by lenovo on 2018/2/7.
 */

public class Album {
    public String AlbumName;
    public Hashtable<String, Song> songList;
    public Album(String AlbumName){
        this.AlbumName=AlbumName;
        songList=new Hashtable<>();
    }
    public void add(Song song){
        songList.put(song.songName,song);
    }
}
