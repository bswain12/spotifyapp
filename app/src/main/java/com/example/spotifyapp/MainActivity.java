package com.example.spotifyapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.spotify.android.appremote.api.error.CouldNotFindSpotifyApp;
import com.spotify.android.appremote.api.error.NotLoggedInException;
import com.spotify.android.appremote.api.error.UserNotAuthorizedException;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.PlayerApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;
//import com.spotify.sdk.android.authentication.sample.R;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

//import com.squareup.picasso.Callback;
//import com.squareup.picasso.Picasso;
//import com.squareup.picasso.Target;

public class MainActivity extends AppCompatActivity {
    //private static final String CLIENT_ID = "2f184ad41615437489cfd03177eade83";
    //private static final String REDIRECT_URI = "com.example.spotifyapp://callback/";
    //public static final int AUTH_TOKEN_REQUEST_CODE = 0x10; //new
    //public static final int AUTH_CODE_REQUEST_CODE = 0x11; //new
    private static final int REQUEST_CODE = 1337;
    public static SpotifyClass spotifyClass;
    //private final OkHttpClient mOkHttpClient = new OkHttpClient();
    //private String mAccessToken;
    //private String mAccessCode;
    //private Call mCall;
    //private SpotifyAppRemote mSpotifyAppRemote;
    //this isnt a real redirect uri so it might mess up the token

    @SuppressLint("all")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getSupportActionBar().setTitle(String.format(
                //Locale.US, "Spotify Auth Sample %s", com.spotify.sdk.android.auth.BuildConfig.VERSION_NAME));
    /*
        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming"});
        AuthorizationRequest request = builder.build();
        AuthorizationClient.openLoginInBrowser(this, request);

     */

        //final TextView tokenView = findViewById(R.id.token_text_view);
        //tokenView.setText(getString(R.string.token, mAccessToken))
    }
    @SuppressLint("all")
    @Override
    protected void onStart() {
        spotifyClass = new SpotifyClass();
        super.onStart();

        LogIn();
    }
    @SuppressLint("all")
    public void LogIn() {
        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(spotifyClass.CLIENT_ID, AuthorizationResponse.Type.TOKEN, spotifyClass.REDIRECT_URI);
        builder.setScopes(new String[]{"streaming"});
        AuthorizationRequest request = builder.build();
        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);

        ConnectionParams connP = new ConnectionParams.Builder(spotifyClass.CLIENT_ID).setRedirectUri(spotifyClass.REDIRECT_URI).showAuthView(true).build();

        SpotifyAppRemote.connect(this, connP, new Connector.ConnectionListener() {
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                spotifyClass.mSpotifyAppRemote = spotifyAppRemote;
                Log.d("Testing!!!", "Connected");

                connected();
            }

            public void onFailure(Throwable throwable) {
                Log.e("Testing!!!", throwable.getMessage(), throwable);
                //Something went wrong when attempting to connect! Handle errors here
            }
        });
    }
    public void connected(){
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Handle successful response
                    Log.d("TOKEN!!", response.getAccessToken());
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }
/*
    @Override
    protected void onDestroy() {
        cancelCall();
        super.onDestroy();
    }

    public void onGetUserProfileClicked(View view) {

        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //setResponse("Failed to fetch data: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    //setResponse(jsonObject.toString(3));
                } catch (JSONException e) {
                    //setResponse("Failed to parse data: " + e);
                }
            }
        });
    }
    public void onRequestCodeClicked(View view) {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(this, AUTH_CODE_REQUEST_CODE, request);
    }

    public void onRequestTokenClicked(View view) {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    public void onClearCredentialsClicked(View view) {
        AuthorizationClient.clearCookies(this);
    }

    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[]{"user-read-email"})
                .setCampaign("your-campaign-token")
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.getAccessToken();
            //updateTokenView();
        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {
            mAccessCode = response.getCode();
            //updateCodeView();
        }
    }



/*
    private void setResponse(final String text) {
        runOnUiThread(() -> {
            final TextView responseView = findViewById(R.id.response_text_view);
            responseView.setText(text);
        });
    }

 */
/*
    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }


/*
    //testtest
    @Override
    protected void onStart() {
        super.onStart();
    }
/*
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");


                        // Now you can start interacting with App Remote
                        connected();
                        Log.d("MainActivity","Connected. Mine yuh");
                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("MyActivity", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    private void connected() {
        // Find album cover using Picasso
        //ImageView album_artwork = (ImageView) findViewById(R.id.album_artwork);
        //Picasso.get().load("https://i.scdn.co/image/b2163e7456f3d618a0e2a4e32bc892d6b11ce673").into(album_artwork);

        // Play a playlist
        mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");

        // Subscribe to PlayerState
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        Log.d("MainActivity", track.name + " by " + track.artist.name);
                        //album_artwork.setImageResource(track.imageUri.hashCode());
                    }
                });
    }

 */


}