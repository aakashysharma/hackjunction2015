package com.cozify.CoziAmbiance;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main extends Activity {

    /**
     * Called when the activity is first created.
     */
    public static final String SERVICECMD = "com.android.music.musicservicecommand";
    public static final String CMDNAME = "command";
    public static final String CMDTOGGLEPAUSE = "togglepause";
    public static final String CMDSTOP = "stop";
    public static final String CMDPAUSE = "pause";
    public static final String CMDPREVIOUS = "previous";
    public static final String CMDNEXT = "next";
    /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        IntentFilter iF = new IntentFilter();
        iF.addAction("com.android.music.metachanged");
        iF.addAction("com.android.music.playstatechanged");
        iF.addAction("fm.last.android.metachanged");
        iF.addAction("fm.last.android.playbackpaused");
        iF.addAction("com.sec.android.app.music.metachanged");
        iF.addAction("com.nullsoft.winamp.metachanged");
        iF.addAction("com.nullsoft.winamp.playstatechanged");
        iF.addAction("com.amazon.mp3.metachanged");
        iF.addAction("com.amazon.mp3.playstatechanged");
        iF.addAction("com.miui.player.metachanged");
        iF.addAction("com.miui.player.playstatechanged");
        iF.addAction("com.real.IMP.metachanged");
        iF.addAction("com.real.IMP.playstatechanged");
        iF.addAction("com.sonyericsson.music.metachanged");
        iF.addAction("com.sonyericsson.music.playstatechanged");
        iF.addAction("com.rdio.android.metachanged");
        iF.addAction("com.rdio.android.playstatechanged");
        iF.addAction("com.samsung.sec.android.MusicPlayer.metachanged");
        iF.addAction("com.samsung.sec.android.MusicPlayer.playstatechanged");
        iF.addAction("com.andrew.apollo.metachanged");
        iF.addAction("com.andrew.apollo.playstatechanged");
        iF.addAction("com.htc.music.metachanged");
        iF.addAction("com.htc.music.playstatechanged");
        iF.addAction("com.spotify.music.playbackstatechanged");
        iF.addAction("com.spotify.music.metadatachanged");
        iF.addAction("com.rhapsody.playstatechanged");

        registerReceiver(mReceiver, iF);
//        ArrayList<String> passing = new ArrayList<String>();
//        passing.add("removeMoodLights");
//        passing.add("homebox56465");
//        PushAsyncTask push = new PushAsyncTask();
//        push.execute(passing);

    } // end onCreate()
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            String cmd = intent.getStringExtra("command");
            Log.d("mIntentReceiver.onReceive ", action + " / " + cmd);
            String artist = intent.getStringExtra("artist");
            String album = intent.getStringExtra("album");
            String track = intent.getStringExtra("track");
            Log.d("Music",artist+":"+album+":"+track);
        }
    };
    private class PushAsyncTask extends AsyncTask<ArrayList<String>, Void, ArrayList<String>> {

        protected ProgressDialog progressDialog = new ProgressDialog(Main.this);
        InputStream inputStream = null;
        String result = "";

        protected void onPreExecute() {
        }

        @Override
        protected ArrayList<String> doInBackground(ArrayList<String>... passing) {
            String url_select = "https://api.parse.com/1/functions/" + passing[0].get(0);

            try {
                // Set up HTTP post

                // HttpClient is more then less deprecated. Need to change to URLConnection
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url_select);
                httpPost.setHeader("X-Parse-Application-Id", "MLoLP7ekO4KO3dLkKHL0izWQyS1N8gA7tAraVJvC");
                httpPost.setHeader("X-Parse-REST-API-Key", "E9xTCvv1sGnE90p2JDlRHueVZFXQ809mg5Nt8a5C");
                httpPost.setHeader("Content-type", "application/json");
                Map<String, String> comment = new HashMap<String, String>();
                comment.put("DeviceID", passing[0].get(1));
                String json = new GsonBuilder().create().toJson(comment, Map.class);
                httpPost.setEntity(new StringEntity(json, "UTF8"));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                inputStream = httpEntity.getContent();
            } catch (UnsupportedEncodingException e1) {
                Log.e("UnsupportedEncodingException", e1.toString());
                e1.printStackTrace();
            } catch (ClientProtocolException e2) {
                Log.e("ClientProtocolException", e2.toString());
                e2.printStackTrace();
            } catch (IllegalStateException e3) {
                Log.e("IllegalStateException", e3.toString());
                e3.printStackTrace();
            } catch (IOException e4) {
                Log.e("IOException", e4.toString());
                e4.printStackTrace();
            }
            // Convert response to string using String Builder
            try {
                BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
                StringBuilder sBuilder = new StringBuilder();

                String line = null;
                while ((line = bReader.readLine()) != null) {
                    sBuilder.append(line + "\n");
                }

                inputStream.close();
                result = sBuilder.toString();

            } catch (Exception e) {
                Log.e("StringBuilding & BufferedReader", "Error converting result " + e.toString());
            }
            return null;
        }

        protected void onPostExecute(Void v) {
            //parse JSON data
            try {
                JSONObject jObject = new JSONObject(result);
                Log.i("jObject", jObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }


            this.progressDialog.dismiss();// catch (JSONException e)
        } // protected void onPostExecute(Void v)
    } // end callWebService()
}
