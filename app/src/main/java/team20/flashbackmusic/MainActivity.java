package team20.flashbackmusic;

import android.database.Cursor;
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
import android.widget.ListAdapter;
import android.widget.ListView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final int SONG_TITLE = MediaMetadataRetriever.METADATA_KEY_TITLE;
    private final int SONG_ARTIST = MediaMetadataRetriever.METADATA_KEY_ARTIST;

    private MediaPlayer mediaPlayer;
    private ListView listView;
    private ListAdapter listAdapter;
    private MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//    private Uri uri = MediaStore.Audio..EXTERNAL_CONTENT_URI;
    private Cursor cursor;
    // TODO: Change to List<Song> later
    private List<String> songList;
    private List<String> songTitleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Play or Pause button
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        // Init tag to play (TODO: should change to previously closed state)
        fab.setTag(R.drawable.ic_play_arrow_black_24dp);

        // Make list
        listView = (ListView) findViewById(R.id.songList);

        songList = new ArrayList<>();
        songTitleList = new ArrayList<>();

        getMusic(songList, songTitleList);

        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songTitleList);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }

                int resID = getResources().getIdentifier(songList.get(i), "raw", getPackageName());
                mediaPlayer = MediaPlayer.create(MainActivity.this, resID);
                mediaPlayer.start();
                fab.setImageResource(R.drawable.ic_pause_black_24dp);
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
                        fab.setImageResource(R.drawable.ic_play_arrow_black_24dp);

                    }
                    else {
                        mediaPlayer.start();
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


        }


    }


//    public void loadMedia(int resourceId) {
//        if (mediaPlayer == null) {
//            mediaPlayer = new MediaPlayer();
//        }
//
//        AssetFileDescriptor assetFileDescriptor = this.getResources().openRawResourceFd(resourceId);
//        try {
//            mediaPlayer.setDataSource(assetFileDescriptor);
//            mediaPlayer.prepareAsync();
//        } catch (Exception e) {
//            System.out.println(e.toString());
//        }
//    }

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
