package team20.flashbackmusic;

import android.location.Location;
import android.support.test.rule.ActivityTestRule;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

import static junit.framework.Assert.assertEquals;

/**
 * Created by lenovo on 2018/2/17.
 */

public class test_flashback {
    public ActivityTestRule<MainActivity> scoreActivityTestRule= new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void test() {
        Song song1 = new Song("test1", "a1", "a1", 0, 0);
        Song song2 = new Song("test2", "a2", "a2", 0, 0);
        int day = 7;
        int time = 1;

        song2.addDayHistory(day);
        song2.addTimeHistory(day);
        //song2.timeHistory.add(time);//score=2
        //song2.status=1;
        song1.addDayHistory(2);     //score=1
        song1.addTimeHistory(time);
        ArrayList<Song> songList = new ArrayList<>();
        songList.add(song1);
        songList.add(song2);
        ArrayList<String> titles = new ArrayList<>();
        titles.add(song1.getTitle());
        titles.add(song2.getTitle());
        Calendar time1 = Calendar.getInstance();
        time1.set(Calendar.HOUR_OF_DAY, 10);
        int hours = time1.get(Calendar.HOUR_OF_DAY);
        String s = time1.toString();
        Score scoretest = new Score(titles, songList);
        Location location = new Location("l1");
        scoretest.score(location, day, time);
        Hashtable<String, Integer> index = new Hashtable<>();
        index.put(song1.getTitle(), 1);
        index.put(song2.getTitle(), 2);
        PlayList_flashback playList_flashback = new PlayList_flashback(titles, songList, index);
        playList_flashback.sorter();
        assertEquals("test2", playList_flashback.sortingList.get(0));
        assertEquals("test1", playList_flashback.sortingList.get(1));

        playList_flashback.changeToDislike(2);     //set the score of song2 to -1;
        playList_flashback.sorter();
        assertEquals("test1", playList_flashback.sortingList.get(0));
        assertEquals("test2", playList_flashback.sortingList.get(1));

        playList_flashback.changeToNeutral(2, location, day, time);   // set the score of song2 to the score before
        playList_flashback.sorter();
        assertEquals("test2", playList_flashback.sortingList.get(0));
        assertEquals("test1", playList_flashback.sortingList.get(1));

        playList_flashback.changeToFavorite(1);         //add 2 to the score of song1, score of song1 is 3
        playList_flashback.sorter();
        assertEquals("test1", playList_flashback.sortingList.get(0));
        assertEquals("test2", playList_flashback.sortingList.get(1));
    }
}

