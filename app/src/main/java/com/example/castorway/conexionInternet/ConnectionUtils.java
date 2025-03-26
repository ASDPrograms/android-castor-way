package com.example.castorway.conexionInternet;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
public class ConnectionUtils {
    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
