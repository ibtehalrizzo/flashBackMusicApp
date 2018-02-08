package com.example.musicplayer;

import android.location.Location;
import android.location.LocationListener;

import java.sql.Time;
import java.util.Calendar;
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
    public TreeSet<String> sortingList;  //Use a TreeSet to store sorting songs
    public Score score;
    public playList_flashback(Score score){
        this.score = score;
        final AlbumList list = score.list;
        sortingList=new TreeSet<String>(new Comparator<String>() {
            @Override
            public int compare(String songname1, String songname2) {
                if(songname1.equals(songname2))
                    return 0;
                String[] songName1 = songname1.split(",");
                String[] songName2 = songname2.split(",");
                Song song1 = list.albumList.get(songName1[0]).songList.get(songName1[1]); //get song from the album
                Song song2 = list.albumList.get(songName2[0]).songList.get(songName2[1]);
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
        sortingList.addAll(score.listSong);  // If meet the first song with -1 score, go to the begining of the set.
    };

    public void changeToDislike(String name){    //add to dislike, score set to -1, skip it
        String [] Name = name.split(",");
        Song song = score.list.albumList.get(Name[0]).songList.get(Name[1]);
        sortingList.remove(name);
        song.status=-1;
        song.score=-1;
        score.list.albumList.get(Name[0]).songList.put(Name[1],song);
        sortingList.add(name);
    }

    public  void changeToNeutral(String name, Location location, int day, int time){   //change from dislike to neutral, calculate the score
        String [] Name = name.split(",");
        Song song = score.list.albumList.get(Name[0]).songList.get(Name[1]);
        sortingList.remove(name);
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
        score.list.albumList.get(Name[0]).songList.put(Name[1], song);
        sortingList.add(name);

    }

    public void changeToFavorite(String name){      //change from neutral to favorite, add 1 to score
        String [] Name = name.split(",");
        sortingList.remove(name);
        Song song = score.list.albumList.get(Name[0]).songList.get(Name[1]);
        song.status=1;
        song.score=song.score+2;
        score.list.albumList.get(Name[0]).songList.put(Name[1], song);
        sortingList.add(name);
    }
    //public void  sortList(){
      //  sortingList.addAll(listSong);     //Add all the songs to the sort list
    //}
}
