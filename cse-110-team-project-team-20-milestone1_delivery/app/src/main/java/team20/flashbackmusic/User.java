package team20.flashbackmusic;

import android.location.Location;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by yujingwen199756 on 3/9/18.
 */

public class User {
    private String userName;
    private Hashtable<String, User> friendList;
    private ArrayList<Song> downloadedSong;
    private Location lastCurrentLocation;

    public User(String userName){
        this.userName = userName;
        friendList = new Hashtable<>();
        downloadedSong = new ArrayList<>();
        lastCurrentLocation = null;
    }

    public User(String userName, Location lastCurrentLocation){
        this.userName = userName;
        friendList = new Hashtable<>();
        downloadedSong = new ArrayList<>();
        this.lastCurrentLocation = lastCurrentLocation;
    }

    public String getUserName(){ return userName;}
    public Hashtable<String, User> getFriendList(){ return friendList;}
    public ArrayList<Song> getDownloadedSong(){ return downloadedSong;}
    public Location getLastCurrentLocation(){ return lastCurrentLocation;}

    public void setUserName(String userName){ this.userName = userName;}
    public void setFriendList(Hashtable<String, User> friendList){ this.friendList = friendList;}
    public void setDownloadedSong(ArrayList<Song> downloadedSong){ this.downloadedSong = downloadedSong;}
    public void setLastCurrentLocation(Location lastCurrentLocation){ this.lastCurrentLocation= lastCurrentLocation;}

}
