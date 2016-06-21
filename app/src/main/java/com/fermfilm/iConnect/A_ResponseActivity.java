package com.fermfilm.iConnect;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.fermfilm.iConnect.Entities.i_Message;
import com.fermfilm.iConnect.Receivers.WifiDirectBroadcastReceiver;
import com.fermfilm.iConnect.i_AsyncTasks.i_SendMessageClient;
import com.fermfilm.iConnect.i_AsyncTasks.i_SendMessageServer;

import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class A_ResponseActivity extends Activity {
    private static final String TAG = "A_ResponseActivity";
    private static final int ROLE_B = 0;
    private static final int ROLE_A = 1;
    public static TextView res_Name, res_Sex, res_Age, resText;
    public static TextView res_NameLabel, res_SexLabel, res_AgeLabel;
    public static ImageButton res_ManimageButton, res_ClothCL, res_JuponCL;
    public static Button CancelButton,ThanksButton;
    private Context mContext;
    public static Context context;
    public static Bitmap ManImage, ClothCL, JuponCL;
    private WifiP2pManager mManager;
    private Channel mChannel;
    private WifiDirectBroadcastReceiver mReceiver;
    public static WifiDirectBroadcastReceiver receiver;
    private IntentFilter mIntentFilter;
    private EditText edit;
    private static ListView listView;
    private Uri fileUri;
    private String fileURL;
    private Handler handler = new Handler();
    private static i_Message mes;
    public static int lan;
    public String positive, negative;
    private static TypedArray CLimages ;
    private InOutState State;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");
        mContext = this;
        context = this;
        State = new InOutState();
        lan = State.loadLanId(mContext);
        setContentView(R.layout.activity_response_a);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = WifiDirectBroadcastReceiver.createInstance();
        mReceiver.setmActivity(this);

        positive = "YES";

        //testooooooooooooooooooooooo

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        res_ManimageButton = (ImageButton)findViewById(R.id.res_man_image);
        res_NameLabel = (TextView)findViewById(R.id.res_name_label);
        res_Name = (TextView)findViewById(R.id.a_res_name);

        res_SexLabel = (TextView)findViewById(R.id.res_sex_label);
        res_Sex = (TextView)findViewById(R.id.res_sex);

        res_AgeLabel = (TextView)findViewById(R.id.res_age_label);
        res_Age = (TextView)findViewById(R.id.res_age);


        res_ClothCL = (ImageButton)findViewById(R.id.res_cloth_image);


        res_JuponCL = (ImageButton)findViewById(R.id.res_jupon_image);

        resText = (TextView)findViewById(R.id.res_text);
        CancelButton = (Button)findViewById(R.id.res_cancel);


        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                State.saveRoleId(mContext, ROLE_B);
                setDeviceName(State.loadDeviceName(mContext));
                disconnect();
                if (mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_OWNER) {
                    if (mReceiver.server != null) {
                        mReceiver.server.interrupt();
                        Log.v(TAG, "server.interrupt()");
                    }
                }

                /*
                if(mReceiver!=null){
                    unregisterReceiver(mReceiver);
                }else{
                    Log.v(TAG, "Not start Receiver");
                }
                */
                //finish();
                handler.postDelayed(killProcessTask, 1000);
            }
        });
        ThanksButton = (Button)findViewById(R.id.res_thanks);
        ThanksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                State.saveRoleId(mContext, ROLE_B);
                //TODO:unregister
                //Send Thx
                i_Message mes = new i_Message(i_Message.THX_ADD, "", null, State.loadChatName(mContext));
                if (mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_OWNER) {
                    new i_SendMessageServer(mContext, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
                }else if (mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_CLIENT) {
                    new i_SendMessageClient(mContext, mReceiver.getOwnerAddr()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
                }
                setDeviceName(State.loadDeviceName(mContext));
                //Log.e(TAG, "mReceiver = " + mReceiver);
                //TODO:killTask
                /*
                handler.postDelayed(killProcessTask, 3000);
                disconnect();
                */

            }
        });
        Intent intent = getIntent();
        i_Message mes = (i_Message)intent.getExtras().get("Mes");

        ChangeState(mes);
    }
    public static void ChangeState(i_Message message){

        mes = message;
        Log.v(TAG, "chatName=" + mes.getChatName() + ", Age=" + mes.getAgeId()
                + ", Sex=" + mes.getSexId() + ", byte=" + mes.getByteArray());
        res_Name.setText(mes.getChatName());

        switch (lan) {
            case 0:
                setJapanese(mes);
                break;
            case 1:
                setEnglish(mes);
                break;
        }
                /*
                String[] names = TabActivity.getContext().getResources().getStringArray(R.array.ja_main_array);
                res_NameLabel.setText(names[0]);

                res_Sex.setText(mes.getSex());
                res_Age.setText(mes.getAge());
                */

        if (mes.getByteArray() != null) {
            ManImage = mes.byteArrayToBitmap(mes.getByteArray());
            res_ManimageButton.setImageBitmap(ManImage);
            //imageView2.setImageBitmap(bb);
            Log.v(TAG, "message.getByteArray() = " + mes.getByteArray());
            Log.v(TAG, "ManImage = " + ManImage);
        } else {
            Log.v(TAG, "Image is null");
        }

        CLimages = context.getResources().obtainTypedArray(R.array.color_array_drawable);
        ClothCL = ((BitmapDrawable) CLimages.getDrawable(mes.getCloId())).getBitmap();
        res_ClothCL.setImageBitmap(ClothCL);
        JuponCL = ((BitmapDrawable) CLimages.getDrawable(mes.getJupId())).getBitmap();
        res_JuponCL.setImageBitmap(JuponCL);
        /*handler.post(new Runnable() {
            public void run() {

            }
        });
        */


    }
    //Task kill
    private final Runnable killProcessTask = new Runnable() {
        @Override
        public void run() {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    };
    public static void setJapanese(i_Message message){
        String[] names = context.getResources().getStringArray(R.array.ja_main_array);
        res_NameLabel.setText(names[0]);
        res_SexLabel.setText(names[1]);
        res_AgeLabel.setText(names[2]);

        String[] sex = context.getResources().getStringArray(R.array.ja_sex_array);
        String[] age = context.getResources().getStringArray(R.array.ja_age_array);
        res_Sex.setText(sex[message.getSexId()]);
        res_Age.setText(age[message.getAgeId()]);
        resText.setText("この人が助けてくれます。\n探しましょう！");
        CancelButton.setText("中止(解決済み)");
        ThanksButton.setText("お礼");
    }
    public static void setEnglish(i_Message message){

        String[] names = context.getResources().getStringArray(R.array.en_main_array);
        res_NameLabel.setText(names[0]);
        res_SexLabel.setText(names[1]);
        res_AgeLabel.setText(names[2]);

        String[] sex = context.getResources().getStringArray(R.array.en_sex_array);
        String[] age = context.getResources().getStringArray(R.array.en_age_array);
        res_Sex.setText(sex[message.getSexId()]);
        res_Age.setText(age[message.getAgeId()]);
        resText.setText("Please find this person !");
        CancelButton.setText("Cancel(Solved)");
        ThanksButton.setText("Thanks");
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

    public void setDeviceName(final String devName) {
        try {
            Class[] paramTypes = new Class[3];
            paramTypes[0] = WifiP2pManager.Channel.class;
            paramTypes[1] = String.class;
            paramTypes[2] = WifiP2pManager.ActionListener.class;
            Method setDeviceName = mManager.getClass().getMethod(
                    "setDeviceName", paramTypes);
            setDeviceName.setAccessible(true);

            Object arglist[] = new Object[3];
            arglist[0] = mChannel;
            arglist[1] = devName;
            arglist[2] = new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.v(TAG, "setDeviceName succeeded "+devName);
                }
                @Override
                public void onFailure(int reason) {
                    Log.v(TAG, "setDeviceName failed");
                }
            };
            setDeviceName.invoke(mManager, arglist);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onBackPressed() {
        setDeviceName(State.loadDeviceName(mContext));
        disconnect();
        handler.postDelayed(killProcessTask, 1000);
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
        //setDeviceName(State.loadDeviceName(mContext));
        Log.e(TAG, "mReceiver = " + mReceiver);
        //disconnect();
        //android.os.Process.killProcess(android.os.Process.myPid());
    }

}
