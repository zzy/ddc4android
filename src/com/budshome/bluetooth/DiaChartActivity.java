package com.hmsoft.bluetooth.le;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetooth.le.R;
import com.materialmenu.MaterialMenuDrawable;
import com.materialmenu.MaterialMenuDrawable.Stroke;
import com.materialmenu.MaterialMenuIcon;
public class DiaChartActivity extends FragmentActivity {
	DialChart03View chart = null;
	private final static String TAG = DiaChartActivity.class.getSimpleName();
	private boolean mConnected = false;
	public static final String BTDEVICE_ACTION_PAIRING_REQUEST =
            "android.bluetooth.device.action.PAIRING_REQUEST";
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String EXTRAS_DEVICE_PASSWORD = "DEVICE_PASSWORD";
    
    private BluetoothLeService mBluetoothLeService;
    
    private boolean isFirst = true;	//是否时刚打开app
    private float staticRange = 0; 
    
	Button btnOne;
	Button btnTwo;
	Button btnThree;
	
	TextView battery;

	TextView rangeNow;
	//蓝牙地址
    private String mDeviceAddress;
    
    //灯光状态
    
    private boolean lightState = false;
    
    /** DrawerLayout */
	private DrawerLayout mDrawerLayout;
	/** 左边栏菜单 */
	private ListView mMenuListView;
	/** 右边栏 */
	private RelativeLayout right_drawer;
	/** 菜单列表 */
	private String[] mMenuTitles;
	/** Material Design风格 */
	private MaterialMenuIcon mMaterialMenuIcon;
	/** 菜单打开/关闭状态 */
	private boolean isDirection_left = false;
	/** 右边栏打开/关闭状态 */
	private boolean isDirection_right = false;
	private View showView;
	
	private ProgressBar pBar;
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
    int iv = 0;
    List<Integer> speedint = new ArrayList<Integer>();
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
            	try {
	            	String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
	            	String returnData = "";
	            	int speedmean = 0;
	            	float nowRange = 0;	//当前里程
	            	if (data != null) {	//速度表盘数据填充
	            		 String reg = "FA03";	//蓝牙主动回传数据
	            		 String regFlag = "FA00";
	            	        Matcher m = Pattern.compile(reg).matcher(data);
	            	        Matcher mFlag = Pattern.compile(regFlag).matcher(data);
	            	        while (m.find()) {
	            	        	returnData = data;
	            	        }
	            	        while (mFlag.find()) {	//唯一标示
	            	        	if(COMMON.valiDate(data)) {	//校验
		            	        	String strNo1 = data.substring(4,10);	//出厂编码
		            	        	String strNo2 = data.substring(10,14);	//唯一标识
		            	        	int no1 = Integer.parseInt(strNo1,16);
		            	        	setTitle("X-Ebike-20"+no1+"-"+strNo2);
	            	        	}
	            	        }
	            		if(!returnData.equals("")){
	            			if(COMMON.valiDate(data)) {	//校验
			            		String strSpeed = data.substring(6,8);	//速度
			            		String strGears = data.substring(4,6);	//档位
			            		String strElectricity = data.substring(8,10);	//电量
			            		String strRange = data.substring(10,18);	    //里程
			            		int speed = Integer.parseInt(strSpeed,16);
			            		int gears = Integer.parseInt(strGears,16);
			            		int electricity = Integer.parseInt(strElectricity,16);
			            		int range = Integer.parseInt(strRange,16);
			            		int max = 30;
			    			    int min = 0;
			    			  
			    			    //里程
			    				DecimalFormat df = new DecimalFormat("0.0");//格式化小数
			    				if(isFirst) {
				    				staticRange = (float)range/1000;
				    				isFirst = false;
			    				}
			    				nowRange = (float)range/1000;
			    				rangeNow.setText("当前里程："+df.format(nowRange - staticRange)+"KM");
			    				
			    			    
		    			    	int p = speed%(max-min+1) + min;					
			    				float pf = p / 40f;
			    				chart.setCurrentStatus(pf,nowRange);
			    			    chart.invalidate();
			    			    
			    			    //电量显示
			    			    battery.setText(String.valueOf(electricity)+"%");
			    			    if(electricity < 30) {
			    			    	battery.setBackground(getResources().getDrawable(R.drawable.battery10));
			    			    }else if(electricity >=30 && electricity < 50 ) {
			    			    	battery.setBackground(getResources().getDrawable(R.drawable.battery30));
			    			    }else if (electricity >=50 && electricity < 70) {
			    			    	battery.setBackground(getResources().getDrawable(R.drawable.battery50));
			    			    }else if (electricity >=70 && electricity < 90) {
			    			    	battery.setBackground(getResources().getDrawable(R.drawable.battery70));
			    			    }else if (electricity >=90) {
			    			    	battery.setBackground(getResources().getDrawable(R.drawable.battery90));
			    			    }
			    			    
			    				if(1 == gears){
			    					btnOne.setBackground(getResources().getDrawable(R.drawable.select));
			    					btnTwo.setBackground(getResources().getDrawable(R.drawable.unselect));
			    					btnThree.setBackground(getResources().getDrawable(R.drawable.unselect));
			    				}else if(2 == gears){
			    					btnTwo.setBackground(getResources().getDrawable(R.drawable.select));
			    					btnThree.setBackground(getResources().getDrawable(R.drawable.unselect));
			    					btnOne.setBackground(getResources().getDrawable(R.drawable.unselect));
			    				}else{
			    					btnThree.setBackground(getResources().getDrawable(R.drawable.select));
			    					btnTwo.setBackground(getResources().getDrawable(R.drawable.unselect));
			    					btnOne.setBackground(getResources().getDrawable(R.drawable.unselect));
			    				}
	            			}
	            		}
	                }
            	} catch (Exception e) {}
            }
        }
    };

    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dial_chart);
		
		if(mConnected == false) {
			setTitle("X-Ebike-离线模式");
		}
		chart = (DialChart03View)findViewById(R.id.circle_view); 

		rangeNow = (TextView)findViewById(R.id.rangeNow);
		
		btnOne = (Button) this.findViewById(R.id.btnOne);
		btnTwo = (Button) this.findViewById(R.id.btnTwo);
		btnThree = (Button) this.findViewById(R.id.btnThree);
		battery = (TextView) this.findViewById(R.id.battery);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        
        Intent intent = getIntent();
        mDeviceAddress = intent.getStringExtra("serviceAddress");

        btnOne.setOnClickListener(onClickListener);
        btnTwo.setOnClickListener(onClickListener);
        btnThree.setOnClickListener(onClickListener);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mMenuListView = (ListView) findViewById(R.id.left_drawer);
		this.showView = mMenuListView;

		// 初始化菜单列表
		mMenuTitles = getResources().getStringArray(R.array.menu_array);
		mMenuListView.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, mMenuTitles));
		mMenuListView.setOnItemClickListener(new DrawerItemClickListener());

		// 设置抽屉打开时，主要内容区被自定义阴影覆盖
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// 设置ActionBar可见，并且切换菜单和内容视图
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mMaterialMenuIcon = new MaterialMenuIcon(this, Color.WHITE, Stroke.THIN);
		mDrawerLayout.setDrawerListener(new DrawerLayoutStateListener());

		if (savedInstanceState == null) {
			//selectItem(0);
		}
		
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event)  
    {  
        if (keyCode == KeyEvent.KEYCODE_BACK )  
        {  
        	// 创建退出对话框  
            AlertDialog isExit = new AlertDialog.Builder(this).create();  
            // 设置对话框标题  
            isExit.setTitle("系统提示");  
            // 设置对话框消息  
            isExit.setMessage("确定要退出吗");  
            // 添加选择按钮并注册监听  
            isExit.setButton("确定", listener);  
            isExit.setButton2("取消", listener);  
            // 显示对话框  
            isExit.show();  
        }  
          
        return false;  
    }
	
	/**监听对话框里面的button点击事件*/  
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()  
    {  
        public void onClick(DialogInterface dialog, int which)  
        {  
            switch (which)  
            {  
            case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序  
            	finish();  
                break;  
            case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框  
                break;  
            default:  
                break;  
            }  
        }  
    };
    
	//当页面加载完成
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
	// TODO Auto-generated method stub
	super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			if(mDeviceAddress != null) {
				//查询机器唯一标识
				mBluetoothLeService.WriteValue("FA00000000FA");
			}
		}
	}
	
	OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(mDeviceAddress != null ) {
				switch (v.getId()) {
				case R.id.btnOne:
					mBluetoothLeService.WriteValue("FA02010000F9");
					break;
				case R.id.btnTwo:
					mBluetoothLeService.WriteValue("FA02020000FA");
					break;
				case R.id.btnThree:
					mBluetoothLeService.WriteValue("FA02030000FB");
					break;
				default:
					break;
				}
			}
		}
	}; 
	
	@Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
       /*if (mBluetoothLeService != null) {
        	
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*unregisterReceiver(mGattUpdateReceiver);
        unbindService(mServiceConnection);*/
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBluetoothLeService != null)
        {
        	mBluetoothLeService.close();
        	mBluetoothLeService = null;
        }
        Log.d(TAG, "We are in destroy");
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
	 
	//灯光
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.light, menu);
        if (lightState == false) {
            menu.findItem(R.id.lightopen).setVisible(true);
            menu.findItem(R.id.lightclose).setVisible(false);
        } else {
        	menu.findItem(R.id.lightopen).setVisible(false);
            menu.findItem(R.id.lightclose).setVisible(true);
        }
        return true;
    }

	   
	    
	    /**
		 * ListView上的Item点击事件
		 * 
		 */
		private class DrawerItemClickListener implements
				ListView.OnItemClickListener {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				selectItem(position);
			}
		}

		/**
		 * DrawerLayout状态变化监听
		 */
		private class DrawerLayoutStateListener extends
				DrawerLayout.SimpleDrawerListener {
			/**
			 * 当导航菜单滑动的时候被执行
			 */
			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				showView = drawerView;
				if (drawerView == mMenuListView) {// 根据isDirection_left决定执行动画
					mMaterialMenuIcon.setTransformationOffset(
							MaterialMenuDrawable.AnimationState.BURGER_ARROW,
							isDirection_left ? 2 - slideOffset : slideOffset);
				} 
			}

			/**
			 * 当导航菜单打开时执行
			 */
			@Override
			public void onDrawerOpened(android.view.View drawerView) {
				if (drawerView == mMenuListView) {
					isDirection_left = true;
				} 
			}

			/**
			 * 当导航菜单关闭时执行
			 */
			@Override
			public void onDrawerClosed(android.view.View drawerView) {
				if (drawerView == mMenuListView) {
					isDirection_left = false;
				} 
			}
		}

		/**
		 * 切换主视图区域的Fragment
		 * 
		 * @param position
		 */
		private void selectItem(int position) {
			Fragment fragment = new ContentFragment();
			Bundle args = new Bundle();
			switch (position) {
			case 0:
				args.putString("key", mMenuTitles[position]);
				break;
			case 1:
				args.putString("key", mMenuTitles[position]);
				break;
			case 2:
				args.putString("key", mMenuTitles[position]);
				break;
			case 3:
				args.putString("key", mMenuTitles[position]);
				break;
			case 4:
				args.putString("key", mMenuTitles[position]);
				break;
			default:
				break;
			}
			/*fragment.setArguments(args); // FragmentActivity将点击的菜单列表标题传递给Fragment
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, fragment).commit();*/
			
			if(args.getString("key").equals("定位导航")) {
				Intent intenta = new Intent(DiaChartActivity.this, BaiduMapActivity.class);
	        	startActivity(intenta);
			}else if(args.getString("key").equals("车身自检")) {
				if(mDeviceAddress != null){
					Intent intenta = new Intent(DiaChartActivity.this, DeviceControlActivity.class);
		        	startActivity(intenta);
				}else{
					Toast.makeText(DiaChartActivity.this, "请先连接车辆!", Toast.LENGTH_SHORT).show();
				}
				
			}else if(args.getString("key").equals("密码修改")) {
				if(mDeviceAddress != null){
					Intent intenta = new Intent(DiaChartActivity.this, PwdUpdateActivity.class);
		        	startActivity(intenta);
				}else{
					Toast.makeText(DiaChartActivity.this, "请先连接车辆!", Toast.LENGTH_SHORT).show();
				}
			}else if(args.getString("key").equals("使用帮助")) {
				Intent intenta = new Intent(DiaChartActivity.this, UseHelpActivity.class);
	        	startActivity(intenta);
			}else if(args.getString("key").equals("连接车辆")) {
				if( mDeviceAddress == null ) {
					Intent intenta = new Intent(DiaChartActivity.this, DeviceScanActivity.class);
		        	startActivity(intenta);
		        	finish();
				}else {
					Toast.makeText(DiaChartActivity.this, "已经连接车辆，无需再连接!", Toast.LENGTH_SHORT).show();
				}
			}
				 

			// 更新选择后的item和title，然后关闭菜单
			mMenuListView.setItemChecked(position, false);
			//setTitle(mMenuTitles[position]);
			mDrawerLayout.closeDrawer(mMenuListView);
		}

		/**
		 * 点击ActionBar上菜单
		 */
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			int id = item.getItemId();
			switch (id) {
			case android.R.id.home:
				if (showView == mMenuListView) {
					if (!isDirection_left) { // 左边栏菜单关闭时，打开
						mDrawerLayout.openDrawer(mMenuListView);
					} else {// 左边栏菜单打开时，关闭
						mDrawerLayout.closeDrawer(mMenuListView);
					}
				} else if (showView == right_drawer) {
					if (!isDirection_right) {// 右边栏关闭时，打开
						mDrawerLayout.openDrawer(right_drawer);
					} else {// 右边栏打开时，关闭
						mDrawerLayout.closeDrawer(right_drawer);
					}
				}
				break;
			case R.id.lightopen:
				if( mDeviceAddress != null ) {
					if(lightState == false) {
						mBluetoothLeService.WriteValue("FA10010000EE");
						item.setIcon(getResources().getDrawable(R.drawable.lightclose));
						lightState = true;
					}else {
						mBluetoothLeService.WriteValue("FA10000000EA");
						item.setIcon(getResources().getDrawable(R.drawable.lightopen));
						lightState = false;
					}
				}else {
					Toast.makeText(DiaChartActivity.this, "请先连接车辆!", Toast.LENGTH_SHORT).show();
				}	
				break;
			default:
				break;
			}
			return super.onOptionsItemSelected(item);
		}

		/**
		 * 根据onPostCreate回调的状态，还原对应的icon state
		 */
		@Override
		protected void onPostCreate(Bundle savedInstanceState) {
			super.onPostCreate(savedInstanceState);
			mMaterialMenuIcon.syncState(savedInstanceState);
		}

		/**
		 * 根据onSaveInstanceState回调的状态，保存当前icon state
		 */
		@Override
		protected void onSaveInstanceState(Bundle outState) {
			mMaterialMenuIcon.onSaveInstanceState(outState);
			super.onSaveInstanceState(outState);
		}
}
