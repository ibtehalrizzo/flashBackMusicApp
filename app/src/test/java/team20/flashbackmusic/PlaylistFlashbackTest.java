package team20.flashbackmusic;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import static org.junit.Assert.assertEquals;

/**
 * Created by dhoei on 14/03/18.
 */

public class PlaylistFlashbackTest {

    ArrayList<String> sortingList = new ArrayList<>();
    ArrayList<Song> songs = new ArrayList<>();
    Hashtable<String, Integer> indexTosong = new Hashtable<>();
    PlaylistFlashback testFlashback;

    Song song1 = new Song("Amass", "foo", "car", 10, 200);
    Song song2 = new Song("Github", "bar", "car", 12, 5);

    @Before
    public void setup() {
        sortingList.add(song1.getTitle());
        sortingList.add(song2.getTitle());

        indexTosong.put(song1.getTitle(), 0);
        indexTosong.put(song2.getTitle(), 1);
    }

    @Test
    public void testSameItem() {
        ArrayList<String> sortingList = new ArrayList<>();
        ArrayList<Song> songs = new ArrayList<>();
        Hashtable<String, Integer> indexTosong = new Hashtable<>();

        songs.add(song1);
        sortingList.add(song1.getTitle());
        indexTosong.put(song1.getTitle(), 0);

        testFlashback = new PlaylistFlashback(sortingList, songs, indexTosong);

        testFlashback.sorter();
        assertEquals(sortingList.get(0), song1.getTitle());
    }

    @Test
    public void testDifferentScore() {
        song1.setScore(10);
        song2.setScore(15);

        songs.add(song1);
        songs.add(song2);

        testFlashback = new PlaylistFlashback(sortingList, songs, indexTosong);

        testFlashback.sorter();
        assertEquals(song2.getTitle(), sortingList.get(0));
        assertEquals(song1.getTitle(), sortingList.get(1));
    }

    @Test
    public void testSameScoreDifferentTime1() {
        song1.setScore(10);
        song2.setScore(10);

        Date time1 = new Date(2);
        Date time2 = new Date(0);

        // Song 1 has more recent date than Song 2
        song1.setMostRecentDateTime(time1);
        song2.setMostRecentDateTime(time2);

        songs.add(song1);
        songs.add(song2);

        testFlashback = new PlaylistFlashback(sortingList, songs, indexTosong);

        testFlashback.sorter();
        assertEquals(song1.getTitle(), sortingList.get(0));
        assertEquals(song2.getTitle(), sortingList.get(1));
    }

    @Test
    public void testSameScoreDifferentTime2() {
        song1.setScore(10);
        song2.setScore(10);

        Date time1 = new Date(2);
        Date time2 = new Date(5);

        // Song 1 has more recent date than Song 2
        song1.setMostRecentDateTime(time1);
        song2.setMostRecentDateTime(time2);

        songs.add(song1);
        songs.add(song2);

        testFlashback = new PlaylistFlashback(sortingList, songs, indexTosong);

        testFlashback.sorter();
        assertEquals(song2.getTitle(), sortingList.get(0));
        assertEquals(song1.getTitle(), sortingList.get(1));
    }

    @Test
    public void testAllTie() {
        song1.setScore(10);
        song2.setScore(10);

        Date time1 = new Date(5);
        Date time2 = new Date(5);

        // Song 1 has more recent date than Song 2
        song1.setMostRecentDateTime(time1);
        song2.setMostRecentDateTime(time2);

        songs.add(song1);
        songs.add(song2);

        testFlashback = new PlaylistFlashback(sortingList, songs, indexTosong);

        testFlashback.sorter();
        assertEquals(song1.getTitle(), sortingList.get(0));
        assertEquals(song2.getTitle(), sortingList.get(1));
    }
}