package com.fermfilm.iConnect;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.TextView;

import com.fermfilm.iConnect.Entities.i_Message;
import com.fermfilm.iConnect.Receivers.WifiDirectBroadcastReceiver;
import com.fermfilm.iConnect.i_AsyncTasks.i_SendMessageClient;
import com.fermfilm.iConnect.i_AsyncTasks.i_SendMessageServer;
import com.google.android.gms.maps.GoogleMap;

import org.w3c.dom.Text;

public class CouponDialogActivity extends Activity{
    private static final String TAG = "CouponDialogActivity";
    private static final int ROLE_B = 0;
    private static final int ROLE_A = 1;
    private static TextView d_Name, d_Sex, d_Age, d_Req, d_Detail;
    private static TextView d_NameLabel, d_SexLabel, d_AgeLabel, d_ReqLabel;
    public static ImageButton d_ManimageButton, d_ClothCL, d_JuponCL;
    public static Button d_traBtn;
    //private static ImageView imageView2;
    private static TextView d_Gods_Name, d_Coupon_Detail, d_Shop_Detail, d_Coupon_Note;
    public static ImageButton d_GodsImgBtn, d_MapImagBtn;

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private WifiDirectBroadcastReceiver mReceiver;
    public static WifiDirectBroadcastReceiver receiver;
    private IntentFilter mIntentFilter;
    public static String test = null;
    private GoogleMap map;//マップ
    public static Context sContext;
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
        sContext = this;
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

        CallCouponDialog();

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
        mRingtone = RingtoneManager.getRingtone(mContext, Uri.parse(State.loadUri(mContext)));
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

    public static void ChangeCouponDialog(i_Message message){
        Log.e(TAG,"ChangeCouponDialog");
        d_Gods_Name.setText(message.getGodsName());
        d_Coupon_Detail.setText(message.getCouponDetail());
        d_Shop_Detail.setText(message.getShopDetail());
        d_Coupon_Note.setText(message.getCouponNote());
        if( message.getByteArray()!=null ){
            d_GodsImgBtn.setScaleType(ImageView.ScaleType.FIT_XY);
            d_GodsImgBtn.setImageBitmap(message.byteArrayToBitmap(message.getByteArray()));
        }
        if( message.getByteArray2()!=null ){
            d_MapImagBtn.setScaleType(ImageView.ScaleType.FIT_XY);
            d_MapImagBtn.setImageBitmap(message.byteArrayToBitmap(message.getByteArray2()));
        }
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
    public void CallCouponDialog() {
        Log.v(TAG,"CallCouponDialog()");
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
        final View layout = inflater.inflate(R.layout.dialog_coupon, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        newDialog.setView(layout);
        switch (lan) {
            case 0:
                newDialog.setTitle("iConnect 耳寄り情報をお届けします！");
                positive = "保留";
                negative = "削除";
                break;
            case 1:
                newDialog.setTitle("iConnect Send a WHAT'S COOL");
                positive = "Save";
                negative = "Delete";
                break;
        }


        d_GodsImgBtn = (ImageButton)layout.findViewById(R.id.d_gods_img);
        d_MapImagBtn = (ImageButton)layout.findViewById(R.id.d_map_image);
        d_Gods_Name = (TextView)layout.findViewById(R.id.d_gods_name);
        d_Coupon_Detail = (TextView)layout.findViewById(R.id.d_coupon_detail);
        d_Shop_Detail = (TextView)layout.findViewById(R.id.d_shop_detail);
        d_Coupon_Note = (TextView)layout.findViewById(R.id.d_coupon_note);

        newDialog.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

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
