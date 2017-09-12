package com.hmsoft.bluetooth.le;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.bluetooth.le.R;

public class DeviceControlActivity extends Activity {
	DialChart03View chart = null;
	private final static String TAG = DiaChartActivity.class.getSimpleName();
	private boolean mConnected = false;

    TextView tvMachineVal;
    TextView tvCellVal;
    TextView tvGaunVal;
    TextView tvBrakeVal;
    
    private BluetoothLeService mBluetoothLeService;
    
    Button btnStart;
    
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            Log.e(TAG, "mBluetoothLeService is okay");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //mBluetoothLeService = null;
        }
    };
    
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {  //连接成功
            	Log.e(TAG, "Only gatt, just wait");
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) { //断开连接
                mConnected = false;
            }else if(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) //可以开始干活了
            {
            	mConnected = true;
            }else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) { //收到数据
            	
            	String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
            	String returnData = "";
            	if (data != null) {	//速度表盘数据填充
            		 String reg = "FA0A";	//蓝牙主动回传数据
            	        Matcher m = Pattern.compile(reg).matcher(data);
            	        while (m.find()) {
            	        	returnData = data;
            	        }
            	        
            		if(!returnData.equals("")){
            			if(COMMON.valiDate(data)) {	//校验
		            		String strStatue = data.substring(4,6);	
		            		try{
		            				Thread.sleep(2000);
			            		if(strStatue.equals("00")) {
			            			tvMachineVal.setText("正常");
			            			tvCellVal.setText("正常");
			            			tvGaunVal.setText("正常");
			            			tvBrakeVal.setText("正常");
			            		}else if(strStatue.equals("01")) {
			            			tvMachineVal.setText("异常");
			            			tvMachineVal.setTextColor(Color.rgb(255, 0, 0));
			            			tvCellVal.setText("正常");
			            			tvGaunVal.setText("正常");
			            			tvBrakeVal.setText("正常");
			            		}else if(strStatue.equals("02")) {
			            			tvMachineVal.setText("正常");
			            			tvCellVal.setText("电压过高");
			            			tvCellVal.setTextColor(Color.rgb(255, 0, 0));
			            			tvGaunVal.setText("正常");
			            			tvBrakeVal.setText("正常");
			            		}else if(strStatue.equals("03")) {
			            			tvMachineVal.setText("正常");
			            			tvCellVal.setText("电压过低");
			            			tvCellVal.setTextColor(Color.rgb(255, 0, 0));
			            			tvGaunVal.setText("正常");
			            			tvBrakeVal.setText("正常");
			            		}else if(strStatue.equals("04")) {
			            			tvMachineVal.setText("正常");
			            			tvCellVal.setText("正常");
			            			tvGaunVal.setText("异常");
			            			tvGaunVal.setTextColor(Color.rgb(255, 0, 0));
			            			tvBrakeVal.setText("正常");
			            		}else if(strStatue.equals("05")) {
			            			tvMachineVal.setText("正常");
			            			tvCellVal.setText("正常");
			            			tvGaunVal.setText("正常");
			            			tvBrakeVal.setText("异常");
			            			tvBrakeVal.setTextColor(Color.rgb(255, 0, 0));
			            		}
			            	}catch(Exception e){}
            			}
            		}
                }
            }
        }
    };

    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.diagnosis);
		
		tvMachineVal = (TextView)findViewById(R.id.tvMachineVal);
	    tvCellVal = (TextView)findViewById(R.id.tvCellVal);
	    tvGaunVal = (TextView)findViewById(R.id.tvGaunVal);
	    tvBrakeVal = (TextView)findViewById(R.id.tvBrakeVal);
	    
		btnStart = (Button)findViewById(R.id.start_button);
                      
        btnStart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mBluetoothLeService.WriteValue("FA04000000FE");
				tvMachineVal.setText("等待...");
				tvMachineVal.setTextColor(Color.rgb(119, 136, 153));
    			tvCellVal.setText("等待...");
    			tvCellVal.setTextColor(Color.rgb(119, 136, 153));
    			tvGaunVal.setText("等待...");
    			tvGaunVal.setTextColor(Color.rgb(119, 136, 153));
    			tvBrakeVal.setText("等待...");
    			tvBrakeVal.setTextColor(Color.rgb(119, 136, 153));
			}
		});
        
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        //Log.d(TAG, "Try to bindService=" + bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE));
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}
	 
	
	@Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
    private static IntentFilter makeGattUpdateIntentFilter() {                        //注册接收的事件
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothDevice.ACTION_UUID);
        return intentFilter;
    }
}
