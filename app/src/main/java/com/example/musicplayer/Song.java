package com.example.musicplayer;

import android.location.Location;
import android.location.LocationListener;

import java.sql.Time;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lenovo on 2018/2/7.
 */

public class Song {
    public String songName;
    public String artist;
    public Time mostRecentDateTime;
    public Location mostRecentLocation; //don’t know yet
    public HashSet<Location> locationHistory;
    public HashSet<Integer> dayHistory;  // 1-Monday 2-Tuesday 3-Wednesday 4- Thursday 5-Friday 6-Saturday 7-Sunday
    public HashSet<Integer> timeHistory;   //1- morning  2-afternoon 3-evening
    public int score;
    public int status; //1 - favorite, 0 - neutral, -1 - dislike
    public Song(String songName, String artist){
        this.songName=songName;
        this.artist=artist;
        mostRecentDateTime=null;
        mostRecentLocation=null;
        locationHistory=new HashSet<>();
        dayHistory = new HashSet<>();
        timeHistory= new HashSet<>();
        score=0;
        status =0;
    }
}
