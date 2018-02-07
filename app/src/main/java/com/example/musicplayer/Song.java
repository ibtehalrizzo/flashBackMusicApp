package com.example.musicplayer;

import android.location.Location;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lenovo on 2018/2/7.
 */

public class Song {
    String songName;
    String artist;
    String mostRecentDateTime;
    Location mostRecentLocation; //don’t know yet
    HashSet<Location> locationHistory;
    HashSet<String> timeHistory;
    int score;
    int status; //1 - favorite, 0 - neutral, -1 - dislike

}
