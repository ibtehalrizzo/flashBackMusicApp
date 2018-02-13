package team20.flashbackmusic;

/**
 * Created by dhoei on 06/02/18.
 */

public class Song {

    //private members of song
    private String title, artist, album, mostRecentDateTime,mostRecentLocation;
    private int score, status;
    private long duration;


    //constructor of song
    public Song(String title, String artist, String album, long duration) {
        mostRecentDateTime = null;
        mostRecentLocation = null;

        //for status, 1 = favorite, 0 = neutral, -1 = dislike
        //initialize with neutral status
        status = 0;

        //initialize with score 0 because the music has not been
        //played
        score = 0;

        this.title = title;
        this.artist = artist;
        this.album = album;

        // Convert duration to time
        this.duration = duration;
    }


    // GETTER AND SETTER METHOD //
    public String getTitle()
    {
        return title;
    }

    public String getArtist()
    {
        return artist;
    }

    public String getAlbum() { return album; }

    public String getRecentDateTime()
    {
        return mostRecentDateTime;
    }

    public String getRecentLoc()
    {
        return mostRecentLocation;
    }

    public int getStatus()
    {
        return status;
    }

    public int getScore()
    {
        return score;
    }

    public void setScore(int s) { score = s; }

    public void setStatus(int s) { status = s; }


}
