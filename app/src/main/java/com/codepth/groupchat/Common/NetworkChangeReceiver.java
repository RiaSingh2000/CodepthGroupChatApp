package com.codepth.groupchat.Common;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.codepth.groupchat.NetworkDialog;
import com.codepth.groupchat.NoConnectionActivity;
import com.codepth.groupchat.R;

import java.net.NetworkInterface;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class NetworkChangeReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(final Context context, final Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Toast.makeText(context, "Wifi enabled", Toast.LENGTH_LONG).show();

            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Toast.makeText(context, "Mobile data enabled", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(context, "No internet is available", Toast.LENGTH_LONG).show();
        }


    }

}
