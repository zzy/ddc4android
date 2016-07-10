package com.moershizhi.ddc;

import com.moershizhi.ddc.util.CONSTANT;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;

public class MainActivity extends Activity {

	private final String TAG = getClass().getName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);

		// 屏幕大小、密度，以及分辨率
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		CONSTANT.heightPixels = dm.heightPixels;
		CONSTANT.widthPixels = dm.widthPixels;

		CONSTANT.density = dm.density;

		CONSTANT.screenHeight = (int) (CONSTANT.heightPixels * CONSTANT.density); // + 0.5);
		CONSTANT.screenWidth = (int) (CONSTANT.widthPixels * CONSTANT.density); // + 0.5);
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(new Intent(MainActivity.this, HomepageActivity.class));
				overridePendingTransition(R.anim.fade, R.anim.hold);
				MainActivity.this.finish();

			}
		}, CONSTANT.DELAYED_TIME);
	}
}
