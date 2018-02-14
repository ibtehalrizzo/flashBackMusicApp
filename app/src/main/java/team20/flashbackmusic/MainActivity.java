package team20.flashbackmusic;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final int SONG_TITLE = MediaMetadataRetriever.METADATA_KEY_TITLE;
    private final int SONG_ARTIST = MediaMetadataRetriever.METADATA_KEY_ARTIST;
    private final int SONG_ALBUM = MediaMetadataRetriever.METADATA_KEY_ALBUM;
    private final int SONG_DURATION = MediaMetadataRetriever.METADATA_KEY_DURATION;

    private MediaPlayer mediaPlayer;

    // List of songs
    private ListView listView;
    private ListAdapter listAdapter;

    // List of albums
    private GridView albumView;
    private ListAdapter albumAdapter;

    private MediaMetadataRetriever mmr = new MediaMetadataRetriever();
    private TextView nowPlayingView, locationView, durationView;
    // TODO: Change to List<Song> later
    private List<String> songList;
    private List<String> songTitleList;

    private List<Song> songListObj;


    //Create list of albums
    private Hashtable<String, Album> albumList;
    private ArrayList<Album> tempListAlbum;


    private boolean flashbackFlag = false;
    private boolean playingAlbumFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button flashbackButton = (Button) findViewById(R.id.fb);

        flashbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flashbackFlag) {
                    flashbackFlag = false;
                    Toast.makeText(MainActivity.this, "Flashback mode off", Toast.LENGTH_SHORT).show();
                } else {
                    flashbackFlag = true;
                    Toast.makeText(MainActivity.this, "Flashback mode on", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Play or Pause button
        final FloatingActionButton playButton = (FloatingActionButton) findViewById(R.id.playButton);

        // Init tag to play (TODO: should change to previously closed state)
        playButton.setTag(R.drawable.ic_play_arrow_black_24dp);

        nowPlayingView = (TextView) findViewById(R.id.nowPlaying);
        locationView = (TextView) findViewById(R.id.playingLoc);
        durationView = (TextView) findViewById(R.id.songDuration);

        albumView = (GridView) findViewById(R.id.albumList);


        // Make list
        listView = (ListView) findViewById(R.id.songList);

        songList = new ArrayList<>();
        songTitleList = new ArrayList<>();
        songListObj = new ArrayList<>();
        albumList = new Hashtable<>();

        //GET LIST OF SONGS FROM RAW DIRECTORY
        getMusic(songList, songTitleList);

        //populate album after we get music
        populateAlbum(songListObj, albumList);

        //show list of song in the track list view
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, songTitleList);
        listView.setAdapter(listAdapter);

        //set up listener for list of tracks list view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // Disable album queue
                playingAlbumFlag = false;

                if(!flashbackFlag){
                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                    }

                    int resID = getResources().getIdentifier(songList.get(i), "raw", getPackageName());
                    mediaPlayer = MediaPlayer.create(MainActivity.this, resID);
                    mediaPlayer.start();
                    playButton.setImageResource(R.drawable.ic_pause_black_24dp);



                    //hover the now playing text view
                    String repeat = new String(new char[1]).replace("\0", " ");
                    nowPlayingView.setText(repeat + songTitleList.get(i) + repeat);
                    nowPlayingView.setSelected(true);
                }
                else
                {
                    //TODO: add pop up message where the user need to press ok
                    Toast.makeText(MainActivity.this, "You are in flashback mode!\n" +
                            "Please go to normal mode to select music", Toast.LENGTH_SHORT).show();
                }


            }
        });


        //show list of albums
        tempListAlbum = new ArrayList<>(albumList.values());
        albumAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, tempListAlbum);
        albumView.setAdapter(albumAdapter);

        //set on item click listener
        albumView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // Enable album queue
                playingAlbumFlag = true;

                if (!flashbackFlag)
                {
                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                    }

                    final Album albumToPlay = tempListAlbum.get(i);
                    albumToPlay.queueAllSong();

//                    while(!albumToPlay.isQueueEmpty() && mediaPlayer.isPlaying())
//                    {
                        Song songToPlay = albumToPlay.getNextSongToPlay();
                        int resID = songToPlay.getSongResId();
                        mediaPlayer = MediaPlayer.create(MainActivity.this, resID);
                        mediaPlayer.start();
                        playButton.setImageResource(R.drawable.ic_pause_black_24dp);

                        //hover the now playing text view TODO: make this a method
                        String repeat = new String(new char[1]).replace("\0", " ");
                        String currentPlayingSongDisplay = songToPlay.getTitle() + " - " + songToPlay.getArtist();
                        nowPlayingView.setText(repeat + currentPlayingSongDisplay + repeat);
                        nowPlayingView.setSelected(true);


//                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                            @Override
//                            public void onCompletion(MediaPlayer mediaPlayer) {
//                                if (playingAlbumFlag)
//                                {
//                                    nowPlayingView.setText("hello");
//                                }
//                            }
//                        });

//                    }

                }
                else
                {
                    Toast.makeText(MainActivity.this, "You are in flashback mode!\n" +
                            "Please go to normal mode to select music", Toast.LENGTH_SHORT).show();
                }
            }

        });


        // Play next song after song ends
//        if(mediaPlayer != null)
//        {
//            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mediaPlayer) {
//                    if (playingAlbumFlag)
//                    {
//                        nowPlayingView.setText("hello");
//                    }
//                }
//            });
//        }



        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Replace with play the current music played
                //or just change to pause button if there is no music
                //selected
                if(mediaPlayer != null)
                {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        nowPlayingView.setSelected(false);
                        playButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);

                    }
                    else
                    {
                        mediaPlayer.start();
                        nowPlayingView.setSelected(true);
                        playButton.setImageResource(R.drawable.ic_pause_black_24dp);

                    }
                }
                else
                {
                    if (playButton.getTag().equals(R.drawable.ic_play_arrow_black_24dp))
                    {
                        playButton.setImageResource(R.drawable.ic_pause_black_24dp);
                        playButton.setTag(R.drawable.ic_pause_black_24dp);
                    }
                    else
                    {
                        playButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                        playButton.setTag(R.drawable.ic_play_arrow_black_24dp);
                    }
                }

            }
        });

    }

    public String convertMillisecondsToTime(long milliseconds) {
        String finalTimerString = "";
        String secondsString;

        int hours = (int) (milliseconds / 1000*60*60);
        int minutes = (int) (milliseconds % (1000*60*60) / (1000*60*60));
        int seconds = (int) (milliseconds % (1000*60*60) % (1000*60*60) / 1000);

        // Add hours
        if (hours > 0) {
            finalTimerString = hours + ":";
        }


        if (seconds < 10) {
            secondsString = "0" + seconds;
        }
        else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        return finalTimerString;
    }


    // TODO: Pull metadata from mp3 and send to Song.
//    public void getMusic(List songList)
//    {
//        Field[] fields = R.raw.class.getFields();
//
//        Context c = getBaseContext();
//        ContentResolver contentResolver = getContentResolver();
//
////        cursor = contentResolver.query(
////                MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
////                null, null, null, null);
//
////        cursor.moveToFirst();
////        if (cursor != null) {
////            while (cursor.moveToNext()) {
////                Log.d(cursor.getString(cursor.getPosition()), "test song list: ");
////            }
////        }
//        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
//        {
////            Log.d(cursor.getString(cursor.getPosition()), "test song list: ");
//            String test = cursor.getString(0);
//            Log.d("tester", test);
//        }
//
////        while (cursor.moveToNext()) {
////            songList.add(cursor.getString(2));
////        }
//
//    }

//    Old getMusic method
    public void getMusic(List songList, List songTitleList)
    {
        Field[] fields = R.raw.class.getFields();

        for (int i = 0; i < fields.length; i++) {
            String songFilename = fields[i].getName();
            songList.add(songFilename);

            int resId = getResources().getIdentifier(songFilename, "raw", getPackageName());

            Uri mediaPath = Uri.parse("android.resource://" + getPackageName() +
                    "/" + resId);
            mmr.setDataSource(this, mediaPath);


            //add list of song objects
            String title = mmr.extractMetadata(SONG_TITLE);
            String artist = mmr.extractMetadata(SONG_ARTIST);
            String album = mmr.extractMetadata(SONG_ALBUM);
            String duration = mmr.extractMetadata(SONG_DURATION);

            long durationToLong = Long.parseLong(duration);

            Song s = new Song(title, artist, album, durationToLong, resId);
            songListObj.add(s);


            //add to list for display TODO: we can get data from song object instead
            String display = mmr.extractMetadata(SONG_TITLE) +" - "
                    + mmr.extractMetadata(SONG_ARTIST);
            songTitleList.add(display);

        }


    }

    //precondition: tracks is already populated
    public void populateAlbum(List<Song> songListObj, Hashtable<String, Album> albumList){
        for(int i = 0; i < songListObj.size(); i++)
        {
            String albumName = songListObj.get(i).getAlbum();
            if(!albumList.contains(albumName))
            {
                //create album
                Album newAlbum = new Album(albumName);
                //add current track
                newAlbum.addTrack(songListObj.get(i));

                albumList.put(albumName, newAlbum);
            }
            else
            {
                //add current track to album
                albumList.get(albumName).addTrack(songListObj.get(i));
            }
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




}
