package com.zhiliantiandi.twodtest;
import com.zltd.decoder.Constants;

import java.lang.Integer;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

public class AutoTestActivity extends TestBaseActivity implements OnClickListener {

    String refNumString, resultNumString;
    int totalScanNum, timeInterNum, testFailNum, testSuccessNum,
            testTimeoutNum, testTotalNum;
    long mElapsedTime;
    float mAvgTime, mTotalTime;
    EditText refNum, totalScan, timeInter;
    Button startButton, stopButton;
    TextView testTotal, testSuccess, testFail, testTimeout;
    ListView testFailList;
    InputMethodManager inputmanger;
    protected ArrayList<HashMap<String, String>> mBarcodeList = new ArrayList<HashMap<String, String>>();
    protected SimpleAdapter mListAdaper;
    private static final String TAG = "AutoTestActivity";
    private static final String DEFAULTTOTAL = "1000";
    private static final String DEFAULTIMTERTIME = "300";
    private static final int MININTERTIME = 200;
    private static final int START_TEST_CASE = 1;  // Start automatic scan test
    private static final int GET_CODE_CASE = 2;  // Obtain reference bar code
    private static final int GET_AVG_TIME = 3;
    private Boolean isScanKeyDown = false;
    private int mScanMode = -1;
    private Toast toast = null;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            HashMap<String, String> result = (HashMap<String, String>)msg.obj;
            switch (msg.what) {
            case START_TEST_CASE:
                testTotalNum++;
                if (!isScanTimeOut()) {
                    mTotalTime += Integer.parseInt(result.get("decodeTime").toString());
                    if (!result.get("decodeResult").toString().equals(refNumString)) {
                        result.put("decodeTime", Integer.toString(testTotalNum));
                        mBarcodeList.add(0, result);
                        mListAdaper.notifyDataSetChanged();
                        testFailNum++;
                    } else {
                        testSuccessNum++;
                    }
                } else {
                    testTimeoutNum++;
                }
                if(testTotalNum == totalScanNum){
                    scanCase = 0;
                }
                testTotal.setText(getText(R.string.test_total)
                    + Integer.toString(testTotalNum));
                testSuccess.setText(getText(R.string.test_success)
                    + Integer.toString(testSuccessNum));
                testFail.setText(getText(R.string.test_fail)
                    + Integer.toString(testFailNum));
                testTimeout.setText(getText(R.string.test_timeout)
                    + Integer.toString(testTimeoutNum));
                break;
            case GET_CODE_CASE:
                if (!isScanTimeOut()) {
                    refNum.setText(result.get("decodeResult"));
                } else {
                    if(toast != null) toast.cancel();
                    toast = Toast.makeText(AutoTestActivity.this, R.string.try_again,
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
                scanCase = 0;
                setEditTextEnable(true);
                break;
            case GET_AVG_TIME:
                mAvgTime = mTotalTime / (testSuccessNum + testFailNum);
                if((testSuccessNum + testFailNum) != 0){
                    if(toast != null) toast.cancel();
                    toast = Toast.makeText(AutoTestActivity.this, String.format(getResources().getString(R.string.average_time), mAvgTime),
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
                setEditTextEnable(true);
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                break;
            default:
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate ..");
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_test);
        refNum = (EditText) findViewById(R.id.reference_number);
        totalScan = (EditText) findViewById(R.id.total_of_scaner);
        timeInter = (EditText) findViewById(R.id.time_interval);
        startButton = (Button) findViewById(R.id.start_test);
        stopButton = (Button) findViewById(R.id.stop_test);
        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        testTotal = (TextView) findViewById(R.id.test_total);
        testSuccess = (TextView) findViewById(R.id.test_success);
        testFail = (TextView) findViewById(R.id.test_fail);
        testTimeout = (TextView) findViewById(R.id.test_timeout);
        testFailList = (ListView) findViewById(R.id.test_fail_list);

        mListAdaper = new SimpleAdapter(this, mBarcodeList, R.layout.list_item,
            new String[] { "decodeTime", "decodeResult" }, new int[] {R.id.text1, R.id.text2 });
        testFailList.setAdapter(mListAdaper);

        inputmanger = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        timeInter.setText(DEFAULTIMTERTIME);
        totalScan.setText(DEFAULTTOTAL);
        mScanMode = mDecoderMgr.getScanMode();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        // TODO Auto-generated method stub
        super.onPause();
        stopTest();
        if(toast != null) toast.cancel();
    }

    private void setEditTextEnable(boolean enable){
        totalScan.setFocusable(enable);
        totalScan.setFocusableInTouchMode(enable);
        timeInter.setFocusable(enable);
        timeInter.setFocusableInTouchMode(enable);
        refNum.setFocusable(enable);
        refNum.setFocusableInTouchMode(enable);
    }

    private void startTest() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
        testTotalNum = 0;
        testSuccessNum = 0;
        testFailNum = 0;
        testTimeoutNum = 0;
        mTotalTime = 0;
        mBarcodeList.clear();
        mListAdaper.notifyDataSetChanged();
        scanCase = START_TEST_CASE;
        setEditTextEnable(false);
        //mDecoderMgr.continuousShoot();
        new ContinueScan().start();
    }

    private void stopTest() {
        scanCase = 0;
        setEditTextEnable(true);
        //mDecoderMgr.stopContinuousShoot();
    }

    private class ContinueScan extends Thread{
        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            while(testTotalNum < totalScanNum){
                if(scanCase != START_TEST_CASE){
                    break;
                }
                try {
                    mDecoderMgr.singleShoot();
                    Thread.sleep(timeInterNum);
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
            mHandler.sendEmptyMessage(GET_AVG_TIME);
            mDecoderMgr.stopShootImmediately();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        switch (keyCode) {
        case KeyEvent.KEYCODE_BUTTON_A:
            if(mScanMode != Constants.SINGLE_SHOOT_MODE){
               if(toast != null) toast.cancel();
               toast = Toast.makeText(AutoTestActivity.this, R.string.switch_to_single_scan, Toast.LENGTH_SHORT);
               toast.show();
               return true;
            }
            if(isOnResume && scanCase != START_TEST_CASE){
                scanCase = GET_CODE_CASE;
                View view = getWindow().peekDecorView();
                if (view != null) {
                    inputmanger.hideSoftInputFromWindow(view.getWindowToken(),0);
                }
                mDecoderMgr.dispatchScanKeyEvent(event);
                if(!isScanKeyDown){
                    setEditTextEnable(false);
                }
            }
            isScanKeyDown = true;
            return true;
        default:
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        switch (keyCode) {
        case KeyEvent.KEYCODE_BUTTON_A:
            if(isOnResume && scanCase != START_TEST_CASE){
                mDecoderMgr.dispatchScanKeyEvent(event);
            }
            isScanKeyDown = false;
            return true;
        default:
            return super.onKeyUp(keyCode, event);
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
        case R.id.start_test:
            if(mScanMode != Constants.SINGLE_SHOOT_MODE){
                if(toast != null) toast.cancel();
                toast = Toast.makeText(AutoTestActivity.this, R.string.switch_to_single_scan, Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            if (!refNum.getText().toString().trim().equals("")
                    && !totalScan.getText().toString().trim().equals("")
                    && !timeInter.getText().toString().trim().equals("")) {
                try{
                    refNumString = refNum.getText().toString();
                    totalScanNum = Integer.parseInt(totalScan.getText().toString());
                    timeInterNum = Integer.parseInt(timeInter.getText().toString());
                }catch(NumberFormatException e){
                    if(toast != null) toast.cancel();
                    toast = Toast.makeText(this, R.string.error_input, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                if (timeInterNum >= MININTERTIME) {
                    startTest();
                    startButton.setEnabled(false);
                    stopButton.setEnabled(true);
                }else{
                    if(toast != null) toast.cancel();
                    toast = Toast.makeText(this, R.string.error_input, Toast.LENGTH_SHORT);
                    toast.show();
                }
            } else {
                if(toast != null) toast.cancel();
                toast = Toast.makeText(this, R.string.not_blank, Toast.LENGTH_SHORT);
                toast.show();
            }
            break;

        case R.id.stop_test:
            stopTest();
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            break;

        default:
            break;
        }
    }

    @Override
    public void onDecoderResultChanage(String result, String time) {
        // TODO Auto-generated method stub
        super.onDecoderResultChanage(result, time);
        if(isOnResume){
            HashMap<String, String> hResult = new HashMap<String, String>();
            hResult.put("decodeTime", time);
            hResult.put("decodeResult", result);
            mHandler.obtainMessage(scanCase, hResult).sendToTarget();
        }
    }
}
