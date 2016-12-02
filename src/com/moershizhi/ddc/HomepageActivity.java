package com.moershizhi.ddc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class HomepageActivity extends Activity {

	private final String TAG = getClass().getName();
	
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.homepage);
		

	}
	
	void setHomepage() {
		
	}
	
	void addListener() {
		
	}
	
}
