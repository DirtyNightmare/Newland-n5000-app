package com.ominidata.device.io;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ominidata.device.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;


public class manuel_upload extends Activity {
    private SharedPreferences setingPreferences;

    TextView txtinfo;
    ProgressBar pbbar;
    EditText edtservername, edtdir;
    Button btncopy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manuelupload);
        txtinfo = (TextView) findViewById(R.id.txtinfo);
        pbbar = (ProgressBar) findViewById(R.id.pbbar);
        pbbar.setVisibility(View.GONE);

        edtdir = (EditText) findViewById(R.id.edtdir);

        edtservername = (EditText) findViewById(R.id.edtservername);
        setingPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        edtservername.setText(setingPreferences.getString("smppath", ""));
        btncopy = (Button) findViewById(R.id.btncopy);

    }

        private class MyCopy extends AsyncTask<String, String, String> {

            String z = "";
            String username = "", password = "", servername = "", filestocopy = "";

            @Override
            protected void onPreExecute() {

                pbbar.setVisibility(View.VISIBLE);

                username = setingPreferences.getString("smbuser","");
                password = setingPreferences.getString("smbpass", "");
                servername = "smb://" + edtservername.getText().toString();
                filestocopy = setingPreferences.getString("copyfrom","") +  edtdir.getText().toString() + ".jpg";
            }

            @Override
            protected void onPostExecute(String r) {
                txtinfo.setVisibility(View.VISIBLE);
                txtinfo.setText(r);
                txtinfo.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        txtinfo.setVisibility(View.GONE);
                    }
                }, 5000);


            pbbar.setVisibility(View.GONE);
            }

            @Override
            protected String doInBackground(String... params) {


 //               File folder = new File("/sdcard/Data/pics");
   //             File[] files = new File("/sdcard/Data/pics").listFiles(new FilenameFilter()
    //            { @Override public boolean accept(File dir, String name) { return name.endsWith(".jpg"); } });


     //           File[] jpgFiles = file1.listFiles((dir, name) -> name.endsWith(".jpg"));

       //       for (File entry : jpgFiles) {

      //      System.out.println("File: " + entry.getName());}

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

                    z = "File copied successfully";
                } catch (Exception ex) {

                    z = z + " " + ex.getMessage().toString();
                }

                return z;

            }
        }

        public void btncopy(View view) {
            // TODO Auto-generated method stub

            MyCopy mycopy = new MyCopy();
            mycopy.execute("");
        }
    }
