package team20.flashbackmusic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yujingwen199756 on 3/6/18.
 */

public class SortArtist implements Sort {
    @Override
    public List<Song> sortPlayList(List<Song> songList) {
        List<Song> sortedSongList = new ArrayList<>();

        for(int i = 0; i < songList.size(); i++){
            String temp = songList.get(i).getArtist();
            int index = i;
            for(int j = i; j < songList.size(); j++){
                String currArtist = songList.get(j).getArtist();
                if(temp.compareTo(currArtist) > 0){
                    temp = currArtist;
                    index = j;
                }
            }
            sortedSongList.add(i,songList.get(index));
        }

        return sortedSongList;
    }
}
