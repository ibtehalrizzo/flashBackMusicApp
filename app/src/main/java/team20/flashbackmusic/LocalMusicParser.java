package team20.flashbackmusic;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by dhoei on 05/03/18.
 */

public class LocalMusicParser implements IMusicParser{

    private final int SONG_TITLE = MediaMetadataRetriever.METADATA_KEY_TITLE;
    private final int SONG_ARTIST = MediaMetadataRetriever.METADATA_KEY_ARTIST;
    private final int SONG_ALBUM = MediaMetadataRetriever.METADATA_KEY_ALBUM;
    private final int SONG_DURATION = MediaMetadataRetriever.METADATA_KEY_DURATION;
    private MainActivity mainActivity;
    private MediaMetadataRetriever mmr;
    private List<Song> songListObj;

    public LocalMusicParser(MainActivity mainActivity, MediaMetadataRetriever mmr, List<Song> songListObj)
    {
        this.mainActivity = mainActivity;
        this.mmr = mmr;
        this.songListObj = songListObj;
    }


    /** This parse all the music from raw directory
     *  and store the songs in all the list inputs
     *
     *  @param songList the list of original file name of song
     *  @param songTitleList the list of title-artist of song to be displayed
     */
    public void getMusic(List songList, List songTitleList) {
        Field[] fields = R.raw.class.getFields();

        for (int i = 0; i < fields.length; i++) {
            String songFilename = fields[i].getName();
            songList.add(songFilename);

            int resId = mainActivity.getResources().getIdentifier(songFilename,
                    "raw", mainActivity.getPackageName());
            Uri mediaPath = Uri.parse("android.resource://" + mainActivity.getPackageName() +
                    "/" + resId);

            mmr.setDataSource(mainActivity, mediaPath);

            //add list of song objects
            String title = mmr.extractMetadata(SONG_TITLE);
            String artist = mmr.extractMetadata(SONG_ARTIST);
            String album = mmr.extractMetadata(SONG_ALBUM);
            String duration = mmr.extractMetadata(SONG_DURATION);

            if(artist == null)
                artist = "Unknown Artist";
            if(album == null)
                album = "Unknown Album";

            long durationToLong = Long.parseLong(duration);

            songListObj.add(new Song(title, artist, album, durationToLong, resId));

            //add to list for display TODO: we can get data from song object instead
            String display = title + " - " + artist;
            songTitleList.add(display);

        }
    }

    /** This populate the album list of MainActivity
     *  PRECONDITION: tracks is already populated
     * @param songListObj
     * @param albumList
     */
    public void populateAlbum(List<Song> songListObj, Hashtable<String, Album> albumList) {


        for (int i = 0; i < songListObj.size(); i++) {
            String albumName = songListObj.get(i).getAlbum();
            Log.d("Populating album:", albumName);
            if (!albumList.containsKey(albumName)) {
                //create album
                Album newAlbum = new Album(albumName);
                //add current track
                newAlbum.addTrack(songListObj.get(i));
                albumList.put(albumName, newAlbum);
                Log.d("new album:", newAlbum.toString() + " with song "
                        + songListObj.get(i).getTitle());

            } else {
                albumList.get(albumName).addTrack(songListObj.get(i));
                Log.d("existing albumPopulate:", albumName + " with song " +
                        songListObj.get(i).getTitle());
            }
        }
    }
}
