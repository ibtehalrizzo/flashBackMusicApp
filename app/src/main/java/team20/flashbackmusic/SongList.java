package team20.flashbackmusic;

import java.util.Hashtable;

/**
 * Created by lenovo on 2018/2/10.
 */

public class SongList {
    Hashtable<String, Song> songlist;
    public SongList(){
        songlist = new Hashtable<String, Song>();
    }
    public void add(Song song){
        songlist.put(song.getTitle(),song);
    }
}
