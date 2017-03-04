package hr.duby.rcmower.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;

import hr.duby.rcmower.network.wifi.NetworkUtil;

/**
 * Created by Duby on 4.3.2017..
 */

/**
 * Receives wifi changes and creates a notification when wifi connects to a network,
 * displaying the SSID and MAC address.
 *
 * Put the following in your manifest !!!
 * <receiver android:name=".WifiReceiver" android:exported="false" >
 *   <intent-filter>
 *     <action android:name="android.net.wifi.WIFI_STATE_CHANGED" />
 *   </intent-filter>
 * </receiver>
 * <service android:name=".WifiReceiver$WifiActiveService" android:exported="false" />
 *
 */
public class WifiReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(final Context context, final Intent intent) {
        String RX_ACTION = intent.getAction();
       // Log.d("DTag", "WifiReceiver: onReceive INTENT -> " + RX_ACTION );

        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(RX_ACTION)) {
            //Log.d("DTag", "WifiReceiver: onReceive -> NETWORK_STATE_CHANGED_ACTION");

            NetworkInfo nwInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            String netState = nwInfo.getState().toString();
            Log.d("DTag", "WifiReceiver: onReceiver -> netState: " + netState);
            if (NetworkInfo.State.CONNECTED.equals(nwInfo.getState())) {//This implies the WiFi connection is through
                Log.d("DTag", "WifiReceiver: onReceive -> CONNECTED");
                String[] netInfo = getNetworkInfo(context);

                Intent outIntent = new Intent();
                outIntent.setAction("ACTION_WIFI_CONNECTED");
                outIntent.putExtra("NETINFO", netInfo);
                context.sendBroadcast(outIntent);

            }
        }
    }

    private String[] getNetworkInfo(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService (Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo ();
        String ssid  = wifiInfo.getSSID();
        int deviceIP = wifiInfo.getIpAddress();
        String deviceIPStr = NetworkUtil.formatIPAddress(deviceIP);

        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        int serverIP = dhcpInfo.serverAddress;
        String serverIPStr = NetworkUtil.formatIPAddressAdvanced(serverIP);
        //String message = "SSID: " + ssid + "; device IP: " + deviceIPStr + ", server IP: " + serverIPStr;

        String[] result = {"SSID: " + ssid, "device IP: " + deviceIPStr, "serverIP: " + serverIPStr };
        return result;

    }



}