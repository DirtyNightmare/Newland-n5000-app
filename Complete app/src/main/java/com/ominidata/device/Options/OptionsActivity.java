package com.ominidata.device.Options;

import android.app.Activity;
import android.os.Bundle;

import com.ominidata.device.R;


/**
 * Created by kewin on 20-07-2016.
 */
public class OptionsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options_activity);
    }
    private void loadprefs(){
    }
    private void saveprefs(String key,boolean value){

    }
    private void saveprefs(String key,String value){

    }
}
