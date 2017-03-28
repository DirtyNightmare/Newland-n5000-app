package com.ominidata.device;

/**
 * Created by kewin on 29-07-2016.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.ominidata.device.Options.OptionsActivity;
import com.ominidata.device.scan.ScanActivity;


public class MenuActivity extends Activity {
    Button btn_scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);
        btn_scan = (Button) findViewById(R.id.btn_scan);


        btn_scan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplication(),ScanActivity.class);
                startActivity(intent);

            }
        });

    }
}
