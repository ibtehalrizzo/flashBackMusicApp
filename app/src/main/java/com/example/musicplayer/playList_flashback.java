package com.example.musicplayer;

import android.location.Location;
import android.location.LocationListener;

import java.sql.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by lenovo on 2018/2/7.
 */

public class playList_flashback{
    public ArrayList<String> sortingList;  //Use a TreeSet to store sorting songs
    public Score score;
    int needChange=0;      //Every time play from the start of the playlist, judge whether the songlist need to be sorted. 0 means no need,1 means need.
    public playList_flashback(final Score score) {    //constructor
        this.score = score;
        sortingList = score.listSong;
    }
    public void sorter(){            //callback this function to sort the playlist
        Collections.sort(sortingList,new Comparator<String>() {
            @Override
            public int compare(String songname1, String songname2) {
                if(songname1.equals(songname2))
                    return 0;
                Song song1 = score.songs.songlist.get(songname1);
                Song song2 = score.songs.songlist.get(songname2);
                if(song2.score!=song1.score)//sort songs with the score
                    return song2.score-song1.score;            //If the scores are not the same, play the song with the higher score first
                else if(song1.mostRecentDateTime!=null&& song2.mostRecentDateTime!=null){
                    Calendar time1 = song1.mostRecentDateTime;
                    Calendar time2 = song2.mostRecentDateTime;
                    if(time1.after(time2))                     //If the scores are the same, play the song with the more recent play time first
                        return -1;
                    else
                        return 1;
                }
                else
                    return -1;
            }
        });
        needChange = 0;     //no need to resort the play list
    }
    public void changeToDislike(String name){    //add to dislike, score set to -1, skip it
        Song song = score.songs.songlist.get(name);
        song.status=-1;
        song.score=-1;
        needChange=1;   //need to resort the play list
    }

    public  void changeToNeutral(String name, Location location, int day, int time){   //change from dislike to neutral, calculate the score
        Song song = score.songs.songlist.get(name);
        song.status=0;
        song.score=0;
        HashSet<Location> locationHistory = song.locationHistory;    //get location and time history of corresponding song
        HashSet<Integer> dayHistory = song.dayHistory;
        HashSet<Integer> timeHistory = song.timeHistory;
        if (locationHistory.contains(location))            //If location is the same, score increase
            song.score++;
        if (timeHistory.contains(time))          //If time is the same, score increase
            song.score++;
        if (dayHistory.contains(day))    //If the song was played in the same day, score increase
            song.score++;
        needChange=1;        //need to resort the play list
    }

    public void changeToFavorite(String name){      //change from neutral to favorite, add 1 to score
        Song song = score.songs.songlist.get(name);
        song.status=1;
        song.score=song.score+2;
        needChange=1;       //need to resort the play list
    }
}
