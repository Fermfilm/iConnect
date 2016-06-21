package com.fermfilm.iConnect;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;





//位置情報の取得
public class LocationFragment extends Fragment
        implements OnMapReadyCallback {
    private static final String TAG = "LocationFragment";
    private Context mContext;
    private FragmentActivity myContext;
    private GoogleMap mMap;


    private final static String BR = System.getProperty("line.separator");
    private final static int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
    private TextView        textView;//テキストビュー
    private Button button1, button2, button3, button4;//ボタン
    private LocationManager manager; //ロケーションマネージャ
    private double La = 2, Lo = 3;

    //起動時に呼ばれる
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        //setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        mContext = getActivity();

        //FragmentManager fragManager = myContext.getFragmentManager();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


/*
    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }
    */

/*


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //レイアウトの生成
        LinearLayout layout = new LinearLayout(this);
        layout.setBackgroundColor(Color.WHITE);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);

        //テキストビューの生成
        textView = new TextView(this);
        textView.setText("Location");
        textView.setTextSize(24);
        textView.setTextColor(Color.BLACK);
        textView.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        layout.addView(textView);

        //ボタンの生成
        button1 = new Button(this);
        button1.setTag("button1");
        button1.setText("send");
        button1.setOnClickListener(this);
        layout.addView(button1);

        button2 = new Button(this);
        button2.setTag("button2");
        button2.setText("cancel");
        button2.setOnClickListener(this);
        layout.addView(button2);

        button3 = new Button(this);
        button3.setTag("button3");
        button3.setText("Change Location");
        button3.setOnClickListener(this);
        layout.addView(button3);


        button4 = new Button(this);
        button4.setTag("button4");
        button4.setText("Get Location");
        button4.setOnClickListener(this);
        layout.addView(button4);

        //ロケーションマネージャの取得(1)
        manager = (LocationManager)getSystemService(
                Context.LOCATION_SERVICE);
    }

    //アクティビティ再開時に呼ばれる
    @Override
    public void onResume() {
        super.onResume();

        if(manager != null){
            //ロケーションマネージャのリスナー登録(2)
            manager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }else{
            La=3;
            Lo=4;
            toast("manager=null");
        }


        textView.setText("Location>" + BR +
                "緯度:" + La + BR +
                "経度:" + Lo);
    }

    //アクティビティ一時停止時に呼ばれる
    @Override
    public void onPause() {
        super.onPause();

        //ロケーションマネージャーの設定(3)
        manager.removeUpdates(this);
    }

    public void onClick(View v){
        String tag = (String) v.getTag();

        switch (tag){
            case "button1":
                //toast("Latitude=" + String.valueOf(La) + "\nLongitude=" + String.valueOf(Lo));
                Intent intent = getIntent();
                intent.putExtra("Latitude", La );
                intent.putExtra("Longitude", Lo);
                setResult(RESULT_OK, intent);
                double d =intent.getDoubleExtra("Latitude",0.0);
                double l = getIntent().getExtras().getDouble("Longitude");
                //toast("Latitude2="+d+"Lo2="+l);
                finish();
                break;
            case "button2":
                finish();
                break;
            case "button3":
                La = 100;
                Lo = 200;
                textView.setText("Location>" + BR +
                        "緯度:" + La + BR +
                        "経度:" + Lo);
                break;

            case "button4":
                manager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, 0, 0, this);
                break;
        }

        if ( "button1".equals(tag)){
            toast("Latitude="+String.valueOf(La)+"\nLongitude="+String.valueOf(Lo));
        }


    }


    //位置情報変更を通知する時に呼ばれる(4)
    public void onLocationChanged(Location location) {
        //緯度と経度の取得(5)
        La = location.getLatitude();
        Lo = location.getLongitude();
        textView.setText("Location>" + BR +
                "緯度:" + La + BR +
                "経度:" + Lo);
        toast("Location>" + BR +
                "緯度:" + La + BR +
                "経度:" + Lo);
    }
*/
    //位置情報取得有効化を通知する時に呼ばれる(4)
    public void onProviderEnabled(String provider) {
    }

    //位置情報取得無効化を通知する時に呼ばれる(4)
    public void onProviderDisabled(String provider) {
    }

    //位置情報状態変更を通知する時に呼ばれる(4)
    public void onStatusChanged(String provider,
                                int status, Bundle extras) {
    }
    //トースト
    private void toast(String text) {
        if (text == null) text = "";
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }

}