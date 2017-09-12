package com.hmsoft.bluetooth.le;

import java.io.File;
import java.net.URISyntaxException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.bluetooth.le.R;


/**
 * 此demo用来展示如何结合定位SDK实现定位，并使用MyLocationOverlay绘制定位位置 同时展示如何使用自定义图标绘制并点击时弹出泡泡
 */
public class BaiduMapActivity extends Activity{

    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    BitmapDescriptor mCurrentMarker;


    MapView mMapView;
    BaiduMap mBaiduMap;

    // UI相关
    OnCheckedChangeListener radioButtonListener;
    Button requestLocButton;
    Button startNav;
    EditText editAddr;
    boolean isFirstLoc = true; // 是否首次定位
    
    //手动定位
    private MapStatus mMapStatus;// 地图当前状态
	private MapStatusUpdate mMapStatusUpdate;// 地图将要变化成的状态
	private LatLng mCurrentLatLng;// 当前经纬度坐标
	public BDLocationListener myLocationListener = new MyLocationListenner();// 定位的回调接口
	
	//导航
	private static final String APP_FOLDER_NAME = "DeviceScanActivity";
	private double starLatitude;//纬度
	private double starLongitude;//经度
	String authinfo = null;
	 	
	private String cityStr;
	private String starAddressStr;
	private String endAddressStr;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        requestLocButton = (Button) findViewById(R.id.request);
        startNav = (Button) findViewById(R.id.startNav);
        editAddr = (EditText) findViewById(R.id.addRess);
        
        // 注册 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
       
        
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        
        location();     
        //定位
        requestLocButton.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				location();
				// 获得地图的当前状态的信息
				mMapStatus = new MapStatus.Builder().zoom(15)
						.target(mCurrentLatLng).build();
				// 设置地图将要变成的状态
				mMapStatusUpdate = MapStatusUpdateFactory
						.newMapStatus(mMapStatus);
				mBaiduMap.setMapStatus(mMapStatusUpdate);// 设置地图的变化
			}
		});
        
        //导航
        startNav.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				endAddressStr = editAddr.getText().toString();
				if(!editAddr.getText().toString().equals("")) {
					startNavi(starLatitude,starLongitude);
				}else {
					Toast.makeText(BaiduMapActivity.this, "请输入目的地!", Toast.LENGTH_SHORT).show();
				}
				
			}
		});        
        
    }

    /**
	 * 定位
	 */
	private void location() {
		// 设置mLocationClient数据,如是否打开GPS,使用LocationClientOption类.
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(3000);// 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
		option.setOpenGps(true);// 打开GPS
		mLocClient.setLocOption(option);
		mLocClient.registerLocationListener(myLocationListener);
		mLocClient.start();
	}
	
    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            // 获取服务器回传的当前经纬度
            starLatitude = location.getLatitude();
            starLongitude = location.getLongitude();
            cityStr = location.getCity();
            starAddressStr = location.getAddrStr();
            
            mCurrentLatLng = new LatLng(location.getLatitude(),
					location.getLongitude());
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(15);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        
        public void onReceivePoi(BDLocation poiLocation) {
        }
    }
    
    //验证是否安装百度地图
    private boolean isInstallByread(String packageName)  
    {  
        return new File("/data/data/" + packageName).exists();  
    }  
    /**
	 * 开始导航
	 * 
	 * @param view
	 */
	public void startNavi(double starLatitude, double starLongitude) {
		Intent intent = null;
		try {   
             //intent = Intent.getIntent("intent://map/marker?location="+starLatitude+","+starLongitude+"&title=我的位置&content="+endAddressStr+"&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");   
			intent = Intent.getIntent("intent://map/direction?origin=latlng:"+starLatitude+","+starLongitude+"|name:"+starAddressStr+"&destination="+endAddressStr+"&mode=driving&region="+cityStr+"&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end"); 
			
			if(isInstallByread("com.baidu.BaiduMap")){  
                        startActivity(intent); //启动调用  
                        Log.e("GasStation", "百度地图客户端已经安装") ;  
                }else{  
                         //Log.e("GasStation", "没有安装百度地图客户端") ;  
                	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        			builder.setMessage("您尚未安装百度地图app，请前往应用商店下载？");
        			builder.setTitle("提示");
        			builder.setPositiveButton("关闭", new OnClickListener() {
        				@Override
        				public void onClick(DialogInterface dialog, int which) {
        					dialog.dismiss();
        				}
        			});

        			builder.create().show();
                }  
        } catch (URISyntaxException e) {  
                e.printStackTrace();  
        }  
	} 

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

}
