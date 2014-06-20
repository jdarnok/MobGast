package com.example.gastronomia;

import gastronomia.helper.AlertDialogManager;
import gastronomia.helper.ConnectionDetector;
import gastronomia.helper.JSONParser;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
 
public class Restraunt extends ListActivity {
    // Connection detector
    ConnectionDetector cd;
     
    // Alert dialog manager
    AlertDialogManager alert = new AlertDialogManager();
     

 
    // Creating JSON Parser object
    JSONParser jsonParser = new JSONParser();
 
    ArrayList<HashMap<String, String>> restrauntList;
 
    // albums JSONArray
    JSONArray restraunts = null;
 
 
    // ALL JSON node names
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_MEALS_COUNT = "place";
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restraunt);
         
        cd = new ConnectionDetector(getApplicationContext());
         
        // Check for internet connection
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            alert.showAlertDialog(Restraunt.this, "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            return;
        }
 
        // Hashmap for ListView
        restrauntList = new ArrayList<HashMap<String, String>>();
        
        
        // get listview
        ListView lv = getListView();
         
        new LoadRestraunts().parseJson();
        

        lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2,
                    long arg3) {
                // on selecting a single album
                // TrackListActivity will be launched to show tracks inside the album
                Intent i = new Intent(getApplicationContext(), TrackListActivity.class);
                 
                // send album id to tracklist activity to get list of songs under that album
                String rest_id = ((TextView) view.findViewById(R.id.restraunt_id)).getText().toString();
                i.putExtra("restraunt_id", rest_id);               
                 
                startActivity(i);
            }
        });     
    }
 
  
    class LoadRestraunts {
 
      
        protected String parseJson() {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
 
            // getting JSON string from URL
            String json;
 
            
            
            byte[] buffer = new byte[1024];
            int n;
            StringBuffer fileContent = new StringBuffer("");
            try {
            	FileInputStream fis = openFileInput("json_gastronomia_file");
    			while (( n = fis.read(buffer)) != -1) 
    			{ 
    			  fileContent.append(new String(buffer, 0, n)); 
    			}
    			fis.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
            json = fileContent.toString();
            

 
            try {               
                restraunts = new JSONArray(json);
                 
                if (restraunts != null) {
                    // looping through All restraunts
                    for (int i = 0; i < restraunts.length(); i++) {
                        JSONObject c = restraunts.getJSONObject(i);
 
                        // Storing each json item values in variable
                        String id = c.getString(TAG_ID);
                        String name = c.getString(TAG_NAME);
                        String meals_count = c.getString(TAG_MEALS_COUNT);
 
                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();
 
                        // adding each child node to HashMap key => value
                        map.put(TAG_ID, id);
                        map.put(TAG_NAME, name);
                        map.put(TAG_MEALS_COUNT, meals_count);
 
                        // adding HashList to ArrayList
                        restrauntList.add(map);
                    }
                }else{
                }
 
            } catch (JSONException e) {
                e.printStackTrace();
            }
 
            ListAdapter adapter = new SimpleAdapter(
                    Restraunt.this, restrauntList,
                    R.layout.list_item_restraunts, new String[] { TAG_ID,
                            TAG_NAME, TAG_MEALS_COUNT }, new int[] {
                            R.id.restraunt_id, R.id.restraunt_name, R.id.meals_count });
             
            // updating listview
            setListAdapter(adapter);
            
            setTitle("Restauracje");
            
            
            return null;
        }
 
       
        protected void onPostExecute(String file_url) {
         
            runOnUiThread(new Runnable() {
                public void run() {

                }
            });
 
        }
 
    }
}