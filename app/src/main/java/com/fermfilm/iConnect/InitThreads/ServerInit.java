package com.fermfilm.iConnect.InitThreads;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.fermfilm.iConnect.CallDialogActivity;
import com.fermfilm.iConnect.Entities.i_Message;
import com.fermfilm.iConnect.InOutState;
import com.fermfilm.iConnect.MainFragment;
import com.fermfilm.iConnect.ReceiverSetThread;
import com.fermfilm.iConnect.Receivers.WifiDirectBroadcastReceiver;
import com.fermfilm.iConnect.TabActivity;
import com.fermfilm.iConnect.WifiService;
import com.fermfilm.iConnect.i_AsyncTasks.i_SendMessageClient;
import com.fermfilm.iConnect.i_AsyncTasks.i_SendMessageServer;

public class ServerInit extends Thread{
	private static final String TAG = "ServerInit";
	private static final int ROLE_B = 0;
	private static final int ROLE_A = 1;
	public static final int REQUEST_MESSAGE = 0;
	public static final int COUPON_MESSAGE = 1;
	private static final int SERVER_PORT = 4444;
	public static ArrayList<InetAddress> clients;
	private ServerSocket serverSocket;
	private boolean halt_;
	private InOutState State;
	public Context mContext;
	private Handler handler = new Handler();
	
	public ServerInit(Context context){
		clients = new ArrayList<InetAddress>();
		halt_ = false;
		State = new InOutState();
		mContext = context;
	}

	@Override
	public void run() {
		clients.clear();
		try {
			serverSocket = new ServerSocket(SERVER_PORT);
			// Collect client ip's
			while(true) {
				Socket clientSocket = serverSocket.accept();
				if(!clients.contains(clientSocket.getInetAddress())){
					clients.add(clientSocket.getInetAddress());
					Log.v(TAG, "New client: " + clientSocket.getInetAddress().getHostAddress());
				}
				clientSocket.close();
				if(State.loadRoleId(mContext) == ROLE_A){
					Log.e(TAG,"ROLE_A");
					//TODO:countdown
					//Log.v(TAG, "s0getcountdown = " + WifiService.doneSignal.getCount());
					ReceiverSetThread.doneSignal.countDown();
					if(State.loadSendMesId(mContext) == REQUEST_MESSAGE){
						handler.postDelayed(Send_OPEN_DIALOG, 1000);
					}else if(State.loadSendMesId(mContext) == COUPON_MESSAGE){
						handler.postDelayed(Send_OPEN_COUPON_DIALOG, 1000);
					}


					//Log.v(TAG, "s1getcountdown = " + WifiService.doneSignal.getCount());
				}else if(State.loadRoleId(mContext) == ROLE_B){
					Log.e(TAG,"ROLE_B");
					i_Message mes = new i_Message(i_Message.SERVER_SET, "", null, State.loadChatName(mContext));
					new i_SendMessageServer(mContext, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);

					Intent startServiceIntent = new Intent(mContext,WifiService.class);
					mContext.startService(startServiceIntent);
					/*
					// ポップアップダイアログ表示
					Intent i = new Intent(mContext,CallDialogActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					mContext.startActivity(i);
					*/
				}


			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.v(TAG, "Server end");
	}
	private final Runnable Send_OPEN_DIALOG = new Runnable() {
		@Override
		public void run() {
			i_Message mes = new i_Message(i_Message.OPEN_DIALOG, "", null, State.loadChatName(mContext));
			new i_SendMessageServer(mContext, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
		}
	};
	private final Runnable Send_OPEN_COUPON_DIALOG = new Runnable() {
		@Override
		public void run() {
			i_Message mes = new i_Message(i_Message.OPEN_COUPON_DIALOG, "", null, State.loadChatName(mContext));
			new i_SendMessageServer(mContext, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
		}
	};
	@Override
	public void interrupt() {
		super.interrupt();
		try {
			serverSocket.close();
			Log.v(TAG, "Server init process interrupted");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void halt() {
		halt_ = true;
		Log.v(TAG, "halt()");
		interrupt();
	}
}
