package com.fermfilm.iConnect;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.fermfilm.iConnect.Entities.i_Message;
import com.fermfilm.iConnect.Receivers.WifiDirectBroadcastReceiver;
import com.fermfilm.iConnect.i_AsyncTasks.i_ReceiveMessageClient;
import com.fermfilm.iConnect.i_AsyncTasks.i_ReceiveMessageServer;
import com.fermfilm.iConnect.i_AsyncTasks.i_SendMessageClient;
import com.fermfilm.iConnect.i_AsyncTasks.i_SendMessageServer;

/**
 * Created by fermfilm on 2015/08/21.
 */
public class WifiDiscoverService extends Service {
	private static final String TAG = "WifiDiscoverService";
	private static final int ROLE_B = 0;
	private static final int ROLE_A = 1;
	private Handler handler = new Handler();
	private boolean running = false;
	private String message = "Hello";
	public static NotificationManager nm;
	public static String chatName;
	private WifiP2pManager mManager;
	private WifiP2pManager.Channel mChannel;
	private static WifiP2pManager sManager;
	private static WifiP2pManager.Channel sChannel;
	private WifiDirectBroadcastReceiver mReceiver;
	private IntentFilter mIntentFilter;
	private InOutState State;
	private i_Message mes;
	private Activity mActivity;
	private TabActivity tabActivity;
	private static TabActivity s_tabActivity;
	private Context mContext;
	private static WifiDiscoverService instance;
	/*
	private WifiDiscoverService(){
		super();
	}
	public static WifiDiscoverService createInstance(){
		if(instance == null){
			Log.v(TAG, "instance=null");
			instance = new WifiDiscoverService();
		}
		Log.v(TAG, "instance=not null");
		return instance;
	}
	*/
	public void setmActivity(Activity mActivity) {
		this.mActivity = mActivity;
		Log.e(TAG, "mActivity = " + mActivity);
	}
	//サービス生成時に呼ばれる
	@Override
	public void onCreate() {
		Log.v(TAG, "onCreate");
		nm = (NotificationManager)
				this.getSystemService(Context.NOTIFICATION_SERVICE);
		super.onCreate();
	}

	//サービス開始時に呼ばれる
	public int onStartCommand(Intent intent, int flags, final int startId) {
		super.onStartCommand(intent, flags, startId);
		Log.v(TAG, "onStartCommand");
		Log.v(TAG, "getApplication = " + getApplication());
		State = new InOutState();
		mContext = getApplicationContext();

		mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		mChannel = mManager.initialize(this, getMainLooper(), null);

		final long cTime = System.currentTimeMillis();
		final long cycleTime = 30 * 1000;
		/**/
		//繰り返し
		Thread thread = new Thread(){public void run() {
			running = true;
			if(running){
				while (running) {
				//while (System.currentTimeMillis() - cTime < endTime) {
					handler.post(new Runnable() {
						public void run() {
							//toast(WifiDiscoverService.this, "Discover...");
							Discover();
						}
					});
					try {Thread.sleep(cycleTime);} catch (Exception e) {}
				}
				stopSelf(startId);
			}
		}};
		thread.start();


		return START_STICKY_COMPATIBILITY;
	}
	public void Discover(){
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

	//サービス停止時に呼ばれる
	@Override
	public void onDestroy() {
		Log.v(TAG, "onDestroy");
		running = false;
		nm.cancel(0);
		super.onDestroy();
	}

	//サービス接続時に呼ばれる
	@Override
	public IBinder onBind(Intent intent) {
		Log.v(TAG, "onBind");
		return IMyServiceBinder;
	}

	//ノーティフィケーションの表示
	public static void showNotification(Context context,
										int iconId, String title, String text, String info) {
		//ノーティフィケーションオブジェクトの生成(9)
		Notification.Builder builder = new Notification.Builder(context);
		builder.setWhen(System.currentTimeMillis());
		builder.setContentTitle(title);
		builder.setContentText(text);
		builder.setContentInfo(info);
		builder.setSmallIcon(iconId);

		//ペンディングインテントの指定(10)
		Intent intent = new Intent(context, TabActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		builder.setContentIntent(PendingIntent.getActivity(
				context, 0, intent, PendingIntent.FLAG_ONE_SHOT));

		//ノーティフィケーションの表示(11)
		//NotificationManager nm = (NotificationManager)
		//		context.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(0);

		nm.notify(0, builder.build());
	}

	//トーストの表示
	private static void toast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	//バインダの生成(8)
	private final IMyService.Stub IMyServiceBinder = new IMyService.Stub() {
		public void setMessage(String msg) throws RemoteException {
			message = msg;
		}
	};




}
