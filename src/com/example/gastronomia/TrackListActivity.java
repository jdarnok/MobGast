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
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

 
public class TrackListActivity extends ListActivity {
    // Connection detector
    ConnectionDetector cd;
     
    // Alert dialog manager
    AlertDialogManager alert = new AlertDialogManager();
   
 
    // Creating JSON Parser object
    JSONParser jsonParser = new JSONParser();
 
    ArrayList<HashMap<String, String>> tracksList;
 
    //JSONArray
    JSONArray categories = null;
     
    String category_id;
 
    // restraunts JSON url
    public static String URL_CATEGORIES = "http://gentle-bastion-2522.herokuapp.com/restraunts.json";
 
    // JSON node name
    private static final String TAG_ID = "id";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracks);
         
        cd = new ConnectionDetector(getApplicationContext());
          
        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            alert.showAlertDialog(TrackListActivity.this, "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            return;
        }
         

        
        
 
        // Hashmap for ListView
        tracksList = new ArrayList<HashMap<String, String>>();
 
        new loadCategories().parseJson();
        
       
         
        // get listview
        ListView lv = getListView();
         
        /**
         * Listview on item click listener
         * SingleTrackActivity will be lauched by passing restraunt id, category id
         * */
        lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2,
                    long arg3) {

                Intent i = new Intent(getApplicationContext(), MealsActivity.class);
                String album_id = ((TextView) view.findViewById(R.id.restraunt_id)).getText().toString();
                String category_id = ((TextView) view.findViewById(R.id.category_id)).getText().toString();
                i.putExtra("restraunt_id", album_id);
                i.putExtra("category_id", category_id);
                 
                startActivity(i);
            }
        }); 
 
    }
 

    class loadCategories {


        protected String parseJson() {
        	String category_name = null;
        	Intent intent = getIntent();
           String restraunt_id = intent.getStringExtra("restraunt_id");
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
             
            // post album id as GET parameter
            params.add(new BasicNameValuePair(TAG_ID, category_id));
            String json;
            // getting JSON string from URL
//            String json = jsonParser.makeHttpRequest(URL_ALBUMS, "GET",
//                    params);
            
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
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
            json = fileContent.toString();
            // Check your log cat for JSON reponse
           String restraunt_name = null;
 
            try {
                JSONArray restraunts = new JSONArray(json);
                if (restraunts != null) {
                	for (int j=0; j<restraunts.length();j++)
                	{
                		JSONObject restraunt = restraunts.getJSONObject(j);
                		String id = restraunt.getString("id");
                		restraunt_name = restraunt.getString("name");
                		 Log.d("Restraunt_id_intent ", restraunt_id);
                		 Log.d("Restraunt_id_node ", id);
                		if (id.equals(restraunt_id)){
                			 Log.d("Success ", id);
                			JSONArray categories = restraunt.getJSONArray("categories");
                			Log.d("Categories ", categories.toString());
                			if (categories != null) {
                				for (int k = 0; k < categories.length(); k++)
                				{ 
                					JSONObject category = categories.getJSONObject(k);
                					category_name = category.getString("category_name");
                					String category_id = category.getString("category_id");
                					HashMap<String, String> map = new HashMap<String, String>();
                					 map.put("category_name", category_name);
                					 map.put("restraunt_id", restraunt_id);
                					 map.put("category_id", category_id);
                					 tracksList.add(map);
                					
                				}
                			}
                		}
                	}

           
 
               
                }
 
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ListAdapter adapter = new SimpleAdapter(
                  TrackListActivity.this, tracksList,
                  R.layout.list_item_categories, new String[] { "category_id", "restraunt_id", "category_name"
                           }, new int[] {
                           R.id.category_id, R.id.restraunt_id, R.id.category_name  });
          // updating listview
          setListAdapter(adapter);
           
          // Change Activity Title with restraunt name
          setTitle(restraunt_name);
      
 
            return null;
        }
 
 
    }
}