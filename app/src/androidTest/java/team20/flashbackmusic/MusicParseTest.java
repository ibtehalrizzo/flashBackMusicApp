package team20.flashbackmusic;

import android.media.MediaMetadataRetriever;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Hashtable;

import static org.junit.Assert.assertEquals;

/**
 * Created by dhoei on 14/03/18.
 */

public class MusicParseTest {

    LocalMusicParser parseTest;
    ArrayList<Song> songObj;
    ArrayList<Song> resultSongObj;
    ArrayList<String> songObjTitle;
    Hashtable<String, Album> albumList;

    @Rule
    public ActivityTestRule<MainActivity> scoreActivityTestRule =
            new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void setup() {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        songObj = new ArrayList<>();
        resultSongObj = new ArrayList<>();
        songObjTitle = new ArrayList<>();
        albumList = new Hashtable<>();

        parseTest = new LocalMusicParser(scoreActivityTestRule.getActivity(), mmr,
                resultSongObj);
    }

    @Test
    public void testGetMusic() {
        parseTest.getMusic(songObj, songObjTitle);

        for (int i = 0; i < resultSongObj.size(); i++) {
            assertEquals(songObjTitle.get(0), resultSongObj.get(0)
                    .getTitle() +
                    " - " + resultSongObj.get(0)
                    .getArtist());
        }
    }

    @Test
    public void testPopulateAlbum() {
        parseTest.getMusic(songObj, songObjTitle);
        parseTest.populateAlbum(resultSongObj, albumList);

        for (int i = 0; i < resultSongObj.size(); i++)
            assertEquals(resultSongObj.get(i)
                    .getAlbum(), albumList.get(
                    resultSongObj.get(i).getAlbum())
                    .toString());
    }

}
