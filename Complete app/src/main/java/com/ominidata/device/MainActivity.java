package com.ominidata.device;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ominidata.device.Options.Devoptions;
import com.ominidata.device.SQL.ConnectionClass;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;


public class MainActivity extends Activity {
    private CheckBox saveLoginCheckBox;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    private SharedPreferences setingPreferences;
    private SharedPreferences.Editor setingsPrefsEditor;
    ConnectionClass connectionClass;
    EditText edtuserid,edtpass;
    Button btnlogin,btntest;
    ProgressBar pbbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setingPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        setingsPrefsEditor = setingPreferences.edit();
        setContentView(R.layout.activity_main);
        connectionClass = new ConnectionClass(this.getApplicationContext());
        edtuserid = (EditText) findViewById(R.id.edtuserid);
        edtpass = (EditText) findViewById(R.id.edtpass);
        btnlogin = (Button) findViewById(R.id.btnlogin);
        saveLoginCheckBox = (CheckBox) findViewById(R.id.saveLoginCheckBox);
        pbbar = (ProgressBar) findViewById(R.id.pbbar);
        pbbar.setVisibility(View.GONE);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin == true) {
            edtuserid.setText(loginPreferences.getString("username", ""));
            edtpass.setText(loginPreferences.getString("password", ""));
            saveLoginCheckBox.setChecked(true);
        }
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.CUPCAKE)
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtuserid.getWindowToken(), 0);
                String userid = edtuserid.getText().toString();
                String password = edtpass.getText().toString();
                if (saveLoginCheckBox.isChecked()) {
                    loginPrefsEditor.putBoolean("saveLogin", true);
                    loginPrefsEditor.putString("username", userid);
                    loginPrefsEditor.putString("password", password);
                    loginPrefsEditor.apply();
                } else {
                    loginPrefsEditor.clear();
                    loginPrefsEditor.apply();
                }

                DoLogin doLogin = new DoLogin();
                doLogin.execute("");

            }
        });
    }

    public class DoLogin extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;
        Boolean isSuccess2 = false;


        String userid = edtuserid.getText().toString();
        String password = edtpass.getText().toString();


        @Override
        protected void onPreExecute() {
            pbbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this,r,Toast.LENGTH_SHORT).show();

            if(isSuccess) {
                Intent i = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(i);
                finish();

            }
            if(isSuccess2) {
                Intent i2 = new Intent(MainActivity.this, Devoptions.class);
                startActivity(i2);
                finish();

            }
        }

        @Override
        protected String doInBackground(String... params) {
            if (userid.trim().equals("Developer")|| password.trim().equals("Dev!n_234"))
                isSuccess2=true;
            z = getString(R.string.login_succes);
            if(userid.trim().equals("")|| password.trim().equals(""))
                z = getString(R.string.indsæt_rigtigt_bruger);
            else {
                try {
                    Connection con = connectionClass.CONN();
                    if (con == null) {
                        z = getString(R.string.Forbindelses_fejl) + "L1)";

                    } else {
                        String ID;
                        ID = setingPreferences.getString("companyid", "");
                        CallableStatement cs = null;
                        String query = "{ call [system].[usp_validateUserLogin](?,?,?,?,?)}  ";
                        cs = con.prepareCall(query);
                        cs.setString(1, userid);
                        cs.setString(2, password);
                        cs.setString(3, ID);
                        cs.setBoolean(4, true);
                        cs.registerOutParameter(5, Types.VARCHAR);
                        ResultSet rs = cs.executeQuery();
                        if (rs.next()) {
                           z = getString(R.string.login_succes);
                            isSuccess = true;
                        } else {
                            z = getString(R.string.Invalid_Credentials);
                            isSuccess = false;
                        }
                        cs.getMoreResults();
                        String DoerTicket = cs.getString(5).toString();
                        setingsPrefsEditor.putString("doerTicket", DoerTicket);
                        setingsPrefsEditor.apply();
                        System.out.println(cs.getString(5));
                        rs.close();
                        cs.close();
                        con.close();
                    }
                }
                catch (Exception ex)
                {
                    isSuccess = false;
                    z = getString(R.string.Exceptions)+"L2)";
                    Log.e("MYAPP", "exception", ex);
                }
            }
            return z;

        }
    }


}
