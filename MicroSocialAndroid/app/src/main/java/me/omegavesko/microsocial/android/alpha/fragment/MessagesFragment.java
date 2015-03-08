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

public class MessagesFragment extends Fragment
{
    private class GetNewestMessagesTask extends AsyncTask<Void, Void, List<ChatMessage>>
    {
        @Override
        protected List<ChatMessage> doInBackground(Void... params)
        {
            List<ChatMessage> messages = new ArrayList<ChatMessage>();

            try
            {
                SharedPreferences settings = getActivity().getSharedPreferences("currentNetworkInfo", 0);
                String serverLocation = settings.getString("networkLocation", "none");

                ObjectSocket objectSocket =
                        ServerConnector.connect(serverLocation, 9000,
                                new RequestCode(RequestCode.Code.GET_LATEST_MESSAGES,
                                        new AuthTokenManager(getActivity()).getClientToken()));

                if (objectSocket.rawSocket != null)
                {
                    // got a valid ObjectSocket back

                    // receive the messages from the server
                    messages = (List<ChatMessage>) objectSocket.inputStream.readObject();
                    writeLog(String.format("Received %s messages from the server.", messages.size()));
                    return messages;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return messages;
            }

            return messages;
        }

        @Override
        protected void onPostExecute(List<ChatMessage> chatMessages)
        {
            // set our ArrayList to the one we got from the server and
            // notify the adapter that the data set has changed

            messagesToDisplay = chatMessages;
            adapter = new ChatListAdapter(getActivity(), messagesToDisplay, false);
            adapter.notifyDataSetChanged();
            messageList.setAdapter(adapter);
        }
    }

    ListView messageList;
    List<ChatMessage> messagesToDisplay = new ArrayList<ChatMessage>();
    ChatListAdapter adapter;

    GetNewestMessagesTask getNewestMessagesTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_messages, container, false);

        this.messageList = (ListView) rootView.findViewById(R.id.messageList);
        this.adapter = new ChatListAdapter(getActivity(), this.messagesToDisplay, true);
        this.messageList.setAdapter(adapter);

        this.getNewestMessagesTask = new GetNewestMessagesTask();
        this.getNewestMessagesTask.execute();

        return rootView;
    }

    private void writeLog(String log)
    {
        Log.i("GetNewestMessages", log);
    }
}

