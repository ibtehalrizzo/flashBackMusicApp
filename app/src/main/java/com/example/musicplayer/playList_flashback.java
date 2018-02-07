package com.example.musicplayer;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by lenovo on 2018/2/7.
 */

public class playList_flashback extends Score {
    List<Song> songList;
    Set<Song> sortingList;  //Use a TreeSet to store sorting songs
    public playList_flashback(){
        sortingList=new TreeSet<Song>(new Comparator<Song>() {
            @Override
            public int compare(Song song1, Song song2) {   //sort songs with the score
                return song2.score-song1.score;
            }
        });
    };
    public void  sortList(){
        sortingList.addAll(songList);     //Add all the songs to the sort list
    }
}
