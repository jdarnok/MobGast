package com.example.gastronomia;

import gastronomia.helper.JSONParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;



public class MainActivity extends ActionBarActivity {

	 JSONParser jsonParser = new JSONParser();
	private ProgressDialog pDialog;
	Random generator = new Random();
	int rand = generator.nextInt(40000);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        File file = getBaseContext().getFileStreamPath("json_gastronomia_file");
        if (!file.exists())
        new LoadAlbums().execute();
        
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        SharedPreferences prefs = this.getSharedPreferences("com.gastronomia.app", Context.MODE_PRIVATE);
        String restoredText = prefs.getString("text", null);
        if (restoredText == null)
        {
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putString("ID", Integer.toString(rand));
        editor.commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void showList(View view) {
    	
    	Intent intent = new Intent(this, Restraunt.class);
    	SharedPreferences prefs = getPreferences(MODE_PRIVATE); 
    	String restoredText = prefs.getString("ID", null);
    	intent.putExtra("IDintent", restoredText);
    	startActivity(intent);
    	
    	}
    
public void refresh(View view) {
	
	 new LoadAlbums().execute();
    	
    	
    	}

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
    
    class LoadAlbums extends AsyncTask<String, String, String> {
    	String json = null;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Wczytywanie restauracji ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        /**
         * getting restraunts JSON
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", "0"));
            // getting JSON string from URL
             json = jsonParser.makeHttpRequest("http://gentle-bastion-2522.herokuapp.com/restraunts.json", "GET",
                    params);
 
            // Check your log cat for JSON reponse
           // Log.d("Albums JSON: ", "> " + json);
          //  SharedPreferences jsonShared = getSharedPreferences( "appData", Context.MODE_WORLD_WRITEABLE );
 
            
           
            
            return null;
        }
 
     

		/**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all albums
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                	try {
						FileOutputStream fos = openFileOutput("json_gastronomia_file", Context.MODE_PRIVATE);
						fos.write(json.getBytes());
						fos.close();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                 
                }
            });
 
        }
 
    }

}
