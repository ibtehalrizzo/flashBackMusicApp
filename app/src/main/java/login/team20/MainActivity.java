package login.team20;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleBrowserClientRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Person;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

//import login.team20.R;


public class MainActivity extends AppCompatActivity implements LocationListener {
    private final int SONG_TITLE = MediaMetadataRetriever.METADATA_KEY_TITLE;
    private final int SONG_ARTIST = MediaMetadataRetriever.METADATA_KEY_ARTIST;
    private final int SONG_ALBUM = MediaMetadataRetriever.METADATA_KEY_ALBUM;
    private final int SONG_DURATION = MediaMetadataRetriever.METADATA_KEY_DURATION;

    //initialize current index playing song
    private int currentindex = 0;


    // List of songs
    private ListView listView;
    private ListAdapter listAdapter;


    private MediaPlayer mediaPlayer;


    private FloatingActionButton playButton;
    private Switch flashback;
    private FloatingActionButton nextSong;
    private FloatingActionButton previousSong;
    private Button addBtn;
    User currentUser;

    private Score score;

    //playlist of the song to be played
    private PlayList_flashback playList_flashback;


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

    //data for playlist of song
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
    private GoogleSignInClient mGoogleSignInClient;
    public static GoogleSignInAccount account;
    public static String emails;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference VMode = database.getReference();
    DatabaseReference VMSongs = database.getReference().child("Songs");
    DatabaseReference VMUsers = database.getReference().child("Users");
    private HashMap<String,User> userListFireBase;
    private HashMap<String,Song> songListFireBase;

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
         *
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
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.listview, null);
            }
            //Handle TextView and display string from your list
            final TextView listItemText = (TextView) v.findViewById(R.id.list_item_string);
            final Button addBtn = (Button) v.findViewById(R.id.add_btn);
            listItemText.setText(list.get(position));
       /*     if(songListObj!=null) {
                if (songListObj.get(position).getStatus() == 0) {
                    addBtn.setBackgroundResource(R.drawable.add);
                } else if (songListObj.get(position).getStatus() == 1) {
                    addBtn.setBackgroundResource(R.drawable.check);
                } else
                    addBtn.setBackgroundResource(R.drawable.cross);
            }
*/

            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //do something
                    if (songListObj.get(position).getStatus() == 0) {


                        //change status of song to favorite
                        addBtn.setBackgroundResource(R.drawable.check);
                        playList_flashback.changeToFavorite(position);

                        Toast.makeText(MainActivity.this, "Added" +
                                " to favorite", Toast.LENGTH_SHORT).show();

                    } else if (songListObj.get(position).getStatus() == 1) {

                        //change status of song to dislike
                        playList_flashback.changeToDislike(position);

                        addBtn.setBackgroundResource(R.drawable.cross);

                        Toast.makeText(MainActivity.this, "Added" +
                                " to dislike", Toast.LENGTH_SHORT).show();

                        if (flashOn && playList_flashback.sortingList.get(currentindex).equals(songList.get(position)))
                            next(playList_flashback.sortingList);
                        else if (!flashOn && currentindex == position && mediaPlayer.isPlaying())
                            next(songList);
                    } else {
                        Toast.makeText(MainActivity.this, "Back" +
                                " to netral", Toast.LENGTH_SHORT).show();
                        songListObj.get(position).setStatus(0);
                        Location location = currentUserlocation;
                        int day = currentUserDayOfWeek;
                        int time = currentUserMNEIndex;

                        //change status of song to neutral
                        playList_flashback.changeToNeutral(position, location, day, time);
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        account = GoogleSignIn.getLastSignedInAccount(this);
        //Log.w("accont",account.getEmail());
        updateUI(account);
        //setup media player
        mediaPlayer = new MediaPlayer();
        detectTimeChanges();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            100);
                    return;
                } else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 305, (LocationListener) MainActivity.this);
                }
            }
        });


        //initialize location of user
        currentUserlocation = getCurrentLocation();

        //set up flashback switch listener
        flashback = (Switch) findViewById(R.id.switch1);
        flashback.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    //update flags
                    flashOn = true;
                    playingAlbumFlag = false;

                    nextSong.setClickable(false);
                    previousSong.setClickable(false);
                    listView.setEnabled(false);
                    playButton.setClickable(false);
                    changeToPauseButton();

                    Location location = currentUserlocation;
                    int time = currentUserMNEIndex;

                    Log.d("(flashback) time", Integer.toString(time));
                    Log.d("flashback: ", "toggled flashback mode on");

                    int day = currentUserDayOfWeek;

                    //get score to list out song to be played
                    score.score(location, day, time);
                    playList_flashback.sorter();
                    sortingList = playList_flashback.sortingList;

                    //play the song according to the score order
                    playTracksOrder();

                    Toast.makeText(MainActivity.this, "Flashback mode on", Toast.LENGTH_SHORT).show();
                } else {
                    flashOn = false;

                    nextSong.setClickable(true);
                    previousSong.setClickable(true);
                    playButton.setClickable(true);
                    listView.setEnabled(true);

                    mediaPlayer.stop();
                    changeToPlayButton();

                    Toast.makeText(MainActivity.this, "Flashback mode off", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //Play or Pause, Previous, Next button
        playButton = (FloatingActionButton) findViewById(R.id.playButton);
        nextSong = findViewById(R.id.next);
        previousSong = findViewById(R.id.previous);
        changeToPlayButton();

        //initialize the views
        nowPlayingView = (TextView) findViewById(R.id.nowPlaying);
        albumView = (GridView) findViewById(R.id.albumList);
        dateTimeView = (TextView) findViewById(R.id.playingTime);
        locationView = (TextView) findViewById(R.id.playingLoc);

        // Make list
        listView = (ListView) findViewById(R.id.songList);

        songList = new ArrayList<>();
        songTitleList = new ArrayList<>();
        songListObj = new ArrayList<>();
        albumList = new Hashtable<>();
        indexTosong = new Hashtable<>();

        //GET LIST OF SONGS FROM RAW DIRECTORY
        getMusic(songList, songTitleList);

        //initialize flashback mode data
        sortingList = new ArrayList<>(songList);
        for (int i = 0; i < songList.size(); i++) {
            indexTosong.put(songList.get(i), i);
        }
        score = new Score(songList, songListObj);
        playList_flashback = new PlayList_flashback(sortingList, (ArrayList<Song>) songListObj, indexTosong);


        /*RESTORING PREVIOUS STATE*/

        //NOTE: BEFORE RESTORING STATE, ADD SONGS IN THE DIRECTORY FIRST
        //SharedPreferences sp = getSharedPreferences("lastState", MODE_PRIVATE);
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
                playList_flashback.sorter();

                sortingList = playList_flashback.sortingList;
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


        //populate album after we get music
        populateAlbum(songListObj, albumList);
        MyAdapter adapter = new MyAdapter((ArrayList<String>) songTitleList, this);

        //handle listview and assign adapter
        listView = (ListView) findViewById(R.id.songList);
        listView.setAdapter(adapter);


        //set up listener for list of tracks list view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Log.d("listView: ", "a song is clicked");

                // Disable album queue
                playingAlbumFlag = false;
                currentindex = i;

                if (!flashOn) {
                    Log.d("listView: ", "playing a song pressed by user");
                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }


                    int resID = getResources().getIdentifier(songList.get(i), "raw", getPackageName());
                    mediaPlayer = MediaPlayer.create(MainActivity.this, resID);
                    mediaPlayer.start();

                    //after music start, change the button into pause button
                    changeToPauseButton();

                    //set the current playing song in the text view
                    setNowPlayingView(songTitleList.get(i));
                    showCurrentLocation(songListObj.get(i));
                    showDateAndTime(songListObj.get(i));
                    storeDateAndTime(songListObj.get(i));

                    try {
                        storeLocation(songListObj.get(i));
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
                    if (currentindex == songList.size() - 1) {
                        currentindex = songList.size() - 1;
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

                    if (playingAlbumFlag) {
                        Toast.makeText(getApplicationContext(),
                                "No previous song available." +
                                        " You are playing an album", Toast.LENGTH_LONG).show();
                    } else {
                        if (currentindex == 0) {
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
        albumAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, tempListAlbum);
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

                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }

                    //get album to play
                    albumToPlay = tempListAlbum.get(i);

                    //prepare album to be played
                    setupAlbum();

                    //play the whole album
                    playAlbum();


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
                //Replace with play the current music played
                //or just change to pause button if there is no music
                //selected
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        changeToPlayButton();

                    } else {
                        mediaPlayer.start();
                        nowPlayingView.setSelected(true);
                        changeToPauseButton();

                    }
                } else {
                    //when there is no music is selected, change play and pause accordingly
                    if (playButton.getTag().equals(R.drawable.ic_play_arrow_black_24dp)) {
                        changeToPauseButton();
                    } else {
                        changeToPlayButton();
                    }
                }

            }
        });
    }


    /**
     * This parse all the music from raw directory
     * and store the songs in all the list inputs
     *
     * @param songList      the list of original file name of song
     * @param songTitleList the list of title-artist of song to be displayed
     */
    public void getMusic(List songList, List songTitleList) {
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

            if (artist == null)
                artist = "Unknown Artist";
            if (album == null)
                album = "Unknown Album";

            long durationToLong = Long.parseLong(duration);

            Song s = new Song(title, artist, album, durationToLong, resId);
            songListObj.add(s);

            //add to list for display TODO: we can get data from song object instead
            String display = title + " - " + artist;
            songTitleList.add(display);

        }


    }


    /**
     * This populate the album list of MainActivity
     * PRECONDITION: tracks is already populated
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

    /**
     * This makes the back button into home button
     * so that the music player is not closed
     */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    /**
     * this is the originaly implemented method from android studio
     *
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
     *
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

    /**
     * This play the current all the current album songs
     * PRECONDITION: album has at least 1 song
     */
    public void playAlbum() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }


        Song songToPlay = albumToPlay.getNextSongToPlay();
        int resID = songToPlay.getSongResId();

        mediaPlayer = MediaPlayer.create(MainActivity.this, resID);
        mediaPlayer.start();

        Log.d("album play", "playing next song in the album");

        //set button tag to pause
        changeToPauseButton();

        //set the current playing song view, location, date and time
        String currentPlayingSongDisplay = songToPlay.getTitle() + " - " + songToPlay.getArtist();
        setNowPlayingView(currentPlayingSongDisplay);
        showCurrentLocation(songToPlay);
        showDateAndTime(songToPlay);

        //update the history of playing date and time of song
        storeDateAndTime(songToPlay);
        try {
            storeLocation(songToPlay);
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
                    else {
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


    /**
     * This saves the last state when the music player
     * app is closed
     */
    @Override
    public void onStop() {
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

    /**
     * This will prepare the album to be played by
     * queueing all song
     */
    public void setupAlbum() {
        albumToPlay.clearQueue();
        //queue all songs to play in album
        albumToPlay.queueAllSong();
    }


    /**
     * This will set the text view to show the current playing song
     *
     * @param nowPlayingString the current playing song title - artist
     */
    public void setNowPlayingView(String nowPlayingString) {
        //hover the now playing text view
        String repeat = new String(new char[1]).replace("\0", " ");
        nowPlayingView.setText(repeat + nowPlayingString + repeat);
        nowPlayingView.setSelected(true);
    }

    /**
     * This changes the pause button into play button
     */
    public void changeToPlayButton() {
        playButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        playButton.setTag(R.drawable.ic_play_arrow_black_24dp);
    }

    /**
     * This changes the play button into pause button
     */
    public void changeToPauseButton() {
        playButton.setImageResource(R.drawable.ic_pause_black_24dp);
        playButton.setTag(R.drawable.ic_pause_black_24dp);
    }


    /**
     * This method is a listener when the location change
     * it will play new song in flashback mode
     *
     * @param location
     */
    public void onLocationChanged(Location location) {
        if (flashOn) {
            if (currentUserlocation.distanceTo(location) > 305) {
                currentUserlocation = location;
                score.score(location, currentUserDayOfWeek, currentUserMNEIndex);
                playList_flashback.sorter();
                playTracksOrder();
            }
        }
    }


    /**
     * This method play the music in order for flashback mode
     */
    public void playTracksOrder() {

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        currentindex = 0;

        //set the now playing text view, location, and time
        Song curPlaying = songListObj.get(indexTosong.get(playList_flashback.sortingList.get(currentindex)));
        String display = curPlaying.getTitle() + " - " + curPlaying.getArtist();

        setNowPlayingView(display);
        showCurrentLocation(curPlaying);
        showDateAndTime(curPlaying);


        int resID = getResources().getIdentifier(playList_flashback.sortingList.get(currentindex), "raw", getPackageName());
        mediaPlayer = MediaPlayer.create(MainActivity.this, resID);
        mediaPlayer.start();
        changeToPauseButton();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                next(playList_flashback.sortingList);
            }
        });

    }


    /**
     * Method to play the next song
     *
     * @param playlist the list of songs in the playlist
     */
    public void next(final List<String> playlist) {
        if (playingAlbumFlag) {
            Toast.makeText(MainActivity.this, "No next song available. " +
                    "You are playing an album", Toast.LENGTH_SHORT).show();
        } else {
            currentindex++;
            if (currentindex < playlist.size()) {


                if (songListObj.get(indexTosong.
                        get(playlist.get(currentindex))).getStatus() != -1) {

                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }


                    //set the now playing text view
                    if (!flashOn) {
                        setNowPlayingView(songTitleList.get(currentindex));
                        showCurrentLocation(songListObj.get(currentindex));
                        showDateAndTime(songListObj.get(currentindex));
                        storeDateAndTime(songListObj.get(currentindex));
                        try {
                            storeLocation(songListObj.get(currentindex));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Song curPlaying = songListObj.get(indexTosong.get(playList_flashback.sortingList.get(currentindex)));
                        String display = curPlaying.getTitle() + " - " + curPlaying.getArtist();

                        setNowPlayingView(display);
                        showCurrentLocation(curPlaying);
                        showDateAndTime(curPlaying);

                    }


                    int resID = getResources().getIdentifier(playlist.get(currentindex),
                            "raw", getPackageName());
                    mediaPlayer = MediaPlayer.create(MainActivity.this, resID);
                    mediaPlayer.start();

                    changeToPauseButton();

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            next(playlist);
                        }
                    });


                } else {
                    next(playlist);
                }

            } else {
                currentindex = playlist.size() - 1;
                if (flashOn) {
                    currentindex = 0;
                    next(playlist);
                } else {
                    currentindex = playlist.size() - 1;
                    Toast.makeText(MainActivity.this, "No next song available"
                            , Toast.LENGTH_SHORT).show();
                }
            }

        }

    }

    /**
     * Method to play the previous song
     *
     * @param playlist the list of songs in the playlist
     */
    public void previous(final List<String> playlist) {
        if (playingAlbumFlag) {
            Toast.makeText(MainActivity.this, "No previous song available." +
                    "You are playing an album", Toast.LENGTH_SHORT).show();
        } else {
            currentindex--;
            if (currentindex >= 0) {

                if (songListObj.get(indexTosong.
                        get(playlist.get(currentindex))).getStatus() != -1) {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }

                    //update title, location, time view
                    setNowPlayingView(songTitleList.get(currentindex));
                    showCurrentLocation(songListObj.get(currentindex));
                    showDateAndTime(songListObj.get(currentindex));
                    storeDateAndTime(songListObj.get(currentindex));
                    try {
                        storeLocation(songListObj.get(currentindex));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    int resID = getResources().getIdentifier(playlist.get(currentindex), "raw", getPackageName());
                    mediaPlayer = MediaPlayer.create(MainActivity.this, resID);
                    mediaPlayer.start();

                    changeToPauseButton();

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            next(playlist);
                        }
                    });
                } else {
                    previous(playlist);
                }

            } else {
                currentindex = 0;
                Toast.makeText(MainActivity.this, "No previous song available",
                        Toast.LENGTH_SHORT).show();
            }

        }

    }


    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }

    /**
     * Method to show date and time text view
     *
     * @param song
     */
    public void showDateAndTime(Song song) {
        if (song.getMostRecentDateTime() != null) {
            dateTimeView.setText(song.getMostRecentDateTimeString());
        } else {
            dateTimeView.setText("No Last Current Time and Date are available");
        }

    }

    /**
     * Method to show location text view
     *
     * @param song
     */
    public void showCurrentLocation(Song song) {
        if (song.getMostRecentLocation() != null) {

            locationView.setText(song.getMostRecentLocationString());
        } else {
            locationView.setText("No Last Current location is available");
        }

    }

    /**
     * Method to store current date and time in the song
     *
     * @param song
     */
    public void storeDateAndTime(Song song) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("kk:mm:ss   dd MMM yyyy");
        String dateAndTime = dateFormatter.format(new Date());
        song.setMostRecentDateTime(new Date());
        song.setMostRecentDateTimeString(dateAndTime);
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        hour = convertToMNEIndex(hour);
        if (!song.getTimeHistory().contains(hour)) {
            song.addTimeHistory(hour);
        }
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (!song.getDayHistory().contains(day)) {
            song.addDayHistory(day);
        }

    }

    /**
     * Get address with given latitude and longitude
     *
     * @param latitude
     * @param longitude
     * @return address string
     * @throws IOException
     */
    public String getAddress(double latitude, double longitude) throws IOException {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String state = addresses.get(0).getAdminArea();
        String finalString = address + ", " + state;
        return finalString;
    }

    /**
     * This method get the current location of the user
     *
     * @return the location of the user
     */
    public Location getCurrentLocation() {
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
            Log.d("test1", "ins");
            return null;
        } else {
            return locationManager.getLastKnownLocation(bestProvider);
        }
    }

    /**
     * store the location to the song input
     *
     * @param song
     * @throws IOException
     */
    public void storeLocation(Song song) throws IOException {
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
            Log.d("test1", "ins");
            return;
        } else {
            Location location = locationManager.getLastKnownLocation(bestProvider);
            if (location != null) {

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();


                String finalString = getAddress(latitude, longitude);
                song.setMostRecentLocationString(finalString);
                song.setMostRecentLocation(location);

                if (!song.getLocationHistory().contains(location)) {
                    Iterator<Location> itr = song.getLocationHistory().iterator();
                    while (itr.hasNext()) {
                        if (itr.next().distanceTo(location) >= 305) {
                            song.addLocationHistory(location);
                        }
                    }
                }
            }
        }
    }


    /**
     * @param hour: the time of day
     * @return return hour which indicates if morning then hour = 1, afternoon then hour = 2
     * evening then hour = 3
     */
    public int convertToMNEIndex(int hour) {
        //hour = 1 if it's a morning time between 4am and 12pm
        if (4 <= hour && hour < 12) {
            hour = 1;
        }
        //hour=2 if it's an afternoon time between 12pm and 8pm
        else if (12 <= hour && hour < 20) {
            hour = 2;
        }
        //otherwise hour = 3
        else {
            hour = 3;
        }
        return hour;
    }

    /**
     * get the hour and the day of week
     */
    public void setCurrentUserMNEAndDay() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        currentUserDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        currentUserMNEIndex = convertToMNEIndex(hour);

    }

    /**
     * Detect the changes every hour for flashback mode.
     */
    public void detectTimeChanges() {
        setCurrentUserMNEAndDay();
        Calendar calendar = Calendar.getInstance();
        int min = calendar.get(Calendar.MINUTE);
        //time it needs to delay. Ex: if the music is played at 12:05pm  then the time will
        //be delayed and time will be detected at 1pm
        int timeDelay = 1000 * 60 * (60 - min);
        Timer timerDelay = new Timer();
        timerDelay.schedule(new TimerTask() {
            public void run() {
                Timer hourlyTime = new Timer();
                TimerTask hourlyTask = new TimerTask() {
                    @Override
                    public void run() {
                        setCurrentUserMNEAndDay();
                        score.score(currentUserlocation, currentUserDayOfWeek, currentUserMNEIndex);
                        playList_flashback.sorter();
                        playTracksOrder();
                    }
                };
                hourlyTime.schedule(hourlyTask, 0l, 1000 * 60 * 60);

            }

        }, timeDelay);
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account == null) {
            Intent intent = new Intent(this, login.class);
            startActivity(intent);
        } else {
            emails = account.getEmail();
            emails = emails.split("@")[0];
            Log.d("emails",emails);
            getUserList();
            //Log.d("name",currentUser.getUserName());
        }
    }

    public void VMPlay(final Song song) {       //add song or update song in database
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference VMode = database.getReference();
        VMSongs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(song.getTitle())) {
                    //downloadsong
                } else {
                    //play thie song
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getUser() {   //get the current user from the firebase
        //Query query = VMode;
        VMUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("name0", emails);
                if (dataSnapshot.hasChild(emails)) {
                    Log.d("exist","exist");
                    currentUser = dataSnapshot.child(emails).getValue(User.class);
                    //Log.d("username",mReadFriends.getUserName());
                    //String getUid = mReadFriends.userID;
                    //List<String> friendSongList = friend.getDownloadedSong();
                    //songList.addAll(friendSongList);
                    //}
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void getUserList() {   //Get all the users from firebase
        //Query query = VMode;
        VMode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iter= dataSnapshot.getChildren().iterator();
                userListFireBase= null;
                while(iter.hasNext()){
                    DataSnapshot snapshot  = iter.next();
                    userListFireBase = (HashMap<String,User>) snapshot.getValue();
                    Log.d("list",String.valueOf(userListFireBase.size()));
                }

                ///////////////////
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void getSongList() {  //get all the songs from firebase
        //Query query = VMode;
        VMSongs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iter= dataSnapshot.getChildren().iterator();
                songListFireBase= null;
                while(iter.hasNext()){
                    DataSnapshot snapshot  = iter.next();
                    songListFireBase = (HashMap<String,Song>) snapshot.getValue();
                    //Log.d("list",String.valueOf(userListFireBase.size()));
                }
                //////////////////////
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}