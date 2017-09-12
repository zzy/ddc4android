package com.hmsoft.bluetooth.le;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bluetooth.le.R;

public class PwdUpdateActivity extends Activity{

	EditText newpwd;
	EditText renewpwd;
	Button btnpwd;
	TextView errormsg;
	
	private boolean mConnected = false;
	private BluetoothLeService mBluetoothLeService;
	
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //mBluetoothLeService = null;
        }
    };
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pwdupdate);
		setTitle("密码修改");
		
		newpwd = (EditText)findViewById(R.id.newpwd);
		renewpwd = (EditText)findViewById(R.id.renewpwd);
		errormsg = (TextView)findViewById(R.id.errormsg);
		btnpwd = (Button)findViewById(R.id.pwdupdade_button);
		
		btnpwd.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if((newpwd.getText().toString()).equals(renewpwd.getText().toString())) {
					mBluetoothLeService.WriteValue(pwdToHexString(renewpwd.getText().toString()));
					//errormsg.setText(pwdToHexString(renewpwd.getText().toString()));
				}else {
					renewpwd.setText("");
					errormsg.setText("两次密码不同，请重新输入！");
				}
			}
		});
		
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}
	
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {  //连接成功
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) { //断开连接
                mConnected = false;
            }else if(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) //可以开始干活了
            {
            	mConnected = true;
            }else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) { //收到数据
            	
            	String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
            	String returnData = "";
            	if (data != null) {	//速度表盘数据填充
            		
            		 String reg = "FA05";	//蓝牙主动回传数据
            	        Matcher m = Pattern.compile(reg).matcher(data);
            	        while (m.find()) {
            	        	returnData = data;
            	        }
            		if(!returnData.equals("")){
	            			if(COMMON.valiDate(data)) {	//校验
	            			DeviceScanActivity.saveData(PwdUpdateActivity.this,renewpwd.getText().toString());
		            		AlertDialog.Builder builder = new AlertDialog.Builder(PwdUpdateActivity.this);
		        			builder.setMessage("您修改的密码为："+renewpwd.getText().toString()+"请妥善保管!");
		        			builder.setTitle("提示");
		        			builder.setPositiveButton("关闭", new OnClickListener() {
		        				@Override
		        				public void onClick(DialogInterface dialog, int which) {
		        					dialog.dismiss();
		        					finish();
		        				}
		        			});
	
		        			builder.create().show();
	            		}
            		}
                }
            }
        }
    };
    
	private static IntentFilter makeGattUpdateIntentFilter() {                        //注册接收的事件
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothDevice.ACTION_UUID);
        return intentFilter;
    }
	
	/*密码异或-------------------------------------------------*/
    private static String xor(String strHex_X,String strHex_Y){   
        //将x、y转成二进制形式   
        String anotherBinary=Integer.toBinaryString(Integer.valueOf(strHex_X,16));   
        String thisBinary=Integer.toBinaryString(Integer.valueOf(strHex_Y,16));   
        String result = "";   
        //判断是否为8位二进制，否则左补零   
        if(anotherBinary.length() != 8){   
        for (int i = anotherBinary.length(); i <8; i++) {   
                anotherBinary = "0"+anotherBinary;   
            }   
        }   
        if(thisBinary.length() != 8){   
        for (int i = thisBinary.length(); i <8; i++) {   
                thisBinary = "0"+thisBinary;   
            }   
        }   
        //异或运算   
        for(int i=0;i<anotherBinary.length();i++){   
        //如果相同位置数相同，则补0，否则补1   
                if(thisBinary.charAt(i)==anotherBinary.charAt(i))   
                    result+="0";   
                else{   
                    result+="1";   
                }   
            }  
    
        return Integer.toHexString(Integer.parseInt(result, 2));   
    }
    
    public static String checkcode_0007(String para){  
        String[] dateArr = new String[5];  
        try {  
            dateArr[0] = para.substring(0, 2);  
            dateArr[1] = para.substring(2, 4);  
            dateArr[2] = para.substring(4, 6);  
            dateArr[3] = para.substring(6, 8);  
            dateArr[4] = para.substring(8, 10);    
       } catch (Exception e) {  
           // TODO: handle exception  
       }  
       String code = "";  
       for (int i = 0; i < dateArr.length-1; i++) {  
           if(i == 0){  
               code = xor(dateArr[i], dateArr[i+1]);  
           }else{  
        	   code = xor(code, dateArr[i+1]);  
           }  
       }  
       return code;  
    }
    
    //字符串转16进制
    public static String strToHex(String str) {
    	String hexStr = "";
    	try {
    		hexStr = Integer.toHexString(Integer.valueOf(str));
        	if(hexStr.length() == 1) {
        		hexStr = "00000" + hexStr;
        	}else if(hexStr.length() == 2) {
        		hexStr = "0000" + hexStr;
        	}else if(hexStr.length() == 3) {
        		hexStr = "000" + hexStr;
        	}else if(hexStr.length() == 4) {
        		hexStr = "00" + hexStr;
        	}else if(hexStr.length() == 5) {
        		hexStr = "0" + hexStr;
        	}
    	}catch(Exception e) {}
    	return hexStr;
    }
    
    //密码转换为6为16进制
    public static String pwdToHexString(String str) {
    	String hexStr = "";
    	String pwdStr = "";
    	try {
    		pwdStr = "FA05" + strToHex(str);
    		hexStr = pwdStr + checkcode_0007(pwdStr);
    	}catch(Exception e) {}
    	return hexStr.toUpperCase();
    }
}
