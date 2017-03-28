package com.ominidata.device.SQL;

import com.ominidata.device.CameraActivity;

import com.ominidata.device.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import butterknife.InjectView;



/**
 * Created by kewin on 21-02-2017.
 */


public class UpdateIocation extends AsyncTask<String, String, String> {
    @InjectView(R.id.cardnumberbox)
    EditText cardnumberbox;
    @InjectView(R.id.shelfnumberbox)
    EditText shelfnumberbox;
    String z = "";
    Boolean isSuccess = false;
    String DBcard = cardnumberbox.getText().toString();
    String DBshelf = shelfnumberbox.getText().toString();

    ConnectionClass connectionClass;
    private SharedPreferences setingPreferences;
    Context context;
    CameraActivity cameraActivity;
    public UpdateIocation (Context context)
    {
        this.context = context;
        setingPreferences = context.getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        connectionClass = new ConnectionClass(context);
       // cameraActivity = new CameraActivity(context);
    }




    @Override
    protected void onPreExecute() {
        //     LStatus aLog = LStatus();
        //     aLog.id="";
        //     aLog.status="";
        //     aLog.action="";
        //    aLog.time="";
    }

    @Override
    protected void onPostExecute(String r) {
     //   Toast.makeText(context-CameraActivity.this, r, Toast.LENGTH_SHORT).show();

        if (isSuccess == true) {

        }

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... params) {

        if (DBcard.trim().equals("") || DBshelf.trim().equals(""))
       //     z = getString(R.string.Invalid_Credentials);
      //  else {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
           //         z = getString(R.string.Forbindelses_fejl);
                } else {
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
                        if (s.getMoreResults()) {
                            System.out.printf(
                                    "INFO:%n" +
                                            "  A second ResultSet was found.%n" +
                                            "  The previous ResultSet was returned by the stored procedure.%n" +
                                            "  Getting next ResultSet ...%n",
                                    "");
                           rs = s.getResultSet();
                            rs.next();
                            System.out.println(rs.getString(1));
                            if (rs.getString(1).equals("0")) {
                     //          z = getString(R.string.Update_succes);
                                isSuccess = true;
                            }

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
                         //       z = getString(R.string.untrashed);
                            }
                            else if (Sold.equals("1.000000"))
                            {
                        //        z = getString(R.string.item_sold);

                            }
                        }
                        con.close();
                       rs.close();
                        rsS.close();
                    }
                }
            } catch (Exception ex) {
                isSuccess = false;
                //z = getString(R.string.Exceptions) + "L2)";
                Log.e("MYAPP", "exception", ex);
            }
      //  }
        return z;
    }
}