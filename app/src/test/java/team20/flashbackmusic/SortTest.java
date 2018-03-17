package team20.flashbackmusic;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Created by dhoei on 14/03/18.
 */

public class SortTest {

    ArrayList<Song> songList;
    ArrayList<String> songTitleList;
    ArrayList<String> songPlayingList;
    Sort sortTest;

    Song song1 = new Song("Amass", "foo", "arb", 10, 200);
    Song song2 = new Song("Github", "bar", "car", 12, 5);
    Song song3 = new Song("Short", "foobar", "bord", 15, 10);
    Song song4 = new Song("Long", "foolar", "dart", 20, 23);


    @Before
    public void setup() {
        songList = new ArrayList<Song>();
        songTitleList = new ArrayList<>();
        songPlayingList = new ArrayList<>();

        songList.add(song1);
        songList.add(song2);
        songList.add(song3);
        songList.add(song4);

        songTitleList.add(song1.getTitle());
        songTitleList.add(song2.getTitle());
        songTitleList.add(song3.getTitle());
        songTitleList.add(song4.getTitle());

        songPlayingList.add(song1.getTitle());
        songPlayingList.add(song2.getTitle());
        songPlayingList.add(song3.getTitle());
        songPlayingList.add(song4.getTitle());

    }

    @Test
    public void sortingTestAlbum() {

        sortTest = new SortAlbum();
        sortTest.sortPlayList(songList, songTitleList,
                songPlayingList);

        assertEquals(song1.getTitle(), songPlayingList.get(0));
        assertEquals(song2.getTitle(), songPlayingList.get(2));
        assertEquals(song3.getTitle(), songPlayingList.get(1));
        assertEquals(song4.getTitle(), songPlayingList.get(3));
    }

    @Test
    public void sortingTestArtist() {

        sortTest = new SortArtist();
        sortTest.sortPlayList(songList, songTitleList,
                songPlayingList);

        assertEquals(song1.getTitle(), songPlayingList.get(1));
        assertEquals(song2.getTitle(), songPlayingList.get(0));
        assertEquals(song3.getTitle(), songPlayingList.get(2));
        assertEquals(song4.getTitle(), songPlayingList.get(3));
    }

    @Test
    public void sortingTestTitle() {

        sortTest = new SortTitle();
        sortTest.sortPlayList(songList, songTitleList,
                songPlayingList);

        assertEquals(song1.getTitle(), songPlayingList.get(0));
        assertEquals(song2.getTitle(), songPlayingList.get(1));
        assertEquals(song3.getTitle(), songPlayingList.get(3));
        assertEquals(song4.getTitle(), songPlayingList.get(2));
    }

    // Sort according to which one is pushed first into the list
    @Test
    public void sortingTestDefault() {

        sortTest = new SortDefault();
        sortTest.sortPlayList(songList, songTitleList,
                songPlayingList);

        assertEquals(song1.getTitle(), songPlayingList.get(0));
        assertEquals(song2.getTitle(), songPlayingList.get(1));
        assertEquals(song3.getTitle(), songPlayingList.get(2));
        assertEquals(song4.getTitle(), songPlayingList.get(3));
    }

    @Test
    public void sortingTestStatus() {

        songList = new ArrayList<Song>();
        songTitleList = new ArrayList<>();
        songPlayingList = new ArrayList<>();

        song1.setStatus(-1);    // Amass
        song2.setStatus(0);     // Github
        song3.setStatus(-1);    // Short
        song4.setStatus(1);     // Long

        songList.add(song1);
        songList.add(song2);
        songList.add(song3);
        songList.add(song4);

        songTitleList.add(song1.getTitle());
        songTitleList.add(song2.getTitle());
        songTitleList.add(song3.getTitle());
        songTitleList.add(song4.getTitle());

        songPlayingList.add(song1.getTitle());
        songPlayingList.add(song2.getTitle());
        songPlayingList.add(song3.getTitle());
        songPlayingList.add(song4.getTitle());

        sortTest = new SortStatus();
        sortTest.sortPlayList(songList, songTitleList,
                songPlayingList);

        assertEquals(song1.getTitle(), songPlayingList.get(3));
        assertEquals(song2.getTitle(), songPlayingList.get(1));
        assertEquals(song3.getTitle(), songPlayingList.get(2));
        assertEquals(song4.getTitle(), songPlayingList.get(0));

    }

}
