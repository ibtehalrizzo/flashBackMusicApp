package team20.flashbackmusic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yujingwen199756 on 3/6/18.
 */

public class SortDefault implements Sort {
    @Override
    public List<Song> sortPlayList(List<Song> songList) {
        List<Song> sortedSongList = new ArrayList<>();
        for(int i = 0; i < songList.size(); i++){
            Date temp = songList.get(i).getMostRecentDateTime();
            int index = i;
            for(int j = i; j < songList.size(); j++){
                Date currDate = songList.get(j).getMostRecentDateTime();
                if(temp.before(currDate)){
                    temp = currDate;
                    index = j;
                }
            }
            sortedSongList.add(i,songList.get(index));
        }
        return sortedSongList;
    }
}
