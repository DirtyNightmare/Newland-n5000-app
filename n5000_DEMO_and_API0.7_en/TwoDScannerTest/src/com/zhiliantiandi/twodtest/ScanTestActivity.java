
package com.zhiliantiandi.twodtest;
import java.lang.Override;
import java.lang.Runnable;
import java.lang.String;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.HashMap;

import com.zhiliantiandi.twodtest.R;
import com.zltd.decoder.Constants;
import com.zhiliantiandi.io.SaveFileToSDCard;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.view.KeyEvent;

public class ScanTestActivity extends TestBaseActivity implements OnCheckedChangeListener{
    private static final String TAG = "ScanTestActivity";
    private ListView mResultListView;
    private CheckBox mWhiteLightCheckBox;
    private CheckBox mRedLightCheckBox;
    private CheckBox mLocationLightCheckBox;
    private CheckBox mScanRingCheckBox;
    private Button mSingleScanButton;
    private Button autoTestButton;
    private Button saveResultButton;
    private ToggleButton mContinousScanButton;
    private TextView mScanTotalTextView;

    protected ArrayList<HashMap<String, String>> mBarcodeList = new ArrayList<HashMap<String, String>>();
    protected SimpleAdapter mListAdaper;
    private int ScanTotalNum = 0;

    private static final int STARTCONTINUESHOOT = 1;
    private static final int STOPCONTINUESHOOT = 2;
    private int scanMode = -1;

    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    HashMap<String, String> result = (HashMap<String, String>) msg.obj;
                    mBarcodeList.add(0, result);
                    mListAdaper.notifyDataSetChanged();
                    mScanTotalTextView.setText(getString(R.string.scan_total) + ScanTotalNum);
                    break;
                case 1:
                    Toast.makeText(ScanTestActivity.this, R.string.save_success, Toast.LENGTH_SHORT).show();
                    enableSaveFile = true;
                    break;
                case 2:
                    Toast.makeText(ScanTestActivity.this, R.string.save_fail, Toast.LENGTH_SHORT).show();
                    enableSaveFile = true;
                    break;
                default:
                    break;
            }
        }
    };

    SaveFileToSDCard mSaveFileToSDCard;
    private boolean enableSaveFileButton = false;
    private boolean enableSaveFile = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate ..");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_code_test);
        mResultListView = (ListView) findViewById(R.id.result_listView);
        mWhiteLightCheckBox = (CheckBox) findViewById(R.id.white_light_checkBox);
        mWhiteLightCheckBox.setOnCheckedChangeListener(this);
        mRedLightCheckBox = (CheckBox) findViewById(R.id.red_light_checkBox);
        mRedLightCheckBox.setOnCheckedChangeListener(this);
        mLocationLightCheckBox = (CheckBox) findViewById(R.id.location_light_checkBox);
        mLocationLightCheckBox.setOnCheckedChangeListener(this);
        mScanRingCheckBox = (CheckBox) findViewById(R.id.scan_ring_checkBox);
        mScanRingCheckBox.setOnCheckedChangeListener(this);

        mSingleScanButton = (Button) findViewById(R.id.single_button);
        autoTestButton = (Button) findViewById(R.id.auto_test);
        saveResultButton = (Button) findViewById(R.id.save_result);
        saveResultButton.setVisibility(enableSaveFileButton ? View.VISIBLE : View.INVISIBLE);
        mContinousScanButton = (ToggleButton) findViewById(R.id.continuous_button);
        mScanTotalTextView = (TextView)findViewById(R.id.scan_total);

        mListAdaper = new SimpleAdapter(this, mBarcodeList, R.layout.list_item, new String[] {
                "decodeTime", "decodeResult"
        }, new int[] {
                R.id.text1, R.id.text2
        });
        mResultListView.setAdapter(mListAdaper);
        //mDecoderMgr.addDecoderStatusListener(this);
    }

    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        scanRing = getScanRingEnable();
        mScanRingCheckBox.setChecked(scanRing);
        initUI();
    }

    private void initUI() {
        mContinousScanButton.setChecked(false);
        mSingleScanButton.setEnabled(true);
        switch (mDecoderMgr.getScanMode()) {
        case Constants.SINGLE_SHOOT_MODE:
            mSingleScanButton.setEnabled(true);
            mContinousScanButton.setEnabled(false);
            //autoTestButton.setEnabled(true);
            mScanTotalTextView.setVisibility(View.GONE);
            scanMode = Constants.SINGLE_SHOOT_MODE;
            break;
        case Constants.CONTINUOUS_SHOOT_MODE:
            mSingleScanButton.setEnabled(false);
            mContinousScanButton.setEnabled(true);
            //autoTestButton.setEnabled(false);
            mScanTotalTextView.setVisibility(View.VISIBLE);
            scanMode = Constants.CONTINUOUS_SHOOT_MODE;
            break;
        case Constants.HOLD_SHOOT_MODE:
            mSingleScanButton.setEnabled(false);
            mContinousScanButton.setEnabled(false);
            //autoTestButton.setEnabled(false);
            mScanTotalTextView.setVisibility(View.GONE);
            scanMode = Constants.HOLD_SHOOT_MODE;
            break;

        default:
            break;
        }
        // mWhiteLightCheckBox.setChecked(false);
        // mRedLightCheckBox.setChecked(false);
        // mLocationLightCheckBox.setChecked(false);
    }

    public void onPause() {
        Log.d(TAG, "onPause");
        if (scanCase == STARTCONTINUESHOOT) {
            scanCase = STOPCONTINUESHOOT;
            mContinousScanButton.setChecked(false);
            //mDecoderMgr.stopContinuousShoot();
        }
        super.onPause();

        // mScanLightController.enableFlashLight(false);
        // mScanLightController.enableFloodLight(false);
        // mScanLightController.enableLocationLight(false);
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        if (mDecoderMgr != null) {
            mDecoderMgr.enableLight(Constants.FLASH_LIGHT, false);
            mDecoderMgr.enableLight(Constants.FLOOD_LIGHT, false);
            mDecoderMgr.enableLight(Constants.LOCATION_LIGHT, false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu);
        //menu.add(0, 0, getString(R.string.settings));
        menu.add(0, 0, 0, R.string.settings);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
        case 0:
            Intent intent = new Intent();
            ComponentName name = new ComponentName("com.zltd.decoder", "com.zltd.decoder.ScannerSettings");
            intent.setComponent(name);
            startActivity(intent);
            return true;

        default:
            break;
        }
        return false;
    }

    public void saveResult(View view){
        final SaveFileToSDCard saveFileToSDCard = new SaveFileToSDCard();
        if(!enableSaveFile){
            Toast.makeText(this, R.string.saving, Toast.LENGTH_SHORT).show();
            return;
        }
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            enableSaveFile = false;
            Toast.makeText(this, R.string.saving, Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean isSaveSuccess = saveFileToSDCard.saveFile(mBarcodeList);
                    mHandler.sendEmptyMessage(isSaveSuccess ? 1 : 2);
                }
            }).start();
        }else{
            Toast.makeText(this, R.string.sdcard_not_exist, Toast.LENGTH_SHORT).show();
        }
    }

    public void autoTestOnClick(View view) {
        if(scanMode == Constants.SINGLE_SHOOT_MODE){
            Intent intent = new Intent(ScanTestActivity.this, AutoTestActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(ScanTestActivity.this, R.string.switch_to_single_scan, Toast.LENGTH_SHORT).show();
        }
    }

    public void singleShootOnClick(View view) {
        //mDecoderMgr.enableLight(Constants.FLASH_LIGHT, true);
        //mDecoderMgr.enableLight(Constants.FLOOD_LIGHT, true);
        mDecoderMgr.singleShoot();
    }

    public void continuousShootOnClick(View view) {
        Log.d(TAG, "continuousShootOnClick");
        if (mContinousScanButton.isChecked()) {
            ScanTotalNum = 0;
            scanCase = STARTCONTINUESHOOT;
            mDecoderMgr.continuousShoot();
        } else {
            scanCase = STOPCONTINUESHOOT;
            mDecoderMgr.stopContinuousShoot();
        }
    }

    public void exitOnClick(View view) {
        onBackPressed();
    }

    public void clearOnClick(View view) {
        mBarcodeList.clear();
        mListAdaper.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown keyCode = " + keyCode + " repeatCount = " + event.getRepeatCount());
        switch (keyCode) {
        case KeyEvent.KEYCODE_BUTTON_A:
            if(isOnResume){
                // Synchronous the state of scan key and the mContinousScanButton when CONTINUOUS_SHOOT_MODE
                if (scanMode == Constants.CONTINUOUS_SHOOT_MODE && event.getRepeatCount() == 0) {
                    if(mContinousScanButton.isChecked()){
                        scanCase = STOPCONTINUESHOOT;
                        mContinousScanButton.setChecked(false);
                    }else{
                        if(scanCase != STARTCONTINUESHOOT){
                            ScanTotalNum = 0;
                        }
                        scanCase = STARTCONTINUESHOOT;
                        mContinousScanButton.setChecked(true);
                    }
                    mContinousScanButton.setEnabled(false);
                }else if(scanMode == Constants.SINGLE_SHOOT_MODE){
                    mSingleScanButton.setEnabled(false);
                }
                mDecoderMgr.dispatchScanKeyEvent(event);
            }
            return true;
        default:
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyUp keyCode=" + keyCode);
        switch (keyCode) {
        case KeyEvent.KEYCODE_BUTTON_A:
            if(isOnResume){
                if(scanMode == Constants.CONTINUOUS_SHOOT_MODE){
                    mContinousScanButton.setEnabled(true);
                }else if(scanMode == Constants.SINGLE_SHOOT_MODE){
                    mSingleScanButton.setEnabled(true);
                }
                mDecoderMgr.dispatchScanKeyEvent(event);
            }
            return true;
        default:
            return super.onKeyUp(keyCode, event);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton checkBox, boolean enable) {
        switch (checkBox.getId()) {
            case R.id.white_light_checkBox:
                mDecoderMgr.enableLight(Constants.FLASH_LIGHT, enable);
                break;
            case R.id.red_light_checkBox:
                mDecoderMgr.enableLight(Constants.FLOOD_LIGHT, enable);
                break;
            case R.id.location_light_checkBox:
                mDecoderMgr.enableLight(Constants.LOCATION_LIGHT, enable);
                break;
            case R.id.scan_ring_checkBox:
                setScanRingEnable(enable);
                scanRing = enable;
                break;
            default:
                break;
        }
    }

    @Override
    public void onDecoderResultChanage(String result, String time) {
        super.onDecoderResultChanage(result, time);
        if(isOnResume){
            Log.d(TAG, "onDecoderResultChanage decodeTime=" + time
                    + " decodeResult " + result);
            HashMap<String, String> hResult = new HashMap<String, String>();
            hResult.put("decodeTime", time);
            hResult.put("decodeResult", result);
            switch (scanMode) {
            case Constants.SINGLE_SHOOT_MODE:
                mHandler.obtainMessage(0, hResult).sendToTarget();
                break;
            case Constants.CONTINUOUS_SHOOT_MODE:
                if(scanCase == STARTCONTINUESHOOT){
                    ScanTotalNum++;
                    mHandler.obtainMessage(0, hResult).sendToTarget();
                }
                break;
            case Constants.HOLD_SHOOT_MODE:
                if(!isScanTimeOut())
                {
                    mHandler.obtainMessage(0, hResult).sendToTarget();
                }
                break;
            default:
                break;
            }
        }
    }
}
