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
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fermfilm.iConnect.Receivers.WifiDirectBroadcastReceiver;

import android.net.wifi.p2p.WifiP2pManager.Channel;
import java.util.List;

public class B_ResponseActivity extends Activity {
    private static final String TAG = "B_ResponseActivity";
    private static final int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;
    private static final int FILL_PARENT = ViewGroup.LayoutParams.FILL_PARENT;
    private TextView res_Name, res_Sex, res_Age, res_Req, res_Detail;
    private TextView res_NameLabel, res_SexLabel, res_AgeLabel, res_ReqLabel, res_Text;
    public ImageButton res_ManimageButton, res_ClothCL, res_JuponCL;
    public static Bitmap ClothCL, JuponCL;
    public Button CancelButton,ThanksButton;
    private Context mContext;
    private static Context sContext;
    public static Bitmap BitmapImage;

    private WifiP2pManager mManager;
    private Channel mChannel;
    private WifiDirectBroadcastReceiver mReceiver;
    public static WifiDirectBroadcastReceiver receiver;
    private IntentFilter mIntentFilter;
    private EditText edit;
    private static ListView listView;
    private Uri fileUri;
    private String fileURL;
    private int lan;
    private static int slan;
    private InOutState State;
    private static InOutState sState;
    private TypedArray CLimages;
    private static TypedArray TitImgs ;
    private static AlertDialog s_dlg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        sContext = this;
        State = new InOutState();
        sState = new InOutState();
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = WifiDirectBroadcastReceiver.createInstance();
        mReceiver.setmActivity(this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        setContentView(R.layout.activity_response_b);
        Intent intent = getIntent();


        res_NameLabel = (TextView)findViewById(R.id.res_name_label);
        res_SexLabel = (TextView)findViewById(R.id.res_sex_label);
        res_AgeLabel = (TextView)findViewById(R.id.res_age_label);
        res_ReqLabel = (TextView)findViewById(R.id.res_req_label);

        res_Text = (TextView)findViewById(R.id.res_text);

        CancelButton =(Button)findViewById(R.id.res_cancel);

        lan = State.loadLanId(mContext);
        slan = lan;
        switch (lan) {
            case 0:
                setJapanese();
                break;
            case 1:
                setEnglish();
                break;
        }

        res_ManimageButton = (ImageButton)findViewById(R.id.res_man_image);
        res_Name = (TextView)findViewById(R.id.res_name);
        res_Sex = (TextView)findViewById(R.id.res_sex);
        res_Age = (TextView)findViewById(R.id.res_age);
        res_Req = (TextView)findViewById(R.id.res_req);
        res_ClothCL = (ImageButton)findViewById(R.id.res_cloth_image);
        res_JuponCL = (ImageButton)findViewById(R.id.res_jupon_image);
        res_Detail = (TextView)findViewById(R.id.res_detail);


        if(intent != null) {
            if(intent.getExtras().get("Img") != null){
                res_ManimageButton.setImageBitmap((Bitmap)intent.getExtras().get("Img"));
            }else{
                res_ManimageButton.setImageResource(R.drawable.unknown);
            }
            res_Name.setText(intent.getStringExtra("Name"));
            res_Sex.setText(intent.getStringExtra("Sex"));
            res_Age.setText(intent.getStringExtra("Age"));
            res_Req.setText(intent.getStringExtra("Req"));

            CLimages = mContext.getResources().obtainTypedArray(R.array.color_array_drawable);
            ClothCL = ((BitmapDrawable) CLimages.getDrawable(intent.getIntExtra("Clo",0 ))).getBitmap();
            res_ClothCL.setImageBitmap(ClothCL);
            JuponCL = ((BitmapDrawable) CLimages.getDrawable(intent.getIntExtra("Jup", 0))).getBitmap();
            res_JuponCL.setImageBitmap(JuponCL);
        /*    res_ClothCL.setImageBitmap((Bitmap) intent.getExtras().get("Clo"));
            res_JuponCL.setImageBitmap((Bitmap) intent.getExtras().get("Jup"));
        */res_Detail.setText(intent.getStringExtra("Det"));
        }



        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });


    }

    public static void ChangeTit(int TitId){
        AlertDialog.Builder adb = new AlertDialog.Builder(sContext);
        TitImgs = sContext.getResources().obtainTypedArray(R.array.title_array_drawable);

        String[] titnames =  {};
        //String Messages =  null;
        TextView DlgTit = new TextView(sContext);

        LinearLayout ll = new LinearLayout(sContext);
        ll.setOrientation(LinearLayout.VERTICAL);
        LinearLayout ll2 = new LinearLayout(sContext);
        ll2.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout ll3 = new LinearLayout(sContext);
        ll3.setOrientation(LinearLayout.VERTICAL);
        TextView Title = new TextView(sContext);
        TextView Messages = new TextView(sContext);
        ImageView Img = new ImageView(sContext);
        ImageView Img2 = new ImageView(sContext);
        switch (slan){
            case 0:
                DlgTit.setText("おめでとう！");
                titnames = sContext.getResources().getStringArray(R.array.ja_title_array);
                Title.setText("「" + titnames[TitId] + "」");
                Messages.setText( "にランクアップしました！");
                break;
            case 1:
                DlgTit.setText("Congratulations！");
                titnames = sContext.getResources().getStringArray(R.array.en_title_array);
                Title.setText("「"+titnames[TitId]+"」");
                Messages.setText( "One rank up！");
                break;
        }
        Title.setTextSize(30);
        Title.setGravity(Gravity.CENTER);
        Title.setTextColor(Color.RED);
        Messages.setTextSize(20);
        Messages.setGravity(Gravity.CENTER);
        Img.setImageResource(R.drawable.party_animal);

        Img2.setImageBitmap(((BitmapDrawable) TitImgs.getDrawable(TitId)).getBitmap());
        Img2.setScaleType(ImageView.ScaleType.FIT_XY);
        Img2.setMaxHeight(500);
        Img2.setMaxWidth(500);
        Img2.setAdjustViewBounds(true);

        //ll2.addView(Img2, new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));


        ViewGroup.LayoutParams p1 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        ll.addView(ll2);
        ll.addView(Img);
        ll2.addView(Img2);
        ll2.addView(ll3, p1);

        ll3.setGravity(Gravity.CENTER);
        ll3.addView(Title);
        ll3.addView(Messages);



        DlgTit.setGravity(Gravity.CENTER);
        DlgTit.setTextSize(30);
        adb.setCustomTitle(DlgTit);
        adb.setView(ll);
        //adb.setMessage(Messages);
        adb.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        s_dlg.dismiss();
                    }
                });
        /*
        // アラートダイアログの中立ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
        alertDialogBuilder.setNeutralButton("中立",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        // アラートダイアログの否定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
        alertDialogBuilder.setNegativeButton("否定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        // アラートダイアログのキャンセルが可能かどうかを設定します
        */
        adb.setCancelable(true);
        //AlertDialog alertDialog = adb.create();
        // アラートダイアログを表示します
        s_dlg = adb.show();
    }



    public void setJapanese(){
        String[] name = this.getResources().getStringArray(R.array.ja_main_array);
        res_NameLabel.setText(name[0]);
        res_SexLabel.setText(name[1]);
        res_AgeLabel.setText(name[2]);
        res_ReqLabel.setText(name[3]);

        CancelButton.setText("中止(解決済み)");
        res_Text.setText("この人を見つけて\n助けてあげてください！");
    }
    public void setEnglish(){
        String[] name = this.getResources().getStringArray(R.array.en_main_array);
        res_NameLabel.setText(name[0]);
        res_SexLabel.setText(name[1]);
        res_AgeLabel.setText(name[2]);
        res_ReqLabel.setText(name[3]);

        CancelButton.setText("Cancel(Solved)");
        res_Text.setText("Please find this person !");
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

    @Override
    public void onBackPressed() {
        disconnect();
        android.os.Process.killProcess(android.os.Process.myPid());
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
        //android.os.Process.killProcess(android.os.Process.myPid());
    }

}
