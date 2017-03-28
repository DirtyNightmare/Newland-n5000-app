package com.ominidata.device.Cam;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.ominidata.device.R;
import com.ominidata.device.SQL.ConnectionClass;
import com.ominidata.device.scan.ScanActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;

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
    @InjectView(R.id.cardnumberbox2)
    TextView cardnumberbox2;
    android.hardware.Camera camera;
    @InjectView(R.id.surfaceView2)
    SurfaceView surfaceView;
    @InjectView(R.id.btn_take_photo2)
    FloatingActionButton btn_take_photo;
    @InjectView(R.id.btn_aprove2)
    Button btn_aprove2;
    @InjectView(R.id.Count2)
    TextView count;
    SurfaceHolder surfaceHolder;
    Camera.PictureCallback jpegCallback;
    Camera.ShutterCallback shutterCallback;
    String proid;
    TextView txtinfocam;
    private RelativeLayout pCameraLayout = null;
    public String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Data";
    public String pathB = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Data/Backup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        txtinfocam = (TextView) findViewById(R.id.txtinfocam);
        setingPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        connectionClass = new ConnectionClass(this.getApplicationContext());
        setContentView(R.layout.camera_activity);
        ButterKnife.inject(this);
        surfaceHolder = surfaceView.getHolder();
        cardnumberbox2.requestFocus();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        String s = getIntent().getStringExtra("EXTRA_SESSION_ID");
        cardnumberbox2.setText(s);
        counter = 0;
        count.setText("0");
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
                    Toast.makeText(getApplicationContext(),getString(R.string.folder_error), Toast.LENGTH_SHORT).show();
                    return;

                }

                String Counting = count.getText().toString().trim();
                String text = cardnumberbox2.getText().toString().trim();
                {
                    if(text.substring(0,1).startsWith("K"))
                    {
                        text =  Counting+text.substring(1);
                    }
                    else
                    {
                        text=  Counting+text ;//.substring(0));
                    }
                }

                String photofile =text + ".jpg";

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
                Toast.makeText(getApplicationContext(), getString(R.string.Pic_taken) , Toast.LENGTH_SHORT).show();
                refreshcamera();
                refreshgallery(picfile);
                MyCopy mycopy = new MyCopy();
                mycopy.execute("");
            }

        };
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
        Log.d("Camera activity", "Cam ..");
        String PreviewFPS;
        String Previewsize;
        String Displayor;
        PreviewFPS = setingPreferences.getString("previewfps", "");
        Previewsize = setingPreferences.getString("screensize", "");
        Displayor = setingPreferences.getString("orientation", "");
        String[] size = Previewsize.split(",");
        try {
            camera = android.hardware.Camera.open();
        } catch (RuntimeException ex) {
        }
        Camera.Parameters parameters;
        parameters = camera.getParameters();

        //modificer parameterene
        parameters.setPreviewFrameRate(Integer.parseInt(PreviewFPS));
        parameters.setPreviewSize(Integer.parseInt(size[0]), Integer.parseInt(size[1]));
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
    public void btn_aprove2(View view) {
        Intent i = new Intent(CameraActivity.this, ScanActivity.class);
        String counts = count.getText().toString().trim();
        i.putExtra("EXTRA_SESSION_IDs", counts);
        String carde = cardnumberbox2.getText().toString().trim();
        i.putExtra("EXTRA_SESSION_ID", carde);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
    private class MyCopy extends AsyncTask<String, String, String> {

        String z = "";
        String username = "", password = "", servername = "", filestocopy = "";

        @Override
        protected void onPreExecute() {

            String Counting = count.getText().toString().trim();
            String picfilename = cardnumberbox2.getText().toString().trim();
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
            //           txtinfocam.setVisibility(View.GONE);
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