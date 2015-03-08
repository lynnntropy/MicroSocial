package me.omegavesko.microsocial.android.alpha.activity;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.List;

import info.hoang8f.widget.FButton;
import me.omegavesko.microsocial.android.alpha.AuthToken;
import me.omegavesko.microsocial.android.alpha.NetworkIdent;
import me.omegavesko.microsocial.android.alpha.NetworkInfo;
import me.omegavesko.microsocial.android.alpha.adapter.NetworkListAdapter;
import me.omegavesko.microsocial.android.alpha.ObjectSocket;
import me.omegavesko.microsocial.android.alpha.R;
import me.omegavesko.microsocial.android.alpha.RequestCode;
import me.omegavesko.microsocial.android.alpha.ServerConnector;

/**
 * Created by Veselin on 1/6/2015.
 */
public class ConnectActivity extends ActionBarActivity
{
    class FindNetworksTask extends AsyncTask<Void, int[], List<NetworkInfo>>
    {
        @Override
        protected List<NetworkInfo> doInBackground(Void... params)
        {
            networksFound.clear();

            // make sure the scanning UI is visible
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    networkList.animate().alpha(0f).setDuration(400).setListener(null);

                    spinner.animate().alpha(1f).setDuration(500).setListener(null);
                    hostNumber.animate().alpha(1f).setDuration(500).setListener(null);
                    networksFoundLabel.animate().alpha(1f).setDuration(500).setListener(null);
                    stopScanningButton.animate().alpha(1f).setDuration(500).setListener(null);
                }
            });

            // get our IP address

            WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
            String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

            // remove the last octet from the IP
            String[] octets = ip.split("\\.");

            // start at 95 for dev purposes!
            // TODO: Set to 0 on alpha/beta/release
            for (int i = 95; i < 255; i++)
            {
                if (!isCancelled())
                {
                    try
                    {
                        // try to connect to every host in our /24 subnet
                        String currentIp = String.format("%s.%s.%s.%d", octets[0], octets[1], octets[2], i);
                        writeLog(String.format("Scanning for networks on host %s", currentIp));
                        publishProgress(new int[]{i});

                        ObjectSocket objectSocket = ServerConnector.connect(currentIp, 9000, new RequestCode(RequestCode.Code.GET_NETWORK_INFO, new AuthToken()));

                        // Implemented a serverside request handler that returns the name of the network.

                        if (objectSocket.rawSocket != null)
                        {
                            // found a network
                            NetworkIdent networkIdent = (NetworkIdent) objectSocket.inputStream.readObject();
                            networksFound.add(new NetworkInfo(networkIdent.networkName, currentIp, 9000));

                            writeLog(String.format("Found network %s at %s", networkIdent.networkName, currentIp));
                            publishProgress(new int[]{i});

                        }
                        else
                        {
                            // not a valid network host, move on
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    return null;
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(int[]... currentHost)
        {
            listAdapter.notifyDataSetChanged();

            hostNumber.setText(String.format("Scanning host %d of 255", currentHost[0][0] + 1));
            networksFoundLabel.setText(String.format("Networks found: %d", networksFound.size()));
        }

        @Override
        protected void onPostExecute(List<NetworkInfo> networkInfos)
        {
            spinner.animate().alpha(0f).setDuration(1000).setListener(null);
            hostNumber.animate().alpha(0f).setDuration(1000).setListener(null);
            networksFoundLabel.animate().alpha(0f).setDuration(1000).setListener(null);
            stopScanningButton.animate().alpha(0f).setDuration(1000).setListener(null);

            networkList.animate().alpha(1f).setDuration(1300).setListener(null);
        }

        void writeLog(String log)
        {
            Log.i("FindNetworksTask", log);
        }

    }

    private FButton changeHotspotButton;
    private TextView hotspotName;
    private ListView networkList;

    private ProgressWheel spinner;
    private TextView hostNumber;
    private TextView networksFoundLabel;
    private FButton stopScanningButton;

    private List<NetworkInfo> networksFound = new ArrayList<NetworkInfo>();
    private NetworkListAdapter listAdapter;

    private FindNetworksTask findNetworksTask;

    @Override
    protected void onStop()
    {
        super.onStop();

        this.findNetworksTask.cancel(true);
//        this.finish();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        this.findNetworksTask.cancel(true);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        this.refreshSSIDLabel();

        // restart the scanning task
        this.findNetworksTask = new FindNetworksTask();
        this.findNetworksTask.execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        this.changeHotspotButton = (FButton) findViewById(R.id.changeHotspot);
        this.changeHotspotButton.setShadowEnabled(false);
        this.changeHotspotButton.setCornerRadius(7);
        this.changeHotspotButton.setTextColor(getResources().getColor(R.color.text_color_dark));

        this.changeHotspotButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });

        this.hotspotName = (TextView) findViewById(R.id.currentHotspotName);
        this.refreshSSIDLabel();

        this.networkList = (ListView) findViewById(R.id.networkList);
        this.listAdapter = new NetworkListAdapter(this, networksFound);
        this.networkList.setAdapter(this.listAdapter);
        this.networkList.setAlpha(0f);

        this.spinner = (ProgressWheel) findViewById(R.id.spinner);
        this.hostNumber = (TextView) findViewById(R.id.hostNumber);
        this.networksFoundLabel = (TextView) findViewById(R.id.networksFound);

        this.stopScanningButton = (FButton) findViewById(R.id.stopScanning);
        this.stopScanningButton.setShadowEnabled(false);
        this.stopScanningButton.setCornerRadius(9);
        this.stopScanningButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                findNetworksTask.cancel(true);

                spinner.animate().alpha(0f).setDuration(1000).setListener(null);
                hostNumber.animate().alpha(0f).setDuration(1000).setListener(null);
                networksFoundLabel.animate().alpha(0f).setDuration(1000).setListener(null);
                stopScanningButton.animate().alpha(0f).setDuration(1000).setListener(null);

                networkList.animate().alpha(1f).setDuration(1300).setListener(null);
            }
        });
    }


    private void refreshSSIDLabel()
    {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String hotspotSSID = wifiInfo.getSSID();
        Log.i("WifiInfo", wifiInfo.toString());

        if (hotspotSSID.contains("unknown ssid")) hotspotSSID = "Not connected.";
        this.hotspotName.setText(hotspotSSID);
    }
}
