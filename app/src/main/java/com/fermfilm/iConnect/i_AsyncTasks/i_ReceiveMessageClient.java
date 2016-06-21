package com.fermfilm.iConnect.i_AsyncTasks;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fermfilm.iConnect.A_ResponseActivity;
import com.fermfilm.iConnect.B_ResponseActivity;
import com.fermfilm.iConnect.CallDialogActivity;
import com.fermfilm.iConnect.CouponDialogActivity;
import com.fermfilm.iConnect.Entities.i_Message;
import com.fermfilm.iConnect.InOutState;
import com.fermfilm.iConnect.MainFragment;
import com.fermfilm.iConnect.ReceiverSetThread;
import com.fermfilm.iConnect.Receivers.WifiDirectBroadcastReceiver;
import com.fermfilm.iConnect.i_AsyncTasks.i_AbstractReceiver;
import com.fermfilm.iConnect.TabActivity;
import com.fermfilm.iConnect.CallDialogActivity;

public class i_ReceiveMessageClient extends i_AbstractReceiver {
	private static final String TAG = "i_ReceiveMessageClient";
	private static final int ROLE_B = 0;
	private static final int ROLE_A = 1;
	public static final int REQUEST_MESSAGE = 0;
	public static final int COUPON_MESSAGE = 1;
	private InOutState State;
	private static final int SERVER_PORT = 4446;
	private Context mContext;
	private ServerSocket socket;
	private Handler handler = new Handler();
	private int TitId;


	public i_ReceiveMessageClient(Context context){
		mContext = context;
		State = new InOutState();
	}

	@Override
	protected void onPreExecute() {
		Log.v(TAG, "onPreExecute()");
		Log.v(TAG, "mContext = " + mContext);
		if(State.loadRoleId(mContext) == ROLE_A){
		}else if(State.loadRoleId(mContext) == ROLE_B){
			/*
			// ポップアップダイアログ表示
			Intent i = new Intent(mContext,CallDialogActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			mContext.startActivity(i);
			*/
		}

	}
	
	@Override
	protected Void doInBackground(Void... params) {
		try {
			socket = new ServerSocket(); // <-- create an unbound socket first
			socket.setReuseAddress(true);
			socket.bind(new InetSocketAddress(SERVER_PORT)); // <-- now bind it
			//socket = new ServerSocket(SERVER_PORT);
			while(true){
				Log.v(TAG, "doInBackground");
				//TODO:cancel
				if(isCancelled()){
					Log.v(TAG,"break");
					break;
				}


				Socket destinationSocket = socket.accept();
				
				InputStream inputStream = destinationSocket.getInputStream();
				BufferedInputStream buffer = new BufferedInputStream(inputStream);
				ObjectInputStream objectIS = new ObjectInputStream(buffer);
				i_Message message = (i_Message) objectIS.readObject();
				
				destinationSocket.close();
				publishProgress(message);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
        
		return null;
	}

	@Override
	protected void onCancelled() {
		Log.v(TAG, "onCancelled()");
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.onCancelled();
	}

	@Override
	protected void onProgressUpdate(i_Message... values) {
		super.onProgressUpdate(values);
		//playNotification(mContext, values[0]);


		int type = values[0].getmType();
		String ChatName = values[0].getChatName();
		String Text = values[0].getmText();

		Log.e(TAG, "type = " + type + ", chatname = " + ChatName);
		//String message = values[0].getmText();

		if(State.loadRoleId(mContext) == ROLE_A){
			if(type == i_Message.SERVER_SET ){
				Log.e(TAG, "SERVER_SET");
				ReceiverSetThread.doneSignal.countDown();
				//ReceiverSetThread.doneSignal2.countDown();
				Log.v(TAG, "SERVER_SET2");

				if(State.loadSendMesId(mContext) == REQUEST_MESSAGE){
					handler.postDelayed(Send_OPEN_DIALOG, 1000);
				}else if(State.loadSendMesId(mContext) == COUPON_MESSAGE){
					handler.postDelayed(Send_OPEN_COUPON_DIALOG, 1000);
				}

			}
			if (type == i_Message.RESPONSE_SET){
				Log.e(TAG, "RESPONSE_SET");
				//ReceiverSetThread.doneSignal2.countDown();
				ReceiverSetThread.doneSignal.countDown();
				Log.v(TAG, "RESPONSE_SET2");
			}
			if(type==i_Message.RESPONSE_MESSAGE){
				if (MainFragment.progressDialog != null && MainFragment.progressDialog.isShowing()) {
					MainFragment.progressDialog.dismiss();
				}
				Intent i = new Intent(mContext, A_ResponseActivity.class);
				i.putExtra("Mes", values[0]);
				mContext.startActivity(i);
				//mContext.startActivity(new Intent(mContext, A_ResponseActivity.class));
			}
			//if(type == i_Message.RESPONSE_MESSAGE){

			//}
		}else if(State.loadRoleId(mContext) == ROLE_B){
			if (type == i_Message.OPEN_DIALOG){
				// ポップアップダイアログ表示
				Intent i = new Intent(mContext,CallDialogActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				mContext.startActivity(i);
			}
			if (type == i_Message.OPEN_COUPON_DIALOG){
				// クーポンダイアログ表示
				Intent i = new Intent(mContext,CouponDialogActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				mContext.startActivity(i);
			}
			if(type == i_Message.REQUEST_MESSAGE ){
				//TODO:Change_Dialog
				CallDialogActivity.ChangeDialog(values[0]);
			}
			if(type == i_Message.COUPON_MESSAGE ){
				//TODO:Change_Dialog
				CouponDialogActivity.ChangeCouponDialog(values[0]);
			}
			if(type == i_Message.THX_ADD ){
				int preTitId = State.loadTitId(mContext);
				int CT = State.loadThxCT(mContext)+1;
				TitId = State.CheckTit(mContext, CT);
				State.saveThxCT(mContext, CT);
				//Change Title Popup
				Log.e(TAG,"preIitid="+preTitId+", Titid="+TitId);
				if(preTitId != TitId){
					B_ResponseActivity.ChangeTit(TitId);
				}
			}
		}
		//Log.v(TAG, "mContext="+mContext+" message="+values[0].getmText());
	}

	private final Runnable Send_OPEN_DIALOG = new Runnable() {
		@Override
		public void run() {
			WifiDirectBroadcastReceiver mReceiver = WifiDirectBroadcastReceiver.createInstance();
			i_Message mes = new i_Message(i_Message.OPEN_DIALOG, "", null, State.loadChatName(mContext));
			new i_SendMessageClient(mContext, mReceiver.getOwnerAddr()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
		}
	};
	private final Runnable Send_OPEN_COUPON_DIALOG = new Runnable() {
		@Override
		public void run() {
			WifiDirectBroadcastReceiver mReceiver = WifiDirectBroadcastReceiver.createInstance();
			i_Message mes = new i_Message(i_Message.OPEN_COUPON_DIALOG, "", null, State.loadChatName(mContext));
			new i_SendMessageClient(mContext, mReceiver.getOwnerAddr()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
		}
	};
	@SuppressWarnings("rawtypes")
	public Boolean isActivityRunning(Class activityClass) {
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
			Log.v(TAG, task.baseActivity.getClassName());
            if (activityClass.getCanonicalName().equalsIgnoreCase(task.baseActivity.getClassName()))
                return true;
        }

        return false;
	}
}
