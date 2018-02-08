package com.example.musicplayer;

import android.content.SharedPreferences;
import android.location.Location;
import android.view.View;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

/**
 * Created by lenovo on 2018/2/7.
 */

public class Score {
    List<String> albumName;
    List<String> listSong;
    AlbumList list;// store format: String("albumname,songname")
    public Score(){}
    public Score(AlbumList list) {
        this.list=list;
        albumName = new ArrayList<>(list.albumList.keySet());
        listSong = new ArrayList<>();
        int len = albumName.size();    //number of songs
        for (int i = 0; i < len; i++) {
            String albumname = albumName.get(i);
            Album album = list.albumList.get(albumname);
            Hashtable<String, Song> songList = album.songList;
            List<String> trackname = new ArrayList<>(songList.keySet());
            for (int j = 0; j < trackname.size(); j++) {
                String songname = trackname.get(j);
                listSong.add(new String(albumname+","+songname));
            }
        }
        //Create score system
    }
    public Score(List<String> listSong){
        this.listSong=listSong;
    }

    //public void addSong(Song song){
    //  songList.add(song.songName);   // add song to the score system;
    // }
    public void score(Location location, int day, int time) {
        int len = listSong.size();// real-time location and time
        for (int i = 0; i < len; i++) {
                String[] name = listSong.get(i).split(",");
                String songName=name[1];
                String albumName=name[0];
                Album album = list.albumList.get(albumName);
                Song song = album.songList.get(songName);
                song.score = 0;
                HashSet<Location> locationHistory = song.locationHistory;    //get location and time history of corresponding song
                HashSet<Integer> dayHistory = song.dayHistory;
                HashSet<Integer> timeHistory = song.timeHistory;
                int status = song.status;
                if (locationHistory.contains(location))            //If location is the same, score increase
                    song.score++;
                if (timeHistory.contains(time))          //If time is the same, score increase
                    song.score++;
                if (dayHistory.contains(day))    //If the song was played in the same day, score increase
                    song.score++;
                if (status == 1)                 //If the song is favorite, score increase
                    song.score++;
                else if (status == -1)           //If the song is disliked, not play it
                    song.score = -1;
                album.songList.put(songName,song);
            }
        }
}


