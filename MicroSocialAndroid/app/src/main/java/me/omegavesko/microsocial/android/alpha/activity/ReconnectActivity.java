package me.omegavesko.microsocial.android.alpha.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;

import me.omegavesko.microsocial.android.alpha.ObjectSocket;
import me.omegavesko.microsocial.android.alpha.R;
import me.omegavesko.microsocial.android.alpha.RequestCode;
import me.omegavesko.microsocial.android.alpha.ServerConnector;

public class ReconnectActivity extends ActionBarActivity
{
    private class ReconnectTask extends AsyncTask <Void, String, Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            // get the location for the last network we were connected to
            SharedPreferences settings = getSharedPreferences("currentNetworkInfo", 0);
            String serverLocation = settings.getString("networkLocation", "none");

            publishProgress("Connecting to server..");

            if (isCancelled()) return null;

            // attempt to reconnect to that network
            ObjectSocket objectSocket = ServerConnector.connect(
                    serverLocation, 9000,
                    new RequestCode(RequestCode.Code.GET_NETWORK_INFO));

            if (isCancelled()) return null;

            Intent activityIntent;
            // if connection successful, go to the main activity
            if (objectSocket.rawSocket != null)
            {
                publishProgress("Server found.");
                activityIntent = new Intent(ReconnectActivity.this, MainActivity.class);
            }
            else
            // if no network found, go to the network finder activity
            {
                publishProgress("Server not found.");
                activityIntent = new Intent(ReconnectActivity.this, ConnectActivity.class);
            }

            if (isCancelled()) return null;

            // start the activity we've decided to go to
            ReconnectActivity.this.startActivity(activityIntent);
            // don't let users go back out into this activity
            ReconnectActivity.this.finish();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values)
        {
            changeLoadingText(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
        }
    }

    private Toolbar toolbar;
    private ProgressWheel progressWheel;
    private TextView progressLabel;

    private ReconnectTask reconnectTask;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reconnect);

        this.toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        this.progressWheel = (ProgressWheel) findViewById(R.id.spinner);
        this.progressLabel = (TextView) findViewById(R.id.progressLabel);

//        this.reconnectTask = new ReconnectTask();
//        this.reconnectTask.execute();
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        this.reconnectTask.cancel(true);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        this.reconnectTask.cancel(true);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        this.reconnectTask = new ReconnectTask();
        this.reconnectTask.execute();
    }

    void changeLoadingText(final String text)
    {
        this.progressLabel.animate().alpha(0f).setDuration(350).setListener(null);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    progressLabel.setText(text);
                                    progressLabel.animate().alpha(1f).setDuration(350).setListener(null);
                                }
                            },
                350);
    }
}