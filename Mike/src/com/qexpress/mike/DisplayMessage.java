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

public class DisplayMessage extends Activity{
	Button bOk,bCancel;
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
	        String response = in.getStringExtra("MESSAGE");
			
	        prefs = this.getSharedPreferences("com.qexpress.mike",
					Context.MODE_PRIVATE);
			myMobileNumber = prefs.getString("MY_MOBILE_NUMBER", "null");
			message=(TextView)findViewById(R.id.message);
			message.setText(response);
			bOk=(Button)findViewById(R.id.ok);
			bCancel=(Button)findViewById(R.id.cancel);
			bCancel.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent=new Intent();  
                    setResult(RESULT_CANCELED,intent);
					DisplayMessage.this.finish();
				}
			});
		}
		
}
