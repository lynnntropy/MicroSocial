package me.omegavesko.microsocial.android.alpha.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.omegavesko.microsocial.android.alpha.R;
import me.omegavesko.microsocial.android.alpha.activity.MessageActivity;
import me.omegavesko.microsocial.android.alpha.adapter.ChatListAdapter;
import me.omegavesko.microsocial.android.alpha.network.RESTManager;
import me.omegavesko.microsocial.android.alpha.schema.Message;
import me.omegavesko.microsocial.android.alpha.schema.User;
import me.omegavesko.microsocial.android.alpha.schema.UserList;

public class MessagesFragment extends Fragment
{
    @InjectView(R.id.messageList)
    ListView messageList;
    @InjectView(R.id.fab)
    FloatingActionButton fab;

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    private class GetNewestMessagesTask extends AsyncTask<Void, Void, List<Message>>
    {
        int first, last;

        private GetNewestMessagesTask(int first, int last)
        {
            this.first = first;
            this.last = last;
        }

        @Override
        protected List<Message> doInBackground(Void... params)
        {
            List<Message> messages = new ArrayList<Message>();

            try
            {
                SharedPreferences storedNetworkSettings = getActivity().getSharedPreferences("lastNetwork", 0);
                String username = storedNetworkSettings.getString("username", "none");
                String session = new Integer(storedNetworkSettings.getInt("session", 0)).toString();

                RESTManager restManager = RESTManager.getManager();
                messages = restManager.restInterface.getNewestMessages(session, this.first, this.last).messages;

                Log.i("GetNewestMessagesTask", messages.toString());

                return messages;
            } catch (Exception e)
            {
                e.printStackTrace();
                return messages;
            }
        }

        @Override
        protected void onPostExecute(List<Message> messages)
        {
            // set our ArrayList to the one we got from the server and
            // notify the adapter that the data set has changed

            messagesToDisplay = messages;
            adapter = new ChatListAdapter(getActivity(), messagesToDisplay, false);
            adapter.notifyDataSetChanged();
            messageList.setAdapter(adapter);
        }
    }

    class OpenDialogTask extends AsyncTask<Void, Void, List<String>>
    {
        List<User> users;

        @Override
        protected List<String> doInBackground(Void... params)
        {
            List<String> names = new ArrayList<String>();

            RESTManager restManager = RESTManager.getManager();
            UserList userList = restManager.restInterface.listUsers();

            this.users = userList.users;

            for (User user : userList.users)
            {
                names.add(user.fullName);
            }

            return names;
        }

        @Override
        protected void onPostExecute(List<String> strings)
        {
            String[] namesArray;
            namesArray = strings.toArray(new String[strings.size()]);

            MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                    .title("New Conversation")
                    .theme(Theme.LIGHT)
                    .titleColor(Color.BLACK)
                    .items(namesArray)
                    .itemsCallback(new MaterialDialog.ListCallback()
                    {
                        @Override
                        public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence)
                        {
                            User selectedUser = users.get(i);
                            Log.i("NewConvo", selectedUser.toString());

                            Intent intent = new Intent(getActivity(), MessageActivity.class);

                            // Pack the user's information into the intent so we have something to show
                            intent.putExtra("USERNAME", selectedUser.username);
                            intent.putExtra("FULLNAME", selectedUser.fullName);
                            intent.putExtra("EMAIL", selectedUser.email);

                            // start the activity
                            getActivity().startActivity(intent);
                        }
                    })
                    .show();
        }
    }

//    ListView messageList;
    List<Message> messagesToDisplay = new ArrayList<Message>();
    ChatListAdapter adapter;

    GetNewestMessagesTask getNewestMessagesTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_messages, container, false);

        ButterKnife.inject(this, rootView);

//        this.messageList = (ListView) rootView.findViewById(R.id.messageList);
//        this.adapter = new ChatListAdapter(getActivity(), this.messagesToDisplay, true);
//        this.messageList.setAdapter(adapter);

        this.getNewestMessagesTask = new GetNewestMessagesTask(0, 20);
        this.getNewestMessagesTask.execute();

        this.fab.attachToListView(messageList);
        this.fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openNewMessageDialog();
            }
        });

        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        new GetNewestMessagesTask(0, 20).execute();
    }

    private void writeLog(String log)
    {
        Log.i("GetNewestMessages", log);
    }

    void openNewMessageDialog()
    {
        new OpenDialogTask().execute();
    }
}

