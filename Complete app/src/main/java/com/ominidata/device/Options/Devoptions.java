package com.ominidata.device.Options;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

import com.ominidata.device.R;


/**
 * Created by kewin on 15-08-2016.
 */
public class Devoptions extends Activity {

    private SharedPreferences setingPreferences;
    private SharedPreferences.Editor setingsPrefsEditor;
    EditText server,db_User,db_Pasw,company_ID,smb_path,smb_user,smb_Pasw,copy_from,Buffer_bytes,db,Pr_fps,Pr_Se,Disp_Orn;
    Button btn_save, btn_nmF;
    TabHost th;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.devoptions);
        th = (TabHost) findViewById(R.id.tabhost);
        th.setup();
        TabHost.TabSpec specs = th.newTabSpec("tag2");
        specs.setContent(R.id.tab2);
        specs.setIndicator("Forbindelser");
        th.addTab(specs);
        specs = th.newTabSpec("tag3");
        specs.setContent(R.id.tab3);
        specs.setIndicator("Parameter Værdier");
        th.addTab(specs);
        server = (EditText) findViewById(R.id.Server);
        db = (EditText) findViewById(R.id.DB);
        db_User = (EditText) findViewById(R.id.DB_User);
        db_Pasw = (EditText) findViewById(R.id.DB_Pasw);
        company_ID = (EditText) findViewById(R.id.Company_ID);
        smb_path = (EditText) findViewById(R.id.SMB_path);
        smb_user = (EditText) findViewById(R.id.SMB_user);
        smb_Pasw = (EditText) findViewById(R.id.SMB_Pasw);
        copy_from= (EditText) findViewById(R.id.Copy_from);
        Buffer_bytes = (EditText) findViewById(R.id.buffer_bytes);
        Pr_fps= (EditText) findViewById(R.id.PR_fps);
        Pr_Se= (EditText) findViewById(R.id.Pr_Se);
        Disp_Orn = (EditText) findViewById(R.id.disp_Orn);
        setingPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        setingsPrefsEditor = setingPreferences.edit();
          //server
            server.setText(setingPreferences.getString("server", ""));
            db.setText(setingPreferences.getString("db", ""));
            db_User.setText(setingPreferences.getString("dbuser", ""));
            db_Pasw.setText(setingPreferences.getString("dbpass", ""));
            company_ID.setText(setingPreferences.getString("companyid", ""));
       //smb
        smb_path.setText(setingPreferences.getString("smppath", ""));
        smb_user.setText(setingPreferences.getString("smbuser", ""));
        smb_Pasw.setText(setingPreferences.getString("smbpass", ""));
        copy_from.setText(setingPreferences.getString("copyfrom", ""));
        Buffer_bytes.setText(setingPreferences.getString("buffer", ""));
        //parameter
        Pr_fps.setText(setingPreferences.getString("previewfps", ""));
        Pr_Se.setText(setingPreferences.getString("screensize", ""));
        Disp_Orn.setText(setingPreferences.getString("orientation", ""));
        //
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_nmF = (Button) findViewById(R.id.btn_nmf);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String z = "";
              //server
                String servers = server.getText().toString();
                String Db = db.getText().toString();
                String dbuser = db_User.getText().toString();
                String dbpas = db_Pasw.getText().toString();
                String id = company_ID.getText().toString();
                    setingsPrefsEditor.putString("server", servers);
                    setingsPrefsEditor.putString("db", Db);
                    setingsPrefsEditor.putString("dbuser", dbuser);
                    setingsPrefsEditor.putString("dbpass", dbpas);
                    setingsPrefsEditor.putString("companyid", id);
               //smb
                String smb = smb_path.getText().toString();
                String smbuser = smb_user.getText().toString();
                String smbpass = smb_Pasw.getText().toString();
                String copyfrom = copy_from.getText().toString();
                String buffer = Buffer_bytes.getText().toString();
                setingsPrefsEditor.putString("smppath", smb);
                setingsPrefsEditor.putString("smbuser", smbuser);
                setingsPrefsEditor.putString("smbpass", smbpass);
                setingsPrefsEditor.putString("copyfrom", copyfrom);
                setingsPrefsEditor.putString("buffer", buffer);
                //parameter
                String previewfps = Pr_fps.getText().toString();
                String screensize = Pr_Se.getText().toString();
                String orientation = Disp_Orn.getText().toString();
                setingsPrefsEditor.putString("previewfps", previewfps);
                setingsPrefsEditor.putString("screensize", screensize);
                setingsPrefsEditor.putString("orientation", orientation);
                setingsPrefsEditor.apply();
                Toast.makeText(getApplicationContext(), getString(R.string.Update_succes), Toast.LENGTH_SHORT).show();
            }});
        btn_nmF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vs) {
                //server
                server.setText("10.5.25.4");
                db.setText("ISTABLocalDB");
                db_User.setText("istab_wpf");
                db_Pasw.setText("istab_!234");
                company_ID.setText("72");
                //smb
                smb_path.setText("10.5.25.4/App/ISTAB.Data/Users/Autoupload/Pics/");
                smb_user.setText("Sindal\\sindakewin");
                copy_from.setText("/sdcard/Data/pics/");
                Buffer_bytes.setText("20971520");
             //parameter
                Pr_fps.setText("20");
                Pr_Se.setText("1280,720");
                Disp_Orn.setText("90");
    }});
    }}








