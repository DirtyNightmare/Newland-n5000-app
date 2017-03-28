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
    String proid;
    TextView txtinfocam;
    public String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Data";
    public String pathB = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Data/Backup";
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



    private File getDirc() {
        File dics = new File("sdcard");

        if (!dics.exists()) {
            dics.mkdir();
        }
        return new File(dics, "Data/pics");
    }


    public void btn_cancel(View view) {
        cardnumberbox.setText("");
        shelfnumberbox.setText("");
        count.setText("0");
        counter = 0;
        cardnumberbox.requestFocus();
    }

    //skriv append og set alting tilbage
    public void btn_aprove(View view) {

        UpdatePro updatePro = new UpdatePro();
        updatePro.execute("");
        final String shelf = shelfnumberbox.getText().toString().trim();
        final String card = cardnumberbox.getText().toString().trim();
        if (card.matches("")) {
            Toast.makeText(getApplicationContext(), getString(R.string.Skan_Udfyld_Kort_Nummer), Toast.LENGTH_SHORT).show();
            cardnumberbox.setText("");
            cardnumberbox.requestFocus();
            return;


        }
        if (shelf.matches("")) {
            Toast.makeText(getApplicationContext(), getString(R.string.Skan_Udfyld_Reol_Nummer), Toast.LENGTH_SHORT).show();
            shelfnumberbox.setText("");
            shelfnumberbox.requestFocus();
            return;
        }

        String text1 = cardnumberbox.getText().toString();
        {
            if (text1.substring(0, 1).startsWith("K")) {
                text1 = text1.substring(1);
            } else {
                text1 = text1;//.substring(0));
            }
        }
        String text2 = shelfnumberbox.getText().toString();
        {
            if (text2.substring(0, 1).startsWith("R")) {
                text2 = text2.substring(1);
            } else {
                text2 = text2;//.substring(0));
            }
        }
        oldcard.setText(text1);
        oldshelf.setText(text2);
        File dir = new File(path);
        dir.mkdirs();
        File file = new File(path + "/append.txt");
        String[] saveText = String.valueOf(System.getProperty("line.separator") + "LAGERIND" + System.getProperty("line.separator") + card + System.getProperty("line.separator") + shelf).split(System.getProperty("line.separator"));


        Save(file, saveText);

        File backF = new File(pathB);
        backF.mkdirs();
        SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy");
        Date now = new Date();
        File back = new File(pathB + "/append" + formatter.format(now) + ".txt");
        String[] saveback = String.valueOf(System.getProperty("line.separator") + "LAGERIND" + System.getProperty("line.separator") + card + System.getProperty("line.separator") + shelf).split(System.getProperty("line.separator"));

        cardnumberbox.setText("");
        shelfnumberbox.setText("");
        count.setText("0");
        Save(back, saveback);
        cardnumberbox.postDelayed(new Runnable() {
            @Override
            public void run() {
                cardnumberbox.requestFocus();
            }
        }, 1000);
        return;

    }

    //Gem append
    public static void Save(File file, String[] data) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            try {
                for (int i = 0; i < data.length; i++) {
                    fos.write(data[i].getBytes());
                    if (i < data.length - 1) {
                        fos.write("\n".getBytes());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public class UpdatePro extends AsyncTask<String, String, String> {
        SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy");
        Date now = new Date();
        String z = "";
        Boolean isSuccess = false;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String time = sdf.format(new Date());

        String DBcard = cardnumberbox.getText().toString();
        String DBshelf = shelfnumberbox.getText().toString();
        String picture = count.getText().toString().trim();
        String itemcard = DBcard;
        {
            if (itemcard.substring(0, 1).startsWith("K")) {
                itemcard = itemcard.substring(1);
            } else {
                itemcard = itemcard;//.substring(0));
            }
        }
        String itemshelf = DBshelf;
        {
            if (itemshelf.substring(0, 1).startsWith("R")) {
                itemshelf = "" + itemshelf.substring(1);
            } else {
                itemshelf = "" + itemshelf;//.substring(0));
            }
        }
        @Override
        protected void onPreExecute() {
            //       LStatus aLog = LStatus();
            //     aLog.id="";
            //     aLog.status="";
            //     aLog.action="";
            //    aLog.time="";
        }

        @Override
        protected void onPostExecute(String t) {
            Toast.makeText(ScanActivity.this, t, Toast.LENGTH_SHORT).show();
            try {
                FileOutputStream fileos = new FileOutputStream (new File(path+ "/" + "Append"+formatter.format(now)+".xml"),true);
                XmlSerializer xmlSerializer = Xml.newSerializer();
                StringWriter writer = new StringWriter();
                xmlSerializer.setOutput(writer);
                xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
                xmlSerializer.startTag(null, "DataAssociation");
                xmlSerializer.startTag(null, "Shelf");
                xmlSerializer.text(itemshelf);
                xmlSerializer.endTag(null, "Shelf");
                xmlSerializer.startTag(null,"Card");
                xmlSerializer.text(itemcard);
                xmlSerializer.endTag(null, "Card");
                xmlSerializer.startTag(null,"Status");
                xmlSerializer.text(t);
                xmlSerializer.endTag(null, "Status");
                xmlSerializer.startTag(null,"picturestaken");
                xmlSerializer.text(picture);
                xmlSerializer.endTag(null,"picturestaken");
                xmlSerializer.startTag(null,"Time");
                xmlSerializer.text(time);
                xmlSerializer.endTag(null,"Time");
                xmlSerializer.endTag(null, "DataAssociation");
//                xmlSerializer.endTag(null, "DataAssociationCollection");
                xmlSerializer.endDocument();
                xmlSerializer.flush();
                String dataWrite = writer.toString();
                fileos.write(dataWrite.getBytes());
                fileos.close();
            }
            catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (isSuccess == true) {
            }

        }

        @Override
        protected String doInBackground(String... params) {

            if (DBcard.trim().equals("") || DBshelf.trim().equals(""))
                z = getString(R.string.Invalid_Credentials);
            else {
                try {
                    Connection con = connectionClass.CONN();
                    if (con == null) {
                        z = getString(R.string.Forbindelses_fejl);
                    } else {
                        String doerTicket;
                        doerTicket = setingPreferences.getString("doerTicket", "");
                        String sql =
                                "SET NOCOUNT ON; " +
                                        "DECLARE @upd INT; " +
                                        "EXEC [file].[usp_assignPartToShelf] " +
                                        "@p_ItemNumber=?, " +
                                        "@p_ShelfNumber=?, " +
                                        "@p_UpdatedItems=@upd OUTPUT, " +
                                        "@p_DoerTicket=?;" +
                                        "SELECT @upd AS UpdatedItems;";
                        try (PreparedStatement s = con.prepareStatement(sql)) {
                            s.setString(1, itemcard);
                            s.setString(2, itemshelf);
                            s.setString(3, doerTicket);
                            ResultSet rs = s.executeQuery();  // above T-SQL always returns at least one ResultSet
                            rs.next();
                            System.out.println(rs.getString(1));
                            if (rs.getString(1).equals("1")) {
                                System.out.printf("test2");
                                z = getString(R.string.Update_succes);
                                isSuccess = true;
                            }
                            if (s.getMoreResults()) {
                                System.out.printf(
                                        "INFO:%n" +
                                                "  A second ResultSet was found.%n" +
                                                "  The previous ResultSet was returned by the stored procedure.%n" +
                                                "  Getting next ResultSet ...%n",
                                        "");
                                //     rs = s.getResultSet();
                                //       rs.next();
                                //         if (rs.getString(1).equals("1")) {
                                //            System.out.printf("test2");
                                //      z = getString(R.string.Succes);
                                //            isSuccess = true;

                                //   }
                            }
                            PreparedStatement preparedStatement = null;
                            String sqli = "select ID,ItemNumber,Trashed,Sold from [file].[Item] where [ItemNumber] =?";
                            preparedStatement = con.prepareStatement(sqli);
                            preparedStatement.setString(1, itemcard);
                            ResultSet rsS = preparedStatement.executeQuery();
                            while (rsS.next()) {
                                String P_id = rsS.getString("ID");
                                String Trashed = rsS.getString("Trashed");
                                String Sold = rsS.getString("Sold");

                                if (Trashed.equals("1.000000")) {
                                    //for updating trash
                                    CallableStatement cs = null;
                                    String queryundo = "{ call [file].[usp_trashItem](?,?,?,?)}  ";
                                    cs = con.prepareCall(queryundo);
                                    cs.setString(1, P_id); // p_ID
                                    cs.setString(2, "U"); //p_Action(U for untrash T for Trash)
                                    cs.setInt(3, 1); //p_Quantity
                                    cs.setString(4, doerTicket); //p_DoerTicket
                                    cs.executeUpdate();
                                    z = getString(R.string.untrashed);
                                } else if (Sold.equals("1.000000")) {
                                    z = getString(R.string.item_sold);

                                }
                            }
                            con.close();
                            rs.close();
                            rsS.close();
                        }
                    }
                } catch (Exception ex) {
                    isSuccess = false;
                    z = getString(R.string.Exceptions) + "L2)";
                    Log.e("MYAPP", "exception", ex);
                }
            }
            return z;
        }
    }

}