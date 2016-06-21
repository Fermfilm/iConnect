package com.fermfilm.iConnect;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.List;

//端末検索アクティビティ
public class DeviceListActivity extends Activity
        implements AdapterView.OnItemClickListener {
    private final static String BR = System.getProperty("line.separator");
    private final static int MP = LinearLayout.LayoutParams.MATCH_PARENT;
    private final static int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
    private static final String TAG = "DeviceList";

    //変数
    public static DeviceListActivity activity;//アクティビティ
    private ArrayAdapter<String> adapter;//リストビューのアダプタ

    //アクティビティ起動時に呼ばれる
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setResult(Activity.RESULT_CANCELED);
        activity = this;

        //レイアウトの生成
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);

        //デバイス
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);

        //リストビューの生成
        ListView listView = new ListView(this);
        listView.setLayoutParams(new LinearLayout.LayoutParams(MP, WC));
        listView.setAdapter(adapter);
        layout.addView(listView);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    //アクティビティ破棄時に呼ばれる
    @Override
    public void onDestroy() {
        activity = null;
        super.onDestroy();
    }

    //更新
    public void update(List<WifiP2pDevice> devices) {
        adapter.clear();
        for (int i = 0; i < devices.size(); i++) {
            //端末名と端末アドレスの取得(9)
            WifiP2pDevice device = devices.get(i);
            String[] STATUS = {
                    "CONNECTED", "INVITED", "FAILED", "AVAILABLE", "UNAVAILABLE"};
            adapter.add(device.deviceName+BR+device.deviceAddress+BR+STATUS[device.status]);

            /*
            if (device.deviceName.matches(".*" + "Server" + ".*")) {
                // 部分一致です
                toast("部分一致です\ni = " + i);
                //戻り値の指定
                Intent intent = new Intent();
                intent.putExtra("device_index", i);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
            */
        }
        Log.v(TAG, "devices="+devices);

    }

    //クリック時に呼ばれる
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        Log.e(TAG, "pos = "+pos);
        //戻り値の指定
        Intent intent = new Intent();
        intent.putExtra("device_index", pos);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
