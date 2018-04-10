package com.lbs;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "AppCompatActivity.class";
    public LocationClient mlocationClient;
    public TextView positionText;
    public MapView mapview;
    private boolean isFirstLocate = true;
    public BaiduMap baiduMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mlocationClient = new LocationClient(getApplicationContext());
        mlocationClient.registerLocationListener(new MyLocationListener());

        SDKInitializer.initialize(getApplicationContext());//
        IntentFilter filter = new IntentFilter();
        filter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        filter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        registerReceiver(new baiDuBroadCast(),filter);

        setContentView(R.layout.activity_main);
      //  positionText = (TextView) findViewById(R.id.position_text);

        mapview = (MapView) findViewById(R.id.bmapView);

        baiduMap = mapview.getMap();//得到百度地图的实例

        //移动到我的位置
        baiduMap.setMyLocationEnabled(true);

        List<String> persionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(MainActivity.this,
            Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
              persionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            Log.d(TAG,"checkSelfPermission...........1..........");
        } if(ContextCompat.checkSelfPermission(MainActivity.this,
            Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
              persionList.add(Manifest.permission.READ_PHONE_STATE);
            Log.d(TAG,"checkSelfPermission...........2..........");
        } if(ContextCompat.checkSelfPermission(MainActivity.this,
             Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
             persionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            Log.d(TAG,"checkSelfPermission...........3..........");
        }if(!persionList.isEmpty()){
           String [] permissons = persionList.toArray(new String[persionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this,permissons,1);
            Log.d(TAG,"checkSelfPermission...........4..........");
        }else {
            requstLocatin();
            Log.d(TAG,"checkSelfPermission...........5..........");
        }

        Log.d(TAG,".--baidu map--------------------");
    }

    private void requstLocatin() {
        initLocation();
        mlocationClient.start();
        Log.d(TAG,"checkSelfPermission...........6..........");
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        Log.d(TAG,"checkSelfPermission...........9..........");
       option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        option.setIsNeedAddress(true);//获取详细信息，省市县
        mlocationClient.setLocOption(option);
        //mlocationClient.start();
    }



    @Override
    protected void onResume() {
        super.onResume();
        mapview.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapview.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"checkSelfPermission...........onDestroy.........");
        mlocationClient.stop();
        mapview.onDestroy();
        //移动未位置的清空
        baiduMap.setMyLocationEnabled(false);
    }
    LatLng  LL;
      private void navigateTo(BDLocation bdLocation){
         if (isFirstLocate){
            LL = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
             // 方法一  显示当前区域的地图位置
          MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(LL, 16.0f);
               baiduMap.animateMapStatus(update);
        //--------------------------------------------------------------
             // 方法二  显示当前区域的地图位置
       /*  MapStatus mMapStatus = new MapStatus.Builder()
                  .target(LL).zoom(14f).build();
          //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
          MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
          //改变地图状态
          baiduMap.animateMapStatus(mMapStatusUpdate);*/
  //-------------------------------------------------------------------
             //误差太大
        /* MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(LL);
             baiduMap.animateMapStatus(update);
             update = MapStatusUpdateFactory.zoomBy(14f);
             baiduMap.animateMapStatus(update);*/
             isFirstLocate = false;

         }
           //把自己的位置显示图标出来
          MyLocationData locData = new MyLocationData.Builder().latitude(bdLocation.getLatitude())
                  .longitude(bdLocation.getLongitude()).build();
          //设置图标在地图上的位置
          baiduMap.setMyLocationData(locData);

         // baiduMap.addOverlay()

          Log.d(TAG,"checkSelfPermission..........移动到我的位置.2........");
      }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            switch (requestCode){
                case 1:
                    Log.d(TAG,"checkSelfPermission...........7..........");
                    if (grantResults.length >0){
                        for(int result : grantResults){
                            if(result != PackageManager.PERMISSION_GRANTED){
                                toast("必须同意所有权限才能使用本程序。。。");
                                finish();
                                return;
                            }
                        }
                        requstLocatin();
                    }else {
                        toast("发生未知的错误。。。");
                    }
                    break;
                default:
            }
    }

//    /*校验*/

    class baiDuBroadCast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
              String result = intent.getAction();
            if (result.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)){
              Toast.makeText(MainActivity.this,"None Net。。。",Toast.LENGTH_SHORT).show();
         }else if (result.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)){
                Toast.makeText(MainActivity.this,"  key Error。。。",Toast.LENGTH_SHORT).show();
            }
        }
    }


    public class  MyLocationListener implements BDLocationListener{

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            if (bdLocation.getLocType() == BDLocation.TypeGpsLocation
                || bdLocation.getLocType()==BDLocation.TypeNetWorkLocation){
               navigateTo(bdLocation);
            }

        StringBuilder currentsion = new StringBuilder();
           currentsion.append("纬度 :").append(bdLocation.getLatitude()).append("\n");
           currentsion.append("经度 :").append(bdLocation.getLongitude()).append("\n");
           currentsion.append("国家 :").append(bdLocation.getCountry()).append("\n");
           currentsion.append("省 :").append(bdLocation.getProvince()).append("\n");
           currentsion.append("市 :").append(bdLocation.getCity()).append("\n");
           currentsion.append("县/区 :").append(bdLocation.getDistrict()).append("\n");
           currentsion.append("街道 :").append(bdLocation.getStreet()+bdLocation.getStreetNumber()).append("\n");


           currentsion.append("定位方式");
            Log.d(TAG,"checkSelfPermission..8...."+currentsion.toString());
            if(bdLocation.getLocType() == BDLocation.TypeGpsLocation){
                currentsion.append(" :GPS");
            }else if(bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
                currentsion.append(" :网络");
            }
          //  positionText.setText(currentsion.toString());
        }
    }

    /*Toast 的方法*/
    protected void toast(String st){
        Toast.makeText(MainActivity.this, st ,Toast.LENGTH_SHORT).show();
    }
}
