package com.fermfilm.iConnect.Entities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.net.InetAddress;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

@SuppressWarnings("serial")
public class i_Message implements Serializable{
    private static final String TAG = "i_Message";
    //Preparation
    public static final int OPEN_DIALOG = 1;
    public static final int OPEN_COUPON_DIALOG = 2;
    public static final int SERVER_SET = 3;
    public static final int RESPONSE_SET = 4;//Set Dialog OK from B

    //Message Type
    public static final int THX_ADD = 10;
    public static final int RESPONSE_MESSAGE = 11;
    public static final int REQUEST_MESSAGE = 12;
    public static final int COUPON_MESSAGE = 13;

    private int mType;
    private String mText;
    private String chatName, godsName;
    private String sex;
    private String age;
    private String req;
    private String DetailText, couponDetail, shopDetail, couponNote;
    private int sexId;
    private int ageId;
    private int reqId;
    private int cloId;
    private int jupId;
    private int TitId;
    private Activity mActivity;
    private Byte chatImage;
    private byte[] byteArray, byteArray2;
    private InetAddress senderAddress;
    private String fileName;
    private long fileSize;
    private String filePath;
    private boolean isMine;

    //Getters and Setters
    public int getmType() { return mType; }
    public void setmType(int mType) { this.mType = mType; }
    public String getmText() { return mText; }
    public String getDetailText() { return DetailText; }
    public Byte getmBitmap() { return chatImage; }
    public void setmText(String mText) { this.mText = mText; }
    public void setDetailText(String dText) { this.DetailText = dText; }
    public String getChatName() { return chatName; }
    public void setChatName(String chatName) { this.chatName = chatName; }
    public byte[] getByteArray() { return byteArray; }
    public void setByteArray(byte[] byteArray) { this.byteArray = byteArray; }
    public byte[] getByteArray2() { return byteArray2; }
    public void setByteArray2(byte[] byteArray2) { this.byteArray2 = byteArray2; }
    public InetAddress getSenderAddress() { return senderAddress; }
    public void setSenderAddress(InetAddress senderAddress) { this.senderAddress = senderAddress; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public boolean isMine() { return isMine; }
    public void setMine(boolean isMine) { this.isMine = isMine; }

    public void setGodsName(String GName) { this.godsName = GName; }
    public String getGodsName() { return godsName; }
    public void setCouponDetail(String CDet) { this.couponDetail = CDet; }
    public String getCouponDetail() { return couponDetail; }
    public void setShopDetail(String SDet) { this.shopDetail = SDet; }
    public String getShopDetail() { return shopDetail; }
    public void setCouponNote(String CNote) { this.couponNote = CNote; }
    public String getCouponNote() { return couponNote; }

    public void setSexId(int SexId) { this.sexId = SexId; }
    public void setAgeId(int AgeId) { this.ageId = AgeId; }
    public void setReqId(int ReqId) { this.reqId = ReqId; }
    public void setCloId(int CloId) { this.cloId = CloId; }
    public void setJupId(int JupId) { this.jupId = JupId; }
    public int getSexId() { return sexId; }
    public int getAgeId() { return ageId; }
    public int getReqId() { return reqId; }
    public int getCloId() { return cloId; }
    public int getJupId() { return jupId; }
    public Activity getmActivity() { return mActivity; }
    public void setmActivity(Activity mActivity) { this.mActivity = mActivity; }




    public i_Message(int type, String text, InetAddress sender, String name){
        mType = type;
        mText = text;
        senderAddress = sender;
        chatName = name;
        //chatImage = image;
    }

    public Bitmap byteArrayToBitmap(byte[] b){
        Log.v(TAG, "Convert byte array to image (bitmap)");
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    public void saveByteArrayToFile(Context context){
        Log.v(TAG, "Save byte array to file");
        /*switch(mType){

            case Message.AUDIO_MESSAGE:
                filePath = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath()+"/"+fileName;
                break;
            case Message.VIDEO_MESSAGE:
                filePath = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath()+"/"+fileName;
                break;
            case Message.FILE_MESSAGE:
                filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/"+fileName;
                break;
            case Message.DRAWING_MESSAGE:
                filePath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath()+"/"+fileName;
                break;
        }
        */
        File file = new File(filePath);

        if (file.exists()) {
            file.delete();
        }

        try {
            FileOutputStream fos=new FileOutputStream(file.getPath());

            fos.write(byteArray);
            fos.close();
            Log.v(TAG, "Write byte array to file DONE !");
        }
        catch (java.io.IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Write byte array to file FAILED !");
        }
    }
}
