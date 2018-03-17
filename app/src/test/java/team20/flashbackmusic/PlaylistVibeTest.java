package team20.flashbackmusic;

import android.util.Log;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Hashtable;

import static org.junit.Assert.assertEquals;

/**
 * Created by dhoei on 14/03/18.
 */

public class PlaylistVibeTest {

    ArrayList<String> sortingList = new ArrayList<>();
    ArrayList<Song> songs = new ArrayList<>();
    Hashtable<String, Integer> indexTosong = new Hashtable<>();
    PlaylistVibe testVibe;

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

        testVibe = new PlaylistVibe(sortingList, songs, indexTosong);

        testVibe.sorter();
        assertEquals(sortingList.get(0), song1.getTitle());
    }

    @Test
    public void testDifferentScore() {
        song1.setScore(10);
        song2.setScore(15);

        songs.add(song1);
        songs.add(song2);

        testVibe = new PlaylistVibe(sortingList, songs, indexTosong);

        testVibe.sorter();
        assertEquals(sortingList.get(0), song2.getTitle());
        assertEquals(sortingList.get(1), song1.getTitle());
    }

    @Test
    public void testSameScorePlayedNearby() {
        song1.setScore(10);
        song2.setScore(10);

        song1.setPlayedNear(true);
        song2.setPlayedNear(false);

        songs.add(song1);
        songs.add(song2);

        testVibe = new PlaylistVibe(sortingList, songs, indexTosong);

        testVibe.sorter();
        assertEquals(sortingList.get(0), song1.getTitle());
        assertEquals(sortingList.get(1), song2.getTitle());

    }

    @Test
    public void testSameScorePlayedLastWeek() {
        song1.setScore(10);
        song2.setScore(10);

        song1.setPlayedLastWeek(true);
        song2.setPlayedLastWeek(false);

        songs.add(song1);
        songs.add(song2);

        testVibe = new PlaylistVibe(sortingList, songs, indexTosong);

        testVibe.sorter();
        assertEquals(sortingList.get(0), song1.getTitle());
        assertEquals(sortingList.get(1), song2.getTitle());

    }

    @Test
    public void testSameScorePlayedByFriend() {
        song1.setScore(10);
        song2.setScore(10);

        song1.setPlayedByAFriend(true);
        song2.setPlayedByAFriend(false);

        songs.add(song1);
        songs.add(song2);

        testVibe = new PlaylistVibe(sortingList, songs, indexTosong);

        testVibe.sorter();
        assertEquals(sortingList.get(0), song1.getTitle());
        assertEquals(sortingList.get(1), song2.getTitle());

    }

    @Test
    public void testAllTied() {
        song1.setScore(10);
        song2.setScore(10);

        song1.setPlayedNear(true);
        song2.setPlayedNear(true);

        song1.setPlayedLastWeek(true);
        song2.setPlayedLastWeek(true);

        song1.setPlayedByAFriend(true);
        song2.setPlayedByAFriend(true);

        songs.add(song1);
        songs.add(song2);

        testVibe = new PlaylistVibe(sortingList, songs, indexTosong);

        testVibe.sorter();
        assertEquals(song1.getTitle(), sortingList.get(1));
        assertEquals(song2.getTitle(), sortingList.get(0));
    }
}
