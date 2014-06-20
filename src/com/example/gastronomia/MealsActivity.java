package com.example.gastronomia;
import gastronomia.helper.AlertDialogManager;
import gastronomia.helper.ConnectionDetector;
import gastronomia.helper.JSONParser;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;


import android.widget.ListAdapter;

import android.widget.SimpleAdapter;


 
public class MealsActivity extends ListActivity {
    // Connection detector
    ConnectionDetector cd;
     
    // Alert dialog manager
    AlertDialogManager alert = new AlertDialogManager();
 
    // Creating JSON Parser object
    JSONParser jsonParser = new JSONParser();
 
    ArrayList<HashMap<String, String>> mealList;
 
    // meals JSONArray
    JSONArray meals = null;
     
    // restraunt id
    String restraunt_id, album_name;
 
    // tracks JSON url
    // id - should be posted as GET params to get track list (ex: id = 5)
    public static String URL_ALBUMS = "http://gentle-bastion-2522.herokuapp.com/restraunts.json";
 
    // ALL JSON node names
   
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meals);
         
        cd = new ConnectionDetector(getApplicationContext());
          
        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            alert.showAlertDialog(MealsActivity.this, "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            return;
        }
         
        // Get album id
        Intent i = getIntent();
        restraunt_id = i.getStringExtra("restraunt_id");
        URL_ALBUMS = "http://gentle-bastion-2522.herokuapp.com/restraunts.json";
 
        // Hashmap for ListView
        mealList = new ArrayList<HashMap<String, String>>();
 
        // Loading tracks in Background Thread
        new loadCategories().parseJson();
         


      
 
    }
 
    /**
     * Background Async Task to Load all tracks under one album
     * */
    class loadCategories {
    	 
 
       
        protected String parseJson() {
        	String category_name = null;
        	Intent intent = getIntent();
           String restraunt_id = intent.getStringExtra("restraunt_id");
           String category_intent_id = intent.getStringExtra("category_id");
            // Building Parameters

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
            // Check your log cat for JSON reponse
           
 
            try {
                JSONArray restraunts = new JSONArray(json);
                if (restraunts != null) {
                	for (int j=0; j<restraunts.length();j++)
                	{
                		JSONObject restraunt = restraunts.getJSONObject(j);
                		String id = restraunt.getString("id");
                		
                		
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
                					 Log.d("category_id ",category_id);
                					 Log.d("category_id_intent ",category_intent_id);
                					if(category_id.equals(category_intent_id)){
                					JSONArray meals = category.getJSONArray("meals");
                					
                						for (int i = 0; i< meals.length(); i++)
                						{
                							JSONObject meal = meals.getJSONObject(i);
                							String meal_name = meal.getString("meal_name");
                							String meal_price = meal.getString("meal_price");
                							 Log.d("meal ", meal_name);
                					HashMap<String, String> map = new HashMap<String, String>();
                					 map.put("meal_name", meal_name);
                					 map.put("meal_price", meal_price);
                					 map.put("category_name", category_name);
                					 mealList.add(map);
                						}
                					}
                				}
                			}
                		}
                	}
              //      String album_id = jObj.getString(TAG_ID);
           
 
               
                }
 
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ListAdapter adapter = new SimpleAdapter(
                  MealsActivity.this, mealList,
                  R.layout.list_item_meals, new String[] { "meal_name", "meal_price"
                           }, new int[] {
                           R.id.meal_name, R.id.meal_price, R.id.category_name  });
          // updating listview
          setListAdapter(adapter);
           
          // Change Activity Title with Album name
          setTitle(category_name);
      
 
            return null;
        }
 
       
 
    }
}