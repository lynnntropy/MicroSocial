package me.omegavesko.microsocial.android.alpha.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import butterknife.ButterKnife;
import butterknife.InjectView;
import info.hoang8f.widget.FButton;
import me.omegavesko.microsocial.android.alpha.R;
import me.omegavesko.microsocial.android.alpha.network.RESTManager;
import me.omegavesko.microsocial.android.alpha.schema.CheckSession;
import me.omegavesko.microsocial.android.alpha.schema.LoginAttempt;
import retrofit.client.Response;

public class ConnectActivity extends ActionBarActivity
{
    Activity connectActivity = this;

    @InjectView(R.id.main_toolbar)
    Toolbar mainToolbar;
    @InjectView(R.id.currentHotspotName)
    TextView currentHotspotName;
    @InjectView(R.id.currentHotspotLabel)
    TextView currentHotspotLabel;
    @InjectView(R.id.changeHotspot)
    FButton changeHotspot;
    @InjectView(R.id.relativeLayout2)
    RelativeLayout relativeLayout2;
    @InjectView(R.id.spinner)
    ProgressWheel spinner;
    @InjectView(R.id.serverIp)
    MaterialEditText serverIp;
    @InjectView(R.id.username)
    MaterialEditText username;
    @InjectView(R.id.password)
    MaterialEditText password;
    @InjectView(R.id.connectButton)
    FButton connectButton;
    @InjectView(R.id.newUserButton)
    FButton newUserButton;

    class AttemptLoginTask extends AsyncTask<LoginAttempt, Void, Integer>
    {
        String IP, username, password;

        AttemptLoginTask(String IP, String username, String password)
        {
            this.IP = IP;
            this.username = username;
            this.password = password;
        }

        @Override
        protected Integer doInBackground(LoginAttempt... params)
        {
            RESTManager restManager = RESTManager.getManager();
            Response loginResponse = restManager.restInterface.login(new LoginAttempt(username, password));

            if (loginResponse.getStatus() != 200)
            {
                // failure
                return null;
            } else
            {
                // success
                try
                {
                    String responseBody = inputStreamToString(loginResponse.getBody().in());
                    Log.i("LoginResponse", "Response body: " + responseBody);

                    JSONObject jsonObject = new JSONObject(responseBody);

                    int session = Integer.parseInt(jsonObject.getString("session_id"));
                    return session;
                } catch (Exception e)
                {
                    Log.e("JSON", "e", e);
                    return null;
                }
            }
        }

        @Override
        protected void onPostExecute(Integer integer)
        {
            Log.i("Login", String.format("Login successful, session ID [%d]", integer));

            // go to main activity

            if (integer != null)
            {
                // login success, open the main activity
                SharedPreferences storedNetworkSettings = getSharedPreferences("lastNetwork", 0);
                SharedPreferences.Editor editor = storedNetworkSettings.edit();
                editor.putString("username", username);
                editor.putInt("session", integer);
                editor.putString("ip", this.IP);
                editor.commit();

                Intent intent = new Intent(connectActivity, MainActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                // failure
//                switchToLoginForm();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
            super.onProgressUpdate(values);
        }
    }

    class CheckSessionTask extends AsyncTask<Void, Void, String>
    {
        int session;

        CheckSessionTask(int session)
        {
            this.session = session;
        }

        @Override
        protected String doInBackground(Void... params)
        {
            RESTManager restManager = RESTManager.getManager();
            Response response = restManager.restInterface.checkSession(new CheckSession(this.session));

            if (response.getStatus() == 200)
            {
                try
                {
                    // success
                    String responseBody = inputStreamToString(response.getBody().in());
                    JSONObject jsonObject = new JSONObject(responseBody);

                    String username = jsonObject.getString("username");
                    return username;
                } catch (Exception e)
                {
                    Log.e("CheckSession", "e", e);
                    return null;
                }
            } else
            {
                // failure
                return null;
            }
        }

        @Override
        protected void onPostExecute(String username)
        {
            if (username != null)
            {
                SharedPreferences sharedPreferences = getSharedPreferences("lastNetwork", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username", username);
                editor.commit();

                // go to main activity
                Intent intent = new Intent(connectActivity, MainActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                // invalid session
                switchToLoginForm();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
            super.onProgressUpdate(values);
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        this.refreshSSIDLabel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        ButterKnife.inject(this);

        setSupportActionBar(mainToolbar);

//        this.connectButton.setTextColor(Color.parseColor("#000000"));
        this.connectButton.setShadowEnabled(false);
        this.connectButton.setCornerRadius(7);
        this.connectButton.setTextColor(getResources().getColor(R.color.text_color_dark));
        this.connectButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String ip = serverIp.getText().toString().toLowerCase().trim();
                String inputUsername = username.getText().toString().toLowerCase().trim();
                String inputPassword = password.getText().toString().toLowerCase().trim();

                AttemptLoginTask attemptLoginTask = new AttemptLoginTask(ip, inputUsername, inputPassword);
                attemptLoginTask.execute();
            }
        });

        this.changeHotspot.setShadowEnabled(false);
        this.changeHotspot.setCornerRadius(7);
        this.changeHotspot.setTextColor(getResources().getColor(R.color.text_color_dark));
        this.changeHotspot.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });

        this.newUserButton.setShadowEnabled(false);
        this.newUserButton.setCornerRadius(7);
        this.newUserButton.setTextColor(getResources().getColor(R.color.text_color_dark));
        this.newUserButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(connectActivity, NewUserActivity.class);
                startActivity(intent);
            }
        });

        this.refreshSSIDLabel();

        SharedPreferences storedNetworkSettings = getSharedPreferences("lastNetwork", 0);
        String storedNetworkIP = storedNetworkSettings.getString("ip", "none");

        if (!storedNetworkIP.equals("none"))
        {
            // found stored network settings
            String storedUsername = storedNetworkSettings.getString("username", "none");
            int storedSession = storedNetworkSettings.getInt("session", 0);

            new CheckSessionTask(storedSession).execute();
        }
        else
        {
            // no network info stored
            switchToLoginForm();
        }
    }

    private void switchToLoginForm()
    {
        this.spinner.animate().alpha(0f).setDuration(500);

        this.serverIp.setVisibility(View.VISIBLE);
        this.serverIp.setAlpha(0f);
        this.serverIp.animate().alpha(1f).setDuration(750);

        this.username.setVisibility(View.VISIBLE);
        this.username.setAlpha(0f);
        this.username.animate().alpha(1f).setDuration(750);

        this.password.setVisibility(View.VISIBLE);
        this.password.setAlpha(0f);
        this.password.animate().alpha(1f).setDuration(750);

        this.connectButton.setVisibility(View.VISIBLE);
        this.connectButton.setAlpha(0f);
        this.connectButton.animate().alpha(1f).setDuration(750);

        this.newUserButton.setVisibility(View.VISIBLE);
        this.newUserButton.setAlpha(0f);
        this.newUserButton.animate().alpha(1f).setDuration(750);
    }

    private void refreshSSIDLabel()
    {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String hotspotSSID = wifiInfo.getSSID();
        Log.i("WifiInfo", wifiInfo.toString());

        if (hotspotSSID.contains("unknown ssid")) hotspotSSID = "Not connected.";
        this.currentHotspotName.setText(hotspotSSID);
    }

    public static String inputStreamToString(InputStream inputStream)
    {
        try
        {
            InputStreamReader is = new InputStreamReader(inputStream);
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(is);
            String read = br.readLine();

            while (read != null)
            {
                //System.out.println(read);
                sb.append(read);
                read = br.readLine();
            }

            return sb.toString();
        } catch (Exception e)
        {
            Log.e("ISToString", "e", e);
            return null;
        }
    }
}
