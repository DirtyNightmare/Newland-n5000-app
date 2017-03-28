package com.ominidata.device.scan;

import com.ominidata.device.Cam.CameraActivity;
import com.ominidata.device.R;
import com.zltd.decoder.Constants;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;
import android.view.inputmethod.InputMethodManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.ominidata.device.SQL.ConnectionClass;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by kewin on 07-07-2016.
 */
@SuppressWarnings("ALL")
public class ScanActivity extends TestBaseActivity {
    InputMethodManager inputmanger;
    protected ArrayList<HashMap<String, String>> mBarcodeList = new ArrayList<HashMap<String, String>>();
    protected SimpleAdapter mListAdaper;
    private static final int START_TEST_CASE = 1;  // Start automatic scan camera_activity
    private static final int GET_CODE_CASE = 2;  // Obtain reference bar code
    private Boolean isScanKeyDown = false;
    private int mScanMode = -1;
    private Toast toast = null;
    ResultSet rs;
    private int counter = 0;
    private SharedPreferences setingPreferences;
    ProgressBar pbbar;
    ConnectionClass connectionClass;
    @InjectView(R.id.cardnumberbox2)
    EditText cardnumberbox;
    @InjectView(R.id.shelfnumberbox2)
    EditText shelfnumberbox;
    @InjectView(R.id.old_card)
    TextView oldcard;
    @InjectView(R.id.Old_shelf)
    TextView oldshelf;
    android.hardware.Camera camera;
    @InjectView(R.id.btn_take_photo)
    FloatingActionButton btn_take_photo;
    @InjectView(R.id.aprove2)
    Button btn_aprove;
    @InjectView(R.id.cancel)
    Button btn_cancel;
    @InjectView(R.id.Count)
    TextView count;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Log.d("Camera activity", "Handler ..");
            HashMap<String, String> result = (HashMap<String, String>) msg.obj;
            switch (msg.what) {
                case GET_CODE_CASE:
                    if (!isScanTimeOut()) {
                        String decoderesult = result.get("decodeResult");
                        if (decoderesult.toUpperCase().startsWith("K")) {
                            cardnumberbox.setText(decoderesult);
                        } else if (decoderesult.toUpperCase().startsWith("R")) {
                            shelfnumberbox.setText(decoderesult);
                            btn_aprove.performClick();
                        }
                    } else {
                        if (toast != null) toast.cancel();
                        toast = Toast.makeText(ScanActivity.this, R.string.try_again,
                                Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    scanCase = 0;
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdaper = new SimpleAdapter(this, mBarcodeList, R.layout.list_item,
                new String[]{"decodeTime", "decodeResult"}, new int[]{R.id.text1, R.id.text2});
        isScanKeyDown = Boolean.TRUE;
        inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        txtinfocam = (TextView) findViewById(R.id.txtinfocam);
        setingPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        connectionClass = new ConnectionClass(this.getApplicationContext());
        setContentView(R.layout.scan_activity);
        ButterKnife.inject(this);
        cardnumberbox.requestFocus();
        String se = getIntent().getStringExtra("EXTRA_SESSION_IDs");
        count.setText(se);
        String s = getIntent().getStringExtra("EXTRA_SESSION_ID");
        cardnumberbox.setText(s);
        btn_take_photo.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                String carde = cardnumberbox.getText().toString().trim();
                if (carde.matches("")) {
                    Toast.makeText(getApplicationContext(), getString(R.string.Skan_Udfyld_Kort_Nummer), Toast.LENGTH_SHORT).show();
                    cardnumberbox.requestFocus();
                    return;
                }
                Intent i = new Intent(ScanActivity.this, CameraActivity.class);
                i.putExtra("EXTRA_SESSION_ID", carde);
                startActivity(i);
                finsih();
            }
        });
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mScanMode = mDecoderMgr.getScanMode();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        stopTest();
        if (toast != null) toast.cancel();
    }


    private void stopTest() {
        scanCase = 0;
        //mDecoderMgr.stopContinuousShoot();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // TODO Auto-generated method stub
        switch (keyCode) {
            case KeyEvent.KEYCODE_BUTTON_A:
                if (mScanMode != Constants.SINGLE_SHOOT_MODE) {
                    if (toast != null) toast.cancel();
                    return true;
                }
                if (isOnResume && scanCase != START_TEST_CASE) {
                    scanCase = GET_CODE_CASE;
                    View view = getWindow().peekDecorView();
                    if (view != null) {
                        inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    mDecoderMgr.dispatchScanKeyEvent(event);
                    if (!isScanKeyDown) {
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
                if (isOnResume && scanCase != START_TEST_CASE) {
                    mDecoderMgr.dispatchScanKeyEvent(event);
                }
                isScanKeyDown = false;
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    @Override
    public void onDecoderResultChanage(String result, String time) {
        // TODO Auto-generated method stub
        super.onDecoderResultChanage(result, time);
        if (isOnResume) {
            HashMap<String, String> hResult = new HashMap<String, String>();
            hResult.put("decodeResult", result);
            mHandler.obtainMessage(scanCase, hResult).sendToTarget();
        }

    }

}
