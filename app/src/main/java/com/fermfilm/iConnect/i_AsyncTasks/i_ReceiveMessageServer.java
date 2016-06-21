package com.fermfilm.iConnect.i_AsyncTasks;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fermfilm.iConnect.A_ResponseActivity;
import com.fermfilm.iConnect.B_ResponseActivity;
import com.fermfilm.iConnect.CallDialogActivity;
import com.fermfilm.iConnect.CouponDialogActivity;
import com.fermfilm.iConnect.Entities.i_Message;
import com.fermfilm.iConnect.InOutState;
import com.fermfilm.iConnect.MainFragment;
import com.fermfilm.iConnect.ReceiverSetThread;

public class i_ReceiveMessageServer extends i_AbstractReceiver {
	private static final int SERVER_PORT = 4445;
	private static final String TAG = "i_ReceiveMessageServer";
	private static final int ROLE_B = 0;
	private static final int ROLE_A = 1;
	private InOutState State;
	private Context mContext;
	private Activity activity;
	private ServerSocket serverSocket;
	private int Counter = 0;
	private int TitId;

	public i_ReceiveMessageServer(Context context){
		mContext = context;
		State = new InOutState();
	}

	@Override
	protected void onPreExecute() {
		Log.v(TAG, "onPreExecute()");
		//Log.v(TAG, "mContext="+mContext);
		if(State.loadRoleId(mContext) == ROLE_A){
		}else if(State.loadRoleId(mContext) == ROLE_B){
			/*
			// ポップアップダイアログ表示
			//Log.e(TAG, "name = "+values[0].getChatName());
			Intent i = new Intent(mContext,CallDialogActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			mContext.startActivity(i);
			*/
		}
	}
	@Override
	protected Void doInBackground(Void... params) {

		try {
			Log.v(TAG, "doInBackground");
			serverSocket = new ServerSocket(SERVER_PORT);
			while(true){
				Socket clientSocket = serverSocket.accept();				
				
				InputStream inputStream = clientSocket.getInputStream();				
				ObjectInputStream objectIS = new ObjectInputStream(inputStream);
				i_Message message = (i_Message) objectIS.readObject();
				
				//Add the InetAdress of the sender to the message
				InetAddress senderAddr = clientSocket.getInetAddress();
				message.setSenderAddress(senderAddr);
				
				clientSocket.close();
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
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.onCancelled();
	}

	@Override
	protected void onProgressUpdate(i_Message... values) {
		super.onProgressUpdate(values);
		Log.e(TAG, "onProgressUpdate");
		int type = values[0].getmType();
		if(State.loadRoleId(mContext) == ROLE_A){
			if(type==i_Message.RESPONSE_SET){
				Log.e(TAG, "RESPONSE_SET");
				//ReceiverSetThread.doneSignal2.countDown();
				ReceiverSetThread.doneSignal.countDown();
				Log.v(TAG, "RESPONSE_SET2");
			}
			if(type==i_Message.RESPONSE_MESSAGE){
			/**/
				if (MainFragment.progressDialog != null && MainFragment.progressDialog.isShowing()) {
					MainFragment.progressDialog.dismiss();
				}
				Intent i = new Intent(mContext, A_ResponseActivity.class);
				i.putExtra("Mes", values[0]);
				mContext.startActivity(i);
				//mContext.startActivity(new Intent(mContext, A_ResponseActivity.class));
			}
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
				CallDialogActivity.ChangeDialog(values[0]);
			}
			if(type == i_Message.COUPON_MESSAGE ){
				CouponDialogActivity.ChangeCouponDialog(values[0]);
			}

			if(type == i_Message.THX_ADD ){
				int preTitId = State.loadTitId(mContext);
				int CT = State.loadThxCT(mContext)+1;
				TitId = State.CheckTit(mContext, CT);
				State.saveThxCT(mContext, CT);
				//Change Title Popup
				if(preTitId != TitId){
					B_ResponseActivity.ChangeTit(TitId);
				}
			}

		}

		Log.v(TAG, "mContext="+mContext+" type ="+type+" name="+values[0].getChatName());
		new i_SendMessageServer(mContext, false).executeOnExecutor(THREAD_POOL_EXECUTOR, values);
	}
	
}
