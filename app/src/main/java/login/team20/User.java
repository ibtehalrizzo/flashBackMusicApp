package login.team20;

/**
 * Created by lenovo on 2018/3/10.
 */

import android.location.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by yujingwen199756 on 3/9/18.
 */

public class User {
    private String userName;
    private Map<String, User> friendList;
    private List<String> downloadedSong;
    private Location lastCurrentLocation;

    public User(){}

    public User(String userName) {
        this.userName = userName;
        friendList = new HashMap<>();
        downloadedSong = new ArrayList<>();
        lastCurrentLocation = null;
    }
    public User(String userName, Location lastCurrentLocation) {
        this.userName = userName;
        friendList = new HashMap<>();
        downloadedSong = new ArrayList<>();
        this.lastCurrentLocation = lastCurrentLocation;
    }

    public String getUserName() {
        return userName;
    }

    public Map<String, User> getFriendList() {
        return friendList;
    }

    public List<String> getDownloadedSong() {
        return downloadedSong;
    }

    public Location getLastCurrentLocation() {
        return lastCurrentLocation;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    //public void setFriendList(Map<String, User> friendList) {
      //  this.friendList = friendList;
    //}

    //public void setDownloadedSong(Map<String, Song> downloadedSong) {
      //  this.downloadedSong = downloadedSong;
    //}

    public void setLastCurrentLocation(Location lastCurrentLocation) {
        this.lastCurrentLocation = lastCurrentLocation;
    }
    public void addfriend(String name, User friend){
        friendList.put(name,friend);
    }

    public void addDownloadedSong(String songname){
        downloadedSong.add(songname);
    }
}

