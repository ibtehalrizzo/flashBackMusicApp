package team20.flashbackmusic;

import android.media.MediaPlayer;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import java.io.IOException;

/**
 * Created by dhoei on 05/03/18.
 */

public class MusicPlayer {

    private MainActivity activity;
    private MediaPlayer mediaPlayer;
    private MusicLocator musicLocator;
    private Album albumToPlay;
    private FloatingActionButton playButton;
    private boolean playingAlbumFlag;

    public MusicPlayer(MediaPlayer mp, MainActivity activity,
                       MusicLocator musicLocator, Album albumToPlay,
                       FloatingActionButton playButton) {

        this.mediaPlayer = mp;
        this.activity = activity;
        this.musicLocator = musicLocator;
        this.albumToPlay = albumToPlay;
        this.playButton = playButton;
    }

    /** This play the current all the current album songs
     *  PRECONDITION: album has at least 1 song
     */
    public void playAlbum()
    {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }


        Song songToPlay = albumToPlay.getNextSongToPlay();
        int resID = songToPlay.getSongResId();

        mediaPlayer = MediaPlayer.create(activity, resID);
        mediaPlayer.start();

        Log.d("album play", "playing next song in the album");

        //set button tag to pause
        changeToPauseButton();

        //set the current playing song view, location, date and time
        String currentPlayingSongDisplay = songToPlay.getTitle() + " - " + songToPlay.getArtist();
        activity.setNowPlayingView(currentPlayingSongDisplay);
        activity.showCurrentLocation(songToPlay);
        activity.showDateAndTime(songToPlay);

        //update the history of playing date and time of song
        activity.storeDateAndTime(songToPlay);
        try {
            musicLocator.storeLocation(songToPlay);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //set media player listener
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("onCompletionCalled", "yes!");
                if (playingAlbumFlag) {

                    //check if there is any song to play left in album
                    if (!albumToPlay.isQueueEmpty()) {
                        playAlbum();
                    }
                    //if there is no more album to play, go to waiting state
                    else
                    {
                        playingAlbumFlag = false;
                        //set button tag to play
                        changeToPlayButton();
                    }

                } else {
                    //set button tag to play
                    changeToPlayButton();
                }
            }
        });

    }

    /** This changes the pause button into play button
     *
     */
    public void changeToPlayButton() {
        playButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        playButton.setTag(R.drawable.ic_play_arrow_black_24dp);
    }

    /** This changes the play button into pause button
     *
     */
    public void changeToPauseButton() {
        playButton.setImageResource(R.drawable.ic_pause_black_24dp);
        playButton.setTag(R.drawable.ic_pause_black_24dp);
    }
}
