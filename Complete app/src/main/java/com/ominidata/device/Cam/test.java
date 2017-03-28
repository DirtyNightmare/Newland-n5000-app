package com.ominidata.device.Cam;

/**
 * Created by sindakewin on 02-03-2017.
 */

import com.ominidata.device.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by kewin on 07-07-2016.
 */
@SuppressWarnings("ALL")
public class test extends Activity implements SurfaceHolder.Callback {
    @InjectView(R.id.cardnumberbox)
    EditText cardnumberbox;
    @InjectView(R.id.shelfnumberbox)
    EditText shelfnumberbox;
    private SharedPreferences setingPreferences;
    android.hardware.Camera camera;
    @InjectView(R.id.surfaceView)
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Camera.PictureCallback jpegCallback;
    Camera.ShutterCallback shutterCallback;
    private RelativeLayout pCameraLayout = null;
    public String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        setingPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        ButterKnife.inject(this);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        {
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


                    String photofile = "test" + ".jpg";

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

                }

            };


        }}



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
        camera.setParameters(parameters);
        camera.setDisplayOrientation(Integer.parseInt(Displayor));
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
    }





