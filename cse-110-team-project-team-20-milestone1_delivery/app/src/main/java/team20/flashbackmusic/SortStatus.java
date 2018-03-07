package team20.flashbackmusic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yujingwen199756 on 3/6/18.
 */

public class SortStatus implements Sort {

    @Override
    public List<Song> sortPlayList(List<Song> songList) {
        List<Song> sortedSongList = new ArrayList<>();
        for(int i = 0; i < songList.size(); i++){
            int temp = songList.get(i).getStatus();
            int index = i;
            for(int j = i; j < songList.size(); j++){
                int currStatus = songList.get(j).getStatus();
                if(temp < currStatus){
                    temp = currStatus;
                    index = j;
                }
            }
            sortedSongList.add(i,songList.get(index));
        }
        return sortedSongList;
    }
}
