package com.ominidata.device.scan;

import com.zltd.decoder.DecoderManager;
import android.app.Application;

public class TestApplication extends Application {

    private static final String TAG = "TestApplication";
    public static DecoderManager mDecoder = null;

    public void onCreate() {
        mDecoder = DecoderManager.getInstance();
    };

    public static DecoderManager getDecoder() {
        return mDecoder;
    }

}
