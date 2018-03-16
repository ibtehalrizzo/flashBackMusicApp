package team20.flashbackmusic;

import android.location.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by yujingwen199756 on 3/9/18.
 */

public class User {
    private String userName;
    private Map<String, User> friendList;
    private ArrayList<Song> downloadedSong;
    private Location lastCurrentLocation;

    public User()
    {
        this.userName = "noname";
        friendList = new HashMap<>();
        downloadedSong = new ArrayList<>();
        lastCurrentLocation = null;
    }

    public User(String userName){
        this.userName = userName;
        friendList = new HashMap<>();
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
    public Map<String, User> getFriendList(){ return friendList;}
    public ArrayList<Song> getDownloadedSong(){ return downloadedSong;}
//    public Location getLastCurrentLocation(){ return lastCurrentLocation;}

    public void setUserName(String userName){ this.userName = userName;}
    public void setFriendList(Hashtable<String, User> friendList){ this.friendList = friendList;}
    public void setDownloadedSong(ArrayList<Song> downloadedSong){ this.downloadedSong = downloadedSong;}
    public void setLastCurrentLocation(Location lastCurrentLocation){ this.lastCurrentLocation= lastCurrentLocation;}

    public void addFriend(String name, User friend){
        friendList.put(name, friend);
    }

}
