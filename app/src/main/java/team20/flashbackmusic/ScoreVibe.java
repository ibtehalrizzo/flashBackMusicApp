package team20.flashbackmusic;

import android.location.Location;
import android.util.Log;

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
            song.setPlayedLastWeek(false);
            song.setPlayedByAFriend(false);
            song.setPlayedNear(false);

            // get location and time history of corresponding song
            HashSet<Location> locationHistory = song.getLocationHistory();
            HashSet<Integer> dayHistory = song.getDayHistory();
            HashSet<Integer> timeHistory = song.getTimeHistory();
            HashSet<Integer> dayMonthHistory = song.getDayOfMonthHistory();

            //get current status of song
            int status = song.getStatus();

            //reset song status before scoring
//            song.setPlayedNear(false);
//            song.setPlayedLastWeek(false);
//            song.setPlayedByAFriend(false); //TODO

            //check if a track was played near the user's location (score + 1) NOTE: SHOULD BE EQUAL WEIGHT
//            Iterator<Location> itr = locationHistory.iterator();
//            while (itr.hasNext()) {
//                if (itr.next().distanceTo(location) <= 305) {
//                    Log.d("location same:", "trigerred");
//                    song.setScore(song.getScore()+1);
//                    song.setPlayedNear(true);
//                    break;
//                }
//            }


            Log.d("Score:", "Scoring called");
            if (locationHistory.contains(location)){
                song.setScore(song.getScore()+2);
                Log.d("location same:", "trigerred");
                song.setPlayedNear(true);

            }
            else{
                Iterator<Location> itr = locationHistory.iterator();
                while (itr.hasNext()) {
                    Log.d("IteratorLocation","iterating location history");
                    if (itr.next().distanceTo(location) <= 305) {
                        song.setScore(song.getScore()+2);
                    }
                }
            }


            //check if the song was played in the last week (score + 1)
            Iterator<Integer> iteratorDayMonth = dayMonthHistory.iterator();
            int mostRecentPlayed = Integer.MAX_VALUE;
            while(iteratorDayMonth.hasNext())
            {
                int nextDayMonth = iteratorDayMonth.next();
                if(mostRecentPlayed == Integer.MAX_VALUE)
                {
                    mostRecentPlayed = nextDayMonth;
                }
                else if(mostRecentPlayed < nextDayMonth)
                {
                    mostRecentPlayed = nextDayMonth;
                }

            }

            //day - 7 means last week
            if(dayMonthHistory.contains(mostRecentPlayed-7)){
                song.setScore(song.getScore()+2);
                song.setPlayedLastWeek(true);
            }

            //check if the song was played by a friend (score + 1) TODO:check from DATABASE



            //If location is the same, score increase

//            if (locationHistory.contains(location))
//                song.setScore(song.getScore()+1);
//            else{
//                Iterator<Location> itr = locationHistory.iterator();
//                while (itr.hasNext()) {
//                    if (itr.next().distanceTo(location) <= 305) {
//                        song.setScore(song.getScore()+1);
//                    }
//                }
//            }
//
//            // If time is the same, score increase
//            if (timeHistory.contains(time))
//                song.setScore(song.getScore()+1);
//
//            // If the song was played in the same day, score increase
//            if (dayHistory.contains(day))
//                song.setScore(song.getScore()+1);


            // If the song is favorite, score increase
            if (status == 1)
                song.setScore(song.getScore()+2);
            // If the song is disliked, the song will never be played
            else if (status == -1)
                song.setScore(-1);
        }
    }
}
