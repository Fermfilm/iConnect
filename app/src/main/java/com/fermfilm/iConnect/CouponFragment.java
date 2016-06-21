package com.fermfilm.iConnect;

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
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.fermfilm.iConnect.Entities.Image;
import com.fermfilm.iConnect.Receivers.WifiDirectBroadcastReceiver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class CouponFragment extends Fragment {
    private static final String TAG = "CouponFragment";
    public ArrayAdapter<CharSequence> genreAdapter;
    public Spinner GenreSpn;
    private static final int ROLE_B = 0;
    private static final int ROLE_A = 1;
    public static final int REQUEST_MESSAGE = 0;
    public static final int COUPON_MESSAGE = 1;
    private static final int REQUEST_CODE = 0;
    private static final int GODS_IMAGE = 0;
    private static final int MAP_IMAGE = 1;
    private static final int G_PICK_IMAGE = 1;
    private static final int G_TAKE_PHOTO = 2;
    private static final int M_PICK_IMAGE = 3;
    private static final int M_TAKE_PHOTO = 4;

    private EditText Gods_Name, Coupon_Detail, Shop_Detail, Coupon_Note;
    public Button CheckBtn, ChargeBtn;
    public ImageButton GodsImgBtn, MapImagBtn;

    public static ProgressDialog progressDialog = null;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private Uri fileUri;
    private ArrayList<Uri> tmpFilesUri;
    private Context mContext;
    private static Context sContext;
    private static int lan;
    public String positive, negative;
    private InOutState State;
    private static InOutState sState;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.v(TAG, "onAttach");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        //setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_coupon, container, false);
    }
    @Override
    public void onStart() {
        super.onStart();
        mContext = getActivity();
        sContext = mContext;
        State = new InOutState();
        sState = new InOutState();

        ChargeBtn = (Button)getActivity().findViewById(R.id.charge_btn);
        ChargeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        CheckBtn = (Button)getActivity().findViewById(R.id.check_btn);
        CheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckDialog();
            }
        });
        GodsImgBtn = (ImageButton)getActivity().findViewById(R.id.gods_img);
        if(!State.loadGodsImg(mContext).equalsIgnoreCase("") ){
            byte[] b = Base64.decode(State.loadGodsImg(mContext), Base64.DEFAULT);
            GodsImgBtn.setScaleType(ImageView.ScaleType.FIT_XY);
            GodsImgBtn.setImageBitmap(BitmapFactory.decodeByteArray(b, 0, b.length));
        }else{
            Drawable godsDrawable = getResources().getDrawable(R.drawable.cl_gray);
            GodsImgBtn.setScaleType(ImageView.ScaleType.FIT_XY);
            GodsImgBtn.setImageDrawable(godsDrawable);

        }
        GodsImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(GODS_IMAGE);
            }
        });
        MapImagBtn = (ImageButton)getActivity().findViewById(R.id.map_image);
        if(!State.loadMapImg(mContext).equalsIgnoreCase("") ){
            byte[] b = Base64.decode(State.loadMapImg(mContext), Base64.DEFAULT);
            MapImagBtn.setScaleType(ImageView.ScaleType.FIT_XY);
            MapImagBtn.setImageBitmap(BitmapFactory.decodeByteArray(b, 0, b.length));
        }else{
            Drawable mapDrawable = getResources().getDrawable(R.drawable.cl_gray);
            MapImagBtn.setScaleType(ImageView.ScaleType.FIT_XY);
            MapImagBtn.setImageDrawable(mapDrawable);

        }
        MapImagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(MAP_IMAGE);
            }
        });
        Gods_Name = (EditText)getActivity().findViewById(R.id.gods_name);
        if(!State.loadGodsName(mContext).equalsIgnoreCase("")){
            Gods_Name.setText(State.loadGodsName(mContext));
        }
        Coupon_Detail = (EditText)getActivity().findViewById(R.id.coupon_detail);
        if(!State.loadCouponDetail(mContext).equalsIgnoreCase("")){
            Coupon_Detail.setText(State.loadCouponDetail(mContext));
        }
        Shop_Detail = (EditText)getActivity().findViewById(R.id.shop_detail);
        if(!State.loadShopDetail(mContext).equalsIgnoreCase("")){
            Shop_Detail.setText(State.loadShopDetail(mContext));
        }
        Coupon_Note = (EditText)getActivity().findViewById(R.id.coupon_note);
        if(!State.loadCouponNote(mContext).equalsIgnoreCase("")){
            Coupon_Note.setText(State.loadCouponNote(mContext));
        }

        genreAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.ja_genre_array, R.layout.spinner_item);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        GenreSpn = (Spinner) getActivity().findViewById(R.id.genre_spn);
        GenreSpn.setAdapter(genreAdapter);
        GenreSpn.setSelection(State.loadGnrId(mContext));
        GenreSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        lan = State.loadLanId(mContext);
//Initialize the list of temporary files URI
        tmpFilesUri = new ArrayList<Uri>();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "onActivityResult");

        switch (requestCode) {
            case G_PICK_IMAGE:
            case M_PICK_IMAGE:
                if (resultCode == getActivity().RESULT_OK && data.getData() != null) {
                    fileUri = data.getData();
                    //State.saveUri(mContext, getPath(mContext, fileUri));
                }
				/*
				Log.v(TAG, "PICK_IMAGE_fileUri="+fileUri);
				Log.v(TAG, "fileUri.getPath()="+fileUri.getPath());
				Log.v(TAG, "loadUri="+State.loadUri(mContext));
				Log.v(TAG, "data.getData()="+data.getData());
				Log.v(TAG, "Uri.parse(loadUri(getApplicationContext()))=" + Uri.parse(State.loadUri(mContext)));
				*/
                break;
            case G_TAKE_PHOTO:
            case M_TAKE_PHOTO:
                if (resultCode == getActivity().RESULT_OK && data.getData() != null) {
                    fileUri = data.getData();
                    tmpFilesUri.add(fileUri);
                    //saveUri
                    //State.saveUri(mContext, fileUri.getScheme());

                }
                //Log.v(TAG, "TAKE_PHOTO_fileUri=" + fileUri);
                break;
        }

        if (resultCode == getActivity().RESULT_OK) {

            Image im = new Image(mContext,fileUri);
            Bitmap bp = scaleDownBitmap(im.getBitmapFromUri(), 170, mContext);
            byte[] bb = im.bitmapToByteArray(bp);

            //byte to text
            String encodedImage = Base64.encodeToString(bb, Base64.DEFAULT);
            //save
            if(requestCode == G_TAKE_PHOTO || requestCode == G_PICK_IMAGE){
                State.saveGodsImg(mContext, encodedImage);
            }else if(requestCode == M_TAKE_PHOTO || requestCode == M_PICK_IMAGE){
                State.saveMapImg(mContext, encodedImage);
            }

            //Check image
            //text to bitmap
            byte[] b2 = Base64.decode(encodedImage, Base64.DEFAULT);
            //Log.v(TAG, "b=" + b + ", bb=" + bb + ", b2=" + b2);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b2, 0, b2.length);
            if(requestCode == G_TAKE_PHOTO || requestCode == G_PICK_IMAGE){
                GodsImgBtn.setImageBitmap(bitmap);
            }else if(requestCode == M_TAKE_PHOTO || requestCode == M_PICK_IMAGE){
                MapImagBtn.setImageBitmap(bitmap);
            }

        }
    }

    public void CheckDialog() {
        //Dialog newDialog = new Dialog(getActivity());
        AlertDialog.Builder newDialog = new AlertDialog.Builder(getActivity());

        switch (lan) {
            case 0:
                newDialog.setTitle("確認");
                newDialog.setMessage("この内容を周りに送信してもよろしいでしょうか？");
                positive = "はい";
                negative = "いいえ";
                break;
            case 1:
                newDialog.setTitle("Check");
                newDialog.setMessage("May I transmit these contents around?");
                positive = "Yes";
                negative = "No";
                break;
        }

        newDialog.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                State.saveRoleId(mContext, ROLE_A);
                //Change DeviceName
                setDeviceName(State.loadDeviceName(mContext) + "_iConnect");
                SaveState();
				/*
				if(mReceiver == null){
					toast("Please start Receiver");
				}else{
				}
				*/
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        Log.v(TAG, "Discovery Failed : " + reasonCode);
                    }
                });
                State.saveSendMesId(mContext, COUPON_MESSAGE);
                ProgressDialog();
            }
        });
        newDialog.setNegativeButton(negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setDeviceName(State.loadDeviceName(mContext));
                dialog.cancel();
            }
        });
        newDialog.show();
    }
    public static void ProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = new ProgressDialog(sContext);
        switch (lan) {
            case 0:
                progressDialog.setTitle("クーポンを配信しています");
                progressDialog.setMessage("キャンセルボタンで配信を中止します");
                break;
            case 1:
                progressDialog.setTitle("Distributing the coupon...");
                progressDialog.setMessage("You cancel a distributing by clicking cancel button.");
                break;
        }
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.v(TAG, "onCancel");
                //TODO:static setDeviceName
//				static_setDeviceName(staticState.loadDeviceName(s_activity));
                sState.saveRoleId(sContext, ROLE_B);

            }
        });
        progressDialog.show();

    }

    public void SaveState() {
        Log.v(TAG, "SaveState()");
        State.saveGodsName(mContext, Gods_Name.getText().toString());
        State.saveCouponDetail(mContext, Coupon_Detail.getText().toString());
        State.saveShopDetail(mContext, Shop_Detail.getText().toString());
        State.saveCouponNote(mContext, Coupon_Note.getText().toString());
        State.saveGnrId(mContext, GenreSpn.getSelectedItemPosition());
        if(State.loadDeviceName(mContext) == ""){
            State.saveDeviceName(mContext,Build.DEVICE);
        }
        Bitmap bitmap = ((BitmapDrawable) GodsImgBtn.getDrawable()).getBitmap();
        State.saveGodsImg(mContext, BitmapToText(bitmap));
        bitmap = ((BitmapDrawable) MapImagBtn.getDrawable()).getBitmap();
        State.saveMapImg(mContext, BitmapToText(bitmap));
    }
    public String BitmapToText(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        //byte to text
        String encodedImage = Base64.encodeToString(bytes, Base64.DEFAULT);
        return encodedImage ;
    }
    public void setDeviceName(final String devName) {
        try {

            if(((TabActivity)getActivity()).getmManager()!=null){
                mManager =((TabActivity)getActivity()).getmManager();
                mChannel = ((TabActivity)getActivity()).getmChannel();
            }
            else{
                Log.v(TAG,"mManager=null");
                return;
            }

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

    //Show the select image popup menu
    public void selectImage(final int img_type) {
        String[] dialogItem_ja = getResources().getStringArray(R.array.ja_select_image2_array);
        String[] dialogItem_en = getResources().getStringArray(R.array.en_select_image2_array);
        int length = dialogItem_ja.length;
        String[] dialogItem = new String[length];
        switch (lan) {
            case 0:
                dialogItem = dialogItem_ja;
                break;
            case 1:
                dialogItem = dialogItem_en;
                break;
        }

        AlertDialog.Builder dialogMenu = new AlertDialog.Builder(getActivity());
        dialogMenu.setItems(dialogItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        if (intent2.resolveActivity(getActivity().getPackageManager()) != null) {
                            if(img_type == GODS_IMAGE){
                                startActivityForResult(intent2, G_TAKE_PHOTO);
                            }else{
                                startActivityForResult(intent2, M_TAKE_PHOTO);
                            }

                        }
                        break;
                    case 1:
                        Intent i = new Intent();
                        i.setAction(Intent.ACTION_GET_CONTENT);
                        i.setType("image/*");
                        if(img_type == GODS_IMAGE){
                            startActivityForResult(i, G_PICK_IMAGE);
                        }else{
                            startActivityForResult(i, M_PICK_IMAGE);
                        }

                        break;

                }
            }
        }).create().show();
    }

    @Override
    public void onPause() {
        super.onPause();
        SaveState();
        Log.v(TAG, "onPause");

    }

    // Scale down
    public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {
        final float densityMultiplier = context.getResources().getDisplayMetrics().density;
        int h= (int) (newHeight*densityMultiplier);
        int w= (int) (h * photo.getWidth()/((double) photo.getHeight()));
        photo=Bitmap.createScaledBitmap(photo, w, h, true);
        return photo;
    }
    //トースト
    private void toast(String text) {
        if (text == null) text = "";
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }
}
