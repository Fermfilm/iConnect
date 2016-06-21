package com.fermfilm.iConnect;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.fermfilm.iConnect.i_AsyncTasks.i_SendMessageServer;
import com.fermfilm.iConnect.i_AsyncTasks.i_SendMessageClient;

/**
 * Created by fermfilm on 2015/08/21.
 */
public class WifiService extends Service {
	private static final String TAG = "WifiService";
	private static final int ROLE_B = 0;
	private static final int ROLE_A = 1;
	public static final int REQUEST_MESSAGE = 0;
	public static final int COUPON_MESSAGE = 1;
	private Handler handler = new Handler();
	private boolean running = false;
	private String message = "Hello";
	public static NotificationManager nm;
	public static String chatName;
	private InOutState State;
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
		//ノーティフィケーションの表示
		/*
		showNotification(this, R.drawable.wifi_icon,
				"iContact",
				"困っている人を探しています",
				"");
		*/
		chatName = intent.getStringExtra("chatName");

		WifiDirectBroadcastReceiver mReceiver = WifiDirectBroadcastReceiver.createInstance();
		//Start the AsyncTask for the server to receive messages
		if(State.loadRoleId(getApplicationContext()) == ROLE_A){
			i_Message mes = null;
			if(State.loadSendMesId(getApplicationContext()) == REQUEST_MESSAGE){
				mes = new i_Message(i_Message.REQUEST_MESSAGE, "", null, State.loadChatName(getApplicationContext()));
				if (!State.loadImage(getApplicationContext()).equalsIgnoreCase("")) {
					//Log.v(TAG, "Bitmap = " + loadImage(getApplicationContext()));
					byte[] b = Base64.decode(State.loadImage(getApplicationContext()), Base64.DEFAULT);
					mes.setByteArray(b);
				} else {
					Log.v(TAG, "Image is null");
				}
				mes.setSexId(State.loadSexId(getApplicationContext()));
				mes.setAgeId(State.loadAgeId(getApplicationContext()));
				mes.setReqId(State.loadReqId(getApplicationContext()));
				mes.setCloId(State.loadClothId(getApplicationContext()));
				mes.setJupId(State.loadJuponId(getApplicationContext()));
				mes.setDetailText(State.loadDetail(getApplicationContext()));
			}else if(State.loadSendMesId(getApplicationContext()) == COUPON_MESSAGE){
				mes = new i_Message(i_Message.COUPON_MESSAGE, "", null, State.loadChatName(getApplicationContext()));
				if (!State.loadGodsImg(getApplicationContext()).equalsIgnoreCase("")) {
					byte[] b = Base64.decode(State.loadGodsImg(getApplicationContext()), Base64.DEFAULT);
					mes.setByteArray(b);
				}
				if (!State.loadMapImg(getApplicationContext()).equalsIgnoreCase("")) {
					byte[] b2 = Base64.decode(State.loadMapImg(getApplicationContext()), Base64.DEFAULT);
					mes.setByteArray2(b2);
				}
				mes.setGodsName(State.loadGodsName(getApplicationContext()));
				mes.setCouponDetail(State.loadCouponDetail(getApplicationContext()));
				mes.setShopDetail(State.loadShopDetail(getApplicationContext()));
				mes.setCouponNote(State.loadCouponNote(getApplicationContext()));

			}




			Log.e(TAG, "A_mReceiver.isGroupeOwner() == " + mReceiver.isGroupeOwner());
			if (mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_OWNER) {
				new i_SendMessageServer(getApplicationContext(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
			}else if(mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_CLIENT){
				new i_SendMessageClient(getApplicationContext(), mReceiver.getOwnerAddr()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
			}

		}else if(State.loadRoleId(getApplicationContext()) == ROLE_B){
			Log.e(TAG,"B_mReceiver.isGroupeOwner() == "+mReceiver.isGroupeOwner());
			if(mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_OWNER){
				new i_ReceiveMessageServer(getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
			}else if (mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_CLIENT) {
				new i_ReceiveMessageClient(getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
			}
			/*
				//Log.v(TAG, "Start the AsyncTask for the client to receive messages");
				//showNotification(this, R.drawable.wifi_icon,
				//		"iContact",
				//		"困っている人がいます",
				//		"");
			*/
		}


		final long cTime = System.currentTimeMillis();
		final long endTime = 10 * 1000;
		/*
		//繰り返し
		Thread thread = new Thread(){public void run() {
			running = true;
			if(running){
				while (running) {
				//while (System.currentTimeMillis() - cTime < endTime) {
					handler.post(new Runnable() {
						public void run() {
							toast(WifiService.this, chatName);
							//init();
						}
					});
					try {Thread.sleep(3000000);} catch (Exception e) {}
				}
				stopSelf(startId);
			}
		}};
		thread.start();
		*/

		return START_NOT_STICKY;
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
