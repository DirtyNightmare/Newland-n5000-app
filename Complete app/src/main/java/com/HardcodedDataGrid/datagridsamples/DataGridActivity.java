package com.HardcodedDataGrid.datagridsamples;

import com.HardcodedDataGrid.datagrid.DataGrid;

import com.HardcodedDataGrid.datatable.DataTable;
import com.ominidata.device.SQL.ConnectionClass;
import com.ominidata.device.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;


public class DataGridActivity extends Activity {
    /** Called when the activity is first created. */
    ConnectionClass connectionClass;
    //define column
    DataTable.DataRow drRow;
    DataTable dtDataSource = new DataTable();
    Button btnsearch;
    DataGrid dg;
    EditText cardnumber,carcode;
    TextView parts;
    private SharedPreferences setingPreferences;
    private SharedPreferences.Editor setingsPrefsEditor;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setingPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        setingsPrefsEditor = setingPreferences.edit();
        setContentView(R.layout.datagridt);
        dg = (DataGrid)findViewById(R.id.datagrid);
        btnsearch = (Button) findViewById(R.id.btnsearch);
        connectionClass = new ConnectionClass(this.getApplicationContext());
        carcode = (EditText) findViewById(R.id.carcode);
        cardnumber = (EditText) findViewById(R.id.cardnumber);
        parts = (TextView)  findViewById(R.id.parts);
        /**
         *  Prepare the DataGrid
         */
        //initialize DataGrid
        //define column style, bond each DataGrid column by DataTable column
        dg.addColumnStyles(new DataGrid.ColumnStyle[]{
                new DataGrid.ColumnStyle(getString(R.string.itemcode), "column_1", 80),
                new DataGrid.ColumnStyle(getString(R.string.Cartype), "column_2", 120),
                new DataGrid.ColumnStyle(getString(R.string.itemnumber), "column_3", 100),
                new DataGrid.ColumnStyle(getString(R.string.partype), "column_4", 150),
                new DataGrid.ColumnStyle(getString(R.string.carnumber), "column_5", 110),
                new DataGrid.ColumnStyle(getString(R.string.condition), "column_6", 85),
                new DataGrid.ColumnStyle(getString(R.string.Enginetype), "column_7", 110),
                new DataGrid.ColumnStyle(getString(R.string.Kilometrage), "column_8", 100),
                new DataGrid.ColumnStyle(getString(R.string.Shelfnumber), "column_9", 120),
                new DataGrid.ColumnStyle(getString(R.string.year), "column_10", 80),
                new DataGrid.ColumnStyle(getString(R.string.OEMNumber), "column_11", 190),
                new DataGrid.ColumnStyle(getString(R.string.Comments), "column_12", 180),
                new DataGrid.ColumnStyle(getString(R.string.Notes), "column_13", 170),
                new DataGrid.ColumnStyle(getString(R.string.WarehouseInputDate), "column_14", 170),
                new DataGrid.ColumnStyle(getString(R.string.CarDoorsType), "column_15", 80),
                new DataGrid.ColumnStyle(getString(R.string.CarTypeApprovalNo), "column_16", 180),
                new DataGrid.ColumnStyle(getString(R.string.CarTypeApprovalDate), "column_17", 210),
                new DataGrid.ColumnStyle(getString(R.string.CarFirstRegistrationDate), "column_18", 180),
                new DataGrid.ColumnStyle(getString(R.string.enginecode), "column_19", 110),
                new DataGrid.ColumnStyle(getString(R.string.Price)+"1", "column_20", 80),
                new DataGrid.ColumnStyle(getString(R.string.Price)+"2", "column_21", 80),
                new DataGrid.ColumnStyle(getString(R.string.Price)+"3", "column_22", 80),
                new DataGrid.ColumnStyle(getString(R.string.DiscountPrice), "column_23", 80),
                new DataGrid.ColumnStyle(getString(R.string.Onhand), "column_24", 90),
                new DataGrid.ColumnStyle(getString(R.string.Quantity), "column_25", 110),
                new DataGrid.ColumnStyle(getString(R.string.EuroNorm), "column_26", 100)

        });

    }


    public class Itemnumber extends AsyncTask<String,String,String> {
        String z = "";
        String Cardnumber = cardnumber.getText().toString();
        String Carcode = carcode.getText().toString();
        String doerTicket;

        @Override
        protected void onPreExecute() {
            dtDataSource.addAllColumns(new String[]{"column_1", "column_2","column_3", "column_4","column_5","column_6", "column_7","column_8", "column_9","column_10","column_11", "column_12","column_13", "column_14","column_15","column_16", "column_17","column_18","column_19", "column_20","column_21", "column_22","column_23", "column_24","column_25", "column_26"});
            drRow = dtDataSource.newRow();
            dtDataSource.add(drRow);
            dg.setDataSource(dtDataSource);
            dg.refresh();

        }
        @Override
        protected void onPostExecute(String r) {
            parts.setText(getString(R.string.Found)+"  "+setingPreferences.getString("Part", "")+"  "+getString(R.string.Part));
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    doerTicket = setingPreferences.getString("doerTicket", "");
                    CallableStatement cs = null;
                    String query = "exec [file].[usp_getParts] \n@p_ItemNumber = ?,\n@p_DoerTicket = ?,\n@p_TotalRows = ?";
                    cs = con.prepareCall(query);
                    cs.setString(1, Cardnumber != null ? Cardnumber : "******");//edittext(@p_ItemNumber)
                    cs.setString(2, doerTicket);
                    cs.registerOutParameter(3, Types.INTEGER);
                    ResultSet rs = cs.executeQuery();
                    while (rs.next()) {
                        //create DataRow
                        drRow.set("column_1", rs.getString(5));//itemcode
                        drRow.set("column_2", rs.getString(25));//Cartype
                        drRow.set("column_3", rs.getString(6));//itemnumber
                        drRow.set("column_4", rs.getString(51));//partype
                        drRow.set("column_5", rs.getString(21));//carnumber
                        drRow.set("column_6", rs.getString(10));//condition
                        drRow.set("column_7", rs.getString(22));//Enginetype
                        drRow.set("column_8", rs.getString(23));//Kilometrage
                        drRow.set("column_9", rs.getString(45));//Shelfnumber
                        drRow.set("column_10", rs.getString(24));//year
                        drRow.set("column_11", rs.getString(44));//oem
                        drRow.set("column_12", rs.getString(41));//Comments
                        drRow.set("column_13", rs.getString(42));//Notes
                        drRow.set("column_14", rs.getString(36));//warehouseinput
                        drRow.set("column_15", rs.getString(32));//CarDoorsType
                        drRow.set("column_16", rs.getString(33));//CarTypeApprovalNo
                        drRow.set("column_17", rs.getString(34));//CarTypeApprovalDate
                        drRow.set("column_18", rs.getString(35));//CarFirstRegistrationDate
                        drRow.set("column_19", rs.getString(28));//CarEngineCode
                        drRow.set("column_20", rs.getString(37));//price1
                        drRow.set("column_21", rs.getString(38));//price2
                        drRow.set("column_22", rs.getString(39));//price3
                        drRow.set("column_23", rs.getString(40));//DiscountPrice
                        drRow.set("column_24", rs.getString(54));//Onhand
                        drRow.set("column_25", rs.getString(57));//Quantity
                        drRow.set("column_26", rs.getString(31));//EuroNorm
                    }
                    String Part = cs.getString(3).toString();
                    setingsPrefsEditor.putString("Part", Part);
                    setingsPrefsEditor.apply();
                }
            } catch (Exception ex) {
                z = "Exceptions";
            }
            return z;

        }
    }

    public void btnsearch (View view) {
        // TODO Auto-generated method stub
        Itemnumber item = new Itemnumber();
        item.execute("");
    }
}


