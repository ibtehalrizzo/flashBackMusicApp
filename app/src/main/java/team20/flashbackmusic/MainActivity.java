package team20.flashbackmusic;


import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import java.util.TimerTask;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.PublicKey;
import java.util.Iterator;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import android.location.Geocoder;
import android.location.Address;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import android.support.v4.app.ActivityCompat;
import android.Manifest;
import android.location.Criteria;
import android.content.Context;
import java.util.Date;
import java.util.Timer;
import android.content.pm.PackageManager;
public class MainActivity extends AppCompatActivity implements LocationListener {

    private final int SONG_TITLE = MediaMetadataRetriever.METADATA_KEY_TITLE;
    private final int SONG_ARTIST = MediaMetadataRetriever.METADATA_KEY_ARTIST;
    private final int SONG_ALBUM = MediaMetadataRetriever.METADATA_KEY_ALBUM;
    private final int SONG_DURATION = MediaMetadataRetriever.METADATA_KEY_DURATION;

    private MediaPlayer mediaPlayer;
    private FloatingActionButton  playButton;
    private Button flashbackButton;


    // List of songs
    private ListView listView;
    private ListAdapter listAdapter;

    // List of albums view
    private GridView albumView;
    private ListAdapter albumAdapter;

    private MediaMetadataRetriever mmr = new MediaMetadataRetriever();

    //TextViews of this activity
    private TextView nowPlayingView, locationView, durationView, dateTimeView;


    //list of songs according to purpose
    private List<String> songList; //to play music
    private List<String> songTitleList; //for display
    private List<Song> songListObj; //for storage of songs


    //Create list of albums
    private Hashtable<String, Album> albumList; //for checking if album exist
    private ArrayList<Album> tempListAlbum;


    private boolean flashbackFlag = false;
    private boolean playingAlbumFlag = false;
    private Album albumToPlay;
    private LocationManager locationManager;
    private int currentPlayingSongIndex = -1;
    private int currentUserMNEIndex = -1;
    private int currentUserDayOfWeek = -1;
    private Location currentUserlocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        detectTimeChanges();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
            Log.d("test1", "ins");
            return;
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, 100, this);
        }
        //set up media player
        mediaPlayer = new MediaPlayer();

        flashbackButton = (Button) findViewById(R.id.fb);
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
        playButton = (FloatingActionButton) findViewById(R.id.playButton);

        // Init tag to play (TODO: should change to previously closed state)
        playButton.setTag(R.drawable.ic_play_arrow_black_24dp);

        nowPlayingView = (TextView) findViewById(R.id.nowPlaying);
        dateTimeView = (TextView)findViewById(R.id.playingTime);
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
                currentPlayingSongIndex = i;
                showCurrentLocation(songListObj.get(i));
                showDateAndTime(songListObj.get(i));
                storeDateAndTime(songListObj.get(i));
                try {
                    storeLocation(songListObj.get(i));
                } catch (IOException e) {
                    e.printStackTrace();
                }


                // Disable album queue
                playingAlbumFlag = false;

                if (!flashbackFlag) {
                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }

                    int resID = getResources().getIdentifier(songList.get(i), "raw", getPackageName());
                    mediaPlayer = MediaPlayer.create(MainActivity.this, resID);
                    mediaPlayer.start();

                    playButton.setImageResource(R.drawable.ic_pause_black_24dp);
                    playButton.setTag(R.drawable.ic_pause_black_24dp);

                    //hover the now playing text view
                    String repeat = new String(new char[1]).replace("\0", " ");
                    nowPlayingView.setText(repeat + songTitleList.get(i) + repeat);
                    nowPlayingView.setSelected(true);
                } else {
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


        //set on item click listener for album list
        albumView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("album:", "album is clicked!");
                // Enable album queue
                playingAlbumFlag = true;

                if (!flashbackFlag) {
                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }

                    //get album to play
                    albumToPlay = tempListAlbum.get(i);
                    albumToPlay.clearQueue();
                    //queue all songs to play in album
                    albumToPlay.queueAllSong();

                    playAlbum();


                } else {
                    Toast.makeText(MainActivity.this, "You are in flashback mode!\n" +
                            "Please go to normal mode to select music", Toast.LENGTH_SHORT).show();
                }
            }

        });


        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Replace with play the current music played
                //or just change to pause button if there is no music
                //selected
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        playButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                        playButton.setTag(R.drawable.ic_play_arrow_black_24dp);

                    } else {
                        mediaPlayer.start();
                        nowPlayingView.setSelected(true);
                        playButton.setImageResource(R.drawable.ic_pause_black_24dp);
                        playButton.setTag(R.drawable.ic_pause_black_24dp);

                    }
                } else {
                    if (playButton.getTag().equals(R.drawable.ic_play_arrow_black_24dp)) {
                        playButton.setImageResource(R.drawable.ic_pause_black_24dp);
                        playButton.setTag(R.drawable.ic_pause_black_24dp);
                    } else {
                        playButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                        playButton.setTag(R.drawable.ic_play_arrow_black_24dp);
                    }
                }

            }
        });

    }

    //TODO: cite source
    public String convertMillisecondsToTime(long milliseconds) {
        String finalTimerString = "";
        String secondsString;

        int hours = (int) (milliseconds / 1000 * 60 * 60);
        int minutes = (int) (milliseconds % (1000 * 60 * 60) / (1000 * 60 * 60));
        int seconds = (int) (milliseconds % (1000 * 60 * 60) % (1000 * 60 * 60) / 1000);

        // Add hours
        if (hours > 0) {
            finalTimerString = hours + ":";
        }
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }
        finalTimerString = finalTimerString + minutes + ":" + secondsString;
        return finalTimerString;
    }


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

            if(artist == null)
                artist = "Unknown Artist";
            if(album == null)
                album = "Unknown Album";

            long durationToLong = Long.parseLong(duration);

            Song s = new Song(title, artist, album, durationToLong, resId);
            songListObj.add(s);


            //add to list for display TODO: we can get data from song object instead
            String display = title + " - " + artist;
            songTitleList.add(display);

        }


    }

    //precondition: tracks is already populated
    public void populateAlbum(List<Song> songListObj, Hashtable<String, Album> albumList) {
        for (int i = 0; i < songListObj.size(); i++) {
            String albumName = songListObj.get(i).getAlbum();
            if (!albumList.containsKey(albumName)) {
                //create album
                Album newAlbum = new Album(albumName);
                //add current track
                newAlbum.addTrack(songListObj.get(i));

                albumList.put(albumName, newAlbum);
            } else {
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

    //pre condition album has at least 1 song
    public void playAlbum()
    {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        Song songToPlay = albumToPlay.getNextSongToPlay();
        int resID = songToPlay.getSongResId();

        mediaPlayer = MediaPlayer.create(MainActivity.this, resID);
        mediaPlayer.start();


        //set button tag
        playButton.setImageResource(R.drawable.ic_pause_black_24dp);
        playButton.setTag(R.drawable.ic_pause_black_24dp);

        //hover the now playing text view TODO: make this a method
        String repeat = new String(new char[1]).replace("\0", " ");
        String currentPlayingSongDisplay = songToPlay.getTitle() + " - " + songToPlay.getArtist();
        nowPlayingView.setText(repeat + currentPlayingSongDisplay + repeat);
        nowPlayingView.setSelected(true);


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("onCompletionCalled", "yes!");
                if (playingAlbumFlag) {
                    //TODO: there is a bug when playing the next song, can't pause
                    //TODO: need to make this listener outside
//                    Log.d("onCompletionCalled", "yes!");
                    if (!albumToPlay.isQueueEmpty()) {
                        playAlbum();
                    } else {
                        playingAlbumFlag = false;
                        //set button tag
                        playButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                        playButton.setTag(R.drawable.ic_play_arrow_black_24dp);
                    }

                } else {
                    //set button tag
                    playButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    playButton.setTag(R.drawable.ic_play_arrow_black_24dp);
                }
            }
        });

    }


    @Override
    public void onLocationChanged(Location location) {
        if(flashbackFlag) {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String finalString = null;
                try {
                    finalString = getAddress(latitude, longitude);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                currentUserlocation = location;
                if (currentPlayingSongIndex != -1) {
                    Song song = songListObj.get(currentPlayingSongIndex);
                    song.setMostRecentDateTimeString(finalString);
                    if (!song.locationHistory.contains(location)) {
                        Iterator<Location> itr = song.locationHistory.iterator();
                        while (itr.hasNext()) {
                            if (itr.next().distanceTo(location) >= 30.48) {
                                song.locationHistory.add(location);
                            }
                        }
                    }
                }
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

    public void showDateAndTime(Song song){
        if(song.getMostRecentDateTime() != null){
            dateTimeView.setText(song.getMostRecentDateTimeString());
        }
        else{
            dateTimeView.setText("No Last Current Time and Date are available");
        }

    }
    public void showCurrentLocation(Song song){
        if(song.getMostRecentLocation() != null){

            locationView.setText(song.getMostRecentLocationString());
        }
        else{
            locationView.setText("No Last Current location is available");
        }

    }
    public void storeDateAndTime(Song song){
        SimpleDateFormat dateFormatter = new SimpleDateFormat("kk:mm:ss   dd MMM yyyy");
        String dateAndTime = dateFormatter.format(new Date());
        song.setMostRecentDateTime(new Date());
        song.setMostRecentDateTimeString(dateAndTime);
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        hour = convertToMNEIndex(hour);
        if(!song.timeHistory.contains(hour)){
            song.timeHistory.add(hour);
        }
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if(!song.dayHistory.contains(day)){
            song.dayHistory.add(day);
        }

    }

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
        String finalString = address + ", "+state;
        return finalString;
    }

    public void storeLocation(Song song) throws IOException {
        Criteria criteria = new Criteria();
        String bestProvider =locationManager.getBestProvider(criteria,true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
            Log.d("test1", "ins");
            return;
        } else {
            Location location = locationManager.getLastKnownLocation(bestProvider);
            if(location != null){
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String finalString = getAddress(latitude, longitude);
                song.setMostRecentLocationString(finalString);
                song.setMostRecentLocation(location);
                if(!song.locationHistory.contains(location)){
                    Iterator<Location> itr = song.locationHistory.iterator();
                    while(itr.hasNext()){
                        if(itr.next().distanceTo(location) >= 30.48){
                            song.locationHistory.add(location);
                        }
                    }
                }
            }
        }
    }

    public int convertToMNEIndex(int hour){
        if(4<=hour && hour < 12 ){
            hour = 1;
        }
        else if(12<=hour && hour < 20 ){
            hour = 2;
        }
        else{
            hour = 3;
        }
        return hour;
    }

    public void setCurrentUserMNEAndDay(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        currentUserDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        currentUserMNEIndex = convertToMNEIndex(hour);

    }
    public void detectTimeChanges(){
        setCurrentUserMNEAndDay();
        Calendar calendar = Calendar.getInstance();
        int min = calendar.get(Calendar.MINUTE);
        int timeDelay = 1000*60*(60 - min);
        Timer timerDelay = new Timer();
        timerDelay.schedule(new TimerTask() {
            public void run() {
                Timer hourlyTime = new Timer ();
                TimerTask hourlyTask = new TimerTask () {
                    @Override
                    public void run () {
                        setCurrentUserMNEAndDay();
                    }
                };
                hourlyTime.schedule (hourlyTask, 0l, 1000*60*60);

            }

        }, timeDelay);
    }
}
