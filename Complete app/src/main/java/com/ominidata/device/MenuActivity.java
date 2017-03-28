package com.ominidata.device;

/**
 * Created by kewin on 29-07-2016.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.HardcodedDataGrid.datagridsamples.DataGridActivity;
import com.ominidata.device.Options.OptionsActivity;
import com.ominidata.device.io.InventoryActivity;
import com.ominidata.device.io.manuel_upload;


public class MenuActivity extends Activity {
    Button btn_cam,btn_inv,btn_option,buttonupload,btn_lager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);
        btn_cam = (Button) findViewById(R.id.btn_cam);
        btn_inv = (Button) findViewById(R.id.btn_inv);
        btn_option = (Button) findViewById(R.id.btn_options);
        buttonupload = (Button) findViewById(R.id.buttonupload);
        btn_lager = (Button) findViewById(R.id.btn_lager);


        btn_cam.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplication(),CameraActivity.class);
                startActivity(intent);

            }
        });
        btn_inv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplication(),InventoryActivity.class);
                startActivity(intent);
            }
        });

        btn_option.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplication(),OptionsActivity.class);
                startActivity(intent);

            }
        });
        buttonupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplication(),manuel_upload.class);
                startActivity(intent);
            }
        });
        btn_lager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplication(),DataGridActivity.class);
                startActivity(intent);
            }
        });

    }
}