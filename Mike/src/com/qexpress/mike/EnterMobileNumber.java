package com.qexpress.mike;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EnterMobileNumber extends Activity {
	Button bOk, bCancel;
	TextView message, alert;
	EditText mobileNumberInput, codeInput;
	int phase = 1;
	String mobileNum,randomCode;
	SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_enter_mobile_number);
		prefs = this.getSharedPreferences("com.qexpress.mike",
				Context.MODE_PRIVATE);
		bOk = (Button) findViewById(R.id.ok);
		bCancel = (Button) findViewById(R.id.cancel);
		message = (TextView) findViewById(R.id.tvMessage);
		alert = (TextView) findViewById(R.id.tvAlert);
		mobileNumberInput = (EditText) findViewById(R.id.etMobileNumber);
		codeInput = (EditText) findViewById(R.id.etCode);
		bOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (phase == 1) {

					// verify that mobile number is 10 digits long
					mobileNum = mobileNumberInput.getText().toString();
					int mobileNumLength = mobileNum.length();
					if (mobileNumLength < 10) {
						alert.setVisibility(TextView.VISIBLE);
						alert.setText("Mobile Number too short!");
					} else {
						alert.setVisibility(TextView.GONE);
						alert.setText(null);
						// verify that mobile number begins with "05"
						String mobilePrefix = mobileNum.substring(0, 2);
						if (!mobilePrefix.equals("05")) {
							alert.setVisibility(TextView.VISIBLE);
							alert.setText("Wrong mobile prefix!");
							mobileNumberInput.setText(null);
						} else {
							// correct mobile number format
							phase=2;
							mobileNumberInput.setText(null);
							mobileNumberInput.setVisibility(TextView.GONE);
							codeInput.setVisibility(TextView.VISIBLE);
							AsyncHttpClient client = new AsyncHttpClient();
							RequestParams params = new RequestParams(
									"mobile_number", mobileNum);
							client.post(
									"http://qexpress.co.il/send_random.php",
									params, new AsyncHttpResponseHandler() {
										@Override
										public void onSuccess(String response) {
											//codeInput.setText(response);
											randomCode=response;
										}
									});
							message.setText("A code has just been sent to you by text messgae. Please enter it here");
						}
					}
				}
				else{
					String Code=codeInput.getText().toString();
					if (Code.equals(randomCode)){
						prefs.edit().putString("MY_MOBILE_NUMBER",mobileNum).commit();
						Toast.makeText(getApplicationContext(), "Registration successful!", 
								Toast.LENGTH_LONG).show();
						EnterMobileNumber.this.finish();
					}
					else{
						Toast.makeText(getApplicationContext(), "no match!", 
								Toast.LENGTH_LONG).show();
					}
				}

			}
		});

		bCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EnterMobileNumber.this.finish();
			}
		});

	}

}
