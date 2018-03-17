package team20.flashbackmusic;

import android.location.Location;
import android.util.Log;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by dhoei on 08/03/18.
 */

public class PlaylistVibe implements Serializable, PlaylistFBM {

    private ArrayList<String> sortingList;  //Use a TreeSet to store sorting songs
    private ArrayList<Song> songs;
    private Hashtable<String, Integer> indexTosong;
    //Every time play from the start of the playlist, judge whether the songlist need to be sorted. 0 means no need,1 means need.
    private int needChange=0;

    public PlaylistVibe(ArrayList<String> sortingList, ArrayList<Song> songs, Hashtable<String, Integer> indexTosong) {//constructor
        this.setSortingList(sortingList);
        this.setSongs(songs);
        this.setIndexTosong(indexTosong);
    }
    public void sorter(){            //callback this function to sort the playlist
        Collections.sort(getSortingList(),new Comparator<String>() {
            @Override
            public int compare(String songname1, String songname2) {
                if(songname1.equals(songname2))
                    return 0;


                Song song1 = getSongs().get(getIndexTosong().get(songname1));
                Song song2 = getSongs().get(getIndexTosong().get(songname2));

                //sort songs with the score

                //If the scores are not the same, play the song with the higher score first
                if(song2.getScore()!=song1.getScore()){
                    Log.d("TEST:","different score");
                    return song2.getScore()-song1.getScore();
                }
                else {
                    Log.d("TEST:","song tied");
                    //prioritize song based on 3 priority

                    //check if a track was played near the user's location (score + 1) NOTE: SHOULD BE EQUAL WEIGHT
                    if(!(song1.isPlayedNear() && song2.isPlayedNear()))
                    {
                        //make the first song a higher priority when it was played nearby
                        if(song1.isPlayedNear() && !song2.isPlayedNear())
                            return -1;

                        else
                            return song1.getScore();
                    }
                    //check if the song was played in the last week (score + 1)
                    if(!(song1.isPlayedLastWeek() && song2.isPlayedLastWeek()))
                    {
                        if(song1.isPlayedLastWeek() && !song2.isPlayedLastWeek())
                            return -1;
                        else
                            return song1.getScore();
                    }
                    //check if the song was played by a friend (score + 1)
                    if(!(song1.isPlayedByAFriend() && song2.isPlayedByAFriend()))
                    {
                        if(song1.isPlayedByAFriend())
                            return -1;
                        else
                            return song1.getScore();
                    }

                    //if they all are tied, we make the first song have higher priority
                    return -1;
                }

                                // track based on first (a) whether it was played near the user's
                                // present location, second (b) whether it was played in the last week,
                                // and third (c) whether it was played by a friend.


//                else if(song1.getRecentDateTime()!=null&& song2.getRecentDateTime()!=null){
//                    Date time1 = song1.getRecentDateTime();
//                    Date time2 = song2.getRecentDateTime();
//
//                    //If the scores are the same, play the song with the more recent play time first
//                    if(time1.after(time2))
//                        return -1;
//                    else
//                        return 1;
//
//                }
//                else if(song1.getRecentDateTime()!=null){
//                    return -1;
//                }
//                else if(song2.getRecentDateTime()!=null)
//                    return 1;
//                else
//                    return 1;
            }
        });
        setNeedChange(0);     //no need to resort the play list

//        reInitializeBooleans();
    }
    public void changeToDislike(int index){    //add to dislike, score set to -1, skip it
        Song song = getSongs().get(index);
        song.setStatus(-1);
        song.setScore(-1);
        setNeedChange(1);   //need to resort the play list
    }

    public  void changeToNeutral(int index, Location location, int day, int time){   //change from dislike to neutral, calculate the score
        Song song = getSongs().get(index);
        song.setStatus(0);
        song.setScore(0);
        HashSet<Location> locationHistory = song.getLocationHistory();    //get location and time history of corresponding song
        HashSet<Integer> dayHistory = song.getDayHistory();
        HashSet<Integer> timeHistory = song.getTimeHistory();
        if (locationHistory.contains(location))            //If location is the same, score increase
            song.setScore(song.getScore()+1);
        if (timeHistory.contains(time))          //If time is the same, score increase
            song.setScore(song.getScore()+1);
        if (dayHistory.contains(day))    //If the song was played in the same day, score increase
            song.setScore(song.getScore()+1);
        setNeedChange(1);        //need to resort the play list
    }

    public void changeToFavorite(int index){      //change from neutral to favorite, add 1 to score
        Song song = getSongs().get(index);
        song.setStatus(1);
        song.setScore(song.getScore()+2);
        setNeedChange(1);       //need to resort the play list
    }

    public ArrayList<String> getSortingList() {
        return sortingList;
    }

    public void setSortingList(ArrayList<String> sortingList) {
        this.sortingList = sortingList;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public Hashtable<String, Integer> getIndexTosong() {
        return indexTosong;
    }

    public void setIndexTosong(Hashtable<String, Integer> indexTosong) {
        this.indexTosong = indexTosong;
    }

    public int getNeedChange() {
        return needChange;
    }

    public void setNeedChange(int needChange) {
        this.needChange = needChange;
    }

}
