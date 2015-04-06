package me.omegavesko.microsocial.android.alpha.fragment;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import me.omegavesko.microsocial.android.alpha.AuthTokenManager;
import me.omegavesko.microsocial.android.alpha.adapter.ChatListAdapter;
import me.omegavesko.microsocial.android.alpha.ChatMessage;
import me.omegavesko.microsocial.android.alpha.ObjectSocket;
import me.omegavesko.microsocial.android.alpha.R;
import me.omegavesko.microsocial.android.alpha.RequestCode;
import me.omegavesko.microsocial.android.alpha.ServerConnector;
import me.omegavesko.microsocial.android.alpha.network.RESTManager;
import me.omegavesko.microsocial.android.alpha.schema.Message;

public class MessagesFragment extends Fragment
{
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
            }
            catch (Exception e)
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

    ListView messageList;
    List<Message> messagesToDisplay = new ArrayList<Message>();
    ChatListAdapter adapter;

    GetNewestMessagesTask getNewestMessagesTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_messages, container, false);

        this.messageList = (ListView) rootView.findViewById(R.id.messageList);
//        this.adapter = new ChatListAdapter(getActivity(), this.messagesToDisplay, true);
//        this.messageList.setAdapter(adapter);

        this.getNewestMessagesTask = new GetNewestMessagesTask(0, 20);
        this.getNewestMessagesTask.execute();

        return rootView;
    }

    private void writeLog(String log)
    {
        Log.i("GetNewestMessages", log);
    }
}

