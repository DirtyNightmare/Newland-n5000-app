
package com.zhiliantiandi.twodtest;

public class ScanLightController {
    private static ScanLightController mScanLightController;
    static {
        System.loadLibrary("scan_light_controller_jni");
    }

    private ScanLightController() {

    }

    public static ScanLightController getInstance() {
        if (mScanLightController == null) {
            mScanLightController = new ScanLightController();
        }
        return mScanLightController;
    }

    public native int openDevice();

    public native int closeDevice();

    public native int enableFloodLight(boolean enable);

    public native int enableLocationLight(boolean enable);
    
    public native int enableFlashLight(boolean enable);
}
