package com.fermfilm.iConnect.i_AsyncTasks;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.fermfilm.iConnect.Entities.i_Message;
import com.fermfilm.iConnect.InOutState;
import com.fermfilm.iConnect.TabActivity;


public class i_SendMessageClient extends AsyncTask<i_Message, i_Message, i_Message>{
	private static final String TAG = "i_SendMessageClient";
	private static final int ROLE_B = 0;
	private static final int ROLE_A = 1;
	private InOutState State;
	private Context mContext;
	private static final int SERVER_PORT = 4445;
	private InetAddress mServerAddr;
	
	public i_SendMessageClient(Context context, InetAddress serverAddr){
		mContext = context;
		mServerAddr = serverAddr;
		State = new InOutState();
	}
	@Override
	protected void onPreExecute() {
		Log.v(TAG, "onPreExecute()");
		Log.v(TAG, "mContext="+mContext);
	}
	
	@Override
	protected i_Message doInBackground(i_Message... msg) {
		Log.v(TAG, "doInBackground");
		Log.v(TAG, "meg="+msg[0].getmText());
		
		//Display le message on the sender before sending it
		publishProgress(msg);
		
		//Send the message
		Socket socket = new Socket();
		try {
			socket.setReuseAddress(true);
			socket.bind(null);
			socket.connect(new InetSocketAddress(mServerAddr, SERVER_PORT));
			Log.v(TAG, "doInBackground: connect succeeded");
			
			OutputStream outputStream = socket.getOutputStream();
			
			new ObjectOutputStream(outputStream).writeObject(msg[0]);
			
		    Log.v(TAG, "doInBackground: send message succeeded");
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if (socket != null) {
		        if (socket.isConnected()) {
		            try {
		                socket.close();
		            } catch (IOException e) {
		            	e.printStackTrace();
		            }
		        }
		    }
		}
		
		return msg[0];
	}

	@Override
	protected void onProgressUpdate(i_Message... msg) {
		super.onProgressUpdate(msg);
		int type = msg[0].getmType();

	}

	@Override
	protected void onPostExecute(i_Message result) {
		super.onPostExecute(result);
		Log.v(TAG, "onPostExecute");
	}
	
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
