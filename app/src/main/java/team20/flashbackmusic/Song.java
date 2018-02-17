package team20.flashbackmusic;
import android.location.Location;
import android.location.LocationManager;

import java.util.HashSet;
import java.util.Date;

/**
 * Created by dhoei on 06/02/18.
 */

public class Song {

    //private members of song
    private String title, artist, album, mostRecentDateTimeString,mostRecentLocationString;
    Date mostRecentDateTime;
    Location mostRecentLocation;
    private int score, status, songResId;
    public HashSet<Location> locationHistory;
    public HashSet<Integer> dayHistory;  // 1-Monday 2-Tuesday 3-Wednesday 4- Thursday 5-Friday 6-Saturday 7-Sunday
    public HashSet<Integer> timeHistory;   //1- morning  2-afternoon 3-evening

    private long duration;


    //constructor of song
    public Song(String title, String artist, String album, long duration, int id) {
        mostRecentDateTime = null;
        mostRecentLocation = null;
        mostRecentDateTimeString = null;
        mostRecentLocationString = null;

        locationHistory = new HashSet<>();
        dayHistory = new HashSet<>();
        timeHistory = new HashSet<>();

        //for status, 1 = favorite, 0 = neutral, -1 = dislike
        //initialize with neutral status
        status = 0;

        //initialize with score 0 because the music has not been
        //played
        score = 0;

        this.title = title;
        this.artist = artist;
        this.album = album;
        this.songResId = id;

        // Convert duration to time
        this.duration = duration;
    }


    // GETTER AND SETTER METHOD //
    public String getTitle()
    {
        return title;
    }

    public String getArtist()
    {
        return artist;
    }

    public String getAlbum() { return album; }

    public int getStatus()
    {
        return status;
    }

    public int getScore()
    {
        return score;
    }

    public int getSongResId(){return songResId;}

    public String getMostRecentDateTimeString(){
        return this.mostRecentDateTimeString;
    }
    public String getMostRecentLocationString(){
        return this.mostRecentLocationString;
    }
    public void setMostRecentDateTimeString(String mostRecentDateTimeString){
        this.mostRecentDateTimeString = mostRecentDateTimeString;
    }
    public void setMostRecentLocationString(String mostRecentLocationString){
        this.mostRecentLocationString = mostRecentLocationString;
    }

    public Date getMostRecentDateTime(){
        return this.mostRecentDateTime;
    }
    public Location getMostRecentLocation(){
        return this.mostRecentLocation;
    }
    public void setMostRecentDateTime(Date mostRecentDateTime){
        this.mostRecentDateTime = mostRecentDateTime;
    }
    public void setMostRecentLocation(Location location){
        this.mostRecentLocation = location;
    }





    public void setScore(int s) { score = s; }

    public void setStatus(int s) { status = s; }


}
