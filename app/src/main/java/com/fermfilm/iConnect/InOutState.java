package com.fermfilm.iConnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by fermfilm on 2015/09/27.
 */
public class InOutState {
	private static final String TAG = "InOutState";
	public static final String DEFAULT_NAME = "";
	public static final String DEFAULT_IMAGE = "";
	public static final String DEFAULT_MESSAGE = "";
	public static final int DEFAULT_ID = 0;
	public static final Boolean DEFAULT_CHECK = true;
	public static final int LV_INTERVAL = 1;
	public static final int REQUEST_MESSAGE = 0;
	public static final int COUPON_MESSAGE = 1;
	ArrayList<String> array = new ArrayList<String>();


	public InOutState(){
	}
	//***********************************************************************************
	//********************************SAVE DATA******************************************
	//***********************************************************************************
	public void saveChatName(Context context, String chatName) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putString("chatName", chatName);
		edit.commit();
	}
	public void saveDeviceName(Context context, String DeviceName) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putString("DeviceName", DeviceName);
		edit.commit();
	}
	public void saveImage(Context context, String image_date) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit=prefs.edit();
		edit.putString("image_data", image_date);
		edit.commit();
		//Log.v(TAG, "saveImage");
		if(image_date == null) Log.v(TAG, "saveImage image = null");
		//Log.v(TAG, "saveImage image_date = "+image_date);
	}
	public void saveSexId(Context context, int SexId) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putInt("SexId", SexId);
		edit.commit();
	}
	public void saveAgeId(Context context, int AgeId) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putInt("AgeId", AgeId);
		edit.commit();
	}
	public void saveReqId(Context context, int ReqId) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putInt("ReqId", ReqId);
		edit.commit();
	}
	public void saveClothId(Context context, int ClothId) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putInt("ClothId", ClothId);
		edit.commit();
	}
	public void saveJuponId(Context context, int JuponId) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putInt("JuponId", JuponId);
		edit.commit();
	}
	public void saveDetail(Context context, String Det) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putString("Det", Det);
		edit.commit();
	}
	public void saveUri(Context context, String Uri) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putString("Uri", Uri);
		edit.commit();
	}
	public void saveThxCT(Context context, int Thx) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putInt("ThxCT", Thx);
		edit.commit();
	}
	public void saveTitId(Context context, int ThxId) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putInt("TitId", ThxId);
		edit.commit();
	}
	public void saveLanId(Context context, int LanId) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putInt("LanId", LanId);
		edit.commit();
	}
	public void saveAlmId(Context context, int AlmId) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putInt("AlmId", AlmId);
		edit.commit();
	}
	public void saveCheck(Context context, Boolean bool) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putBoolean("Checkbox", bool);
		edit.commit();
	}
	public void saveAlarmName(Context context, String almName) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putString("almName", almName);
		edit.commit();
	}
	public void saveRingTimeId(Context context, int RTId) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putInt("RTId", RTId);
		edit.commit();
	}
	public void saveRoleId(Context context, int RoleId) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putInt("RoleId", RoleId);
		edit.commit();
	}
	public void saveGnrId(Context context, int GnrId) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putInt("GnrId", GnrId);
		edit.commit();
	}
	public void saveGetGnrId(Context context, int GetGnrId) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putInt("GetGnrId", GetGnrId);
		edit.commit();
	}
	public void saveSendMesId(Context context, int MesId) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putInt("MesId", MesId);
		edit.commit();
	}
	public void saveGodsName(Context context, String GName) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putString("GName", GName);
		edit.commit();
	}
	public void saveCouponDetail(Context context, String CDet) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putString("CDet", CDet);
		edit.commit();
	}
	public void saveShopDetail(Context context, String SDet) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putString("SDet", SDet);
		edit.commit();
	}
	public void saveCouponNote(Context context, String CNote) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putString("CNote", CNote);
		edit.commit();
	}
	public void saveGodsImg(Context context, String GImg) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit=prefs.edit();
		edit.putString("GImg", GImg);
		edit.commit();
	}
	public void saveMapImg(Context context, String MImg) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit=prefs.edit();
		edit.putString("MImg", MImg);
		edit.commit();
	}




	//***********************************************************************************
	//********************************LOAD DATA******************************************
	//***********************************************************************************
	public String loadChatName(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString("chatName", DEFAULT_NAME);
	}
	public String loadDeviceName(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString("DeviceName", DEFAULT_NAME);
	}
	public String loadDetail(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString("Det", DEFAULT_NAME);
	}
	public String loadImage(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		//Log.v(TAG, "loadImage");
		if(prefs.getString("image_data", DEFAULT_IMAGE) == null)Log.v(TAG, "loadImage = null");
		//Log.v(TAG, "loadImage = "+prefs.getString("image_data", DEFAULT_IMAGE));
		return prefs.getString("image_data", DEFAULT_IMAGE);
	}
	public int loadSexId(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getInt("SexId", DEFAULT_ID);
	}
	public int loadAgeId(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getInt("AgeId", DEFAULT_ID);
	}
	public int loadReqId(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getInt("ReqId", DEFAULT_ID);
	}
	public int loadClothId(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getInt("ClothId", DEFAULT_ID);
	}
	public int loadJuponId(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getInt("JuponId", DEFAULT_ID);
	}
	public String loadUri(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString("Uri", DEFAULT_MESSAGE);
	}
	public int loadLanId(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		//Log.v(TAG,"loadID = "+prefs.getInt("LanId", DEFAULT_ID) );
		return prefs.getInt("LanId", DEFAULT_ID);
	}
	public int loadThxCT(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		//Log.v(TAG,"ThxCT = "+prefs.getInt("ThxCT", DEFAULT_ID) );
		return prefs.getInt("ThxCT", DEFAULT_ID);
	}
	public int loadTitId(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		//Log.v(TAG,"TitId = "+prefs.getInt("TitId", DEFAULT_ID) );
		return prefs.getInt("TitId", DEFAULT_ID);
	}
	public Boolean loadCheck(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean("Checkbox", DEFAULT_CHECK);
	}
	public int loadAlmId(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		//Log.v(TAG,"AlmId = "+prefs.getInt("AlmId", DEFAULT_ID) );
		return prefs.getInt("AlmId", DEFAULT_ID);
	}
	public String loadAlarmName(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString("almName", DEFAULT_NAME);
	}
	public int loadRingTimeId(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getInt("RTId", DEFAULT_ID);
	}
	//A or B
	public int loadRoleId(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		//Log.v(TAG,"RoleId = "+prefs.getInt("RoleId", DEFAULT_ID) );
		return prefs.getInt("RoleId", DEFAULT_ID);
	}
	public int loadGnrId(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		//Log.v(TAG,"GnrId = "+prefs.getInt("GnrId", DEFAULT_ID) );
		return prefs.getInt("GnrId", DEFAULT_ID);
	}
	public int loadGetGnrId(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		//Log.v(TAG,"GnrId = "+prefs.getInt("GnrId", DEFAULT_ID) );
		return prefs.getInt("GetGnrId", DEFAULT_ID);
	}
	//Judge Coupon Message
	public int loadSendMesId(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getInt("MesId", DEFAULT_ID);
	}
	public String loadGodsName(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString("GName", DEFAULT_NAME);
	}
	public String loadCouponDetail(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString("CDet", DEFAULT_NAME);
	}
	public String loadShopDetail(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString("SDet", DEFAULT_NAME);
	}
	public String loadCouponNote(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString("CNote", DEFAULT_NAME);
	}
	public String loadGodsImg(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		//if(prefs.getString("GImg", DEFAULT_IMAGE) == null)Log.v(TAG, "loadGodsImg = null");
		return prefs.getString("GImg", DEFAULT_IMAGE);
	}
	public String loadMapImg(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		//if(prefs.getString("GImg", DEFAULT_IMAGE) == null)Log.v(TAG, "loadGodsImg = null");
		return prefs.getString("MImg", DEFAULT_IMAGE);
	}

	//***********************************************************************************
	//********************************CHECK DATA******************************************
	//***********************************************************************************
	public int CheckTit(Context mContext, int ThxCT) {
		int TitId = 0;
		TitId = ThxCT/LV_INTERVAL;


		int[] array = mContext.getResources().getIntArray(R.array.title_array_drawable) ;


		if(TitId > array.length-1){
			TitId =array.length-1;
		}
		Log.e(TAG,"ThxCT = "+ThxCT+", array.length = "+array.length+", LV_INTERVAL = "+LV_INTERVAL);
		return TitId;
	}




}
