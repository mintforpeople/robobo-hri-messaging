package com.mytechia.robobo.framework.messaging;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.TextureView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.exception.ModuleNotFoundException;
import com.mytechia.robobo.framework.hri.messaging.twitter.ITwitterListener;
import com.mytechia.robobo.framework.service.RoboboServiceHelper;

import com.mytechia.robobo.framework.hri.messaging.twitter.ITwitterModule;

import java.util.ArrayList;

public class TestActivity extends AppCompatActivity implements ITwitterListener {

    private static final String TAG="MainActivity";


    private RoboboServiceHelper roboboHelper;
    private RoboboManager roboboManager;


    private ITwitterModule twitterModule;


    private RelativeLayout rellayout = null;
    private TextView textView = null;
    private SurfaceView surfaceView = null;
    private ImageView imageView = null;
    private TextureView textureView = null;

    private boolean paused = true;
    private long lastDetection = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robobo_custom_main);
        this.textView = (TextView) findViewById(R.id.textView);
        this.rellayout = (RelativeLayout) findViewById(R.id.rellayout);
        // this.surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        this.imageView = (ImageView) findViewById(R.id.imageView);

//        this.textureView = (TextureView) findViewById(R.id.textureView);
        roboboHelper = new RoboboServiceHelper(this, new RoboboServiceHelper.Listener() {
            @Override
            public void onRoboboManagerStarted(RoboboManager robobo) {

                //the robobo service and manager have been started up
                roboboManager = robobo;


                //dismiss the wait dialog


                //start the "custom" robobo application
                startRoboboApplication();

            }

            @Override
            public void onError(String errorMsg) {

                final String error = errorMsg;


            }

        });

        //start & bind the Robobo service
        Bundle options = new Bundle();
        roboboHelper.bindRoboboService(options);
    }
    private void startRoboboApplication() {

        try {

            this.twitterModule = this.roboboManager.getModuleInstance(ITwitterModule.class);

        } catch (ModuleNotFoundException e) {
            e.printStackTrace();
        }


        twitterModule.suscribe(this);

        twitterModule.setAutoCheckDelay(10000);

    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        if (!paused){
//            //twitterModule.updateStatus("Hola mundo!");
//            paused = true;
//        }
//        twitterModule.checkMentions();
        return super.onTouchEvent(event);
    }


    @Override
    protected void onDestroy() {


        super.onDestroy();

        //we unbind and (maybe) stop the Robobo service on exit
        if (roboboHelper != null) {
            roboboHelper.unbindRoboboService();
        }

    }



    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        paused = true;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        paused = false;
    }

    @Override
    public void onNewMention(ArrayList<String> mentions) {
        Log.d(TAG,"NEW MENTIONS!");
        for (String mention :mentions){
            Log.d(TAG,mention);
        }
    }
}
