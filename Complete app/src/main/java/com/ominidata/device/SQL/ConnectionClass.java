package com.ominidata.device.SQL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.util.Log;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
/**
 * Created by kewin on 07-07-2016.
 */
public class ConnectionClass {
    Context context;
    private SharedPreferences setingPreferences;
    String ip;
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    String db;
    String un;
    String password;
    public ConnectionClass (Context context)
    {
        this.context = context;
    }
    @SuppressLint("NewApi")
    public Connection CONN() {
        setingPreferences = context.getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        ip = setingPreferences.getString("server", "");
        db = setingPreferences.getString("db", "");
        un = setingPreferences.getString("dbuser", "");
        password = setingPreferences.getString("dbpass", "");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        try {

            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://" + ip + ";"
                    + "databaseName=" + db + ";user=" + un + ";password="
                    + password + ";";
            conn = DriverManager.getConnection(ConnURL);
        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }
        return conn;
    }
}