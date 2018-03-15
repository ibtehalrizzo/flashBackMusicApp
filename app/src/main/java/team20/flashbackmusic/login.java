package team20.flashbackmusic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;

import static team20.flashbackmusic.MainActivity.account;
import static team20.flashbackmusic.MainActivity.emails;


/**
 * Created by christhoperbernard on 14/03/18.
 */

public class login extends AppCompatActivity{
    SignInButton sign_in;
    GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // .enableAutoManage(this, this)
                //.addOnConnectionFailedListener(this)
                //.addConnectionCallbacks(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        sign_in = (SignInButton) findViewById(R.id.sign_in_button);
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;
                }
            }
        });
    }

    private void signIn() {
        Intent signInIntent =  Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 0) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
            emails = account.getEmail();
            initializeDatabase(emails);
            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("error","signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }
    public void initializeDatabase(String email){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference VMode = database.getReference();
        final String name = email.split("@")[0];
        VMode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //get current hashmap from database

//                if(!dataSnapshot.child("HashUsers").hasChild("hash"))
//                {
//                    Log.d("list USERS empty", "EMPTY");
//                    HashMap<String, User> userList = new HashMap<>();
//                    userList.put(name, new User(name));
//                    Gson gson = new Gson();
//                    String json = gson.toJson(userList);
//                    VMode.child("HashUsers").child("hash").setValue(json);
//
//                }
//                else
//                {
//                    if(!dataSnapshot.child("Users").hasChild(name)){
//                        Log.d("name empty", "EMPTY");
//
//                        //get current hashmap
//                        String currMap = (String) dataSnapshot.child("HashUsers").child("hash").getValue();
//                        HashMap<String, User> userList = new Gson().fromJson(currMap, HashMap.class);
////                        HashMap<String, User> userList = (HashMap<String,User>) dataSnapshot
////                                .child("HashUsers").getValue();
//                        userList.put(name, new User(name));
//                        Gson gson = new Gson();
//                        String json = gson.toJson(userList);
//                        VMode.child("HashUsers").child("hash").setValue(json);
//                        VMode.child("Users").child(name).setValue(new User(name));
//                    }
//
//                }

//                if (!dataSnapshot.child("HashSongs").hasChild("hash")))
//                {
//
//                }

                if(!dataSnapshot.child("Users").hasChild(name)){

                    Log.d("USER empty", "EMPTY");
                    User user = new User(name);
                    //user.addfriend("2", new User("2"));
                    //friend.put(name,user);
                    //user.setFriendList(friend);
                    VMode.child("Users").child(name).setValue(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //if(VMode.child(email) == null){
        //Map<String,User> friend = new HashMap<String,User>();
    }
    private void updateUI(GoogleSignInAccount account){
        if(account != null){
            Intent intent  = new Intent(this, MainActivity.class);
            finish();
        }
    }
}