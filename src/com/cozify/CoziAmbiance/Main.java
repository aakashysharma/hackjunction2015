package com.cozify.CoziAmbiance;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final Button btnSearch = (Button) findViewById(R.id.buttonPost);
        ArrayList<String> passing = new ArrayList<String>();
        passing.add("homebox56465");
        passing.add("pop");
        btnSearch.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                PushAsyncTask push = new PushAsyncTask();
                push.execute(passing);
            }
        });

    } // end onCreate()

    private class PushAsyncTask extends AsyncTask<ArrayList<String>, Void, ArrayList<String>> {

        protected ProgressDialog progressDialog = new ProgressDialog(Main.this);
        InputStream inputStream = null;
        String result = "";

        protected void onPreExecute() {
        }

        @Override
        protected ArrayList<String> doInBackground(ArrayList<String>... passing) {
            String url_select = "https://api.parse.com/1/functions/setMoodLights";

            try {
                // Set up HTTP post

                // HttpClient is more then less deprecated. Need to change to URLConnection
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url_select);
                httpPost.setHeader("X-Parse-Application-Id", "MLoLP7ekO4KO3dLkKHL0izWQyS1N8gA7tAraVJvC");
                httpPost.setHeader("X-Parse-REST-API-Key", "E9xTCvv1sGnE90p2JDlRHueVZFXQ809mg5Nt8a5C");
                httpPost.setHeader("Content-type", "application/json");
                Map<String, String> comment = new HashMap<String, String>();
                comment.put("DeviceID", passing[0].get(0));
                comment.put("MusicGenre", passing[0].get(1));
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
