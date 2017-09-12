package com.hmsoft.bluetooth.le;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.example.bluetooth.le.R;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivity extends ListActivity {
	private final static String TAG = DeviceScanActivity.class.getSimpleName();
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private ListView listView;

    //是否查找到设备
    private boolean isFind = false;
    
    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 1500;
    public static final String EXTRAS_DEVICE_PASSWORD = "DEVICE_PASSWORD";
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    
    //是否连接
    private boolean mConnected = false;
    private BluetoothLeService mBluetoothLeService;
    
    //是否跳转
    boolean flag = true;
    
    //是否发送唯一标示查询
    private boolean isValiData = false;
    
    //弹出蓝牙列表
    private List<String> list = new ArrayList<String>();
    private List<BluetoothDevice> listWithoutDup ;
	private AlertDialog.Builder builder;
	private AlertDialog alertDialog;
	
	//蓝牙列表
	private List<BluetoothDevice> deviceLists = new ArrayList<BluetoothDevice>();
	
	//是否填充列表
	private boolean isFill = true;
    //蓝牙地址
    private String mDeviceAddress;
    //密码
    private String bloothpassword;
    //唯一标识
    private String textNo;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_list);
        getActionBar().setTitle(R.string.title_devices);
//        listView = (ListView) findViewById(R.id.);
        
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        setListAdapter(mLeDeviceListAdapter);
        
       //获取本地蓝牙适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //判断是否硬件支持蓝牙
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "本地蓝牙不可用", Toast.LENGTH_SHORT).show();
            //退出应用
            finish();
        }

        //判断是否打开蓝牙
        if (!mBluetoothAdapter.isEnabled()) {
            //弹出对话框提示用户是后打开
            //Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //startActivityForResult(intent, REQUEST_ENABLE_BT);
            //不做提示，强行打开
            mBluetoothAdapter.enable();
        }
        
        try{
    		Thread.sleep(1000);
    		}catch(Exception e){
    		}
        
        //搜索蓝牙
        scanLeDevice(true);
        
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        Log.d(TAG, "Try to bindService=" + bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE));
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);	
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
            	mBluetoothLeService.close();
                mLeDeviceListAdapter.clear();
                mLeDeviceListAdapter = new LeDeviceListAdapter();
                setListAdapter(mLeDeviceListAdapter);
                scanLeDevice(true);
                isValiData = false;
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.mBluetoothLeService.connect(mDeviceAddress);
        
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        //mLeDeviceListAdapter.clear();
    } 

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                	
                	if(mScanning)
                	{
                		mScanning = false;
                		mBluetoothAdapter.stopLeScan(mLeScanCallback);
                		invalidateOptionsMenu();
                	}
                	
                	try{
	                	listWithoutDup = new ArrayList<BluetoothDevice>(new HashSet<BluetoothDevice>(deviceLists));
	                	System.out.println("-------------------------------------------------------------"+listWithoutDup);
	                	if(listWithoutDup.size() > 1) {
	                		ShowDialog();
	                	}else if(listWithoutDup.size() == 1){
	                   		mLeDeviceListAdapter.addDevice(listWithoutDup.get(0));
	                   		mHandler.sendEmptyMessage(1);
	                   		deviceLists.clear();
	                	}
	                	
	                	if(isFind == false) {
	                		Toast.makeText(DeviceScanActivity.this, "未找到设备，进入离线模式!", Toast.LENGTH_SHORT).show();
	                    	try{
	                    		//Thread.sleep(1000);
	                    		Intent intenta = new Intent(DeviceScanActivity.this, DiaChartActivity.class);
	            	        	startActivity(intenta);
	            	        	finish();
	                    		}catch(Exception e){
	                    		}
	                	}
                	}catch(Exception e){}
                }
            }, 
            SCAN_PERIOD);
            mScanning = true;
            //F000E0FF-0451-4000-B000-000000000000
            mLeDeviceListAdapter.clear();
            mHandler.sendEmptyMessage(1);
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }
    
    

    // Adapter for holding devices found through scanning.
    ViewHolder viewHolder;
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;
        
        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = DeviceScanActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }
        
        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                
                viewHolder = new ViewHolder();
                
                viewHolder.bloothName = (EditText) view.findViewById(R.id.login_blooth_name);
                viewHolder.bloothName.setKeyListener(null);
                viewHolder.devicepassword = (EditText) view.findViewById(R.id.password_edit);
                viewHolder.bloothconnect = (Button) view.findViewById(R.id.signin_button);
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.deviceaddress);
                viewHolder.img = (ImageView) view.findViewById(R.id.img);
                viewHolder.pwderror = (TextView)view.findViewById(R.id.pwderror);
                view.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) view.getTag();
            }

            DisplayMetrics metric = new DisplayMetrics();  
            getWindowManager().getDefaultDisplay().getMetrics(metric); 
            viewHolder.img.getLayoutParams().height = (int) (0.8*metric.heightPixels);

            if(deviceName != null && deviceName.length() > 0 && deviceName.equals("XBTX1")) {
            	mBluetoothLeService.connect(device.getAddress());
            	viewHolder.deviceAddress.setText(device.getAddress());
            	viewHolder.bloothName.setText("查找设备中...");
            }
            
            viewHolder.bloothconnect.setOnClickListener(new OnClickListener() {
				
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					
					bloothpassword = viewHolder.devicepassword.getText().toString();
					mDeviceAddress = viewHolder.deviceAddress.getText().toString();
					if(mConnected == false) {
						mBluetoothLeService.connect(viewHolder.deviceAddress.getText().toString());
					}else{
						mBluetoothLeService.WriteValue(pwdToHexString(bloothpassword));
					}
			        
			        if (mScanning) {
			            mBluetoothAdapter.stopLeScan(mLeScanCallback);
			            mScanning = false;
			        }
				        
				}
			});
            
            viewHolder.devicepassword.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
					viewHolder.bloothconnect.setEnabled(true);
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
						int arg3) {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void afterTextChanged(Editable arg0) {
					// TODO Auto-generated method stub
					if(viewHolder.devicepassword.getText().toString().equals("")) {
						viewHolder.bloothconnect.setEnabled(false);
					}
				}
			});
            return view;
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                	if(null != device) {
                		try{
                			if(device.getName().equals("XBTX1")){
                				//list.add(device.getAddress());
                				deviceLists.add(device);
                				 if(isFill) {
		                    		//mLeDeviceListAdapter.addDevice(device);
		                    		//mHandler.sendEmptyMessage(1);
		                    		isFind = true;
		                    		isFill = false;
                				 }
	                    	}
                				 System.out.println("============================================="+deviceLists);
                		}catch(Exception e){}
                	}
                }
            });
        }
        
    };

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView bloothName;
        TextView pwderror;
        EditText devicepassword;
        Button bloothconnect;
        ImageView img;
    }
    
	// Hander
	public final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1: // Notify change
				mLeDeviceListAdapter.notifyDataSetChanged();
				break;
			}
		}
	};
	
	/*蓝牙链接-------------------------------------------------*/
	
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
            	//mBluetoothLeService.WriteValue("FA00000000FA");
            	if(isValiData == false) {
    	        	try{
                		Thread.sleep(1000);
                		}catch(Exception e){}
	        		mBluetoothLeService.WriteValue("FA00000000FA");
	        		isValiData = true;
	        	}
            }else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) { //收到数据
            	try{
	            	String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
	            	if (data != null) {	//速度表盘数据填充
	            		 String regPwdError = "FA0100";
	            		 String regPwdTrue = "FA0101";
	            		 String regFlag = "FA00";
	            	        Matcher mError = Pattern.compile(regPwdError).matcher(data);
	             	        Matcher mTrue = Pattern.compile(regPwdTrue).matcher(data);
	             	       Matcher mFlag = Pattern.compile(regFlag).matcher(data);
	            	        while (mError.find()) {
	            	        	// 创建Dialog 
	            	        	if(COMMON.valiDate(data)) {	//校验
		            	        	try{
		            	        		viewHolder.pwderror.setText("密码错误，请重新输入！");
		            	        	}catch(Exception e){
		            	        		e.printStackTrace();
		            	        	}
	            	        	}
	            	        }
	            	        while (mTrue.find()) {	//密码正确
	            	        	if(COMMON.valiDate(data)) {	//校验
		            	        	if(flag) {
		            	        		Intent intenta = new Intent(DeviceScanActivity.this, DiaChartActivity.class);
			            	        	//保存密码
			            	        	saveData(DeviceScanActivity.this,bloothpassword);
			            	        	intenta.putExtra("serviceAddress", mDeviceAddress);
			            	        	startActivity(intenta);
			            	        	finish();
			            	        	flag = false;
		            	        	}
	            	        	}
	            	        } 
	            	        while(mFlag.find()) {	//唯一标示
	            	        	if(COMMON.valiDate(data)) {	//校验
	            	        		String strNo1 = data.substring(4,10);	//出厂编码
		            	        	String strNo2 = data.substring(10,14);	//唯一标识
		            	        	int no1 = Integer.parseInt(strNo1,16);
		            	        	
		            	        	viewHolder.bloothName.setText("20"+no1+"-"+strNo2);
		            	        	getActionBar().setTitle("连接成功");
		            	        	scanLeDevice(false);
		            	        	///密码填充
		                    		viewHolder.devicepassword.setText(loadData(DeviceScanActivity.this));
	            	        	}
	            	        }
	                }
            	} catch (Exception e) {}
            }
        }
    };
    
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            Log.e(TAG, "mBluetoothLeService is okay");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
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
	
    //弹出蓝牙选择
	public void ShowDialog() {
		Context context = DeviceScanActivity.this;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.formcommonlist, null);
        ListView myListView = (ListView) layout.findViewById(R.id.formcustomspinner_list);
        listWithoutDup = new ArrayList<BluetoothDevice>(new HashSet<BluetoothDevice>(deviceLists));	//去重
	    MyAdapter adapter = new MyAdapter(context, listWithoutDup);
        myListView.setAdapter(adapter);
        myListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int positon, long id) {
				mLeDeviceListAdapter.addDevice(listWithoutDup.get(positon));
        		mHandler.sendEmptyMessage(1);
        		deviceLists.clear();
				if (alertDialog != null) {
					alertDialog.dismiss();
				}
			}
        });
        builder = new AlertDialog.Builder(context);
        builder.setView(layout);
        alertDialog = builder.create();
        alertDialog.show();
	}
    
   class MyAdapter extends BaseAdapter {
		private List<BluetoothDevice> mlist;
		private Context mContext;

		public MyAdapter(Context context, List<BluetoothDevice> list) {
			listWithoutDup = new ArrayList<BluetoothDevice>(new HashSet<BluetoothDevice>(list));
			this.mContext = context;
			this.mlist = listWithoutDup;
		}

		@Override
		public int getCount() {
			return mlist.size();
		}

		@Override
		public Object getItem(int position) {

			return mlist.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Person person = null;
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(mContext);
				convertView = inflater.inflate(R.layout.rtu_item,null);
	            person = new Person();
	            person.name = (TextView)convertView.findViewById(R.id.tv_name);
	            convertView.setTag(person);
			}else{
				person = (Person)convertView.getTag();
			}
			listWithoutDup = new ArrayList<BluetoothDevice>(new HashSet<BluetoothDevice>(deviceLists));	//去重
			person.name.setText(listWithoutDup.get(position).toString());
			return convertView;
		}
		class Person{
			TextView name;
		}
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
    		pwdStr = "FA01" + strToHex(str);
    		hexStr = pwdStr + checkcode_0007(pwdStr);
    	}catch(Exception e) {}
    	return hexStr.toUpperCase();
    }
    
    //读取密码
    private String loadData(Context context) {
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        //Toast.makeText(this, sp.getString("content", "").toString(), 0).show();
        return sp.getString("content", "").toString();
    }
    //保存密码
	public static void saveData(Context context,String string){
	    SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
	    Editor editor = sp.edit();
	    editor.putString("content", string);
	    editor.commit();
	}
}