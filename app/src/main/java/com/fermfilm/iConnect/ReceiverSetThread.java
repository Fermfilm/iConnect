package com.fermfilm.iConnect;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fermfilm.iConnect.Receivers.WifiDirectBroadcastReceiver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * Created by fermfilm on 2015/09/07.
 */
public class ReceiverSetThread extends Thread{

	private static final String TAG = "ReceiverSetThread";
	private static final int ROLE_B = 0;
	private static final int ROLE_A = 1;
	public static final int N = 2;
	public static CountDownLatch doneSignal ;
	//public static CountDownLatch doneSignal2 ;
	public Context mContext;
	private InOutState State;
	public WifiDirectBroadcastReceiver mReceiver;

	public ReceiverSetThread(Context context){
		mContext = context;
		State = new InOutState();
		mReceiver = WifiDirectBroadcastReceiver.createInstance();
	}

	@Override
	public void run() {
		Log.v(TAG,"run()");
		doneSignal = new CountDownLatch(N);
		//doneSignal2 = new CountDownLatch(N);
		if(State.loadRoleId(mContext) == ROLE_A){
			try {
				// Initサービスの接続待ち
				Log.v(TAG, "doneSignal = " + doneSignal.getCount());
				doneSignal.await();
				Log.v(TAG, "doneSignal_end");
			} catch (InterruptedException e) {
			}
		/*
			Log.v(TAG, "doneSignal2_start");
			try {
				//Bのdialog表示待ち
				Log.v(TAG, "doneSignal2 = "+doneSignal2.getCount());
				doneSignal2.await();
				Log.v(TAG, "doneSignal2_end");
			} catch (InterruptedException e) {
			}
			*/
			//start service
			Intent startServiceIntent = new Intent(mContext,WifiService.class);
			mContext.startService(startServiceIntent);
		}else if(State.loadRoleId(mContext) == ROLE_B){

		}

		Log.v(TAG, "SetThread end");
	}


}
