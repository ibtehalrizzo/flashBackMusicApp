package team20.flashbackmusic;

import android.location.Location;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by christhoperbernard on 04/03/18.
 */

public class ScoreVibe implements IScore {
    ArrayList<String> listSong;
    ArrayList<Song> songs;

    public ScoreVibe(List<String> listSong, List<Song> songs) {   //add the tracks from the album to the score
        this.listSong = new ArrayList<>(listSong);
        this.songs = new ArrayList<>(songs);
        //Create score system
    }

    public void score(Location location, int day, int time) {
        int len = listSong.size();// real-time location and time

        //iterate through list of songs and put scores
        for (int i = 0; i < len; i++) {
            String name = listSong.get(i);
            Song song = songs.get(i);

            //reinitialize song score initially
            song.setScore(0);

            // get location and time history of corresponding song
            HashSet<Location> locationHistory = song.getLocationHistory();
            HashSet<Integer> dayHistory = song.getDayHistory();
            HashSet<Integer> timeHistory = song.getTimeHistory();

            //get current status of song
            int status = song.getStatus();


            //check if a track was played near the user's location (score + 3) NOTE: SHOULD BE EQUAL WEIGHT

            //check if the song was played in the last week (score + 2)

            //check if the song was played by a friend (score + 1)

            //If location is the same, score increase TODO: CHECK FROM DATABASE
            if (locationHistory.contains(location))
                song.setScore(song.getScore()+1);
            else{
                Iterator<Location> itr = locationHistory.iterator();
                while (itr.hasNext()) {
                    if (itr.next().distanceTo(location) <= 305) {
                        song.setScore(song.getScore()+1);
                    }
                }
            }

            // If time is the same, score increase
            if (timeHistory.contains(time))
                song.setScore(song.getScore()+1);

            // If the song was played in the same day, score increase
            if (dayHistory.contains(day))
                song.setScore(song.getScore()+1);


            // If the song is favorite, score increase
            if (status == 1)
                song.setScore(song.getScore()+2);
            // If the song is disliked, the song will never be played
            else if (status == -1)
                song.setScore(-1);
        }
    }
}
