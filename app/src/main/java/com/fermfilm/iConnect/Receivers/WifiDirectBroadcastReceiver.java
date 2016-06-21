package com.fermfilm.iConnect.Receivers;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.fermfilm.iConnect.DeviceListActivity;
import com.fermfilm.iConnect.InOutState;
import com.fermfilm.iConnect.InitThreads.ClientInit;
import com.fermfilm.iConnect.InitThreads.ServerInit;
import com.fermfilm.iConnect.ReceiverSetThread;
import com.fermfilm.iConnect.TabActivity;
import com.fermfilm.iConnect.WifiService;
import com.fermfilm.iConnect.i_AsyncTasks.i_ReceiveMessageClient;
import com.fermfilm.iConnect.i_AsyncTasks.i_ReceiveMessageServer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

/*
 * This class implements the Singleton pattern
 */
public class WifiDirectBroadcastReceiver extends BroadcastReceiver{
	private static final int ROLE_B = 0;
	private static final int ROLE_A = 1;
	public static final int IS_OWNER = 1;
	public static final int IS_CLIENT = 2;
	private static final String TAG = "WDBroadcastReceiver";
	private static int CONNECT_COUNT = 0;
	private WifiP2pManager mManager;
	private Channel mChannel;
	private Activity mActivity;
	private List<String> peersName = new ArrayList<String>();
	private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
	private List<WifiP2pDevice> devices = new ArrayList<WifiP2pDevice>();
	private int isGroupeOwner;
	private InetAddress ownerAddr;
	private static WifiDirectBroadcastReceiver instance;
	public static ServerInit server;
	public static final CountDownLatch cdl = new CountDownLatch(1);
	private InOutState State;
	private static TabActivity s_TabActivity;

	private WifiDirectBroadcastReceiver(){
		super();
		State = new InOutState();
	}
	public static WifiDirectBroadcastReceiver createInstance(){
		if(instance == null){
			Log.v(TAG, "instance=null");
			instance = new WifiDirectBroadcastReceiver();
		}
		Log.v(TAG, "instance=not null");
		return instance;
	}

	public List<String> getPeersName() { return peersName; }
	public List<WifiP2pDevice> getPeers() { return peers; }
	public int isGroupeOwner() { return isGroupeOwner; }
	public InetAddress getOwnerAddr() { return ownerAddr; }
	public void setmManager(WifiP2pManager mManager) { this.mManager = mManager; }
	public void setmChannel(Channel mChannel) { this.mChannel = mChannel; }
	public void setmActivity(Activity mActivity) {
		this.mActivity = mActivity;
		Log.e(TAG,"mActivity = "+mActivity);
	}




	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		/*
		Log.e(TAG,"device="+Build.DEVICE);
		if(Build.DEVICE.matches(".*" + "iConnect" + ".*")){
			Log.e(TAG,"device2="+Build.DEVICE);
		}
		*/

		/**********************************
		 Wifi P2P is enabled or disabled 
		**********************************/
		//端末本体のWi-Fi Directの有効・無効の変更通知
		if(action.equals(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)){
			//Log.v(TAG, "WIFI_P2P_STATE_CHANGED_ACTION");
			//check if Wifi P2P is supported
			int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
			if(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED){
			//	Toast.makeText(mActivity, "Wifi P2P is supported by this device", Toast.LENGTH_SHORT).show();
			} else{
			//	Toast.makeText(mActivity, "Wifi P2P is not supported by this device", Toast.LENGTH_SHORT).show();
			}
		}
		
		/**********************************
		 Available peer list has changed
		**********************************/
		//Wi-Fi Direct端末一覧の変更通知
		else if(action.equals(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)){ 
//			Log.v(TAG, "WIFI_P2P_PEERS_CHANGED_ACTION");
			//発見したピアの情報の要求→onPeersAvailable()


			if(CONNECT_COUNT == 0){
				Log.e(TAG, "WIFI_P2P_PEERS_CHANGED_ACTION");
				mManager.requestPeers(mChannel,getPeersListListener);
			}
		}
		
		/***************************************
		 This device's wifi state has changed 
		***************************************/
		else if(action.equals(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)){ 
			Log.v(TAG, "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
			Log.v(TAG, "COUNT = "+CONNECT_COUNT);
			
		}
		//Wi-Fi Direct通信状態の変更通知
		/******************************************************************
		 State of connectivity has changed (new connection/disconnection) 
		******************************************************************/
		else if(action.equals(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)){
			//Log.v(TAG, "WIFI_P2P_CONNECTION_CHANGED_ACTION");
			
			if(mManager == null){
				return;
			}
			NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
			if(networkInfo.isConnected()){
				mManager.requestConnectionInfo(mChannel, new ConnectionInfoListener() {
					
					@Override
					public void onConnectionInfoAvailable(WifiP2pInfo info) {
						InetAddress groupOwnerAddress = info.groupOwnerAddress;
						ownerAddr= groupOwnerAddress;
						Log.e(TAG, "networkInfo.is connected");
						/******************************************************************
						 The GO : create a server thread and accept incoming connections
						******************************************************************/
						if (info.groupFormed && info.isGroupOwner) {
							isGroupeOwner = IS_OWNER;
								activateGoToChat("server");
						}

						/******************************************************************
						 The client : create a client thread that connects to the group owner
						******************************************************************/
						else if (info.groupFormed) {
							isGroupeOwner = IS_CLIENT;
								activateGoToChat("client");
						}
					}
				});				
			}
			//端末本体の状態の変更通知
			else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
				/**/WifiP2pDevice device = (WifiP2pDevice)intent.getParcelableExtra(
						WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
				String[] STATUS = {
						"CONNECTED", "INVITED", "FAILED", "AVAILABLE", "UNAVAILABLE"};
				Log.v(TAG, "端末本体の状態>"+device.deviceName+","+STATUS[device.status]);
				//   parent.addText("端末本体の状態>"+device.deviceName+","+STATUS[device.status]);


			}
			if(!networkInfo.isConnected()){
				Log.v(TAG, "networkInfo.is not connected");
				CONNECT_COUNT = 0;
			}
			if(networkInfo.isFailover()){
				Log.v(TAG, "networkInfo.isFailover()");
			}
		}
	}

	WifiP2pManager.PeerListListener getPeersListListener = new WifiP2pManager.PeerListListener(){
		@Override
		public void onPeersAvailable(WifiP2pDeviceList peers) {
			if(peers.getDeviceList().size() <=0){
				Log.v(TAG, "No peers found");
			}

			////更新DeviceListActivity
			devices.clear();
			devices.addAll(peers.getDeviceList());
			if (DeviceListActivity.activity != null) {
				DeviceListActivity.activity.update(devices);
			}
			//
			Iterator<WifiP2pDevice> i = peers.getDeviceList().iterator();
			while(i.hasNext()){
				WifiP2pDevice device = i.next();
				WifiP2pConfig config = new WifiP2pConfig();
				config.deviceAddress = device.deviceAddress;

				config.wps.setup = WpsInfo.PBC;
				if (device.deviceName.matches(".*" + "iConnect" + ".*")) {
					// 部分一致です
					Log.v(TAG, "iConnect_部分一致です");

					mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
						@Override
						public void onSuccess() {
							//connect add
							CONNECT_COUNT = 1;
						}
						@Override
						public void onFailure(int reason) {
						}
					});

				}


				Log.v(TAG, device.deviceName);
			}
		}


	};

	public void activateGoToChat(String role){
		Log.e(TAG, "activeGoToChat_activity = "+mActivity.getClass());

		if(mActivity.getClass() == TabActivity.class){
			if(isGroupeOwner == IS_OWNER ){
				server = new ServerInit(mActivity);
				server.start();
				if(State.loadRoleId(mActivity) == ROLE_A){
					Log.e(TAG, "I'm Owner && Role_A");
					ReceiverSetThread setThread = new ReceiverSetThread(mActivity);
					setThread.start();
					//Log.v(TAG,"mActivity = "+mActivity);
					new i_ReceiveMessageServer(mActivity).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);

				}else if(State.loadRoleId(mActivity) == ROLE_B) {
					Log.e(TAG, "I'm Owner && Role_B");
					//start service
					/*
					Intent startServiceIntent = new Intent(mActivity,WifiService.class);
					//startServiceIntent.putExtra("chatName",chatName);
					mActivity.startService(startServiceIntent);
					*/
				}


			}else if(isGroupeOwner == IS_CLIENT){
				ClientInit client = new ClientInit(getOwnerAddr(), mActivity);
				client.start();
				if(State.loadRoleId(mActivity) == ROLE_A){
					Log.e(TAG, "I'm Client & Role_A");
					ReceiverSetThread setThread = new ReceiverSetThread(mActivity);
					setThread.start();
					//Log.v(TAG,"mActivity = "+mActivity);
					new i_ReceiveMessageClient(mActivity).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
				}else if(State.loadRoleId(mActivity) == ROLE_B){
					Log.e(TAG, "I'm Client & Role_B");

					//start service
					Intent startServiceIntent = new Intent(mActivity,WifiService.class);
					//startServiceIntent.putExtra("chatName",chatName);
					mActivity.startService(startServiceIntent);
				}


			}

		}
	}


}
