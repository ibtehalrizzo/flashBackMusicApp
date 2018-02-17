package team20.flashbackmusic;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Created by dhoei on 16/02/18.
 */

public class AlbumClassTest {


    ArrayList<Song> songList;
    Album albumTest;

    @Before
    public void onSetUp() {

        Song song1 = new Song("Amass", "foo", "car", 10, 20);
        Song song2 = new Song("Github", "bar", "car", 10, 20);
        Song song3 = new Song("Short", "foobar", "car", 10, 20);
        Song song4 = new Song("Long", "foolar", "car", 10, 20);

        songList.add(song1);
        songList.add(song2);
        songList.add(song3);
        songList.add(song4);

        albumTest = new Album("Yellow");

    }

    @Test
    public void constructorCheck() {
        onSetUp();
        assertEquals("Yellow", albumTest.to);
    }


}
