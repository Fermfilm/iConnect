package com.fermfilm.iConnect;

/**
 * Created by fermfilm on 2015/08/25.
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fermfilm.iConnect.Entities.i_Message;
import com.fermfilm.iConnect.Receivers.WifiDirectBroadcastReceiver;
import com.fermfilm.iConnect.i_AsyncTasks.i_SendMessageClient;
import com.fermfilm.iConnect.i_AsyncTasks.i_SendMessageServer;
import com.google.android.gms.maps.GoogleMap;

import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class CallDialogActivity extends Activity {
	private static final String TAG = "CallDialogActivity";
	private static final int ROLE_B = 0;
	private static final int ROLE_A = 1;
	private static TextView d_Name, d_Sex, d_Age, d_Req, d_Detail;
	private static TextView d_NameLabel, d_SexLabel, d_AgeLabel, d_ReqLabel;
	public static ImageButton d_ManimageButton, d_ClothCL, d_JuponCL;
	public static Button d_traBtn;
	//private static ImageView imageView2;

	private WifiP2pManager mManager;
	private WifiP2pManager.Channel mChannel;
	private WifiDirectBroadcastReceiver mReceiver;
	public static WifiDirectBroadcastReceiver receiver;
	private IntentFilter mIntentFilter;
	public static String test = null;
	private GoogleMap map;//マップ
	public static Context context;
	private Context mContext;
	private Activity activity;
	public static Bitmap ManImage, ClothCL, JuponCL;
	public AlertDialog.Builder newDialog;
	public static int lan;
	public String positive, negative;
	private static TypedArray CLimages ;
	private InOutState State;
	public Ringtone mRingtone;
	private static int i_Clo = 0, i_Jup = 0;
	private String ret = "";


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate");
		mContext = this;
		context = this;
		activity = this;
		State = new InOutState();

		lan = State.loadLanId(mContext);
		mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		mChannel = mManager.initialize(this, getMainLooper(), null);
		mReceiver = WifiDirectBroadcastReceiver.createInstance();
		mReceiver.setmActivity(this);

		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

		CallDialog();

		i_Message mes = new i_Message(i_Message.RESPONSE_SET, "", null, State.loadChatName(mContext));
		if (mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_OWNER) {
			new i_SendMessageServer(mContext, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
		}else if (mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_CLIENT) {
			new i_SendMessageClient(mContext, mReceiver.getOwnerAddr()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
		}
		/*
		Intent intent = getIntent();
		i_Message mes = (i_Message)intent.getExtras().get("Mes");
		ChangeDialog(mes);
		*/
		//Log.e(TAG,"uni = "+Uri.parse(State.loadUri(mContext)));


		//数字を抜き出す
		String[] names = mContext.getResources().getStringArray(R.array.ja_ringingTime_array);
		String s = names[State.loadRingTimeId(mContext)];
		mRingtone = RingtoneManager.getRingtone(mContext,Uri.parse(State.loadUri(mContext)));
		if(State.loadRingTimeId(mContext)!=0){
			mRingtone.play();
			int i = Integer.parseInt(s.replaceAll("[^0-9]", ""));
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if(mRingtone!=null){
						if (mRingtone.isPlaying())
							mRingtone.stop();
					}
				}
			}, i*1000);
		}



	}

	public static void ChangeDialog(i_Message message){
		Log.e(TAG,"ChangeDialog");
		d_Name.setText(message.getChatName());
		switch (lan) {
			case 0:
				setJapanese(message);
				break;
			case 1:
				setEnglish(message);
				break;
		}

		if( message.getByteArray()!=null ){
			ManImage =message.byteArrayToBitmap(message.getByteArray());
			d_ManimageButton.setImageBitmap(ManImage);
			Log.v(TAG, "Set image");
		}else{
			Log.v(TAG,"Image is null");
		}
		CLimages = context.getResources().obtainTypedArray(R.array.color_array_drawable);
		i_Clo = message.getCloId();
		ClothCL = ((BitmapDrawable) CLimages.getDrawable(i_Clo)).getBitmap();
		d_ClothCL.setImageBitmap(ClothCL);
		i_Jup = message.getJupId();
		JuponCL = ((BitmapDrawable) CLimages.getDrawable(i_Jup)).getBitmap();
		d_JuponCL.setImageBitmap(JuponCL);
		d_Detail.setText(message.getDetailText());
	}

	public boolean onTouchEvent(MotionEvent event)
	{
		if(event.getAction() == MotionEvent.ACTION_OUTSIDE){
			Log.v(TAG, "TOuch outside the dialog ******************** ");
			//CallDialog().cancel;
		}
		return false;
	}

	//Main Body
	public void CallDialog() {
		Log.v(TAG,"CallDialog()");
		/*
		if (newDialog != null && newDialog.isShowing()) {
			newDialog.dismiss();
		}
		*/
		newDialog = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog));
		//Backキーでキャンセル
		newDialog.setCancelable(true);
		//AlertDialog.Builder newDialog = new AlertDialog.Builder(new ContextThemeWrapper(this, ProgressDialog.STYLE_SPINNER));
		// Get the layout inflater
		LayoutInflater inflater = getLayoutInflater();
		//レイアウトのインフレーター
		final View layout = inflater.inflate(R.layout.dialog_mainfragment, null);
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		newDialog.setView(layout);
		switch (lan) {
			case 0:
				newDialog.setTitle("iConnect 近くに助けて欲しい人がいます");
				positive = "助ける!";
				negative = "すみません...";
				break;
			case 1:
				newDialog.setTitle("There is a person wanting you to help me near.");
				positive = "Save!";
				negative = "Sorry...";
				break;
		}

		d_NameLabel = (TextView)layout.findViewById(R.id.d_name_label);
		d_SexLabel = (TextView)layout.findViewById(R.id.d_sex_label);
		d_AgeLabel = (TextView)layout.findViewById(R.id.d_age_label);
		d_ReqLabel = (TextView)layout.findViewById(R.id.d_req_label);


		d_ManimageButton = (ImageButton)layout.findViewById(R.id.d_man_image);
		d_Name = (TextView)layout.findViewById(R.id.d_name);
		d_Sex = (TextView)layout.findViewById(R.id.d_sex);
		d_Age = (TextView)layout.findViewById(R.id.d_age);
		d_Req = (TextView)layout.findViewById(R.id.d_req);
		d_ClothCL = (ImageButton)layout.findViewById(R.id.d_cloth_image);
		d_JuponCL = (ImageButton)layout.findViewById(R.id.d_jupon_image);
		d_Detail = (TextView)layout.findViewById(R.id.d_detail_text);
		d_traBtn = (Button)layout.findViewById(R.id.translation_button);
		//TODO:Translation
		/*
		d_traBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Object content = null;
				try {
					// 英単語1個ならurlエンコードは必要ないが、文のため必要
					String encStr = URLEncoder.encode(d_Detail.getText().toString().trim(), "UTF-8");
					URL	url = null;
					if(lan == 0){
						url = new URL("http://api.microsofttranslator.com/V2/" +
								"Http.svc/Translate?appid=AD697D10EC7A3751BB125AB58715693EBCB224E2&" +
								"from=en&to=ja&text=" + encStr);
					}else if(lan == 1){
						url = new URL("http://api.microsofttranslator.com/V2/" +
								"Http.svc/Translate?appid=AD697D10EC7A3751BB125AB58715693EBCB224E2&" +
								"from=ja&to=en&text=" + encStr);
					}


					if (url != null) {
						// InputStreamオブジェクトを返す
						Log.d(TAG,"url = "+url);
						content = url.getContent();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					// SAXパーサーファクトリを生成
					SAXParserFactory spfactory = SAXParserFactory.newInstance();
					// SAXパーサーを生成
					SAXParser parser = null;
					parser = spfactory.newSAXParser();
					parser.parse((InputStream)content, new DefaultHandler() {
						// charactersメソッドをオーバーライド
						public void characters(char[] ch, int offset, int length) {
							ret = new String(ch, offset, length);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}

				d_Detail.setText(ret);


			}
		});
		*/
		newDialog.setPositiveButton(positive, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(mRingtone!=null){
					if(mRingtone.isPlaying()){
						mRingtone.stop();
					}
				}

				//Send my status
				i_Message mes = new i_Message(i_Message.RESPONSE_MESSAGE, "", null, State.loadChatName(mContext));
				if (!State.loadImage(mContext).equalsIgnoreCase("")) {
					//Log.v(TAG, "Bitmap = " + loadImage(getApplicationContext()));
					byte[] b = Base64.decode(State.loadImage(mContext), Base64.DEFAULT);
					mes.setByteArray(b);
				} else {
					Log.v(TAG, "Image is null");
				}
				mes.setSexId(State.loadSexId(mContext));
				mes.setAgeId(State.loadAgeId(mContext));
				mes.setCloId(State.loadClothId(mContext));
				mes.setJupId(State.loadJuponId(mContext));
				//Send information
				Log.e(TAG, "mReceiver.isGroupeOwner() == " + mReceiver.isGroupeOwner());
				if (mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_OWNER) {
					new i_SendMessageServer(mContext, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
				}else if (mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_CLIENT) {
					new i_SendMessageClient(mContext, mReceiver.getOwnerAddr()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
				}


				//Send myActivity
				Intent i = new Intent(mContext, B_ResponseActivity.class);

				i.putExtra("Name", d_Name.getText().toString());
				i.putExtra("Sex", d_Sex.getText().toString());
				i.putExtra("Age", d_Age.getText().toString());
				i.putExtra("Req", d_Req.getText().toString());
				i.putExtra("Det", d_Detail.getText().toString());
				if (ManImage != null) {
					i.putExtra("Img", scaleDownBitmap(ManImage, 170, mContext));
				} else {
					Log.e(TAG, "ManImage is null");
				}
				i.putExtra("Clo",i_Clo);
				i.putExtra("Jup",i_Jup);
				/*i.putExtra("Clo", ClothCL);
				i.putExtra("Jup", JuponCL);*/
				//TODO:startActivity bug
				//Log.v(TAG, "mContent = "+mContext+", activity = "+activity);
				//i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

				startActivity(i);


			}
		});
		newDialog.setNegativeButton(negative, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		newDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				// Resultがキャンセルのとき呼ばれる
				Log.d(TAG, "onCancel");
				if(mRingtone!=null){
					if(mRingtone.isPlaying()){
						mRingtone.stop();
					}
				}

				disconnect();
				if (mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_OWNER) {
					if (mReceiver.server != null) {
						mReceiver.server.interrupt();
					}
				}
				if(mReceiver!=null){
					//unregisterReceiver(mReceiver);
				}else{
					Log.v(TAG, "Not start Receiver");
				}
				//finish();
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		});
		newDialog.show().setCanceledOnTouchOutside(true);



	}
	public static void setJapanese(i_Message message){
		String[] names = context.getResources().getStringArray(R.array.ja_main_array);
		d_NameLabel.setText(names[0]);
		d_SexLabel.setText(names[1]);
		d_AgeLabel.setText(names[2]);
		d_ReqLabel.setText(names[3]);
		d_traBtn.setText(names[9]);
		String[] sex = context.getResources().getStringArray(R.array.ja_sex_array);
		String[] age = context.getResources().getStringArray(R.array.ja_age_array);
		String[] req = context.getResources().getStringArray(R.array.ja_req_array);
		d_Sex.setText(sex[message.getSexId()]);
		d_Age.setText(age[message.getAgeId()]);
		d_Req.setText(req[message.getReqId()]);
	}
	public static void setEnglish(i_Message message){
		String[] names = context.getResources().getStringArray(R.array.en_main_array);
		d_NameLabel.setText(names[0]);
		d_SexLabel.setText(names[1]);
		d_AgeLabel.setText(names[2]);
		d_ReqLabel.setText(names[3]);
		d_traBtn.setText(names[9]);
		String[] sex = context.getResources().getStringArray(R.array.en_sex_array);
		String[] age = context.getResources().getStringArray(R.array.en_age_array);
		String[] req = context.getResources().getStringArray(R.array.en_req_array);
		d_Sex.setText(sex[message.getSexId()]);
		d_Age.setText(age[message.getAgeId()]);
		d_Req.setText(req[message.getReqId()]);
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.v(TAG, "onResume");
	}
	@Override
	public void onPause() {
		super.onPause();
		Log.v(TAG, "onPause");
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.v(TAG, "onDestroy");
	}

	public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {

		final float densityMultiplier = context.getResources().getDisplayMetrics().density;

		int h= (int) (newHeight*densityMultiplier);
		int w= (int) (h * photo.getWidth()/((double) photo.getHeight()));

		photo=Bitmap.createScaledBitmap(photo, w, h, true);

		return photo;
	}

	public void disconnect() {
		mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
			@Override
			public void onFailure(int reasonCode) {
				Log.v(TAG, "Disconnect failed. Reason :" + reasonCode);
			}
			@Override
			public void onSuccess() {
				Log.v(TAG, "Disconnect succeeded");
			}
		});
	}


}