package tests;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.IBinder;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.ServiceTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import android.app.Service;
import android.widget.TextView;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.lang.String;

import team20.flashbackmusic.MainActivity;
import team20.flashbackmusic.Song;
import java.util.Date;
import team20.flashbackmusic.*;
import android.util.Log;


/**
 * Created by yujingwen199756 on 2/17/18.
 */


public class JUnitTest {
    Song song;



    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);
    @Rule //use for getting service
    public final ServiceTestRule mServiceRule = new ServiceTestRule();

    @Before
    public void setUp() {
        song = new Song("SuperHeros", "The script", "album1", 3l, 0);

    }

/*
    @Test
    public void testShowDateAndTime(){
        song.setMostRecentDateTimeString("No Last Current Time and Date are available");
        mainActivity.getActivity().showDateAndTime(song);
        TextView testTextView = (TextView)mainActivity.getActivity().findViewById(R.id.playingTime);
        assertEquals("No Last Current Time and Date are available", testTextView.getText().toString());
    }

    @Test
    public void testStoreDateAndTime(){
        mainActivity.getActivity().storeDateAndTime(song);

    }*/

    @Test
    public void testGetAddress() throws IOException {
        String address1 = mainActivity.getActivity().getAddress(32.881131, -117.237569);
        assertEquals(address1, "Library Walk, California");
        String address2 = mainActivity.getActivity().getAddress(36.299335, 43.149057);
        assertEquals(address2, "The Right Coast, Nineveh Governorate");

    }



}

