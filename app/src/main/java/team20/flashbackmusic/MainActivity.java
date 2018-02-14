package team20.flashbackmusic;

import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final int SONG_TITLE = MediaMetadataRetriever.METADATA_KEY_TITLE;
    private final int SONG_ARTIST = MediaMetadataRetriever.METADATA_KEY_ARTIST;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private ListView listView;
    private ListAdapter listAdapter;
    private int currentindex = 0;
    private MediaMetadataRetriever mmr = new MediaMetadataRetriever();
    private TextView nowPlayingView, locationView, durationView;
    private Cursor cursor;
//    private Hashtable<Integer, String> songTitle;
    // TODO: Change to List<Song> later
    public ArrayList<String> songList;
    public ArrayList<String> songTitleList;
    static public SongList songs=new SongList();
    static public Score score = new Score(songs);
    static public PlayList_flashback playList_flashback= new PlayList_flashback(score);
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);;
        //Play or Pause button
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        // Init tag to play (TODO: should change to previously closed state)
        fab.setTag(R.drawable.ic_play_arrow_black_24dp);

        nowPlayingView = (TextView) findViewById(R.id.nowPlaying);
        locationView = (TextView) findViewById(R.id.playingLoc);
        durationView = (TextView) findViewById(R.id.songDuration);


        // Make list
        listView = (ListView) findViewById(R.id.songList);

        songList = new ArrayList<>();
        songTitleList = new ArrayList<>();

        getMusic(songList, songTitleList);

        //layout of listview
        MyAdapter adapter = new MyAdapter(songTitleList, this);
        //handle listview and assign adapter
        ListView lView = (ListView)findViewById(R.id.songList);
        lView.setAdapter(adapter);
        //listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songTitleList);
        //listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentindex = i;
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }

                int resID = getResources().getIdentifier(songList.get(i), "raw", getPackageName());
                mediaPlayer = MediaPlayer.create(MainActivity.this, resID);
                mediaPlayer.start();
                fab.setImageResource(R.drawable.ic_pause_black_24dp);



                //hover the now playing text view
                String repeat = new String(new char[1]).replace("\0", " ");
                nowPlayingView.setText(repeat+ songTitleList.get(i) + repeat);
                nowPlayingView.setSelected(true);
            }
        });

        Button flashback = (Button)findViewById(R.id.flashbackButton);
        flashback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playTracksOrder(songList);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                next(songList);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
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
                        fab.setImageResource(R.drawable.ic_play_arrow_black_24dp);

                    }
                    else {
                        mediaPlayer.start();
                        nowPlayingView.setSelected(true);
                        fab.setImageResource(R.drawable.ic_pause_black_24dp);

                    }
                } else {
                    if (fab.getTag().equals(R.drawable.ic_play_arrow_black_24dp)) {
                        fab.setImageResource(R.drawable.ic_pause_black_24dp);
                        fab.setTag(R.drawable.ic_pause_black_24dp);
                    } else {
                        fab.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                        fab.setTag(R.drawable.ic_play_arrow_black_24dp);
                    }
                }

            }
        });

        id = R.layout.listview;
        final Button add = (Button)findViewById(R.id.add_btn);
       // add.setOnClickListener(new View.OnClickListener() {
            //@Override
         //   public void onClick(View view) {
                /*final int position = listView.getPositionForView((View) view.getParent());
                String songname = songList.get(position);
                playList_flashback.changeToFavorite(songname);
                Resources resources = view.getResources();
                Drawable check = resources.getDrawable(R.drawable.check);
                add.setBackground(check);
                */
           // }
        //});
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
            String title = mmr.extractMetadata(SONG_TITLE) +" - "
                            + mmr.extractMetadata(SONG_ARTIST);
//            songList.add(title);
            songTitleList.add(title);

//            songTitle.put(resId, title);


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
    public void next(final List<String> playlist){
        currentindex++;
        if(currentindex<playlist.size()) {
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
    public void playTracksOrder(final List<String> playlist){
        if(mediaPlayer!=null&&mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        currentindex = 3;
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
            //int k=0;
            //while(mediaPlayer.isPlaying()) {
               // nowPlayingView.setText(repeat+ songTitleList.get(i) + repeat);
                //nowPlayingView.setSelected(true);
            //}
            //mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
              //  @Override
                //public void onCompletion(MediaPlayer mediaPlayer) {
                  //  mediaPlayer.release();
                //}
            //});
        //}
        //listView.setEnabled(true);
    }


}
