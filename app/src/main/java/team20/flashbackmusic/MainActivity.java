package team20.flashbackmusic;


import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    class MyAdapter extends BaseAdapter implements ListAdapter {
        private ArrayList<String> list = new ArrayList<String>();
        private Context context;

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
            listItemText.setText(list.get(position));

            addBtn = (Button) v.findViewById(R.id.add_btn);
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //do something
                    if (songListObj.get(currentindex).getStatus() == 0) {
                        addBtn.setBackgroundResource(R.drawable.check);
                        playList_flashback.changeToFavorite(currentindex);//       MainActivity.playList_flashback.changeToFavorite(list.get(position));
                    } else if (songListObj.get(currentindex).getStatus() == 1) {
                        playList_flashback.changeToDislike(currentindex);//     MainActivity.playList_flashback.changeToDislike(list.get(position));
                        next(playList_flashback.sortingList);
                        addBtn.setBackgroundResource(R.drawable.cross);
                    } else {
                        //Location location;//                Location location;
                        //Date time;//              Calendar time;
                        //playList_flashback.changeToNeutral(position,location,time);//            MainActivity.playList_flashback.changeToNeutral(list.get(position),location, time);
                        addBtn.setBackgroundResource(R.drawable.add);
                    }
                }
            });

            return v;
        }
    }
    private final int SONG_TITLE = MediaMetadataRetriever.METADATA_KEY_TITLE;
    private final int SONG_ARTIST = MediaMetadataRetriever.METADATA_KEY_ARTIST;
    private final int SONG_ALBUM = MediaMetadataRetriever.METADATA_KEY_ALBUM;
    private final int SONG_DURATION = MediaMetadataRetriever.METADATA_KEY_DURATION;
    private int  currentindex = 0;
    private ListView listView;
    private MediaPlayer mediaPlayer;
    private FloatingActionButton  playButton;
    private Switch flashback;
    private FloatingActionButton nextSong;
    private FloatingActionButton previousSong;
    private Button addBtn;
    private Score score;
    private PlayList_flashback playList_flashback;
    // List of songs
    // List of albums view
    private GridView albumView;
    private ListAdapter albumAdapter;
    boolean flashOn = false;
    private MediaMetadataRetriever mmr = new MediaMetadataRetriever();

    //TextViews of this activity
    private TextView nowPlayingView;
    private TextView locationView, durationView;


    //list of songs according to purpose
    private ArrayList<String> songList; //to play music
    private ArrayList<String> songTitleList; //for display
    private ArrayList<Song> songListObj; //for storage of songs
    private ArrayList<String> sortingList;
    private Hashtable<String, Integer> indexTosong;


    //Create list of albums
    private Hashtable<String, Album> albumList; //for checking if album exist
    private ArrayList<Album> tempListAlbum;


    private boolean flashbackFlag = false;
    private boolean playingAlbumFlag = false;
    private Album albumToPlay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set up media player
        mediaPlayer = new MediaPlayer();

        flashback = (Switch) findViewById(R.id.switch1);
        flashback.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    playList_flashback.sorter();
                    sortingList = playList_flashback.sortingList;
                    playTracksOrder(songList);
                }
                else{
                    mediaPlayer.stop();
                    listView.setClickable(true);
                }
            }
        });

        //Play or Pause button
        playButton = (FloatingActionButton) findViewById(R.id.playButton);

        // Init tag to play (TODO: should change to previously closed state)
        playButton.setTag(R.drawable.ic_play_arrow_black_24dp);
        nextSong = findViewById(R.id.next);
        previousSong = findViewById(R.id.previous);
        nowPlayingView = (TextView) findViewById(R.id.nowPlaying);
        locationView = (TextView) findViewById(R.id.playingLoc);
        durationView = (TextView) findViewById(R.id.songDuration);

        albumView = (GridView) findViewById(R.id.albumList);

        // Make list
        //listView = (ListView) findViewById(R.id.songList);

        songList = new ArrayList<>();
        songTitleList = new ArrayList<>();
        songListObj = new ArrayList<>();
        albumList = new Hashtable<>();
        indexTosong = new Hashtable<>();

        //GET LIST OF SONGS FROM RAW DIRECTORY
        getMusic(songList, songTitleList);
        sortingList = new ArrayList<>(songList);
        for(int i=0;i<songList.size();i++){
            indexTosong.put(songList.get(i),i);
        }
        score = new Score(songList,songListObj);
        playList_flashback = new PlayList_flashback(sortingList,songListObj,indexTosong);
        //populate album after we get music
        populateAlbum(songListObj, albumList);
        MyAdapter adapter = new MyAdapter(songTitleList, this);
        //handle listview and assign adapter
        listView = (ListView)findViewById(R.id.songList);
        listView.setAdapter(adapter);
        //show list of song in the track list view
        //listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, songTitleList);
        //listView.setAdapter(listAdapter);

        //set up listener for list of tracks list view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // Disable album queue
                playingAlbumFlag = false;
                currentindex = i;
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

        nextSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flashOn == false){
                    next(songList);
                }
            }
        });
        previousSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flashOn == false){
                    previous(songList);
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
        //LayoutInflater inflater = LayoutInflater.from(MyAdapter.this);
        //LayoutInflater inflater = (LayoutInflater) .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View v = inflater.inflate(R.layout.listview,null);
        //View v=adapter.v;


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
        });}
    public void playTracksOrder(final List<String> playlist){
        if(mediaPlayer!=null&&mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        currentindex = 0;
        listView.setEnabled(false);

        String repeat = new String(new char[1]).replace("\0", " ");
        nowPlayingView.setText(repeat+ songTitleList.get(currentindex) + repeat);
        nowPlayingView.setSelected(true);
        //for(int i=3;i<playlist.size();i++) {
        int resID = getResources().getIdentifier(songList.get(currentindex), "raw", getPackageName());
        mediaPlayer = MediaPlayer.create(MainActivity.this, resID);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                next(songList);
            }
        });

    }
    public void next(final List<String> playlist){
        currentindex++;
        if(mediaPlayer!=null&&mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        if(currentindex<playlist.size()) {
            String repeat = new String(new char[1]).replace("\0", " ");
            nowPlayingView.setText(repeat + songTitleList.get(currentindex) + repeat);
            nowPlayingView.setSelected(true);
            //for(int i=3;i<playlist.size();i++) {
            //mediaPlayer = MediaPlayer.create(MainActivity.this,resID);
            int resID = getResources().getIdentifier(songList.get(currentindex), "raw", getPackageName());
            mediaPlayer = MediaPlayer.create(MainActivity.this, resID);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    next(songList);
                }
            });
        }
    }
    public void previous(final List<String> playlist){
        if(mediaPlayer!=null&&mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        currentindex--;
        if(currentindex>=0) {
            String repeat = new String(new char[1]).replace("\0", " ");
            nowPlayingView.setText(repeat + songTitleList.get(currentindex) + repeat);
            nowPlayingView.setSelected(true);
            //for(int i=3;i<playlist.size();i++) {
            int resID = getResources().getIdentifier(songList.get(currentindex), "raw", getPackageName());
            mediaPlayer = MediaPlayer.create(MainActivity.this, resID);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    next(songList);
                }
            });
        }
    }

}



