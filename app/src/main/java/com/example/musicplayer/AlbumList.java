package com.example.musicplayer;

import java.util.Hashtable;

/**
 * Created by lenovo on 2018/2/7.
 */

public class AlbumList {
    public Hashtable<String, Album> albumList;
    public AlbumList(){
        albumList = new Hashtable<>();
    }
    public void add(Album album){
        albumList.put(album.AlbumName,album);

    }
}
