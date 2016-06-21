package com.fermfilm.iConnect;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
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


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class SettingFragment extends Fragment {
	private static final String TAG = "SettingFragment";
	private Context mContext;
	private EditText setDeviceName;
	private TextView AlarmLabel, selectAlarm, RingTimeLabel;
	public TextView setDeviceNameLabel,setNotiLabel,setNotiSabLabel,setLanLabel;
	public Spinner RingSpinner, GenreSpn;;
	public ArrayAdapter<CharSequence> ringAdapter, genreAdapter;
	//Other Activity send
	public static String chatName;


	public CheckBox Noti_checkBox;
	public Spinner LanSpinner;
	public Ringtone mRingtone;
	public int AlmId = 0;
	public RingtoneManager ringtoneManager;
	public Cursor cursor;
	private int lan = 0;
	//public TextView textEncode = null;
	private Dialog a_Dlg = null;
	private InOutState State;

	public interface CheckBoxListener {
		public void onCheckClicked();
	}
	public interface NoCheckBoxListener {
		public void onNoCheckClicked();
	}
	private CheckBoxListener cListener;
	private NoCheckBoxListener ncListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.v(TAG, "onAttach");

		if (activity instanceof CheckBoxListener == false) {
			throw new ClassCastException("activity has no CheckBoxListener.");
		}
		cListener = ((CheckBoxListener) activity);
		if (activity instanceof NoCheckBoxListener == false) {
			throw new ClassCastException("activity has no NoCheckBoxListener.");
		}
		ncListener = ((NoCheckBoxListener) activity);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		//setHasOptionsMenu(true);
		return inflater.inflate(R.layout.settings, container, false);
	}
	@Override
	public void onStart() {
		super.onStart();
		mContext = getActivity();
		State = new InOutState();
		lan = State.loadLanId(mContext);

		/**/

		//check box
		Noti_checkBox = (CheckBox)getActivity().findViewById(R.id.noti_checkBox);
		Noti_checkBox.setChecked(State.loadCheck(mContext));
		Noti_checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
													 @Override
													 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
														 if (isChecked) {
															 if (cListener != null) {
																 cListener.onCheckClicked();
															 }
														 } else {
															 if (ncListener != null) {
																 ncListener.onNoCheckClicked();
															 }
														 }
													 }
												 }
		);
		AlarmLabel = (TextView)getActivity().findViewById(R.id.selectAlarm_Label);
		AlarmLabel.setClickable(true);
		AlarmLabel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				select_Alarm();
			}
		});

		ringtoneManager = new RingtoneManager(getActivity().getApplicationContext());
		mRingtone = ringtoneManager.getRingtone(State.loadAlmId(mContext));
		cursor = ringtoneManager.getCursor();

		selectAlarm = (TextView)getActivity().findViewById(R.id.selectAlarm);
		selectAlarm.setClickable(true);
		selectAlarm.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				select_Alarm();
			}
		});

		/**/
		if(State.loadAlmId(mContext)==0){
			selectAlarm.setText(ringtoneManager.getRingtone(1).getTitle(mContext));
			State.saveAlmId(mContext, 1);
		}else{
			selectAlarm.setText(ringtoneManager.getRingtone(State.loadAlmId(mContext)).getTitle(mContext));
			//Log.e(TAG,"almID="+State.loadAlmId(mContext)+"title"+ringtoneManager.getRingtone(State.loadAlmId(mContext)).getTitle(mContext));
		}


		//Set the Language
		// 配列リソースIDから取得した文字列配列をアダプタに入れる
		ArrayAdapter<CharSequence> languageAdapter =
				ArrayAdapter.createFromResource(getActivity(), R.array.language_array,
						R.layout.spinner_item);
		languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		LanSpinner = (Spinner) getActivity().findViewById(R.id.language_spinner);
		LanSpinner.setAdapter(languageAdapter);
		LanSpinner.setSelection(State.loadLanId(mContext));
		LanSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Spinner spinner = (Spinner) parent;
				//Log.v(TAG, Integer.toString(LanSpinner.getSelectedItemPosition()));
				//Log.v(TAG, (String) LanSpinner.getSelectedItem());
				switch (LanSpinner.getSelectedItemPosition()) {
					case 0:
						setJapanese();
						break;
					case 1:
						setEnglish();
						break;
				}
				State.saveLanId(mContext, LanSpinner.getSelectedItemPosition());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		setNotiLabel = (TextView)getActivity().findViewById(R.id.noti_label);
		setNotiSabLabel = (TextView)getActivity().findViewById(R.id.notiSab_label);
		setLanLabel = (TextView)getActivity().findViewById(R.id.language_label);
		/*
		//Set the device name
		setDeviceNameLabel = (TextView)getActivity().findViewById(R.id.deviceName_label);
		setDeviceName = (EditText)getActivity().findViewById(R.id.deviceName);
		if(State.loadDeviceName(mContext) == ""){
			setDeviceName.setText(Build.DEVICE);
		}else{
			setDeviceName.setText(State.loadDeviceName(mContext));
		}
		*/

		RingTimeLabel = (TextView)getActivity().findViewById(R.id.ringingTime_Label);
		RingSpinner = (Spinner) getActivity().findViewById(R.id.ringingTime_spinner);
		switch (lan) {
			case 0:
				setJapanese();
				break;
			case 1:
				setEnglish();
				break;
		}

		RingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Spinner spinner = (Spinner) parent;
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		genreAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.ja_genre_array, R.layout.spinner_item);
		genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		GenreSpn = (Spinner) getActivity().findViewById(R.id.genre_spn);
		GenreSpn.setAdapter(genreAdapter);
		GenreSpn.setSelection(State.loadGetGnrId(mContext));
		GenreSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				State.saveGetGnrId(mContext, position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

	}

	public void select_Alarm(){
		ArrayList<String> mArrayList = new ArrayList<String>();
		for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			// The Cursor is now set to the right position
			mArrayList.add(cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX));

		}


		ListView lv = new ListView(getActivity());
		lv.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_single_choice, mArrayList));
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lv.setScrollingCacheEnabled(false);
		lv.setSelection(State.loadAlmId(mContext));

		/**/
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> items, View view, int position, long id) {
				//Log.e(TAG, "pos =" + position + ", id =" + id + ", items=" + items);
				mRingtone = ringtoneManager.getRingtone(position);
				AlmId = position;
				mRingtone.play();
			}
		});

		//Long tap
		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				mRingtone = ringtoneManager.getRingtone(position);
				AlmId = position;
				a_Dlg.cancel();
				return false;
			}
		});
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(lv);
		//Cancel
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				if(mRingtone != null){
					if(mRingtone.isPlaying()){
						mRingtone.stop();  // 停止
					}
					State.saveAlmId(mContext, AlmId);
					State.saveUri(mContext, ringtoneManager.getRingtoneUri(AlmId).toString());
					String almName = mRingtone.getTitle(getActivity().getApplicationContext());
					selectAlarm.setText(almName);
					State.saveAlarmName(mContext,almName);

					//Log.e(TAG, "uri = " + ringtoneManager.getRingtoneUri(AlmId));
					//Log.e(TAG, "loaduri = " + State.loadUri(mContext));




				}



				a_Dlg.dismiss();
			}
		});
		a_Dlg = builder.create();
		a_Dlg.show();
	}
	public void setJapanese(){
		String[] names = getResources().getStringArray(R.array.ja_setting_array);
		setNotiLabel.setText(names[0]);
		setNotiSabLabel.setText(names[1]);
		AlarmLabel.setText(names[2]);
		RingTimeLabel.setText(names[3]);
		setLanLabel.setText(names[4]);
		ringAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.ja_ringingTime_array, R.layout.spinner_item);
		ringAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		RingSpinner.setAdapter(ringAdapter);
		RingSpinner.setSelection(State.loadRingTimeId(mContext));
	}
	public void setEnglish(){
		String[] names = getResources().getStringArray(R.array.en_setting_array);
		setNotiLabel.setText(names[0]);
		setNotiSabLabel.setText(names[1]);
		AlarmLabel.setText(names[2]);
		RingTimeLabel.setText(names[3]);
		setLanLabel.setText(names[4]);
		ringAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.en_ringingTime_array, R.layout.spinner_item);
		ringAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		RingSpinner.setAdapter(ringAdapter);
		RingSpinner.setSelection(State.loadRingTimeId(mContext));
	}

	@Override
	public void onPause() {
		super.onPause();

		State.saveCheck(mContext, Noti_checkBox.isChecked());
		State.saveLanId(mContext, LanSpinner.getSelectedItemPosition());
//		State.saveDeviceName(mContext, setDeviceName.getText().toString());
		State.saveRingTimeId(mContext, RingSpinner.getSelectedItemPosition());
//		DeviceName = State.loadDeviceName(mContext);
		Log.v(TAG, "onPause");

	}


	//トースト
	private void toast(String text) {
		if (text == null) text = "";
		Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
	}
}