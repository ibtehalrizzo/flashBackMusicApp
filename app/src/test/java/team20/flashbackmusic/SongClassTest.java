package team20.flashbackmusic;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by dhoei on 16/02/18.
 */

public class SongClassTest {

    Song testSong = new Song("Test", "Hello", "Album", 132, 1234);

    @Test
    public void songConstructorAndGetterTest() throws Exception {

        assertEquals("Test", testSong.getTitle());
        assertEquals("Hello", testSong.getArtist());
        assertEquals("Album", testSong.getAlbum());
        assertEquals(132, testSong.getDuration());
        assertEquals(1234, testSong.getSongResId());
        assertEquals(null, testSong.getRecentDateTime());
        assertEquals(null, testSong.getRecentLoc());

        assertEquals(0, testSong.getStatus());
        assertEquals(0, testSong.getScore());
    }

    @Test
    public void setterTest() throws Exception {
/*        public void setScore(int s) { score = s; }

        public void setStatus(int s) { status = s; }*/

        testSong.setScore(20);
        testSong.setStatus(1);

        assertEquals(1, testSong.getStatus());
        assertEquals(20, testSong.getScore());
    }

}
