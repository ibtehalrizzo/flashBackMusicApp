package com.example.musicplayer;

import java.sql.Time;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by lenovo on 2018/2/7.
 */

public class playList_flashback{
    public TreeSet<String> sortingList;  //Use a TreeSet to store sorting songs
    public playList_flashback(Score score){
        final AlbumList list = score.list;
        sortingList=new TreeSet<String>(new Comparator<String>() {
            @Override
            public int compare(String songname1, String songname2) {
                String[] songName1 = songname1.split(",");
                String[] songName2 = songname2.split(",");
                Song song1 = list.albumList.get(songName1[0]).songList.get(songName1[1]); //get song from the album
                Song song2 = list.albumList.get(songName2[0]).songList.get(songName2[1]);
                if(song2.score!=song1.score)//sort songs with the score
                    return song2.score-song1.score;            //If the scores are not the same, play the song with the higher score first
                else{
                    Time time1 = song1.mostRecentDateTime;
                    Time time2 = song2.mostRecentDateTime;
                    if(time1.after(time2))                     //If the scores are the same, play the song with the more recent play time first
                        return -1;
                    else
                        return 1;
                }
            }
        });
        sortingList.addAll(score.listSong);
    };
    //public void  sortList(){
      //  sortingList.addAll(listSong);     //Add all the songs to the sort list
    //}
}
