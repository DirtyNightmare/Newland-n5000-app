package com.ominidata.device;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.melnykov.fab.FloatingActionButton;
import com.ominidata.device.SQL.ConnectionClass;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

/**
 * Created by kewin on 07-07-2016.
 */
@SuppressWarnings("ALL")
public class CameraActivity extends Activity implements SurfaceHolder.Callback {
    ResultSet rs;
    private int counter = 0;
    private SharedPreferences setingPreferences;
    ProgressBar pbbar;
    ConnectionClass connectionClass;
    @InjectView(R.id.cardnumberbox)
    EditText cardnumberbox;
    @InjectView(R.id.shelfnumberbox)
    EditText shelfnumberbox;
    @InjectView(R.id.old_card)
    TextView oldcard;
    @InjectView(R.id.Old_shelf)
    TextView oldshelf;
    android.hardware.Camera camera;
    @InjectView(R.id.surfaceView)
    SurfaceView surfaceView;
    @InjectView(R.id.btn_take_photo)
    FloatingActionButton btn_take_photo;
    @InjectView(R.id.aprove)
    Button btn_aprove;
    @InjectView(R.id.cancel)
    Button btn_disaprove;
    @InjectView(R.id.Count)
    TextView count;
    SurfaceHolder surfaceHolder;
    Camera.PictureCallback jpegCallback;
    Camera.ShutterCallback shutterCallback;
    String proid;
    TextView txtinfocam;
    private ZoomControls zoomControls;
    private RelativeLayout pCameraLayout = null;
    public String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Data";
    public String pathB = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Data/Backup";

    EditText refNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        txtinfocam = (TextView) findViewById(R.id.txtinfocam);
        setingPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        connectionClass = new ConnectionClass(this.getApplicationContext());
        setContentView(R.layout.camera_activity);
        ButterKnife.inject(this);
        surfaceHolder = surfaceView.getHolder();
        cardnumberbox.requestFocus();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        btn_take_photo.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter++;
                count.setText(String.valueOf(counter));
                cameraimage();

            }
        });
        jpegCallback = new Camera.PictureCallback() {
            @Override
            //tag billed og gem det
            public void onPictureTaken(byte[] bytes, Camera camera) {
                FileOutputStream outputStream = null;
                File file_image = getDirc();
                if (!file_image.exists() && !file_image.mkdirs()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.folder_error), Toast.LENGTH_SHORT).show();
                    return;

                }

                String Counting = count.getText().toString().trim();
                String text = cardnumberbox.getText().toString().trim();
                {
                    if (text.substring(0, 1).startsWith("K")) {
                        text = Counting + text.substring(1);
                    } else {
                        text = Counting + text;//.substring(0));
                    }
                }

                String photofile = text + ".jpg";

                String file_name = file_image.getAbsolutePath() + "/" + photofile;
                File picfile = new File(file_name);
                try {
                    outputStream = new FileOutputStream(picfile);
                    outputStream.write(bytes);
                    outputStream.close();
                } catch (FileNotFoundException e) {
                } catch (IOException ex) {
                } finally {

                }
                refreshcamera();
                refreshgallery(picfile);
                MyCopy mycopy = new MyCopy();
                mycopy.execute("");

            }

        };

        cardnumberbox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                final String text1 = cardnumberbox.getText().toString().trim();
                if (text1.startsWith("R")) {
                    Toast.makeText(getApplicationContext(), getString(R.string.not_cardnumber), Toast.LENGTH_SHORT).show();
                    cardnumberbox.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            cardnumberbox.setText("");
                            cardnumberbox.requestFocus();
                        }
                    }, 1000);
                    return;
                }
            }

        });
        shelfnumberbox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                final String text1 = shelfnumberbox.getText().toString().trim();
                if (text1.startsWith("K")) {
                    Toast.makeText(getApplicationContext(), getString(R.string.not_shelfnumber), Toast.LENGTH_SHORT).show();
                    shelfnumberbox.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            shelfnumberbox.setText("");
                            shelfnumberbox.requestFocus();
                        }
                    }, 1000);
                    return;
                }
            }

        });


        shelfnumberbox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_NULL
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    btn_aprove.performClick();
                }
                return false;

            }
        });
    }


    private void refreshgallery(File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        sendBroadcast(intent);
    }

    //opdater kammera
    public void refreshcamera() {
        if (surfaceHolder.getSurface() == null) {
            return;
        }

        try {
            camera.stopPreview();
        } catch (Exception e) {

        }
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
        }
    }

    private File getDirc() {
        File dics = new File("sdcard");

        if (!dics.exists()) {
            dics.mkdir();
        }
        return new File(dics, "Data/pics");
    }

    public void cameraimage() {
        camera.takePicture(null, null, jpegCallback);
    }

    @Override
    //start preview
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        String PreviewFPS;
        String Previewsize;
        String Displayor;
        PreviewFPS = setingPreferences.getString("previewfps", "");
        Previewsize = setingPreferences.getString("screensize", "");
        Displayor = setingPreferences.getString("orientation", "");
        String[] size = Previewsize.split(",");
        try {
            camera = Camera.open();
        } catch (RuntimeException ex) {
        }
        Camera.Parameters parameters;
      parameters = camera.getParameters();

        //modificer parameterene
        parameters.setPreviewFrameRate(Integer.parseInt(PreviewFPS));
        parameters.setPreviewSize(Integer.parseInt(size[0]),Integer.parseInt(size[1]));
       camera.setDisplayOrientation(Integer.parseInt(Displayor));
        parameters.setFlashMode(parameters.FLASH_MODE_AUTO);
        parameters.setFocusMode(parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        parameters.setSceneMode(parameters.SCENE_MODE_AUTO);
        parameters.setWhiteBalance(parameters.WHITE_BALANCE_AUTO);
        
        camera.setParameters(parameters);
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception ex) {
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        refreshcamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        camera.stopPreview();
        camera.release();
        camera = null;
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
        counter = 0;
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
            Toast.makeText(CameraActivity.this, t, Toast.LENGTH_SHORT).show();
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
                                      z = getString(R.string.Succes);
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

                                if(Trashed.equals("1.000000")) {
                                    //for updating trash
                                    CallableStatement cs = null;
                                    String queryundo = "{ call [file].[usp_trashItem](?,?,?,?)}  ";
                                    cs = con.prepareCall(queryundo);
                                    cs.setString(1, P_id); // p_ID
                                    cs.setString(2, "U"); //p_Action(U for untrash T for Trash)
                                    cs.setInt(3, 1); //p_Quantity
                                    cs.setString(4, doerTicket); //p_DoerTicket
                                    cs.executeUpdate();
                                    z = getString(R.string.Untrashed);
                                }
                                else if (Sold.equals("1.000000"))
                                {
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

    private class MyCopy extends AsyncTask<String, String, String> {

        String z = "";
        String username = "", password = "", servername = "", filestocopy = "";

        @Override
        protected void onPreExecute() {

            String Counting = count.getText().toString().trim();
            String picfilename = cardnumberbox.getText().toString().trim();
            {
                if (picfilename.substring(0, 1).startsWith("K")) {
                    picfilename = Counting + picfilename.substring(1);
                } else {
                    picfilename = Counting + picfilename;//.substring(0));
                }
            }

            username = setingPreferences.getString("smbuser","");
            password = setingPreferences.getString("smbpass", "");
            servername = "smb://"+setingPreferences.getString("smppath","");
            filestocopy = setingPreferences.getString("copyfrom","") + picfilename + ".jpg";
        }

        @Override
        protected void onPostExecute(String r) {
            Toast.makeText(CameraActivity.this, r, Toast.LENGTH_SHORT).show();
          //  txtinfocam.setVisibility(View.VISIBLE);
         //   txtinfocam.setText(r);
         //   txtinfocam.postDelayed(new Runnable() {
          //      @Override
        //        public void run() {
         //           txtinfocam.setVisibility(View.invisible);
         //       }
          //  }, 5000);
        }

        @Override
       protected String doInBackground(String... params) {
                      File file = new File(filestocopy);
                  String filename = file.getName();
                NtlmPasswordAuthentication auth1 = new NtlmPasswordAuthentication(
                      servername, username, password);
            try {

                SmbFile sfile = new SmbFile(servername + "/" + filename, auth1);
                          if (!sfile.exists())
                            sfile.createNewFile();
                      sfile.connect();

                    InputStream in = new FileInputStream(file);

                SmbFileOutputStream sfos = new SmbFileOutputStream(sfile);
                          byte[] buf = new byte[8192];
                        int len;
                      while ((len = in.read(buf)) >= 0) {
                        sfos.write(buf, 0, len);
                  }
                in.close();
              sfos.close();
                z = getString(R.string.pic_uploaded);

                      } catch (Exception ex) {
            }

                return z;

        }

        }
    }