package com.fermfilm.iConnect;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fermfilm.iConnect.Entities.Image;
import com.fermfilm.iConnect.Entities.i_Message;
import com.fermfilm.iConnect.Receivers.WifiDirectBroadcastReceiver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements WifiP2pManager.ConnectionInfoListener {
	private static final String TAG = "MainFragment";
	private static final int ROLE_B = 0;
	private static final int ROLE_A = 1;
	private static final int REQUEST_CODE = 0;
	public static final int REQUEST_MESSAGE = 0;
	public static final int COUPON_MESSAGE = 1;

	private static final int PICK_IMAGE = 1;
	private static final int TAKE_PHOTO = 2;
	private static final int REQUEST_GALLERY = 3;
	private static final int PERSON_IMAGE = 0;
	private static final int CLOTH_COLOR = 1;
	private static final int JUPON_COLOR = 2;
	private static int MODE = 0;
	private EditText Name, DetailEdit;
	private TextView NameLabel,SexLabel,AgeLabel,ReqLabel, TitLabel, DetailLabel;
	private TextView d_Name, d_Sex, d_Age, d_Req;
	private TextView d_NameLabel, d_SexLabel, d_AgeLabel, d_ReqLabel, d_detailText;
	//Other Activity send
	public static String ManImage;
	public static Bitmap BitmapImage;

	private Uri fileUri;
	private ArrayList<Uri> tmpFilesUri;
	public Button CheckBtn, VoiceBtn;
	public ImageButton ManimageButton, d_ManimageButton;
	public ImageButton ClothCL, JuponCL, d_ClothCL, d_JuponCL, Tit_IMG;
	public ArrayAdapter<CharSequence> sexAdapter, ageAdapter, reqAdapter;
	public Spinner SexSpinner;
	public Spinner AgeSpinner;
	public Spinner ReqSpinner;
	public static ProgressDialog progressDialog = null;
	private Dialog s_Dlg = null;
	private Dialog v_Dlg = null;
	private TypedArray CLimages ;
	private TypedArray TitImgs ;
	private WifiP2pManager mManager;
	private WifiP2pManager.Channel mChannel;
	private static WifiP2pManager sManager;
	private static WifiP2pManager.Channel sChannel;
	private WifiDirectBroadcastReceiver mReceiver;
	private IntentFilter mIntentFilter;
	private Context mContext;
	private static Context s_activity;
	private static int lan;
	private int ThxCT;
	private int TitId;
	public String positive, negative;
	private static final int RQ_CONNECT_DEVICE = 10;
	//private TabActivity tabActivity;
	private static TabActivity tabActivity;
	private InOutState State;
	private static InOutState staticState;
	private static TypedArray sTitImgs ;
	private static AlertDialog s_dlg;


	public interface OnOkBtnClickListener {
		public void onOkClicked();
	}
	private OnOkBtnClickListener mListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.v(TAG,"onAttach");

		//This devicename...
		Log.e(TAG,"DeviceName=");
		if (activity instanceof OnOkBtnClickListener == false) {
			throw new ClassCastException("activity has no OnOkBtnClickListener.");
		}
		mListener = ((OnOkBtnClickListener) activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		//setHasOptionsMenu(true);
		//bundle1 = savedInstanceState;
		return inflater.inflate(R.layout.fragment_main, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.v(TAG, "onActivityCreated");
		// 再生成時にはsavedInstanceStateがnullじゃない
		if (savedInstanceState != null) {

		}
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

			default:
				break;
		}

		return false;
	}
	@Override
	public void onStart() {
		super.onStart();
		mContext = getActivity();
		s_activity = mContext;
		State = new InOutState();
		staticState = State;
		CLimages = mContext.getResources().obtainTypedArray(R.array.color_array_drawable);
		TitImgs = mContext.getResources().obtainTypedArray(R.array.title_array_drawable);
		//CLimages.getDrawable(i);

		/**/
		//imageView = (ImageView)getActivity().findViewById(R.id.imageView);
		NameLabel = (TextView)getActivity().findViewById(R.id.name_label);
		SexLabel = (TextView)getActivity().findViewById(R.id.sex_label);
		AgeLabel = (TextView)getActivity().findViewById(R.id.age_label);
		ReqLabel = (TextView)getActivity().findViewById(R.id.request_label);
		//ClothLabel = (TextView)getActivity().findViewById(R.id.cloth_label);
		//JuponLabel = (TextView)getActivity().findViewById(R.id.jupon_label);
		TitLabel = (TextView)getActivity().findViewById(R.id.title_label);
		DetailLabel = (TextView)getActivity().findViewById(R.id.detail_label);
		VoiceBtn = (Button) getActivity().findViewById(R.id.voice_btn);
		CheckBtn = (Button) getActivity().findViewById(R.id.check_btn);

		ThxCT = State.loadThxCT(mContext);
		TitId = State.CheckTit(mContext, ThxCT);
		State.saveTitId(mContext, TitId);

		//Set Language
		lan = State.loadLanId(mContext);
		switch (lan) {
			case 0:
				setJapanese();
				break;
			case 1:
				setEnglish();
				break;
		}
		//Set the chat name
		Name = (EditText)getActivity().findViewById(R.id.name);
		//Name.setText(loadChatName(mContext));
		Name.setText(State.loadChatName(mContext));
		//Set the image
		ManimageButton = (ImageButton)getActivity().findViewById(R.id.man_image);
		ManImage =State.loadImage(mContext);
		//ManImage =State.loadImage(mContext);
		if( !ManImage.equalsIgnoreCase("") ){
			byte[] b = Base64.decode(ManImage, Base64.DEFAULT);
			BitmapImage = BitmapFactory.decodeByteArray(b, 0, b.length);
			ManimageButton.setImageBitmap(BitmapImage);
		}
		ManimageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selectImage();
			}
		});
		//Initialize the list of temporary files URI
		tmpFilesUri = new ArrayList<Uri>();
		//set the check button
		CheckBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//saveState
				if (mListener != null) {
					//Click btn
					mListener.onOkClicked();
				}
				SaveState();
				CheckDialog();


				//RingtoneManager ringtoneManager = new RingtoneManager(getActivity().getApplicationContext());
				//Ringtone mRingtone = ringtoneManager.getRingtone(State.loadAlmId(mContext));
				//mRingtone.play();

			}
		});

		SexSpinner = (Spinner) getActivity().findViewById(R.id.sex_spinner);
		SexSpinner.setAdapter(sexAdapter);
		SexSpinner.setSelection(State.loadSexId(mContext));
		//SexSpinner.setSelection(State.loadSexId(mContext));
		SexSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Spinner spinner = (Spinner) parent;
				//Log.v(TAG, Integer.toString(spinner.getSelectedItemPosition()));
				//Log.v(TAG, (String) SexSpinner.getSelectedItem());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});


		AgeSpinner = (Spinner) getActivity().findViewById(R.id.age_spinner);
		AgeSpinner.setAdapter(ageAdapter);
		AgeSpinner.setSelection(State.loadAgeId(mContext));
		AgeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		ReqSpinner = (Spinner) getActivity().findViewById(R.id.request_spinner);
		ReqSpinner.setAdapter(reqAdapter);
		ReqSpinner.setSelection(State.loadReqId(mContext));
		ReqSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		ClothCL = (ImageButton)getActivity().findViewById(R.id.cloth_image);
		ClothCL.setImageBitmap(((BitmapDrawable) CLimages.getDrawable(State.loadClothId(mContext))).getBitmap());
		ClothCL.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selectDialog(CLOTH_COLOR);
			}
		});

		JuponCL = (ImageButton)getActivity().findViewById(R.id.jupon_image);
		JuponCL.setImageBitmap(((BitmapDrawable) CLimages.getDrawable(State.loadJuponId(mContext))).getBitmap());
		JuponCL.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selectDialog(JUPON_COLOR);
			}
		});



		Tit_IMG = (ImageButton)getActivity().findViewById(R.id.title_image);
		Tit_IMG.setScaleType(ImageView.ScaleType.FIT_XY);
		Tit_IMG.setImageBitmap(((BitmapDrawable) TitImgs.getDrawable(State.loadTitId(mContext))).getBitmap());
		Tit_IMG.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				State.saveThxCT(mContext, 0);
				State.saveTitId(mContext, State.CheckTit(mContext, State.loadThxCT(mContext)));
				Tit_IMG.setImageBitmap(((BitmapDrawable) TitImgs.getDrawable(State.loadTitId(mContext))).getBitmap());
				if(lan == 0){
					String[] titnames = getResources().getStringArray(R.array.ja_title_array);
					TitLabel.setText(titnames[State.loadTitId(mContext)]);
				}else if(lan == 1){
					String[] titnames = getResources().getStringArray(R.array.en_title_array);
					TitLabel.setText(titnames[State.loadTitId(mContext)]);
				}
			}
		});
		if(lan == 0){
			String[] titnames = getResources().getStringArray(R.array.ja_title_array);
			TitLabel.setText(titnames[State.loadTitId(mContext)]);
		}else if(lan == 1){
			String[] titnames = getResources().getStringArray(R.array.en_title_array);
			TitLabel.setText(titnames[State.loadTitId(mContext)]);
		}
		DetailEdit= (EditText)getActivity().findViewById(R.id.detail_edit);
		DetailEdit.setText(State.loadDetail(mContext));

		//voice recognition
		VoiceBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					// インテント作成
					Intent intent = new Intent(
							RecognizerIntent.ACTION_RECOGNIZE_SPEECH); // ACTION_WEB_SEARCH
					intent.putExtra(
							RecognizerIntent.EXTRA_LANGUAGE_MODEL,
							RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
					intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "VoiceRec");

					// インテント発行
					startActivityForResult(intent, REQUEST_CODE);
				} catch (ActivityNotFoundException e) {
					// このインテントに応答できるアクティビティがインストールされていない場合
					Log.e(TAG, "ActivityNotFoundException");
				}
			}
		});

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
		SaveState();
		//chatName = loadChatName(mContext);
		//ManImage = loadImage(mContext);
		//DeviceName = loadDeviceName(mContext);
		//if(bundle1!=null)SaveState(bundle1);

	}

	public void SaveState() {
		Log.v(TAG, "SaveState()");
		State.saveChatName(mContext, Name.getText().toString());
		State.saveDetail(mContext, DetailEdit.getText().toString());
		State.saveSexId(mContext, SexSpinner.getSelectedItemPosition());
		State.saveAgeId(mContext, AgeSpinner.getSelectedItemPosition());
		State.saveReqId(mContext, ReqSpinner.getSelectedItemPosition());
		if(State.loadDeviceName(mContext) == ""){
			State.saveDeviceName(mContext,Build.DEVICE);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.v(TAG, "onActivityResult");

		switch (requestCode) {
			case PICK_IMAGE:
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
			case TAKE_PHOTO:
				if (resultCode == getActivity().RESULT_OK && data.getData() != null) {
					fileUri = data.getData();
					tmpFilesUri.add(fileUri);
					//saveUri
					//State.saveUri(mContext, fileUri.getScheme());

				}
				//Log.v(TAG, "TAKE_PHOTO_fileUri=" + fileUri);
				break;
		}
		if (requestCode == REQUEST_CODE &&resultCode == getActivity().RESULT_OK) {
			// Voice word list
			ArrayList<String> results = data.getStringArrayListExtra(
					RecognizerIntent.EXTRA_RESULTS);
			selectVoice(results);
		}
		else if (resultCode == getActivity().RESULT_OK) {
			//Log.v(TAG, "RESULT_OK");
			Image im = new Image(mContext,fileUri);
			Bitmap bp = scaleDownBitmap(im.getBitmapFromUri(), 170, mContext);
			byte[] bb = im.bitmapToByteArray(bp);

			//byte to text
			String encodedImage = Base64.encodeToString(bb, Base64.DEFAULT);
			//save
			State.saveImage(mContext, encodedImage);
			//Check image
			//text to bitmap
			byte[] b2 = Base64.decode(encodedImage, Base64.DEFAULT);
			//Log.v(TAG, "b=" + b + ", bb=" + bb + ", b2=" + b2);
			Bitmap bitmap = BitmapFactory.decodeByteArray(b2, 0, b2.length);
			ManimageButton.setImageBitmap(bitmap);
		}
	}
	//Show the select voice word popup menu
	public void selectVoice(final ArrayList<String> results) {
		ListView lv = new ListView(getActivity());
		lv.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, results));
		lv.setScrollingCacheEnabled(false);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			public void onItemClick(AdapterView<?> items, View view, int position, long id) {
				//Log.e(TAG,"po ="+position+", id ="+id+", items="+items);
				DetailEdit.setText(results.get(position).toString());
				State.saveDetail(mContext,results.get(position).toString());
				v_Dlg.dismiss();

			}
		});

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(lv);
		v_Dlg = builder.create();
		v_Dlg.show();
	}
	//Show the select image popup menu
	public void selectImage() {
		String[] dialogItem_ja = getResources().getStringArray(R.array.ja_select_image_array);
		String[] dialogItem_en = getResources().getStringArray(R.array.en_select_image_array);
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
		// ダイアログ表示

		//String[] dialogItem = new String[]{"写真を撮る", "ライブラリから選択","イラストから選択","写真を消去"};
		AlertDialog.Builder dialogMenu = new AlertDialog.Builder(getActivity());
		//dialogMenu.setTitle("写真を登録");
		dialogMenu.setItems(dialogItem, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case 0:
						//Log.v(TAG, "Take a photo");
						Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

						if (intent2.resolveActivity(getActivity().getPackageManager()) != null) {
							startActivityForResult(intent2, TAKE_PHOTO);
						}
						break;
					case 1:
						//Log.v(TAG, "Pick an image from gallery");
						Intent i = new Intent();
						i.setAction(Intent.ACTION_GET_CONTENT);
						i.setType("image/*");
						startActivityForResult(i, PICK_IMAGE);
						break;
					case 2:
						//Log.v(TAG, "Pick an image");
						selectDialog(PERSON_IMAGE);
						break;
					case 3:
						//Log.v(TAG, "delete image");
						State.saveImage(mContext, null);
						ManimageButton.setImageResource(R.drawable.unknown);

						break;
				}
			}
		}).create().show();
	}

	private void selectDialog(int mode) {
		// Prepare grid view
		final GridView gridView = new GridView(getActivity());
		MODE = mode;
		int array = 0;
		switch (mode){
			case PERSON_IMAGE:
				array =R.array.person_array_drawable;
				break;
			case CLOTH_COLOR:
			case JUPON_COLOR:
				array =R.array.color_array_drawable;
				break;
		}
		final String[] filenames = getResources().getStringArray(array);
		final TypedArray images = mContext.getResources().obtainTypedArray(array);
		int length = images.length();
		final Integer[] mThumbIds = new Integer[length];

		for (int i = 0; i < length; i++) {
			mThumbIds[i] = images.getResourceId(i, 0);
		}
		images.recycle();
		ImageAdapter myAdapter = new ImageAdapter(mContext);
		myAdapter.SetImages(mThumbIds);
		gridView.setAdapter(myAdapter);
		//gridView.setAdapter(new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, bList));
		gridView.setNumColumns(5);
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (MODE) {
					case PERSON_IMAGE:
						//String rsName = mContext.getResources().getResourceEntryName();
						String str = filenames[position], str2 = "", str3 = "";
						if (str.matches("res/drawable" + ".*")) {
							// particle match
							str2 = str.substring(21);
							// delete ".jpg"
							str3 = str2.substring(0, str2.length()-4);
						}
						//Log.e(TAG, "str2="+str2   + ", str3==" +str3+", selected2=" + filenames[position] + ", position=" + position + ", id=" + id);
						Uri uri = Uri.parse("android.resource://com.fermfilm.iConnect/drawable/"+str3);
						Image im = new Image(mContext, uri);
						byte[] bb = im.bitmapToByteArray(im.getBitmapFromUri());
						//byte to text
						String encodedImage = Base64.encodeToString(bb, Base64.DEFAULT);
						//save
						State.saveImage(mContext, encodedImage);
						//text to bitmap
						byte[] b2 = Base64.decode(encodedImage, Base64.DEFAULT);
						Bitmap bitmap = BitmapFactory.decodeByteArray(b2, 0, b2.length);
						ManimageButton.setImageBitmap(bitmap);
						BitmapImage = bitmap;
						break;
					case CLOTH_COLOR:
						ClothCL.setImageBitmap(((BitmapDrawable) CLimages.getDrawable(position)).getBitmap());
						State.saveClothId(mContext, position);
						break;
					case JUPON_COLOR:
						JuponCL.setImageBitmap(((BitmapDrawable) CLimages.getDrawable(position)).getBitmap());
						State.saveJuponId(mContext, position);
						break;
				}

				s_Dlg.dismiss();

			}
		});

		// Set grid view to alertDialog
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setView(gridView);
		//builder.setTitle("Goto");
		s_Dlg = builder.create();
		s_Dlg.show();


	}

	public class ImageAdapter extends BaseAdapter {
		private Context mContext;
		private Integer[] pics;

		public ImageAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			return pics.length;
		}

		public Object getItem(int position) {return null;}

		public long getItemId(int position) {return 0;}

		// create a new ImageView for each item referenced by the Adapter
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			if (convertView == null) {  // if it's not recycled, initialize some attributes
				imageView = new ImageView(mContext);
				//You can set some params here
			} else {
				imageView = (ImageView) convertView;
			}
			imageView.setImageResource(pics[position]);
			return imageView;
		}

		public void SetImages(Integer[] id){
			pics = id.clone();
		}
	}

	public void CheckDialog() {
		//Dialog newDialog = new Dialog(getActivity());
		AlertDialog.Builder newDialog = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		//レイアウトのインフレーター
		final View layout = inflater.inflate(R.layout.dialog_mainfragment, null);

				// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		newDialog.setView(layout);
		//newDialog.setContentView(layout);
		d_NameLabel = (TextView)layout.findViewById(R.id.d_name_label);
		d_SexLabel = (TextView)layout.findViewById(R.id.d_sex_label);
		d_AgeLabel = (TextView)layout.findViewById(R.id.d_age_label);
		d_ReqLabel = (TextView)layout.findViewById(R.id.d_req_label);
		d_detailText = (TextView)layout.findViewById(R.id.d_detail_text);
		//Bttn is gone
		Button tra_bttn = (Button)layout.findViewById(R.id.translation_button);
		tra_bttn.setVisibility(View.GONE);
		switch (lan) {
			case 0:
				newDialog.setTitle("この内容を周りに送信してもよろしいでしょうか？");
				positive = "はい";
				negative = "いいえ";
				String[] name = mContext.getResources().getStringArray(R.array.ja_main_array);
				d_NameLabel.setText(name[0]);
				d_SexLabel.setText(name[1]);
				d_AgeLabel.setText(name[2]);
				d_ReqLabel.setText(name[3]);

				break;
			case 1:
				newDialog.setTitle("May I transmit these contents around?");
				positive = "Yes";
				negative = "No";
				String[] names = mContext.getResources().getStringArray(R.array.en_main_array);
				d_NameLabel.setText(names[0]);
				d_SexLabel.setText(names[1]);
				d_AgeLabel.setText(names[2]);
				d_ReqLabel.setText(names[3]);

				break;
		}


		d_ManimageButton = (ImageButton)layout.findViewById(R.id.d_man_image);
		d_ManimageButton.setImageBitmap(BitmapImage);
		d_Name = (TextView)layout.findViewById(R.id.d_name);
		d_Name.setText(Name.getText().toString());
		d_Sex = (TextView)layout.findViewById(R.id.d_sex);
		d_Sex.setText((String) SexSpinner.getSelectedItem());
		d_Age = (TextView)layout.findViewById(R.id.d_age);
		d_Age.setText((String) AgeSpinner.getSelectedItem());
		d_Req = (TextView)layout.findViewById(R.id.d_req);
		d_Req.setText((String) ReqSpinner.getSelectedItem());
		d_ClothCL = (ImageButton)layout.findViewById(R.id.d_cloth_image);
		d_ClothCL.setImageBitmap(((BitmapDrawable) CLimages.getDrawable(State.loadClothId(mContext))).getBitmap());
		d_JuponCL = (ImageButton)layout.findViewById(R.id.d_jupon_image);
		d_JuponCL.setImageBitmap(((BitmapDrawable) CLimages.getDrawable(State.loadJuponId(mContext))).getBitmap());
		d_detailText.setText(State.loadDetail(mContext));

		newDialog.setPositiveButton(positive, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				State.saveRoleId(mContext, ROLE_A);
				//Change DeviceName
				setDeviceName(State.loadDeviceName(mContext)+"_iConnect");

				/*
				if(mReceiver == null){
					toast("Please start Receiver");
				}else{
				}
				*/
				mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
					@Override
					public void onSuccess() {}
					@Override
					public void onFailure(int reasonCode) {
						Log.v(TAG,"Discovery Failed : " + reasonCode);}
				});
				State.saveSendMesId(mContext, REQUEST_MESSAGE);
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
		progressDialog = new ProgressDialog(s_activity);
		switch (lan) {
			case 0:
				progressDialog.setTitle("助けてくれる人を探しています");
				progressDialog.setMessage("キャンセルボタンで検索を中止します");
				break;
			case 1:
				progressDialog.setTitle("Discovering the person who helps you...");
				progressDialog.setMessage("You cancel a search by clicking cancel button.");
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
				// Resultがキャンセルのとき呼ばれる
				Log.v(TAG, "onCancel");
				//TODO:static setDeviceName
//				static_setDeviceName(staticState.loadDeviceName(s_activity));
				staticState.saveRoleId(s_activity, ROLE_B);

			}
		});
		progressDialog.show();
		//thread = new Thread(getActivity());
		//thread.start();
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
	public static void static_setDeviceName(final String devName) {
		try {


			if(tabActivity.getsManager()!=null){
				sManager = tabActivity.getsManager();
				sChannel = tabActivity.getsChannel();
			}
			else{
				Log.v(TAG,"mManager=null");
				return;
			}

			Class[] paramTypes = new Class[3];
			paramTypes[0] = WifiP2pManager.Channel.class;
			paramTypes[1] = String.class;
			paramTypes[2] = WifiP2pManager.ActionListener.class;
			Method setDeviceName = sManager.getClass().getMethod(
					"setDeviceName", paramTypes);
			setDeviceName.setAccessible(true);

			Object arglist[] = new Object[3];
			arglist[0] = sChannel;
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
			setDeviceName.invoke(sManager, arglist);
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

	public void setJapanese(){
		String[] names = getResources().getStringArray(R.array.ja_main_array);
		NameLabel.setText(names[0]);
		SexLabel.setText(names[1]);
		AgeLabel.setText(names[2]);
		ReqLabel.setText(names[3]);
		//ClothLabel.setText(names[4]);
		//JuponLabel.setText(names[5]);
		DetailLabel.setText(names[6]);
		VoiceBtn.setText(names[7]);
		CheckBtn.setText(names[8]);
		sexAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.ja_sex_array, R.layout.spinner_item);
		sexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ageAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.ja_age_array, R.layout.spinner_item);
		ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		reqAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.ja_req_array, R.layout.spinner_item);
		reqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		String[] titnames = getResources().getStringArray(R.array.ja_title_array);
		Log.e(TAG,"titid="+State.loadTitId(mContext));
		TitLabel.setText(titnames[State.loadTitId(mContext)]);
	}
	public void setEnglish(){
		String[] names = getResources().getStringArray(R.array.en_main_array);
		NameLabel.setText(names[0]);
		SexLabel.setText(names[1]);
		AgeLabel.setText(names[2]);
		ReqLabel.setText(names[3]);
		//ClothLabel.setText(names[4]);
		//JuponLabel.setText(names[5]);
		DetailLabel.setText(names[6]);
		VoiceBtn.setText(names[7]);
		CheckBtn.setText(names[8]);
		sexAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.en_sex_array, R.layout.spinner_item);
		sexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ageAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.en_age_array, R.layout.spinner_item);
		ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		reqAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.en_req_array, R.layout.spinner_item);
		reqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		String[] titnames = getResources().getStringArray(R.array.en_title_array);
		TitLabel.setText(titnames[State.loadTitId(mContext)]);
	}
	@Override
	public void onConnectionInfoAvailable(final WifiP2pInfo info) {
		Log.v(TAG, "onConnectionInfoAvailable");
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}


	/**
	 * UriからPathへの変換処理
	 * @param uri
	 * @return String
	 */
	public static String getPath(Context context, Uri uri) {
		ContentResolver contentResolver = context.getContentResolver();
		String[] columns = { MediaStore.Images.Media.DATA };
		Cursor cursor = contentResolver.query(uri, columns, null, null, null);
		cursor.moveToFirst();
		String path = cursor.getString(0);
		cursor.close();
		return path;
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
