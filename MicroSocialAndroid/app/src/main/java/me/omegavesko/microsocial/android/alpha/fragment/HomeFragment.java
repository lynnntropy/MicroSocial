package me.omegavesko.microsocial.android.alpha.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import info.hoang8f.widget.FButton;
import me.omegavesko.microsocial.android.alpha.R;
import me.omegavesko.microsocial.android.alpha.RegisteredUser;
import me.omegavesko.microsocial.android.alpha.adapter.UserListAdapter;
import me.omegavesko.microsocial.android.alpha.UsersDataSource;
import me.omegavesko.microsocial.android.alpha.activity.ConnectActivity;

public class HomeFragment extends Fragment
{
    FButton disconnectButton;
    ListView userListview;

    TextView networkName;
    TextView networkLocation;

//    private class CheckServerTask extends AsyncTask<Void, Void, Void>
//    {
//        @Override
//        protected Void doInBackground(Void... params)
//        {
//            SharedPreferences settings = getActivity().getSharedPreferences("currentNetworkInfo", 0);
//            String serverLocation = settings.getString("networkLocation", "none");
//
//            if (!serverLocation.equals("") && !serverLocation.equals("none"))
//            {
//                ObjectSocket objectSocket = ServerConnector.connect(serverLocation, 9000, new RequestCode(RequestCode.Code.GET_NETWORK_INFO, new AuthToken()));
//
//                // Implemented a serverside request handler that returns the name of the network.
//
//                if (objectSocket.rawSocket == null)
//                {
//                    // no server found at location, go to network finder
//                    SharedPreferences.Editor editor = settings.edit();
//                    editor.putString("networkName", "");
//                    editor.putString("networkLocation", "");
//                    editor.commit();
//
//                    Intent connectActivityIntent = new Intent(getActivity(), ConnectActivity.class);
//                    getActivity().startActivity(connectActivityIntent);
//                }
//            }
//            else
//            {
//                // no server stored, go to network finder
//                SharedPreferences.Editor editor = settings.edit();
//                editor.putString("networkName", "");
//                editor.putString("networkLocation", "");
//                editor.commit();
//
//                Intent connectActivityIntent = new Intent(getActivity(), ConnectActivity.class);
//                getActivity().startActivity(connectActivityIntent);
//            }
//
//            return null;
//        }
//    }

    private class RefreshUsersTask extends AsyncTask<Void, Void, List<RegisteredUser>>
    {
        protected List<RegisteredUser> doInBackground(Void... input)
        {
//            Toast.makeText(getActivity().getApplicationContext(), "Refreshing users from server.. ", Toast.LENGTH_SHORT).show();

            UsersDataSource usersDataSource = new UsersDataSource(getActivity().getApplicationContext());
            return usersDataSource.refreshUsersFromServer();
        }

        protected void onPostExecute(List<RegisteredUser> result)
        {
            UserListAdapter adapter = new UserListAdapter(getActivity().getApplicationContext(), result, false);
            userListview.setAdapter(adapter);

            Toast.makeText(getActivity().getApplicationContext(), "Users refreshed!", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_home, container, false);

//        new CheckServerTask().execute();

        final SharedPreferences settings = getActivity().getSharedPreferences("currentNetworkInfo", 0);
        this.networkName = (TextView) rootView.findViewById(R.id.networkName);
        this.networkLocation = (TextView) rootView.findViewById(R.id.networkLocation);
        this.networkName.setText(settings.getString("networkName", "Unknown network"));
        this.networkLocation.setText(settings.getString("networkLocation", "Unknown location"));

        this.disconnectButton = (FButton) rootView.findViewById(R.id.disconnectButton);
        this.disconnectButton.setShadowEnabled(false);
        this.disconnectButton.setCornerRadius(9);
        this.disconnectButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("networkName", "");
                editor.putString("networkLocation", "");
                editor.commit();

                Intent connectActivityIntent = new Intent(getActivity(), ConnectActivity.class);
                getActivity().startActivity(connectActivityIntent);
            }
        });

        this.userListview = (ListView) rootView.findViewById(R.id.userListView);
        List<RegisteredUser> usersFromDatabase = new UsersDataSource(getActivity()).getAllUsers();
        this.userListview.setAdapter(new UserListAdapter(getActivity(), usersFromDatabase, false));

        Toast.makeText(getActivity().getApplicationContext(), "Refreshing users from server.. ", Toast.LENGTH_SHORT).show();
        new RefreshUsersTask().execute();

        return rootView;
    }


}
