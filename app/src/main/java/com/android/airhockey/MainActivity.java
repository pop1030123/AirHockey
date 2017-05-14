package com.android.airhockey;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {

    public static final String TAG ="AirHockey" ;
    private boolean rendererSet = false ;

    private GLSurfaceView glSurfaceView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glSurfaceView = new GLSurfaceView(this) ;

        boolean isSupportEs2 = isSupportEs2() ;
        Log.d(TAG ,"isSupportEs2:"+isSupportEs2) ;
        if(isSupportEs2){
            glSurfaceView.setEGLContextClientVersion(2);
            glSurfaceView.setRenderer(new AirHockeyRenderer(this));
            setContentView(glSurfaceView);
            rendererSet = true ;

        }else{
            Toast.makeText(this ,"The device does not support es2 ." ,Toast.LENGTH_SHORT).show();
            rendererSet = false ;
            return;
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        if(rendererSet){
            glSurfaceView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(rendererSet){
            glSurfaceView.onPause();
        }
    }

    private boolean isSupportEs2(){
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo() ;
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000 ;
        return supportsEs2 ;
    }
}
