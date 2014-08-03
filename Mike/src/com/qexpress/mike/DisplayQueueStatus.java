package com.qexpress.mike;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;









import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayQueueStatus extends Activity{
	Button  buttonGetMeIn; 
	TextView organizationName, queueName, qlastOrTicketLabel, qout, qeta; 
	TextView openingHours,qlastOrTicketNumber,qoutNumber,qetaTime;
	MenuItem item_cancel_ticket;
	SharedPreferences prefs;
	
	long startTime = 0;
	
	// JSON Node names
	private static final String TAG_CONTACTS = "contacts";
	private static final String TAG_SITE_KEY = "site_key";
	private static final String TAG_SITE_NAME = "site_name";
	private static final String TAG_QUEUE = "queue";
	private static final String TAG_QUEUE_NAME = "queue_name";
	private static final String TAG_QLAST = "qlast";
	private static final String TAG_QOUT = "qout";
	private static final String TAG_USER_ALREADY_IN_LINE = "user_is_already_in_line";
	private static final String TAG_USER_TICKET_NUMBER = "user_ticket_number";
	private static final String TAG_AVG_HOP_DUR_SECS = "avg_hop_dur_secs";
	private static final String TAG_OPENING_TIME="opening_time";
	private static final String TAG_CLOSING_TIME="closing_time";
	public static final String TAG_WEB_SERVER = "http://www.qexpress.co.il/";
    public static final String TAG_COLOR_DARK_BLUE="#0099CC";
    public static final String TAG_COLOR_LIGHT_BLUE="#33B5E5";
    public static final String TAG_COLOR_DARK_PURPLE="#9933CC";
    public static final String TAG_COLOR_LIGHT_PURPLE="#AA66CC";
    public static final String TAG_COLOR_DARK_GREEN="#669900";
    public static final String TAG_COLOR_LIGHT_GREEN="#99CC00";
    public static final String TAG_COLOR_DARK_ORANGE="#FF8800";
    public static final String TAG_COLOR_LIGHT_ORANGE="#FFBB33";
    public static final String TAG_COLOR_DARK_RED="#CC0000";
    public static final String TAG_COLOR_LIGHT_RED="#FF4444";
    
    public static final int GET_IN_LINE=1;
    
	// contacts JSONArray
	JSONArray contacts = null;

	String myMobileNumber;
	int user_is_already_in_line=0;
	int user_ticket_number;
	String site_key,site_name,queue_name;
	String color_in_dark=TAG_COLOR_DARK_BLUE;
    String color_in_light=TAG_COLOR_LIGHT_BLUE;
    String url =TAG_WEB_SERVER+"queue_status.php";
    
	// Hashmap for ListView
	ArrayList<HashMap<String, String>> contactList;
	// runs without a timer by reposting this handler at the end of the runnable
	Handler timerHandler = new Handler();
	Runnable timerRunnable = new Runnable() { 
		@ReportsCrashes(formKey = "", formUri = "https://doron.cloudant.com/_all_dbs") class MyApplication extends Application {
		  @Override
		  public void onCreate() {
		    // The following line triggers the initialization of ACRA
		    super.onCreate();
		    ACRA.init(this);
		  }
		}			
		@Override
		public void run() { 
			getQueueStatus();
			timerHandler.postDelayed(this, 2000); 
		}

		private void getQueueStatus() {
			// TODO Auto-generated method stub
			AsyncHttpClient client = new AsyncHttpClient();
			RequestParams params = new RequestParams("site_key",
					site_key);
			params.add("mobile_number", myMobileNumber);
			client.post(TAG_WEB_SERVER+"queue_status.php", params,
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(String jsonStr) {
							if (jsonStr != null) { 
								try {
									JSONObject jsonObj = new JSONObject(jsonStr);

									// Getting JSON Array node 
									contacts = jsonObj
											.getJSONArray(TAG_CONTACTS);

									// only one element in the array
									JSONObject c = contacts.getJSONObject(0);
									
									//String organization_name = c.getString(TAG_ORGANIZATION_NAME);
									organizationName.setText(site_name);
									String opening_time=c.getString(TAG_OPENING_TIME);
									String closing_time=c.getString(TAG_CLOSING_TIME);
									//openingHours.setText("Opening Hours: "+opening_time+"-"+closing_time);
									
									
									// Queue node is JSON Object
									JSONObject queue = c.getJSONObject(TAG_QUEUE);
									//String queue_name = queue.getString(TAG_QUEUE_NAME);
									queueName.setText(queue_name); 
									
									int avg_hop_dur_secs = queue.getInt(TAG_AVG_HOP_DUR_SECS);
									int qsize;
				
									user_is_already_in_line = c.getInt(TAG_USER_ALREADY_IN_LINE);
									if (user_is_already_in_line == 1) { 
										try{
										item_cancel_ticket.setVisible(true);
										}catch(Exception e){
											//do nothing
										}
										buttonGetMeIn.setVisibility(View.GONE);
										user_ticket_number = c.getInt(TAG_USER_TICKET_NUMBER);
										qlastOrTicketLabel.setText(R.string.your_number);
										qlastOrTicketNumber.setText(Integer.toString(user_ticket_number));
										qout.setVisibility(View.VISIBLE);
										int q_out = queue.getInt(TAG_QOUT);
										qoutNumber.setText(Integer.toString(q_out));
										qsize=user_ticket_number-q_out; 
										int exp_wait_secs=qsize*avg_hop_dur_secs; 
										String strWaitTime=getWaitTime(exp_wait_secs);
										qeta.setText(R.string.remaining_wait_time); 
										qetaTime.setText(strWaitTime);
										
									} else {
										try{
										item_cancel_ticket.setVisible(false);
										}
										catch(Exception e){
											//do nothing
										}
										buttonGetMeIn.setVisibility(View.VISIBLE);
										qlastOrTicketLabel.setText(R.string.last_in_line);
										int q_last = queue.getInt(TAG_QLAST);
										qlastOrTicketNumber.setText(Integer.toString(q_last));
										qout.setVisibility(View.VISIBLE);
										int q_out = queue.getInt(TAG_QOUT);
										qoutNumber.setText(Integer.toString(q_out)); 
										qsize=q_last-q_out;
										int exp_wait_secs=qsize*avg_hop_dur_secs;
										String strWaitTime=getWaitTime(exp_wait_secs);
										qeta.setText(R.string.estimated_wait_time); 
										//qeta.setTextColor(R.color.c1dark);
										qetaTime.setText(strWaitTime); 
										//qetaTime.setTextColor(R.color.c1light);
									}

								} catch (JSONException e) {
									e.printStackTrace();
								}
							} else {
								Log.e("ServiceHandler", 
										"Couldn't get any data from the url");
							}
						}

						private String getWaitTime(int exp_wait_secs) {
							// TODO Auto-generated method stub
							String strWaitTime = null;
							if (exp_wait_secs>=3600){
								int hours=exp_wait_secs/3600;
								int minutes=(exp_wait_secs%3600)/60;
								strWaitTime=String.format("%02d:%02d שעות", hours,minutes);
							}
							else{
								int minutes=exp_wait_secs/60;
								strWaitTime=String.format("%02d דקות", minutes);
							}
							return strWaitTime;
						}
					});
		}
	};
	@Override
	public boolean onCreateOptionsMenu(Menu menu) { 

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.queue, menu);
		item_cancel_ticket=menu.findItem(R.id.action_cancel_ticket);
		if (user_is_already_in_line==1){
		item_cancel_ticket.setVisible(true);
		}
		else{
			item_cancel_ticket.setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) { 
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_cancel_ticket) {
			cancelTicket();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void cancelTicket() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this,
				ReleaseTicket.class);
		intent.putExtra(TAG_SITE_KEY, site_key);
		startActivity(intent);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // Check which request we're responding to
	    if (requestCode == GET_IN_LINE) {
	        // Make sure the request was successful
	        if (resultCode == RESULT_OK) {
	        	String response=data.getStringExtra("MESSAGE");
	        	if (response != null) { 
					try {
						JSONObject jsonObj = new JSONObject(response);
						
						// Getting JSON Array node 
						JSONArray sms = jsonObj
								.getJSONArray("sms");

						// only one element in the array
						JSONObject s = sms.getJSONObject(0);
						
						String sms_response=s.getString("text");
						
						Intent intent = new Intent(DisplayQueueStatus.this,
						DisplayMessage.class); 
							intent.putExtra("MESSAGE", sms_response);
						startActivity(intent);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} 
	        	
	        }
	    }
	}
	@Override
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.queue_status);
		
		// getting intent data
        Intent in = getIntent();
        site_key = in.getStringExtra(TAG_SITE_KEY);
        site_name = in.getStringExtra(TAG_SITE_NAME);
        queue_name=in.getStringExtra(TAG_QUEUE_NAME);
		prefs = this.getSharedPreferences("com.qexpress.mike",
				Context.MODE_PRIVATE);
		myMobileNumber = prefs.getString("MY_MOBILE_NUMBER", "null");
		timerHandler.postDelayed(timerRunnable, 0);
		buttonGetMeIn = (Button) findViewById(R.id.bGetMeInOrQuit);
		buttonGetMeIn.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
					myMobileNumber = prefs.getString("MY_MOBILE_NUMBER", "null");
					if (!myMobileNumber.equals("null")){
						onClickGetMeIn();
					}
					else{
						Intent intent = new Intent(DisplayQueueStatus.this,
								EnterMobileNumber.class);
						startActivity(intent);
					}
			}

			private void onClickGetMeIn() {
				// TODO Auto-generated method stub
				Intent intent = new Intent(DisplayQueueStatus.this,
						GetInLine.class); 
				intent.putExtra(TAG_SITE_KEY, site_key);
				startActivityForResult(intent,GET_IN_LINE); 
			}
		});

		organizationName = (TextView) findViewById(R.id.tvOrganizationName);
		queueName = (TextView) findViewById(R.id.tvQueueName);
		qlastOrTicketLabel = (TextView) findViewById(R.id.tvLastInLine);
		qlastOrTicketNumber = (TextView) findViewById(R.id.tvLastInLineNumber);
		qout = (TextView) findViewById(R.id.tvNowInService);
		qoutNumber = (TextView) findViewById(R.id.tvNowInServiceNumber);
		qeta = (TextView) findViewById(R.id.tvExpWaitTime); 
		qetaTime = (TextView) findViewById(R.id.tvExpWaitTimeTime); 
		//openingHours=(TextView) findViewById(R.id.tvOpeningHours);
		
		organizationName.setText("");
		queueName.setText("");
		qlastOrTicketLabel.setText("");
		qeta.setText("");
		qout.setVisibility(View.INVISIBLE);
		//openingHours.setText("");
	}
}
