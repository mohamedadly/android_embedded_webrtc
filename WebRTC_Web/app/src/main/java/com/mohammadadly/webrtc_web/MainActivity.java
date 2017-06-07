package com.mohammadadly.webrtc_web;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int CAMERA_MIC_PERMISSION_REQUEST_CODE = 1;
    private WebView mWebRTCWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Request Permissions
        this.requestPermissionForCameraAndMicrophone();

        //WebView
        mWebRTCWebView = (WebView) findViewById(R.id.a_webview);

        //Setting
        this.setUpWebViewDefaults(mWebRTCWebView);

        //JS Interface for Viewers Count
        mWebRTCWebView.addJavascriptInterface(this, "Android");

        //Load WebRTC Page
        mWebRTCWebView.loadUrl("https://www.searchandmap.com/php/cast/webview.php");

        //Grant WebView Permissions
        final MainActivity context = this;
        mWebRTCWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                Log.d(TAG, "onPermissionRequest");
                context.runOnUiThread(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {
                        if (request.getOrigin().toString().equals("https://www.searchandmap.com/")) {
                            request.grant(request.getResources());
                        } else {
                            request.deny();
                        }
                    }
                });
            }

            @Override
            public Bitmap getDefaultVideoPoster() {
                return Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
            }
        });

        //Buttons
        Button _startBtn = (Button) findViewById(R.id.btn_start);
        _startBtn.setOnClickListener(this);
        Button _stopBtn = (Button) findViewById(R.id.btn_stop);
        _stopBtn.setOnClickListener(this);
        Button _switchBtn = (Button) findViewById(R.id.btn_switch);
        _switchBtn.setOnClickListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        /**
         * When the application falls into the background we want to stop the media stream
         * such that the camera is free to use by other apps.
         */
        mWebRTCWebView.evaluateJavascript("stopStream();", null);
    }

    private void requestPermissionForCameraAndMicrophone() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
            Toast.makeText(this,
                    "WE NEED CAMERA AND MIC PERMISSIONS",
                    Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                    CAMERA_MIC_PERMISSION_REQUEST_CODE);
        }
    }

    private void setUpWebViewDefaults(WebView webView) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Enable Javascript

        // Use WideViewport and Zoom out if there is no viewport defined
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        // Allow use of Local Storage
        webSettings.setDomStorageEnabled(true);

        // Hide the zoom controls
        webSettings.setDisplayZoomControls(false);

        // Enable remote debugging via chrome://inspect
        WebView.setWebContentsDebuggingEnabled(true);

        // Disable user interactions
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        webView.setWebViewClient(new WebViewClient());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start: {
                Toast.makeText(this, "Start", Toast.LENGTH_SHORT).show();

                //Session ID
                long sid = System.currentTimeMillis();
                TextView textview = (TextView) findViewById(R.id.textView);
                textview.setText("SID = " + String.valueOf(sid));

                //Start Stream
                mWebRTCWebView.evaluateJavascript("startStream('" + String.valueOf(sid) + "');", null);
            }
            break;
            case R.id.btn_stop: {
                Toast.makeText(this, "Stop", Toast.LENGTH_SHORT).show();

                //Stop Stream
                mWebRTCWebView.evaluateJavascript("stopStream();", null);
            }
            break;
            case R.id.btn_switch: {
                Toast.makeText(this, "Switch", Toast.LENGTH_SHORT).show();

                //Switch Camera
                mWebRTCWebView.evaluateJavascript("switchCamera();", null);
            }
            break;
            default:
                break;
        }
    }


    /**
     * Show a toast from the web page with viewers count
     */
    @JavascriptInterface
    public void viewersCountDidChange(String count) {
        Toast.makeText(this, "Viewers = " + count, Toast.LENGTH_SHORT).show();
    }

}
