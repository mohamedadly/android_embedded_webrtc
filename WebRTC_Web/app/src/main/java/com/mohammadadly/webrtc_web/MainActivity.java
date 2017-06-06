package com.mohammadadly.webrtc_web;

import android.Manifest;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

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

    }

    @Override
    protected void onStop() {
        super.onStop();

        /**
         * When the application falls into the background we want to stop the media stream
         * such that the camera is free to use by other apps.
         */
        mWebRTCWebView.evaluateJavascript("", null);
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
}
