package me.omegavesko.microsocial.android.alpha.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import info.hoang8f.widget.FButton;
import me.omegavesko.microsocial.android.alpha.R;
import me.omegavesko.microsocial.android.alpha.network.RESTManager;
import me.omegavesko.microsocial.android.alpha.schema.LoginAttempt;
import me.omegavesko.microsocial.android.alpha.schema.RegisterAttempt;
import retrofit.client.Response;

public class NewUserActivity extends ActionBarActivity
{
    Activity newUserActivity = this;

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
    @InjectView(R.id.serverIp)
    MaterialEditText serverIp;
    @InjectView(R.id.fullName)
    MaterialEditText fullName;
    @InjectView(R.id.email)
    MaterialEditText email;
    @InjectView(R.id.username)
    MaterialEditText username;
    @InjectView(R.id.password)
    MaterialEditText password;
    @InjectView(R.id.connectButton)
    FButton connectButton;

    class RegisterAndConnectTask extends AsyncTask<Void, Void, Integer>
    {
        String fullName, email, serverIP, username, password;

        RegisterAndConnectTask(String fullName, String email, String serverIP, String username, String password)
        {
            this.fullName = fullName;
            this.email = email;
            this.serverIP = serverIP;
            this.username = username;
            this.password = password;
        }

        @Override
        protected Integer doInBackground(Void... params)
        {
            RESTManager restManager = RESTManager.getManager(NewUserActivity.this);
            Response response = restManager.restInterface.registerUser(new RegisterAttempt(username, password, fullName, email));

            if (response.getStatus() == 200)
            {
                // success
                Response loginResponse = restManager.restInterface.login(new LoginAttempt(username, password));

                if (loginResponse.getStatus() == 200)
                {
                    // success
                    try
                    {
                        String responseBody = ConnectActivity.inputStreamToString(loginResponse.getBody().in());
                        Log.i("LoginResponse", "Response body: " + responseBody);

                        JSONObject jsonObject = new JSONObject(responseBody);

                        int session = Integer.parseInt(jsonObject.getString("session_id"));
                        return session;
                    }
                    catch (Exception e)
                    {
                        Log.e("Login", "e", e);
                        return null;
                    }
                }
                else
                {
                    // failure
                    return null;
                }
            }
            else
            {
                // failure
                return null;
            }
        }

        @Override
        protected void onPostExecute(Integer integer)
        {
            if (integer != null)
            {
                // login success, open the main activity
                SharedPreferences storedNetworkSettings = getSharedPreferences("lastNetwork", 0);
                SharedPreferences.Editor editor = storedNetworkSettings.edit();
                editor.putString("username", username);
                editor.putInt("session", integer);
                editor.commit();

                Intent intent = new Intent(newUserActivity, MainActivity.class);
                startActivity(intent);
            }
            else
            {
                // failure
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        ButterKnife.inject(this);

        setSupportActionBar(mainToolbar);

        styleFlatButton(changeHotspot);
        styleFlatButton(connectButton);

        connectButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                RegisterAndConnectTask registerAndConnectTask = new RegisterAndConnectTask(
                        fullName.getText().toString(),
                        email.getText().toString(),
                        serverIp.getText().toString(),
                        username.getText().toString(),
                        password.getText().toString());

                registerAndConnectTask.execute();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_new_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void styleFlatButton(FButton fButton)
    {
        fButton.setShadowEnabled(false);
        fButton.setCornerRadius(7);
        fButton.setTextColor(getResources().getColor(R.color.text_color_dark));
    }
}
