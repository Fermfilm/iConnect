package com.fermfilm.iConnect.i_AsyncTasks;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.fermfilm.iConnect.A_ResponseActivity;
import com.fermfilm.iConnect.CallDialogActivity;
import com.fermfilm.iConnect.Entities.i_Message;
import com.fermfilm.iConnect.InOutState;
import com.fermfilm.iConnect.InitThreads.ServerInit;

public class i_SendMessageServer extends AsyncTask<i_Message, i_Message, i_Message>{
	private static final String TAG = "i_SendMessageServer";
	private static final int ROLE_B = 0;
	private static final int ROLE_A = 1;
	private InOutState State;
	private Context mContext;
	private static final int SERVER_PORT = 4446;
	private boolean isMine;


	public i_SendMessageServer(Context context, boolean mine){
		mContext = context;
		isMine = mine;
		State = new InOutState();
		//chat.toast("sendserver="+test);

	}
	@Override
	protected void onPreExecute() {
		Log.v(TAG, "onPreExecute()");
		Log.v(TAG, "mContext="+mContext);
	}

	@Override
	protected i_Message doInBackground(i_Message... msg) {
		Log.v(TAG, "doInBackground");
		//Display le message on the sender before sending it
		publishProgress(msg);

		//Send the message to clients
		try {
			ArrayList<InetAddress> listClients = ServerInit.clients;
			for(InetAddress addr : listClients){

				if(msg[0].getSenderAddress()!=null && addr.getHostAddress().equals(msg[0].getSenderAddress().getHostAddress())){
					return msg[0];
				}
				Log.v(TAG, "addr = "+addr+", SERVER_PORT = "+SERVER_PORT);
				Socket socket = new Socket();
				socket.setReuseAddress(true);
				socket.bind(null);
				Log.v(TAG,"Connect to client: " + addr.getHostAddress());
				socket.connect(new InetSocketAddress(addr, SERVER_PORT));
				Log.v(TAG, "doInBackground: connect to "+ addr.getHostAddress() +" succeeded");
				OutputStream outputStream = socket.getOutputStream();
				new ObjectOutputStream(outputStream).writeObject(msg[0]);
				Log.v(TAG, "doInBackground: write to "+ addr.getHostAddress() +" succeeded");
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "Erreur d'envoie du message");
		}

		return msg[0];
	}

	@Override
	protected void onProgressUpdate(i_Message... values) {
		super.onProgressUpdate(values);
		int type = values[0].getmType();
		Log.e(TAG, "type=" + type + "name=" + values[0].getChatName());

		/*
		if(State.loadRoleId(mContext) == ROLE_A){
			if(type == i_Message.RESPONSE_MESSAGE){
				//Log.v(TAG, "age=" + values[0].getAge() + "byte=" + values[0].getByteArray());
				//A_ResponseActivity.ChangeState(values[0]);
			}
		}else if(State.loadRoleId(mContext) == ROLE_B){
			if(type == i_Message.REQUEST_MESSAGE ){
				Intent i2 = new Intent(mContext,CallDialogActivity.class);
				i2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				i2.putExtra("Mes", values[0]);
				mContext.startActivity(i2);
			}
		}
		*/

	}

	@Override
	protected void onPostExecute(i_Message result) {
		super.onPostExecute(result);
		Log.v(TAG, "onPostExecute");
		int type = result.getmType();
		if(type == i_Message.RESPONSE_MESSAGE){
			//Intent i = new Intent(mContext, A_ResponseActivity.class);
			//mContext.startActivity(i);
			//mContext.startActivity(new Intent(mContext, A_ResponseActivity.class));
			//A_ResponseActivity.ChangeState(result);
		}

	}
	
	@SuppressWarnings("rawtypes")
	public Boolean isActivityRunning(Class activityClass)
	{
		Log.v(TAG, "isActivityRunning");
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