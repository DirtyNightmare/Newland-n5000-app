package com.ominidata.device.io;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.TextView;
import com.ominidata.device.R;
import com.ominidata.device.SQL.ConnectionClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import butterknife.InjectView;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

/**
 * Created by kewin on 21-02-2017.
 */

public class Imageuploader extends AsyncTask<String, String, String> {
    @InjectView(R.id.cardnumberbox)
    EditText cardnumberbox;
    @InjectView(R.id.Count)
    TextView count;
    String z = "";
    String username = "", password = "", servername = "", filestocopy = "";
    private SharedPreferences setingPreferences;
    Context context;
    public Imageuploader (Context context)
    {
        this.context = context;
        setingPreferences = context.getSharedPreferences("Settings", Activity.MODE_PRIVATE);
    }


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
  //      Toast.makeText(CameraActivity.this, r, Toast.LENGTH_SHORT).show();
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
        //    z = getString(R.string.pic_uploaded);

        } catch (Exception ex) {
        }

        return z;

    }

}
