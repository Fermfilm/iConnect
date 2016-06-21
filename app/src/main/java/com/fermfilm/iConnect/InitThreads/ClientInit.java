package com.fermfilm.iConnect.InitThreads;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.fermfilm.iConnect.Entities.i_Message;
import com.fermfilm.iConnect.InOutState;
import com.fermfilm.iConnect.ReceiverSetThread;
import com.fermfilm.iConnect.Receivers.WifiDirectBroadcastReceiver;
import com.fermfilm.iConnect.i_AsyncTasks.i_SendMessageClient;
import com.fermfilm.iConnect.i_AsyncTasks.i_SendMessageServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientInit extends Thread{
	private static final String TAG = "ClientInit";
	private static final int ROLE_B = 0;
	private static final int ROLE_A = 1;
	private static final int SERVER_PORT = 4444;
	private InetAddress mServerAddr;
	private WifiDirectBroadcastReceiver mReceiver;
	private Handler handler = new Handler();
	private boolean halt_;
	private InOutState State;
	public Context mContext;

	
	public ClientInit(InetAddress serverAddr, Context context){
		mServerAddr = serverAddr;
		halt_ = false;
		State = new InOutState();
		mContext = context;
	}
	
	@Override
	public void run() {
		mReceiver = WifiDirectBroadcastReceiver.createInstance();
		Socket socket = new Socket();
		try {
			socket.bind(null);
			socket.connect(new InetSocketAddress(mServerAddr, SERVER_PORT));
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(State.loadRoleId(mContext) == ROLE_A){
			//handler.postDelayed(Send_OPEN_DIALOG, 1000);

		}else if(State.loadRoleId(mContext) == ROLE_B){

		}
		Log.v(TAG, "Client end");
	}

	private final Runnable Send_OPEN_DIALOG = new Runnable() {
		@Override
		public void run() {
			i_Message mes = new i_Message(i_Message.OPEN_DIALOG, "", null, State.loadChatName(mContext));
			new i_SendMessageServer(mContext, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
			new i_SendMessageClient(mContext, mReceiver.getOwnerAddr()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
		}
	};

	public void halt() {
		halt_ = true;
		interrupt();
	}
}
