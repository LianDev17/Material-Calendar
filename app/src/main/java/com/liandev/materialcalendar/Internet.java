package com.liandev.materialcalendar;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import java.net.HttpURLConnection;
import java.net.URL;

public class Internet {

    private Context _context;

    public Internet(Context context) {
        this._context = context;
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager cm = (ConnectivityManager) this._context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
                try {
                    URL url = new URL("https://www.google.com");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(300);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        return true;
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.e("ConnectionERROR","ERROR",e);
                    return false;
                }
        }
        return false;
    }
}