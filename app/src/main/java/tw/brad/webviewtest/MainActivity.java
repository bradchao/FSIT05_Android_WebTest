package tw.brad.webviewtest;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private LocationManager lmgr;
    private MyListener myListener;
    private TextView username;
    private UIHandler uiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,},
                    123);
        }else{
            init();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        init();
    }

    private void init(){

        uiHandler = new UIHandler();

        lmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        myListener = new MyListener();
        lmgr.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,0,0,myListener);

        username = findViewById(R.id.username);
        webView = findViewById(R.id.webview);
        initWebView();

    }

    @Override
    public void finish() {
        lmgr.removeUpdates(myListener);
        super.finish();
    }

    private class MyListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            //Log.v("brad", lat + " x " + lng);
            webView.loadUrl("javascript:gotoWhere(" + lat + "," + lng + ")");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }


    private void initWebView(){
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        webView.addJavascriptInterface(new MyJSObject(), "brad");

        webView.loadUrl("file:///android_asset/brad01.html");
        //webView.loadUrl("file:///android_asset/map.html");
        //webView.loadUrl("http://www.iii.org.tw");
    }

    public class MyJSObject {

        @JavascriptInterface
        public void callFromJS(String username){
            Message message = new Message();
            Bundle data = new Bundle();
            data.putString("username", username);
            message.setData(data);
            uiHandler.sendMessage(message);
        }
    }

    private class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            String name = msg.getData().getString("username");
            username.setText(name);
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage("歡迎, " + name)
                    .show();

        }
    }



    @Override
   public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
        }else {
            new AlertDialog.Builder(this)
                    .setMessage("Exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
        }
    }

    public void test1(View view) {
        //webView.loadUrl("javascript:test4('Brad')");
        webView.loadUrl("javascript:gotoKD()");
    }
}
