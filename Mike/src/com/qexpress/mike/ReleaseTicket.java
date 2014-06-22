package com.qexpress.mike;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ReleaseTicket extends Activity{
	Button bOkRelease,bCancelRelease;
	String myMobileNumber,site_key;
	SharedPreferences prefs;
	TextView message;
	private static final String TAG_SITE_KEY = "site_key";
	
	public static final String TAG_WEB_SERVER = "http://www.qexpress.co.il/";
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);	
			setContentView(R.layout.dialog);
			
			// getting intent data 
	        Intent in = getIntent();
	        site_key = in.getStringExtra(TAG_SITE_KEY);
			
	        prefs = this.getSharedPreferences("com.qexpress.mike",
					Context.MODE_PRIVATE);
			myMobileNumber = prefs.getString("MY_MOBILE_NUMBER", "null");
			message=(TextView)findViewById(R.id.message);
			message.setText(R.string.confirm_cancel_ticket);
			bOkRelease=(Button)findViewById(R.id.ok);
			bCancelRelease=(Button)findViewById(R.id.cancel);
			bOkRelease.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) { 
					// TODO Auto-generated method stub
					AsyncHttpClient client = new AsyncHttpClient();
					RequestParams params = new RequestParams("mobile_number",myMobileNumber);
					params.put("site_key", site_key);
					client.post(TAG_WEB_SERVER+"remote/release_ticket.php", params,
							new AsyncHttpResponseHandler() {
								@Override
								public void onSuccess(String jsonStr) {
									Log.d("Qexpress", jsonStr);
								}

							});
					Toast.makeText(getApplicationContext(), "You have successfully released the ticket!", 
							Toast.LENGTH_LONG).show();
					ReleaseTicket.this.finish();
				}
			});
			bCancelRelease.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), "You are still in line!", 
							Toast.LENGTH_LONG).show();
					ReleaseTicket.this.finish();
				}
			});
		}
		
}
