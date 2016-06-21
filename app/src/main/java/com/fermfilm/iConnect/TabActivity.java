package com.fermfilm.iConnect;

/**
 * Created by fermfilm on 2015/08/17.
 */
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.fermfilm.iConnect.Entities.i_Message;
import com.fermfilm.iConnect.InitThreads.ServerInit;
import com.fermfilm.iConnect.Receivers.WifiDirectBroadcastReceiver;
import com.fermfilm.iConnect.i_AsyncTasks.i_ReceiveMessageClient;
import com.fermfilm.iConnect.i_AsyncTasks.i_ReceiveMessageServer;
import com.fermfilm.iConnect.i_AsyncTasks.i_SendMessageServer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TabActivity extends FragmentActivity implements
		FragmentTabHost.OnTabChangeListener, WifiP2pManager.ChannelListener,
		WifiP2pManager.PeerListListener, WifiP2pManager.ConnectionInfoListener,
		MainFragment.OnOkBtnClickListener, SettingFragment.CheckBoxListener,
		SettingFragment.NoCheckBoxListener{
	private static final String TAG = "TabActivity";
	private static final int ROLE_B = 0;
	private static final int ROLE_A = 1;
	private static final String SEND_ACTION = "com.fermfilm.iConnect.receiver.TOAST_ACTION";
	private WifiP2pManager mManager;
	private WifiP2pManager.Channel mChannel;
	private static WifiP2pManager sManager;
	private static WifiP2pManager.Channel sChannel;
	private WifiDirectBroadcastReceiver mReceiver;
	private IntentFilter mIntentFilter;
	private i_ReceiveMessageClient i_receiveMessageClient;
	//private Intent re_intent;
	private Context mContext;
	private static Context context;
	private static final int RQ_CONNECT_DEVICE = 10;
	private List<WifiP2pDevice> devices = new ArrayList<WifiP2pDevice>();
	private Handler handler = new Handler();
	private boolean retryChannel = false;

	private Intent     serviceIntent;
	private Intent     Discoverservice;
	private IMyService binder;
	private MainFragment mainFragment;
	public static String chatName;
	private WifiDiscoverService discoverService;

	private InOutState State;
	//private WifiManager wifi = (WifiManager) getSystemService(WIFI_SERVICE);


	//Getters and Setters
	public WifiP2pManager getmManager() { return mManager; }
	public WifiP2pManager.Channel getmChannel() { return mChannel; }
	public WifiP2pManager getsManager() {
		sManager = mManager;
		return sManager; }
	public WifiP2pManager.Channel getsChannel() {
		sChannel = mChannel;
		return sChannel; }
	public WifiDirectBroadcastReceiver getmReceiver() { return mReceiver; }
	public IntentFilter getmIntentFilter() { return mIntentFilter; }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_tabhost);
		State = new InOutState();
		//Hide keyboard
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		// FragmentTabHost を取得する
		FragmentTabHost tabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
		tabHost.setup(this, getSupportFragmentManager(), R.id.container);

		TabHost.TabSpec tabSpec1, tabSpec2, tabSpec3, tabSpec4;

		// TabSpec を生成する
		tabSpec1 = tabHost.newTabSpec("Tab1");
		tabSpec1.setIndicator("お助け作成");
		//Bundle bundle1 = new Bundle();
		// TabHost に追加
		tabHost.addTab(tabSpec1, MainFragment.class, null);

		// TabSpec を生成する
		tabSpec2 = tabHost.newTabSpec("tab2");
		tabSpec2.setIndicator("設定");
		// TabHost に追加
		tabHost.addTab(tabSpec2, SettingFragment.class, null);

		// TabSpec を生成する
		tabSpec3 = tabHost.newTabSpec("tab3");
		tabSpec3.setIndicator("クーポン作成");
		// TabHost に追加
		tabHost.addTab(tabSpec3, CouponFragment.class, null);

		// TabSpec を生成する
		tabSpec4 = tabHost.newTabSpec("tab4");
		tabSpec4.setIndicator("位置情報");
		// TabHost に追加
		tabHost.addTab(tabSpec4, LocationFragment.class, null);

		// リスナー登録
		tabHost.setOnTabChangedListener(this);
		mContext = this;
		context = this;

		//Wifi有効、無効切り替え
		WifiManager wifi = (WifiManager) getSystemService(WIFI_SERVICE);
		if(wifi.isWifiEnabled()){
			//TODO:Once cut
			//wifi.setWifiEnabled(false);
			wifi.setWifiEnabled(true);
		}else{
			wifi.setWifiEnabled(true);
		}


		if(State.loadCheck(mContext)){
			onCheckClicked();

			/*
			//receiver起動
			init();
			registerReceiver(mReceiver, mIntentFilter);
			mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
				@Override
				public void onSuccess() {
					Log.v(TAG, "Discovery process succeeded");
				}
				@Override
				public void onFailure(int reason) {
					Log.v(TAG, "Discovery process failed");
				}
			});*/
		}else{
			onNoCheckClicked();
		}


		//TODO:いるかこれ
		/*
		//サービスインテントの生成
		serviceIntent = new Intent(this,WifiService.class);
		//Discoverservice = new Intent(this,WifiDiscoverService.class);

		//サービスとの接続(2)
		if (isServiceRunning("com.fermfilm.iConnect.WifiService")) {
			bindService(serviceIntent,connection,0);
		}
		*/


		/*
		if (isServiceRunning("com.fermfilm.iConnect.WifiDiscoverService")) {
			bindService(Discoverservice,connection,0);
		}
		*/
	}
	public static Context getContext(){
		return context;
	}
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		Log.v(TAG, "onPostCreate");
		ActivityUtilities.customiseActionBar(this);
	}
	/**/
	//アクティビティ復帰時に呼ばれる
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	super.onActivityResult(requestCode, resultCode, data);
	Log.v(TAG, "onActivityResult");
		if (requestCode == RQ_CONNECT_DEVICE){
			if (resultCode == Activity.RESULT_OK) {
				//端末との接続
				int idx = data.getExtras().getInt("device_index");
				connectDevice(devices.get(idx));
			}
		}
	}

	@Override
	public void onOkClicked() {
		//intent.putExtra("chatName", loadChatName(mContext));
		//sendBroadcast(intent);
	}
	//通知設定のチェックボックス
	@Override
	public void onCheckClicked() {
		Log.e(TAG, "check");
		//TODO:service wifi discover
		init();
		registerReceiver(mReceiver, mIntentFilter);
		Discoverservice = new Intent(this,WifiDiscoverService.class);
		//start service
		startService(Discoverservice);

		mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
			@Override
			public void onSuccess() {
				Log.v(TAG, "Discovery process succeeded");
			}
			@Override
			public void onFailure(int reason) {
				Log.e(TAG, "Discovery process failed");
			}
		});

	}
	@Override
	public void onNoCheckClicked() {
		Log.v(TAG, "no_check");
		stopService(new Intent(this, WifiDiscoverService.class));

		//wifi off
		WifiManager wifi = (WifiManager) getSystemService(WIFI_SERVICE);
		wifi.setWifiEnabled(false);

		/*
		if(mReceiver!=null){
			setDeviceName(State.loadDeviceName(mContext));
			disconnect();
			if (mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_OWNER) {
				if (mReceiver.server != null) {
					mReceiver.server.interrupt();
				}
			}
			if (mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_CLIENT) {
			}
		}else{
			Log.e(TAG,"mRecerver is null");
		}
		if(mReceiver!=null){
			//unregisterReceiver(mReceiver);
		}else{
			Log.e(TAG,"Not start Receiver");
		}
		*/
	}
	@Override
	public void onChannelDisconnected() {
		Log.v(TAG, "onChannelDisconnected()");
		// we will try once more
		if (mManager != null && !retryChannel) {
			toast("Channel lost. Trying again");
			retryChannel = true;
			mManager.initialize(this, getMainLooper(), this);
		} else {
			toast("Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.");
		}
	}
	//サービスが起動中かどうかを調べる(12)
	private boolean isServiceRunning(String className) {
		ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceInfos =
				am.getRunningServices(Integer.MAX_VALUE);
		for (int i = 0; i < serviceInfos.size(); i++) {
			if (serviceInfos.get(i).service.getClassName().equals(className)) {
				Log.v(TAG,className + " Running");
				return true;
			}
		}
		Log.v(TAG,className+" Not Running");
		return false;
	}


	public void init(){
		mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		mChannel = mManager.initialize(this, getMainLooper(), null);


		//intent = new Intent(getApplicationContext(), WifiDirectBroadcastReceiver.class);
		mReceiver = WifiDirectBroadcastReceiver.createInstance();
		//mReceiver = new WifiDirectBroadcastReceiver(mManager, mChannel, this);

		mReceiver.setmManager(mManager);
		mReceiver.setmChannel(mChannel);
		mReceiver.setmActivity(this);
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		wifiManager.setWifiEnabled(true);

		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		mIntentFilter.addAction(SEND_ACTION);
	}

	//更新DeviceListActivity
	public void onPeersAvailable(WifiP2pDeviceList deviceList) {
		devices.clear();
		devices.addAll(deviceList.getDeviceList());
		if (DeviceListActivity.activity != null) {
			DeviceListActivity.activity.update(devices);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.v(TAG, "onPause");
	}
	@Override
	public void onResume() {
		super.onResume();
		Log.v(TAG, "onResume");

	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e(TAG, "onDestroy");
		//setDeviceName(State.loadDeviceName(mContext));
		stopService(new Intent(this, WifiDiscoverService.class));
	}
	/**/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			//discover
			case R.id.discover:
				Intent intent = new Intent(mContext, DeviceListActivity.class);
				startActivityForResult(intent, RQ_CONNECT_DEVICE);

				mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
					@Override
					public void onSuccess() {}
					@Override
					public void onFailure(int reasonCode) {
						Log.v(TAG,"Discovery Failed : " + reasonCode);}
				});
				return true;
			case R.id.disconnect:
				if (mReceiver != null) {
					State.saveRoleId(mContext, ROLE_B);
					setDeviceName(State.loadDeviceName(mContext));
					disconnect();
					if (mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_OWNER) {
						if (mReceiver.server != null) {
							mReceiver.server.interrupt();
						}
						//ServerInit server = new ServerInit();
						//server.halt();
					}
					if (mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_CLIENT) {
						//i_receiveMessageClient.cancel(true);
					}

				}else{
					Log.e(TAG,"mRecerver is null");
				}
				/*
				if(binder!=null && connection!=null){
					//サービスとの切断(4)
					unbindService(connection);
					//サービスの停止(5)
					stopService(serviceIntent);
				}else{
					Log.e(TAG,"Not start Service");
				}
				*/
				return true;
			case R.id.setting:
				//通信設定
				startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
				return true;
			/**/
			case R.id.client_mode:
				setDeviceName(State.loadDeviceName(mContext));
				return true;
			case R.id.server_mode:
				setDeviceName(State.loadDeviceName(mContext)+"_iConnect");
				return true;

			case R.id.service_start:
				//serviceIntent.putExtra("chatName",loadChatName(this));
				//サービスの開始(1)
				startService(serviceIntent);
				//サービスとの接続(2)
				bindService(serviceIntent,connection,0);
				return true;
			case R.id.service_end:
				if(binder!=null){
					//サービスとの切断(4)
					unbindService(connection);
					//サービスの停止(5)
					stopService(serviceIntent);
				}else{
					Log.e(TAG,"Not start Service");
				}

				return true;
			case R.id.receiver_start:
				//receiver起動
				init();
				registerReceiver(mReceiver, mIntentFilter);
				mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
					@Override
					public void onSuccess() {
						Log.v(TAG, "Discovery process succeeded");
					}

					@Override
					public void onFailure(int reason) {
						Log.v(TAG, "Discovery process failed");
					}
				});
				return true;
			case R.id.receiver_end:
				if(mReceiver!=null){
					unregisterReceiver(mReceiver);
				}else{
					Log.e(TAG,"Not start Receiver");
				}

				return true;
		}


//        int idItem = item.getItemId();
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabChanged(String tabId) {
		Log.d("onTabChanged", "tabId: " + tabId);
	}

	//Wi-Fi Direct接続情報の通知時に呼ばれる
	public void onConnectionInfoAvailable(WifiP2pInfo info) {
	}

	//端末との接続
	public void connectDevice(final WifiP2pDevice device) {
		//接続設定の生成
		WifiP2pConfig config = new WifiP2pConfig();
		config.deviceAddress = device.deviceAddress;
		config.wps.setup = WpsInfo.PBC;
		mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
			@Override
			public void onSuccess() {
			}

			@Override
			public void onFailure(int reason) {
			}
		});
	}
	public void disconnect() {
		mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
			@Override
			public void onFailure(int reasonCode) {
				Log.v(TAG, "Disconnect failed. Reason :" + reasonCode);
			}
			@Override
			public void onSuccess() {
				Log.v(TAG, "Disconnect succeeded");
			}
		});
	}

	public void setDeviceName(final String devName) {
		try {
			Class[] paramTypes = new Class[3];
			paramTypes[0] = WifiP2pManager.Channel.class;
			paramTypes[1] = String.class;
			paramTypes[2] = WifiP2pManager.ActionListener.class;
			Method setDeviceName = mManager.getClass().getMethod(
					"setDeviceName", paramTypes);
			setDeviceName.setAccessible(true);

			Object arglist[] = new Object[3];
			arglist[0] = mChannel;
			arglist[1] = devName;
			arglist[2] = new WifiP2pManager.ActionListener() {
				@Override
				public void onSuccess() {
					Log.v(TAG, "setDeviceName succeeded "+devName);
				}
				@Override
				public void onFailure(int reason) {
					Log.v(TAG, "setDeviceName failed");
				}
			};
			setDeviceName.invoke(mManager, arglist);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

	}

	//サービスコネクションの生成(3)
	private ServiceConnection connection = new ServiceConnection() {
		//サービス接続時に呼ばれる
		public void onServiceConnected(ComponentName name,IBinder service) {
			Log.v(TAG,"Service Start");
			binder = IMyService.Stub.asInterface(service);
		}

		//サービス切断時に呼ばれる
		public void onServiceDisconnected(ComponentName name) {

			Log.v(TAG,"Service End");
			unbindService(connection);// これ呼ばないとリークする
			binder = null;
		}
	};
	//トースト
	public void toast(String text) {
		if (text == null) text = "";
		Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
	}
}

