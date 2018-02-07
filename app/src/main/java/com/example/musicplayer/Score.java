package com.example.musicplayer;

import android.location.Location;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lenovo on 2018/2/7.
 */

public class Score {
    List<Song> songList;
    public Score(){
        songList=new ArrayList<Song>();  //Create score system
    }
    public void addSong(Song song){
        songList.add(song);   // add song to the score system;
    }
    public void score(Location location, String time){     // real-time location and time
        int len=songList.size();    //number of songs
        for(int i=0;i<len;i++){
            Song song=songList.get(i);
            song.score=0;
            HashSet<Location> locationHistory=song.locationHistory;    //get location and time history of corresponding song
            HashSet<String> timeHistory = song.timeHistory;
            int status = song.status;
            if(locationHistory.contains(location))            //If location is the same, score increase
                song.score++;
            if(timeHistory.contains(time))          //If time is the same, score increase
                song.score++;
            if(status==1)                 //If the song is favorite, score increase
                song.score++;
            else if(status==-1)           //If the song is disliked, not play it
                song.score=0;
        }
    }
}


