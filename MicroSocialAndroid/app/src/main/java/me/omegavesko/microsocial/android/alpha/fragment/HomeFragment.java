package me.omegavesko.microsocial.android.alpha.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import info.hoang8f.widget.FButton;
import me.omegavesko.microsocial.android.alpha.R;
import me.omegavesko.microsocial.android.alpha.activity.ConnectActivity;
import me.omegavesko.microsocial.android.alpha.adapter.UserListAdapter;
import me.omegavesko.microsocial.android.alpha.network.RESTManager;
import me.omegavesko.microsocial.android.alpha.schema.User;
import me.omegavesko.microsocial.android.alpha.schema.UserList;
import retrofit.client.Response;

public class HomeFragment extends Fragment
{
    @InjectView(R.id.cardHeader)
    TextView cardHeader;
    @InjectView(R.id.networkName)
    TextView networkName;
    @InjectView(R.id.disconnectButton)
    FButton disconnectButton;
    @InjectView(R.id.networkLocation)
    TextView networkLocation;
    @InjectView(R.id.networkCard)
    CardView networkCard;
    @InjectView(R.id.fullName)
    TextView fullName;
    @InjectView(R.id.userListView)
    ListView userListView;

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    private class RefreshUsersTask extends AsyncTask<Void, Void, List<User>>
    {
        protected List<User> doInBackground(Void... input)
        {
            RESTManager restManager = RESTManager.getManager();
            UserList userList = restManager.restInterface.listUsers();

            return userList.users;
        }

        protected void onPostExecute(List<User> result)
        {
            UserListAdapter adapter = new UserListAdapter(getActivity().getApplicationContext(), result, false);
            userListView.setAdapter(adapter);

            // dev
            Toast.makeText(getActivity().getApplicationContext(), "Users refreshed!", Toast.LENGTH_SHORT).show();
        }
    }

    class GetNetworkInfoTask extends AsyncTask<Void, Void, Void>
    {
        String serverName;

        @Override
        protected Void doInBackground(Void... params)
        {
            RESTManager restManager = RESTManager.getManager();
            Response response = restManager.restInterface.serverInfo();

            try
            {
                String responseBody = ConnectActivity.inputStreamToString(response.getBody().in());
                JSONObject jsonObject = new JSONObject(responseBody);

                this.serverName = jsonObject.getString("serverName");
            }
            catch (Exception e)
            {
                Log.e("NetworkInfo", "e", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            networkName.setText(this.serverName);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_home, container, false);

        ButterKnife.inject(this, rootView);

//        new CheckServerTask().execute();

        final SharedPreferences settings = getActivity().getSharedPreferences("lastNetwork", 0);
        this.networkName.setText("---");
        this.networkLocation.setText(settings.getString("ip", "---"));

        this.disconnectButton.setShadowEnabled(false);
        this.disconnectButton.setCornerRadius(9);
        this.disconnectButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SharedPreferences.Editor editor = settings.edit();
                editor.remove("username");
                editor.remove("session");
                editor.commit();

                Intent connectActivityIntent = new Intent(getActivity(), ConnectActivity.class);
                getActivity().startActivity(connectActivityIntent);
            }
        });

        Toast.makeText(getActivity().getApplicationContext(), "Refreshing users from server.. ", Toast.LENGTH_SHORT).show();
        new GetNetworkInfoTask().execute();
        new RefreshUsersTask().execute();

        return rootView;
    }
}
