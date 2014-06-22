package com.qexpress.mike;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends ListActivity { 
	SharedPreferences prefs;
	String mobileNum,site_key,queue_name;
	private ProgressDialog pDialog;
	 
    // URL to get sites JSON 
    private static String url = "http://www.qexpress.co.il/sites.php";
 
    // JSON Node names
    private static final String TAG_SITES = "sites"; 
    private static final String TAG_SITE_KEY = "site_key";
    private static final String TAG_SITE_NAME = "site_name";
    private static final String TAG_QUEUE_NAME = "queue_name";
    private static final String TAG_SITE_PHONE = "phone";
    //private static final String TAG_PHONE_MOBILE = "mobile";
  
    // sites JSONArray
    JSONArray sites = null;
 
    // Hashmap for ListView
    ArrayList<HashMap<String, String>> siteList;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main); 
		siteList = new ArrayList<HashMap<String, String>>();
	
        ListView lv = getListView();
     // Listview on item click listener
        
     		lv.setOnItemClickListener(new OnItemClickListener() { 

     			@Override
     			public void onItemClick(AdapterView<?> parent, View view,
     					int position, long id) { 
     				// getting values from selected ListItem
     				String site_name = ((TextView) view.findViewById(R.id.name)).getText().toString();
     				String site_key = ((TextView) view.findViewById(R.id.site_key)).getText().toString();
     				String queue_name = ((TextView) view.findViewById(R.id.queue_name)).getText().toString();
     				TextView tv_mobile=(TextView) view.findViewById(R.id.mobile);
     				String mobile=tv_mobile.getText().toString();
     				

     				// Starting single contact activity 
     				Intent in = new Intent(getApplicationContext(),
     						DisplayQueueStatus.class);
     				in.putExtra(TAG_SITE_KEY, site_key);
     				in.putExtra(TAG_SITE_NAME, site_name);
     				in.putExtra(TAG_QUEUE_NAME, queue_name);
     				startActivity(in);

     			}
     		});
        // Calling async task to get json 
        new GetContacts().execute();
        
		prefs = this.getSharedPreferences("com.qexpress.mike", 
				Context.MODE_PRIVATE); 
		//prefs.edit().remove("MY_MOBILE_NUMBER").commit();
		
		//check if mobile number is registered
		mobileNum = prefs.getString("MY_MOBILE_NUMBER", "null");
		if (mobileNum.equals("null")) {
			Intent intent = new Intent(MainActivity.this,
					EnterMobileNumber.class);
			startActivity(intent);
			
			//check again in case user has just registered
			mobileNum = prefs.getString("MY_MOBILE_NUMBER", "");
		}
		
		if (mobileNum.equals("null")){
			Toast.makeText(getApplicationContext(), "You are still not registered!", 
					Toast.LENGTH_LONG).show();
		}
		else{
				Toast.makeText(getApplicationContext(), "mobile number found:"+mobileNum, 
					Toast.LENGTH_LONG).show();
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	/**
     * Async task class to get json by making HTTP call
     * */
    private class GetContacts extends AsyncTask<Void, Void, Void> {
 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
 
        }
 
        @Override
        protected Void doInBackground(Void... arg0) { 
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
 
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
 
            Log.d("Response: ", "> " + jsonStr);
 
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                     
                    // Getting JSON Array node
                    sites = jsonObj.getJSONArray(TAG_SITES);
 
                    // looping through All Sites
                    for (int i = 0; i < sites.length(); i++) {
                        JSONObject c = sites.getJSONObject(i);
                         
                        site_key = c.getString(TAG_SITE_KEY);
                        queue_name=c.getString(TAG_QUEUE_NAME);
                        String site_name = c.getString(TAG_SITE_NAME);
                        String site_phone = c.getString(TAG_SITE_PHONE);
                      
 
                       
                        // tmp hashmap for single site
                        HashMap<String, String> site = new HashMap<String, String>();
 
                        // adding each child node to HashMap key => value
                        site.put(TAG_SITE_KEY, site_key);
                        site.put(TAG_SITE_NAME, site_name);
                        site.put(TAG_QUEUE_NAME, queue_name);
                        site.put(TAG_SITE_PHONE, site_phone);
                       
 
                        // adding site to contact list
                        siteList.add(site);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
 
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss(); 
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, siteList,
                    R.layout.list_item, new String[] { TAG_SITE_NAME,TAG_SITE_KEY,TAG_QUEUE_NAME,TAG_SITE_PHONE
                             }, new int[] { R.id.name,R.id.site_key, R.id.queue_name, R.id.mobile});
 
            setListAdapter(adapter);
        }
 
    }
 

}
