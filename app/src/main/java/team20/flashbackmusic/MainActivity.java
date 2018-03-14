package team20.flashbackmusic;


import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements LocationListener {

    private final int SONG_TITLE = MediaMetadataRetriever.METADATA_KEY_TITLE;
    private final int SONG_ARTIST = MediaMetadataRetriever.METADATA_KEY_ARTIST;
    private final int SONG_ALBUM = MediaMetadataRetriever.METADATA_KEY_ALBUM;
    private final int SONG_DURATION = MediaMetadataRetriever.METADATA_KEY_DURATION;

    //initialize current index playing song
    private int  currentIndex = 0;

    // List of songs
    private ListView listView;
    private ListAdapter listAdapter;

    private MediaPlayer mediaPlayer;
    private MusicPlayer player;

    private Switch flashback;
    private FloatingActionButton playButton, nextSong,
            previousSong, showCurrentPlaylist;
    private Button addBtn, clickDownload;
    private EditText downloadInput;

//    private ScoreFlashback scoreFlashback;
    private IScore score;

    //playlistFlashback of the song to be played
    private PlaylistFBM playlist;

    // List of albums view
    private GridView albumView;
    private ListAdapter albumAdapter;
    private boolean flashOn = false;

    private MediaMetadataRetriever mmr = new MediaMetadataRetriever();

    //TextViews of this activity
    private TextView nowPlayingView, locationView, dateTimeView; //durationView;

    //list of songs according to purpose
    private List<String> songList; //to play music
    private List<String> songTitleList; //for display
    private List<Song> songListObj; //for storage of songs

    //data for playlistFlashback of song
    private ArrayList<String> sortingList;
    private Hashtable<String, Integer> indexTosong;

    //Create list of albums
    private Hashtable<String, Album> albumList; //for checking if album exist
    private ArrayList<Album> tempListAlbum;

    private boolean playingAlbumFlag = false;
    private Album albumToPlay;

    private LocationManager locationManager;
    private int currentUserMNEIndex = -1;
    private int currentUserDayOfWeek = -1;
    private Location currentUserlocation;

    private MusicLocator musicLocator;

    private DownloadManager downloadManager;
    private long Music_DownloadId;
    private String downloadTitle;
    private long downloadReference;
    private Uri currDownloadUri;

    private MyAdapter adapter;


    /**
     * Class for adapting list view to put +,x, and checklist signs
     */
    class MyAdapter extends BaseAdapter implements ListAdapter {
        private ArrayList<String> list = new ArrayList<String>();
        private Context context;
        //        private Button addBtn;
        int buttonimage = 1;

        /**
         * Constructor of the adapter of list view
         * @param list
         * @param context
         */
        public MyAdapter(ArrayList<String> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int pos) {
            return list.get(pos);
        }

        //@Override
        public long getItemId(int pos) {
            //return list.get(pos).getiD();
            //just return 0 if your list items do not have an Id variable.
            return 0;
        }

        @Override
        public View getView(final int position, final View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater)
                        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.listview, null);
            }
            //Handle TextView and display string from your list
            final TextView listItemText = v.findViewById(R.id.list_item_string);
            final Button addBtn = v.findViewById(R.id.add_btn);
            listItemText.setText(list.get(position));

            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //do something
                    if (songListObj.get(position).getStatus() == 0) {


                        //change status of song to favorite
                        addBtn.setBackgroundResource(R.drawable.check);
                        playlist.changeToFavorite(position);

                        Toast.makeText(MainActivity.this, "Added" +
                                " to favorite", Toast.LENGTH_SHORT).show();

                    } else if (songListObj.get(position).getStatus() == 1) {

                        //change status of song to dislike
                        playlist.changeToDislike(position);

                        addBtn.setBackgroundResource(R.drawable.cross);

                        Toast.makeText(MainActivity.this, "Added" +
                                " to dislike", Toast.LENGTH_SHORT).show();

                        if (flashOn && playlist.getSortingList().get(currentIndex)
                                .equals(songList.get(position)))
                            next(playlist.getSortingList());
                        else if (!flashOn && currentIndex == position) //TODO: FIXED BUG
                            next(songList);
                    } else {
                        Toast.makeText(MainActivity.this, "Back" +
                                " to netral", Toast.LENGTH_SHORT).show();
                        songListObj.get(position).setStatus(0);
                        Location location = currentUserlocation;
                        int day = currentUserDayOfWeek;
                        int time = currentUserMNEIndex;

                        //change status of song to neutral
                        playlist.changeToNeutral(position,location,day, time);
                        addBtn.setBackgroundResource(R.drawable.add);
                    }
                }
            });

            return v;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        downloadInput = (EditText) findViewById(R.id.downloadTextField);

        //set filter to only when download is complete and register broadcast receiver
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, filter);

        //initialize all buttons
        createButton();

        //setup media player
        mediaPlayer = new MediaPlayer();

        /** This play the current all the current album songs
         *  PRECONDITION: album has at least 1 song
         */
        player = new MusicPlayer(mediaPlayer, this, musicLocator, albumToPlay,
                playButton);

        //set up location manager, ask user for permission
        detectTimeChanges();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(MainActivity.this,
                                Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            100);
                    return;
                } else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 305, MainActivity.this);
                }
            }
        });

        musicLocator = new MusicLocator(locationManager, MainActivity.this);

        //initialize location of user
        currentUserlocation = musicLocator.getCurrentLocation();


        //set up flashback switch listener
        flashback = findViewById(R.id.switch1);
        flashback.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    //update flags
                    flashOn = true;
                    playingAlbumFlag = false;

//                    nextSong.setClickable(false);
//                    previousSong.setClickable(false);
                    listView.setEnabled(false);
//                    playButton.setClickable(false);
                    player.changeToPauseButton();

                    Location location = currentUserlocation;
                    int time = currentUserMNEIndex;

                    Log.d("(flashback) time",Integer.toString(time));
                    Log.d("flashback: ", "toggled flashback mode on");

                    int day = currentUserDayOfWeek;

                    //get score to list out song to be played
                    score.score(location, day, time);
                    playlist.sorter();
                    sortingList = playlist.getSortingList();

                    //play the song according to the score order
                    playTracksOrder();

                    Toast.makeText(MainActivity.this,
                            "Flashback mode on", Toast.LENGTH_SHORT).show();
                }
                else {
                    flashOn = false;

                    nextSong.setClickable(true);
                    previousSong.setClickable(true);
                    playButton.setClickable(true);
                    listView.setEnabled(true);

                    player.getMediaPlayer().stop();

                    player.changeToPlayButton();


                    Toast.makeText(MainActivity.this,
                            "Flashback mode off", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //Play or Pause, Previous, Next button

        player.changeToPlayButton();

        //initialize the views
        initializeView();

        // Make list
        listView = findViewById(R.id.songList);

        songList = new ArrayList<>();
        songTitleList = new ArrayList<>();
        songListObj = new ArrayList<>();
        albumList = new Hashtable<>();
        indexTosong = new Hashtable<>();

        //GET LIST OF SONGS FROM RAW DIRECTORY
//        getMusic(songList, songTitleList);
        LocalMusicParser musicParser = new LocalMusicParser(this, mmr, songListObj);

        musicParser.getMusic(songList, songTitleList);
        //populate album after we get music
        musicParser.populateAlbum(songListObj, albumList);


        //initialize flashback mode data
        sortingList = new ArrayList<>(songList);
        for(int i=0;i<songList.size();i++){
            indexTosong.put(songList.get(i),i);
        }
        score = new ScoreVibe(songList,songListObj);
        playlist = new PlaylistVibe(sortingList, (ArrayList<Song>) songListObj, indexTosong);


        /*RESTORING PREVIOUS STATE*/

        //NOTE: BEFORE RESTORING STATE, ADD SONGS IN THE DIRECTORY FIRST
        SharedPreferences sp = getSharedPreferences("lastState", MODE_PRIVATE);
     //   flashOn = sp.getBoolean("flashbackFlag", false);
       // currentindex = sp.getInt("lastPlayedIndex", -1);
   /*     if(getSongListObj()!=null)
            songListObj = getSongListObj();
        if(currentindex != -1) {   //stored before
            //TODO: need to store Date and Location of each song
            //TODO: need to store data on list views
            if (flashOn) {
                flashback.setChecked(true);
                playingAlbumFlag = false;
                nextSong.setClickable(false);
                previousSong.setClickable(false);
                listView.setEnabled(false);
                changeToPauseButton();

                Location location = currentUserlocation;
                int time = currentUserMNEIndex;

                Log.d("ti", Integer.toString(time));

                int day = currentUserDayOfWeek;

                score.score(location, day, time);
                playlistFlashback.sorter();

                sortingList = playlistFlashback.sortingList;
                playTracksOrder();
            } else {
                setNowPlayingView(songTitleList.get(currentindex));
                showDateAndTime(songListObj.get(currentindex));
                showCurrentLocation(songListObj.get(currentindex));
            }
        }

*/
        //TODO: need to show in list views all the data stored

        /*RESTORING PREVIOUS STATE*/

        adapter = new MyAdapter((ArrayList<String>) songTitleList, this);

        //handle listview and assign adapter
        listView = findViewById(R.id.songList);
        listView.setAdapter(adapter);


        //set up listener for list of tracks list view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Log.d("listView: ", "a song is clicked");

                // Disable album queue
                playingAlbumFlag = false;
                currentIndex = i;

                if (!flashOn) {
                    Log.d("listView: ", "playing a song pressed by user");

                    if(!songListObj.get(i).isDownload())
                    {
                        int resID = getResources().getIdentifier(songList.get(i),
                                "raw", getPackageName());
                        player.playMusicId(resID);

                    }
                    else
                    {
                        Uri path = songListObj.get(i).getSongUri();
                        player.playMusicUri(path);
                    }

                    //set the current playing song in the text view
                    setNowPlayingView(songTitleList.get(i));
                    showCurrentLocation(songListObj.get(i));
                    showDateAndTime(songListObj.get(i));
                    storeDateAndTime(songListObj.get(i));

                    try {
                        musicLocator.storeLocation(songListObj.get(i));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }



                } else {

                    Log.d("listView: ", "user pressed a track in flashback mode");
                    //TODO: add pop up message where the user need to press ok
                    Toast.makeText(MainActivity.this, "You are in flashback mode!\n" +
                            "Please go to normal mode to select music", Toast.LENGTH_SHORT).show();
                }


            }
        });


        //set listener for next song button
        nextSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flashOn == false) {
                    if(currentIndex == songList.size()-1){
                        currentIndex = songList.size()-1;
                        Toast lastSong = Toast.makeText(getApplicationContext(),
                                "No next song available", Toast.LENGTH_SHORT);

                        lastSong.show();
                        return;
                    }
                    next(songList);
                }
            }
        });


        //set listener for previous song button
        previousSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flashOn == false) {

                    if(playingAlbumFlag)
                    {
                        Toast.makeText(getApplicationContext(),
                                "No previous song available." +
                                        " You are playing an album", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        if(currentIndex == 0){
                            Toast firstSong = Toast.makeText(getApplicationContext(),
                                    "No previous song available", Toast.LENGTH_LONG);
                            firstSong.show();
                            return;
                        }
                        previous(songList);
                    }
                }
            }
        });

        //show list of albums
        tempListAlbum = new ArrayList<>(albumList.values());
        albumAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_expandable_list_item_1, tempListAlbum);
        albumView.setAdapter(albumAdapter);


        //set on item click listener for album list
        albumView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("album:", "album is clicked!");
                // Enable album queue
                playingAlbumFlag = true;

                if (!flashOn) {
                    Log.d("album:", "playing the whole album");

                    player.releaseMusicPlayer();

                    //get album to play
                    albumToPlay = tempListAlbum.get(i);

                    //prepare album to be played
                    albumToPlay.setupAlbum();

                    player.setAlbumToPlay(albumToPlay);
                    player.setMusicLocator(musicLocator);

                    ArrayList<Song> albumTracks = albumToPlay.getListOfTracks();
                    for(int j = 0; j < albumTracks.size(); j++)
                    {
                        Log.d("album track list:", albumTracks.get(j).getTitle());
                    }
                    //play the whole album
                    player.playAlbum();

                    //after we are done playing the album
                    playingAlbumFlag = false;


                } else {
                    Log.d("album:", "user pressed album in flashback mode");

                    Toast.makeText(MainActivity.this, "You are in flashback mode!\n" +
                            "Please go to normal mode to select music", Toast.LENGTH_SHORT).show();
                }
            }

        });

        //set listener for the play button
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.playingAndPausing();
            }
        });

        // Set listener for download button
        clickDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("download button: ", "clicked!");
                if (downloadInput.getText().toString() == null) {
                    Toast.makeText(MainActivity.this, "Download Link not found",
                            Toast.LENGTH_LONG).show();
                }
                else
                {
                    // Get the text from the input and parse it as Uri
                    String getDownloadLink = downloadInput.getText().toString();
                    Uri song_uri = Uri.parse(getDownloadLink);

//                    String[] permit = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
//                    ActivityCompat.requestPermissions(MainActivity.this, permit, 2);
//                    permit = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
//                    ActivityCompat.requestPermissions(MainActivity.this, permit, 3);
                    Music_DownloadId = downloadData(song_uri);

                }
            }
        });


        // Set listener for show current playlist button
        showCurrentPlaylist.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Log.d("show current list:", "clicked!");
               CharSequence trackList[];
               final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

               //check if it is in vibe mode
               if(flashOn){ //TODO: need to change to vibe boolean

                   //get the neutral and favorited song
                   ArrayList<String> listSong = new ArrayList<>();
                   for(int i = 0; i < sortingList.size(); i++)
                   {
                       Song currSong = songListObj.get(
                               indexTosong.get(playlist.getSortingList().get(i)));
                       if (currSong.getStatus() != -1) {
                           String display = currSong.getTitle() + " - " + currSong.getArtist();
                           listSong.add(display);
                       }
                   }

                   //display the song that is going to be played
                   trackList = new CharSequence[listSong.size()];
                   for(int i = 0; i < listSong.size(); i++)
                   {
                       // Put song to playlist if not disliked
                       trackList[i] = listSong.get(i);
                   }
                   builder.setTitle("Vibe Mode Queue");

               }
               else if(playingAlbumFlag) {
                   trackList = new CharSequence[albumToPlay.getListOfTracks().size()];
                   ArrayList<Song> albumTracks = albumToPlay.getListOfTracks();
                   for (int i = 0; i < albumTracks.size(); i++) {
                       String display = albumTracks.get(i).getTitle() +
                               " - " + albumTracks.get(i).getArtist();
                       trackList[i] = display;
                   }
                   builder.setTitle(albumToPlay.getName());
               }
               else
               {
                   trackList = new CharSequence[]{ "Empty" };
                   builder.setTitle("Normal Mode - No Track List Displayed");
               }



               builder.setItems(trackList, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {

                   }
               });
               builder.show();


           }
        });
    }

    private void createButton() {
        playButton = findViewById(R.id.playButton);
        nextSong = findViewById(R.id.next);
        previousSong = findViewById(R.id.previous);
        showCurrentPlaylist = findViewById(R.id.currentPlaylist);
        clickDownload = findViewById(R.id.downloadButton);
    }

    private void initializeView() {
        nowPlayingView = findViewById(R.id.nowPlaying);
        albumView = findViewById(R.id.albumList);
        dateTimeView = findViewById(R.id.playingTime);
        locationView = findViewById(R.id.playingLoc);
    }


    /** This makes the back button into home button
     *  so that the music player is not closed
     */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    /**
     * this is the originaly implemented method from android studio
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * this is the originaly implemented method from android studio
     * @param item
     * @return true or super.onOptionsItemSelected(item)
     */
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

    /** This saves the last state when the music player
     *  app is closed
     */
    @Override
    public void onStop()
    {
        super.onStop();

        //save the last state using sharedPreference
    /*    SharedPreferences sp = getSharedPreferences("lastState", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("flashbackFlag", flashOn);
        editor.putInt("lastPlayedIndex", currentindex);
        editor.apply();
        storeSongs();
        //TODO: put more data such as resId, durationLastPlayed
        */

    }


    /** This will set the text view to show the current playing song
     *  @param nowPlayingString the current playing song title - artist
     */
    public void setNowPlayingView(String nowPlayingString)
    {
        //hover the now playing text view
        String repeat = new String(new char[1]).replace("\0", " ");
        nowPlayingView.setText(repeat + nowPlayingString + repeat);
        nowPlayingView.setSelected(true);
    }

    /**
     * This method is a listener when the location change
     * it will play new song in flashback mode
     * @param location
     */
    public void onLocationChanged(Location location) {
        if(flashOn) {
            if(currentUserlocation.distanceTo(location)>305) {
                currentUserlocation = location;
                score.score(location, currentUserDayOfWeek, currentUserMNEIndex);
                playlist.sorter();
                playTracksOrder();
            }
        }
    }


    /**
     * This method play the music in order for flashback mode
     */
    public void playTracksOrder(){

        //make sure that we are in vibe mode
        playingAlbumFlag = false;

        player.stop();
        currentIndex = 0;

        //set the now playing text view, location, and time
        Song curPlaying = songListObj.get(indexTosong.get(playlist.getSortingList().get(currentIndex)));
        String display = curPlaying.getTitle() + " - " + curPlaying.getArtist();

        setNowPlayingView(display);
        showCurrentLocation(curPlaying);
        showDateAndTime(curPlaying);

        if(curPlaying.isDownload())
        {
            player.playMusicUri(curPlaying.getSongUri());
        }
        else
        {
            int resID = getResources().getIdentifier(playlist.getSortingList().get(currentIndex),
                    "raw", getPackageName());

            player.playMusicId(resID);
        }

        player.changeToPauseButton();

        player.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                next(playlist.getSortingList());
            }
        });

    }

    /**
     * Method to play the next song
     * @param playlist the list of songs in the playlistFlashback
     */
    public void next(final List<String> playlist){
        if(playingAlbumFlag)
        {
            Toast.makeText(MainActivity.this, "No next song available. " +
                    "You are playing an album", Toast.LENGTH_SHORT).show();
        }
        else
        {
            currentIndex++;
            if (currentIndex < playlist.size())
            {


                if (songListObj.get(indexTosong.
                        get(playlist.get(currentIndex))).getStatus() != -1) {


                    if(player.getMediaPlayer().isPlaying()){
                        player.getMediaPlayer().stop();
                        player.releaseMusicPlayer();
                    }



                    //set the now playing text view
                    if(!flashOn) {
                        setNowPlayingView(songTitleList.get(currentIndex));
                        showCurrentLocation(songListObj.get(currentIndex));
                        showDateAndTime(songListObj.get(currentIndex));
                        storeDateAndTime(songListObj.get(currentIndex));
                        try {
                            musicLocator.storeLocation(songListObj.get(currentIndex));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        Song curPlaying = songListObj.get(indexTosong.get(
                                this.playlist.getSortingList().get(currentIndex)));
                        String display = curPlaying.getTitle() + " - " + curPlaying.getArtist();

                        setNowPlayingView(display);
                        showCurrentLocation(curPlaying);
                        showDateAndTime(curPlaying);

                    }


                    if(songListObj.get(indexTosong.get(
                            this.playlist.getSortingList().get(currentIndex))).isDownload())
                    {
                        player.playMusicUri(songListObj.get(indexTosong.get(
                                this.playlist.getSortingList().get(currentIndex))).getSongUri());
                    }
                    else
                    {
                        int resID = getResources().getIdentifier(playlist.get(currentIndex),
                                "raw", getPackageName());
                        player.playMusicId(resID);
                    }

                    player.changeToPauseButton();

                    player.getMediaPlayer().
                            setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            next(playlist);
                        }
                    });

                }

                else
                {
                    next(playlist);
                }

            }
            else
            {
                currentIndex = playlist.size() - 1;
                if (flashOn)
                {
                    currentIndex = 0;
                    next(playlist);
                }
                else
                {
                    currentIndex = playlist.size()-1;
                    Toast.makeText(MainActivity.this, "No next song available"
                            , Toast.LENGTH_SHORT).show();
                }
            }

        }

    }

    /**
     * Method to play the previous song
     * @param playlist the list of songs in the playlistFlashback
     */
    public void previous(final List<String> playlist) {
        if(playingAlbumFlag)
        {
            Toast.makeText(MainActivity.this, "No previous song available." +
                    "You are playing an album", Toast.LENGTH_SHORT).show();
        }
        else
        {
            currentIndex--;
            if (currentIndex >= 0)
            {

                if(songListObj.get(indexTosong.
                        get(playlist.get(currentIndex))).getStatus() != -1)
                {
                    player.stop();

                    //update title, location, time view
                    setNowPlayingView(songTitleList.get(currentIndex));
                    showCurrentLocation(songListObj.get(currentIndex));
                    showDateAndTime(songListObj.get(currentIndex));
                    storeDateAndTime(songListObj.get(currentIndex));
                    try {
                        musicLocator.storeLocation(songListObj.get(currentIndex));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(songListObj.get(indexTosong.
                            get(playlist.get(currentIndex))).isDownload())
                    {
                        player.playMusicUri(songListObj.get(indexTosong.
                                get(playlist.get(currentIndex))).getSongUri());
                    }
                    else
                    {
                        int resID = getResources().getIdentifier(playlist.get(currentIndex),
                                "raw", getPackageName());

                        player.playMusicId(resID);
                    }

                    player.changeToPauseButton();

                    player.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            next(playlist);
                        }
                    });
                }
                else
                {
                    previous(playlist);
                }

            }
            else
            {
                currentIndex = 0;
                Toast.makeText(MainActivity.this, "No previous song available",
                        Toast.LENGTH_SHORT).show();
            }

        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {}

    @Override
    public void onProviderDisabled(String s) {}

    /**
     * Method to show date and time text view
     * @param song
     */
    public void showDateAndTime(Song song){
        if(song.getMostRecentDateTime() != null){
            dateTimeView.setText(song.getMostRecentDateTimeString());
        }
        else{
            dateTimeView.setText("No Last Current Time and Date are available");
        }

    }

    /**
     * Method to show location text view
     * @param song
     */
    public void showCurrentLocation(Song song){
        if(song.getMostRecentLocation() != null){

            locationView.setText(song.getMostRecentLocationString());
        }
        else{
            locationView.setText("No Last Current location is available");
        }

    }

    /**
     * Method to store current date and time in the song
     * @param song
     */
    public void storeDateAndTime(Song song){
        SimpleDateFormat dateFormatter = new SimpleDateFormat("kk:mm:ss   dd MMM yyyy");
        String dateAndTime = dateFormatter.format(new Date());
        song.setMostRecentDateTime(new Date());
        song.setMostRecentDateTimeString(dateAndTime);
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        hour = convertToMNEIndex(hour);

        //store time history
        if(!song.getTimeHistory().contains(hour)){
            song.addTimeHistory(hour);
        }



        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if(!song.getDayHistory().contains(day)){
            song.addDayHistory(day);
        }

        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        if(!song.getDayOfMonthHistory().contains(day)){
            song.addDayOfMonthHistory(dayOfMonth);
        }

    }


    /**
     *
     * @param hour: the time of day
     * @return return hour which indicates if morning then hour = 1, afternoon then hour = 2
     *                                        evening then hour = 3
     */
    public int convertToMNEIndex(int hour){
        //hour = 1 if it's a morning time between 4am and 12pm
        if(4<=hour && hour < 12 ){
            hour = 1;
        }
        //hour=2 if it's an afternoon time between 12pm and 8pm
        else if(12<=hour && hour < 20 ){
            hour = 2;
        }
        //otherwise hour = 3
        else{
            hour = 3;
        }
        return hour;
    }

    /**
     * get the hour and the day of week
     */
    public void setCurrentUserMNEAndDay(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        currentUserDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        currentUserMNEIndex = convertToMNEIndex(hour);

    }

    /**
     * Detect the changes every hour for flashback mode.
     */
    public void detectTimeChanges(){
        setCurrentUserMNEAndDay();
        Calendar calendar = Calendar.getInstance();
        int min = calendar.get(Calendar.MINUTE);
        //time it needs to delay. Ex: if the music is played at 12:05pm  then the time will
        //be delayed and time will be detected at 1pm
        int timeDelay = 1000*60*(60 - min);
        Timer timerDelay = new Timer();
        timerDelay.schedule(new TimerTask() {
            public void run() {
                Timer hourlyTime = new Timer ();
                TimerTask hourlyTask = new TimerTask () {
                    @Override
                    public void run () {
                        setCurrentUserMNEAndDay();
                        score.score(currentUserlocation,currentUserDayOfWeek,currentUserMNEIndex);
                        playlist.sorter();
                        playTracksOrder();
                    }
                };
                hourlyTime.schedule (hourlyTask, 0l, 1000*60*60);

            }

        }, timeDelay);
    }

    /**
     * Source: https://www.androidtutorialpoint.com/networking/android-
     * download-manager-tutorial-download-file-using-download-manager-internet/
     *
     * Download the the file from the given URI
     * @param uri
     * @return downloadReference
     */
    public long downloadData(Uri uri) {

        currDownloadUri = uri;

        File f = new File("" + uri);
        downloadTitle = f.getName();
        Log.d("downloadTitle: ", downloadTitle);

        // Create request for android download manager
        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        Log.d("filename", f.getName());
//        Log.d("destination download:", Environment.DIRECTORY_DOWNLOADS);

        String path = Environment.DIRECTORY_MUSIC;
        Log.d("storing in path:", path);

        // Set destination
        request.setDestinationInExternalFilesDir(MainActivity.this,
                Environment.DIRECTORY_MUSIC, downloadTitle);


//        request.setDestinationUri(Uri.parse(Environment.DIRECTORY_DOWNLOADS));

        Toast.makeText(MainActivity.this, "Downloading Music",
                Toast.LENGTH_LONG).show();

        downloadReference = downloadManager.enqueue(request);

        return downloadReference;
    }

    /**
     * Source: https://www.androidtutorialpoint.com/networking/android-
     * download-manager-tutorial-download-file-using-download-manager-internet/
     *
     * Generates a pop-up when download is complete
     */
    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //check if the broadcast message is for our Enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if(referenceId == Music_DownloadId) {

                Log.d("Music download done", "");
                Toast.makeText(MainActivity.this,
                        "Music Download Complete", Toast.LENGTH_LONG).show();

                Log.d("Directory: ", Environment.DIRECTORY_MUSIC);

                //get external file directory
                File dir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);

                //get the list of files inside music directory
                File[] target = dir.listFiles();
//                for(int i = 0; i < target.length; i++)
//                {
//                    Log.d("file", target[i].getName());
//                }

                //get the uri of the file we just downloaded
                Uri filePath = Uri.parse(target[target.length-1].getAbsolutePath());


                mmr = new MediaMetadataRetriever();

                mmr.setDataSource(MainActivity.this, filePath);

//                //add list of song objects
                String title = mmr.extractMetadata(SONG_TITLE);
                String artist = mmr.extractMetadata(SONG_ARTIST);
                String album = mmr.extractMetadata(SONG_ALBUM);
                String duration = mmr.extractMetadata(SONG_DURATION);

                if (title == null) {
                    title = "Unknown Title";
                }
                if(artist == null)
                    artist = "Unknown Artist";
                if(album == null)
                    album = "Unknown Album";
                Log.d("title:", title);
                Log.d("artist:", artist);
                Log.d("album:", album);

                long durationToLong = Long.parseLong(duration);
                Log.d("duration:", duration);

//                Song downloadedSong = new Song(title, artist, album, durationToLong, )

                songListObj.add(new Song(title, artist, album,
                        durationToLong, filePath));

                String display = title + " - " + artist;
                songTitleList.add(display);
                songList.add(target[target.length-1].getName());

                //notify changes

                indexTosong.put(songList.get(songList.size()-1),songList.size()-1);
                sortingList.add(target[target.length-1].getName());

                score = new ScoreVibe(songList,songListObj);
                playlist = new PlaylistVibe(sortingList, (ArrayList<Song>) songListObj, indexTosong);
                adapter.notifyDataSetChanged();

//                player.playMusicUri(filePath);
//                //handle listview and assign adapter
//                listView = findViewById(R.id.songList);
//                listView.setAdapter(adapter);

                // Update album
                Log.d("Populating album: ", album);

                LocalMusicParser musicParser = new LocalMusicParser(
                        MainActivity.this, mmr, songListObj);

                musicParser.populateAlbum(songListObj, albumList);

                //show list of albums
                tempListAlbum = new ArrayList<>(albumList.values());
                albumAdapter = new ArrayAdapter<>(MainActivity.this,
                        android.R.layout.simple_expandable_list_item_1, tempListAlbum);
                albumView.setAdapter(albumAdapter);

//                if (!albumList.containsKey(album)) {
//                    //create album
//                    Album newAlbum = new Album(album);
//                    //add current track
//                    newAlbum.addTrack(songListObj.get(songListObj.size()-1));
//                    albumList.put(album, newAlbum);
//                    Log.d("new album:", newAlbum.toString() + " with song "
//                            + songListObj.get(songListObj.size()-1).getTitle());
//                }
//                else {
//                    albumList.get(album).addTrack(songListObj.get(songListObj.size()-1));
//                    Log.d("existing albumPopulate:", album + " with song " +
//                            songListObj.get(songListObj.size()-1).getTitle());
//                }


            }

        }
    };

  /* public boolean storeSongs() {
       SharedPreferences sharedPreferences = getSharedPreferences("lastState",MODE_PRIVATE);
       SharedPreferences.Editor editor = sharedPreferences.edit();
       Gson gson = new Gson();
       String json = gson.toJson(songListObj);
       editor.putString("tracks",json);
       editor.apply();
            //ByteArrayInputStream jis = new ByteArrayInputStream(tbyte2);
            //ObjectInputStream dis = new ObjectInputStream(jis);
            //ArrayList<Song> songs =(ArrayList<Song>)dis.readObject();
       return true;
    }

    public ArrayList<Song> getSongListObj(){
        SharedPreferences sharedPreferences = getSharedPreferences("lastState",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("tracks","");
        Type type = new TypeToken<List<Song>>(){}.getType();
        ArrayList<Song> songs = gson.fromJson(json,type);
        return songs;
   }*/

}