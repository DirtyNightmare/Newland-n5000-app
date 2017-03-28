package com.zhiliantiandi.twodtest;

import com.zltd.decoder.Constants;
import com.zltd.decoder.DecoderManager;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.util.Log;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;

public class TestBaseActivity extends Activity implements DecoderManager.IDecoderStatusListener{

    private static final String TAG = "BaseActivity";
    protected static final int DECODE_TIMEOUT = 2100;
    private boolean isScanTimeOut = false;
    DecoderManager mDecoderMgr = null;
    protected boolean isOnResume = false;
    protected int scanCase = 0;
    private static final int OPENLIGHT = 1;
    private static final int CLOSELIGHT = 2;
    protected static LightControlHandler lightControlHandler = null;
    protected static boolean scanRing = true;
    protected Utils mUtils;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    // how to control location light and flash light
    class LightControlHandler extends Handler{
        public void handleMessage(android.os.Message msg) {
            //Log.d(TAG, "lightControlHandler msg.what = " + msg.what + " mDecoderMgr = " + mDecoderMgr);
            //if(mDecoderMgr != null){
            //    Log.d(TAG, "FlashMode = " + mDecoderMgr.getFlashMode());
            //}
            switch (msg.what) {
            case OPENLIGHT:
                if(mDecoderMgr != null){
                    mDecoderMgr.enableLight(Constants.LOCATION_LIGHT, true);
                    if(mDecoderMgr.getFlashMode() == Constants.FLASH_ALWAYS_ON_MODE ){
                        mDecoderMgr.enableLight(Constants.FLASH_LIGHT, true);
                        mDecoderMgr.enableLight(Constants.FLOOD_LIGHT, true);
                    }
                }
                break;
            case CLOSELIGHT:
                if(mDecoderMgr != null){
                    mDecoderMgr.enableLight(Constants.LOCATION_LIGHT, false);
                    if(mDecoderMgr.getFlashMode() == Constants.FLASH_ALWAYS_ON_MODE ){
                        mDecoderMgr.enableLight(Constants.FLASH_LIGHT, false);
                        mDecoderMgr.enableLight(Constants.FLOOD_LIGHT, false);
                    }
                }
                break;

            default:
                break;
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        Log.d(TAG, "onCreate ..");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        preferences = getSharedPreferences("settings", MODE_PRIVATE);
        mDecoderMgr = DecoderManager.getInstance();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mUtils = Utils.getInstance();
        mUtils.init(this);
        Log.d(TAG, "onResume this=" + this);
        isOnResume = true;
        scanCase = 0;
        int res = mDecoderMgr.connectDecoderSRV();
        if(res == Constants.RETURN_CAMERA_CONN_ERR){
            new AlertDialog.Builder(this)
            .setTitle(R.string.app_name)
            .setIcon(android.R.drawable.ic_dialog_info)
            .setMessage(R.string.scan_message)
            .setPositiveButton(R.string.dialog_ok, new OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    closeSelf();
                }
            })
            .setCancelable(false)
            .show();
        }
        mDecoderMgr.addDecoderStatusListener(this);
        //lightControlHandler.removeMessages(CLOSELIGHT);
        //lightControlHandler.sendEmptyMessage(OPENLIGHT);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mUtils.release();
        Log.d(TAG, "onPause this=" + this);
        isOnResume = false;
        mDecoderMgr.removeDecoderStatusListener(this);
        mDecoderMgr.stopDecode();
        //lightControlHandler.sendEmptyMessageDelayed(CLOSELIGHT, 1);
        mDecoderMgr.disconnectDecoderSRV();
    }

    protected boolean isScanTimeOut(){
        return isScanTimeOut;
    }

    @Override
    public void onDecoderResultChanage(String result, String time) {
        // TODO Auto-generated method stub
        isScanTimeOut = false;
        if(result.equals(DecoderManager.DECODER_TIMEOUT)){
            isScanTimeOut = true;
        }else{
            isScanTimeOut = false;
            if (scanRing && isOnResume) {
                mUtils.playSound(Utils.SOUND_TYPE_BEEP);
            }
        }
    }

    @Override
    public void onDecoderStatusChanage(int arg0) {
        // TODO Auto-generated method stub
    }

    protected void closeSelf() {
        this.finish();
    }

    protected void setScanRingEnable(boolean enable){
        editor = preferences.edit();
        editor.putBoolean("scan_ring", enable);
        editor.commit();
    }

    protected boolean getScanRingEnable(){
        return preferences.getBoolean("scan_ring", true);
    }
}
