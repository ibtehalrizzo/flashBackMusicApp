package team20.flashbackmusic;

import android.location.Location;
import android.net.Uri;

import java.sql.Time;
import java.util.Date;
import java.util.HashSet;

/**
 * Created by lenovo on 2018/2/7.
 */

public class Song {
    private String title, artist, album,mostRecentDateTimeString,mostRecentLocationString;
    private String lastUserName;
    private Date mostRecentDateTime;
    private Location mostRecentLocation; 
    private HashSet<Location> locationHistory;
    private HashSet<Integer> dayHistory;  // 1-Monday 2-Tuesday 3-Wednesday 4- Thursday 5-Friday 6-Saturday 7-Sunday
    private HashSet<Integer> timeHistory;   //1- morning  2-afternoon 3-evening

    private HashSet<Integer> dayOfMonthHistory; //to check if it was played last week
    
    private int songResId;
    private String songUri, songDownloadUri;

    private int score;
    private int status; //1 - favorite, 0 - neutral, -1 - dislike
    private long duration;


    private boolean playedNear;
    private boolean playedLastWeek;
    private boolean playedByAFriend;
    private boolean download;

    /**
     * Constructor for song that is local
     * @param title
     * @param artist
     * @param album
     * @param duration
     * @param id
     */
    public Song(String title, String artist, String album, long duration, int id) {
        mostRecentDateTime = null;
        mostRecentLocation = null;

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

        locationHistory = new HashSet<>();
        dayHistory = new HashSet<>();
        timeHistory = new HashSet<>();
        dayOfMonthHistory = new HashSet<>();

        playedNear = false;
        playedLastWeek = false;
        playedByAFriend = false;
        download = false;

    }

    /**
     * Constructor for downloaded song
     * @param title
     * @param artist
     * @param album
     * @param duration
     * @param id
     */
    public Song(String title, String artist, String album, long duration, Uri id, Uri songDownloadUri) {
        mostRecentDateTime = null;
        mostRecentLocation = null;

        //for status, 1 = favorite, 0 = neutral, -1 = dislike
        //initialize with neutral status
        status = 0;

        //initialize with score 0 because the music has not been
        //played
        score = 0;

        this.title = title;
        this.artist = artist;
        this.album = album;
        this.songUri = id+"";
        this.songDownloadUri = songDownloadUri+"";

        // Convert duration to time
        this.duration = duration;

        locationHistory = new HashSet<>();
        dayHistory = new HashSet<>();
        timeHistory = new HashSet<>();
        dayOfMonthHistory = new HashSet<>();

        playedNear = false;
        playedLastWeek = false;
        playedByAFriend = false;
        download = true;


    }

    // GETTER AND SETTER METHOD //
    public String getLastUserName(){
        return lastUserName;
    }
    public void setLastUserName(String userName){
        lastUserName = userName;
    }

    public String getTitle()
    {
        return title;
    }

    public String getArtist()
    {
        return artist;
    }

    public String getAlbum() { return album; }

    public Date getRecentDateTime()
    {
        return mostRecentDateTime;
    }

    public Location getRecentLoc()
    {
        return mostRecentLocation;
    }

    public int getStatus()
    {
        return status;
    }

    public int getScore()
    {
        return score;
    }

    public int getSongResId(){return songResId;}

    public HashSet<Location> getLocationHistory(){
        return locationHistory;
    }

    public HashSet<Integer> getDayHistory(){
        return dayHistory;
    }

    public HashSet<Integer> getTimeHistory(){
        return timeHistory;
    }




    public void setScore(int s) { score = s; }

    public void setStatus(int s) { status = s; }

    public void setMostRecentDateTime(Time time){
        mostRecentDateTime = time;
    }
    public void addLocationHistory(Location location){
        locationHistory.add(location);
    }

    public void addDayHistory(int day){
        dayHistory.add(day);
    }

    public void addTimeHistory(int time){
        timeHistory.add(time);
    }
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

    public void addDayOfMonthHistory(int dayWeek) {
        getDayOfMonthHistory().add(dayWeek);}

    public HashSet<Integer> getDayOfMonthHistory() {
        return dayOfMonthHistory;
    }

    public boolean isPlayedByAFriend() {
        return playedByAFriend;
    }

    public void setPlayedByAFriend(boolean playedByAFriend) {
        this.playedByAFriend = playedByAFriend;
    }

    public boolean isPlayedNear() {
        return playedNear;
    }

    public void setPlayedNear(boolean playedNear) {
        this.playedNear = playedNear;
    }

    public boolean isPlayedLastWeek() {
        return playedLastWeek;
    }

    public void setPlayedLastWeek(boolean playedLastWeek) {
        this.playedLastWeek = playedLastWeek;
    }

    public boolean isDownload() {
        return download;
    }

    public String getSongUri() {
        return songUri;
    }

    public String getSongDownloadUri(){return songDownloadUri;}
}
