package com.example.android.popularmoviesstage2.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.widget.Toast;

import com.example.android.popularmoviesstage2.R;

public final class NetworkUtils {

    private NetworkUtils() {

    }

    public static boolean checkConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        return  ((activeNetwork != null) && (activeNetwork.isConnectedOrConnecting()));
    }

    public static void noConnection(Context context) {
        Toast toast = Toast.makeText(context, R.string.network_unavailable, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
