package me.omegavesko.microsocial.android.alpha.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pnikosis.materialishprogress.ProgressWheel;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketHandler;
import me.omegavesko.microsocial.android.alpha.AuthTokenManager;
import me.omegavesko.microsocial.android.alpha.adapter.MessageListAdapter;
import me.omegavesko.microsocial.android.alpha.ObjectSocket;
import me.omegavesko.microsocial.android.alpha.R;
import me.omegavesko.microsocial.android.alpha.RequestCode;
import me.omegavesko.microsocial.android.alpha.ResponseCode;
import me.omegavesko.microsocial.android.alpha.ServerConnector;
import me.omegavesko.microsocial.android.alpha.network.RESTManager;
import me.omegavesko.microsocial.android.alpha.schema.Message;
import me.omegavesko.microsocial.android.alpha.schema.OutboundMessage;
import retrofit.client.Response;


public class MessageActivity extends ActionBarActivity
{
    private Activity messageActivity = this;

    private class GetMessagesTask extends AsyncTask<Void, Void, List<Message>>
    {
        private String username;
        private int first;
        private int last;

        private GetMessagesTask(String username, int first, int last)
        {
            this.username = username;
            this.first = first;
            this.last = last;
        }

        @Override
        protected List<Message> doInBackground(Void... params)
        {
            List<Message> messages = new ArrayList<Message>();

            try
            {
                SharedPreferences storedNetworkSettings = messageActivity.getSharedPreferences("lastNetwork", 0);
                String userUsername = storedNetworkSettings.getString("username", "none");
                String session = new Integer(storedNetworkSettings.getInt("session", 0)).toString();

                RESTManager restManager = RESTManager.getManager();
                messages = restManager.restInterface.getMessages(session, username, first, last).messages;

                return messages;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return messages;
            }
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(List<Message> messages)
        {
            MessageActivity.this.chatMessages.addAll(0, messages);
            messageListAdapter.notifyDataSetChanged();

            // fade the wheel out
            if (progressWheel.getAlpha() > 0)
                progressWheel.animate().alpha(0f).setDuration(500).setListener(null);

            // fade in the ListView
            if (messageListView.getAlpha() == 0)
            {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            messageListView.animate().alpha(1f).setDuration(500).setListener(null);
                                        }
                                    },
                        500);
            }
        }
    }

    private class SendMessageTask extends AsyncTask <Void, Void, Integer>
    {
        private Message sentMessage;

        private SendMessageTask(Message sentMessage)
        {
            this.sentMessage = sentMessage;
        }

        @Override
        protected Integer doInBackground(Void... params)
        {
            try
            {
//                SharedPreferences settings = getSharedPreferences("currentNetworkInfo", 0);
//                String serverLocation = settings.getString("networkLocation", "none");
                SharedPreferences storedNetworkSettings = messageActivity.getSharedPreferences("lastNetwork", 0);
                String userUsername = storedNetworkSettings.getString("username", "none");
                String session = new Integer(storedNetworkSettings.getInt("session", 0)).toString();

                OutboundMessage outboundMessage = new OutboundMessage(session, sentMessage.recipientName, sentMessage.messageBody);

                RESTManager restManager = RESTManager.getManager();
                Response response = restManager.restInterface.sendMessage(outboundMessage);

                return response.getStatus();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return 400;
            }
        }

        @Override
        protected void onPostExecute(Integer code)
        {
            if (code == 200)
            {
                // add it to the list
                chatMessages.add(0, sentMessage);
                messageListAdapter.notifyDataSetChanged();
            }
        }
    }

    private GetMessagesTask getMessagesTask;
    private String otherUserName;

    private MessageListAdapter messageListAdapter;
    private List<Message> chatMessages;

    private Toolbar toolbar;
    private ListView messageListView;
    private ProgressWheel progressWheel;
    private EditText messageTextField;
    private Button sendButton;

    private WebSocketConnection webSocketConnection = new WebSocketConnection();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        this.toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        this.messageListView = (ListView) findViewById(R.id.listView);
        this.progressWheel = (ProgressWheel) findViewById(R.id.spinner);

        this.messageTextField = (EditText) findViewById(R.id.messageTextField);
        this.messageTextField.setImeOptions(EditorInfo.IME_ACTION_SEND);

        this.sendButton = (Button) findViewById(R.id.sendButton);


        this.sendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendMessage();
            }
        });

        this.messageTextField.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                sendMessage();

                return true;
            }
        });

        Bundle bundle = getIntent().getExtras();
        String userName = null;
        String fullName = null;
        if (bundle != null)
        {
            userName = (String) bundle.get("USERNAME");
            fullName = (String) bundle.get("FULLNAME");
        }

        this.otherUserName = userName;
        setTitle(String.format("%s (%s)", fullName, userName));

        this.chatMessages = new ArrayList<Message>();
        this.messageListAdapter = new MessageListAdapter(MessageActivity.this, MessageActivity.this.chatMessages);
        this.messageListView.setAdapter(messageListAdapter);

        this.getMessagesTask = new GetMessagesTask(otherUserName, 0, 30);
        this.getMessagesTask.execute();

        this.connectWebSocket();
    }

    void sendMessage()
    {
        String username = getSharedPreferences("lastNetwork", 0).getString("username", "none");
        String message = this.messageTextField.getText().toString().trim();
        this.messageTextField.setText("");

        if (!message.trim().equals("")) // make sure the message isn't empty or whitespace
        {
            new SendMessageTask(
                    new Message(
                    message,
                    "",
                    username,
                    otherUserName))
                    .execute();
        }

    }

    void connectWebSocket()
    {
        final String uri = "ws://" + getSharedPreferences("lastNetwork", 0).getString("ip", "localhost") + ":9001/chat";

        try
        {
            final String TAG = "WebSocket";

            this.webSocketConnection.connect(uri, new WebSocketHandler()
            {
                boolean authenticated = false;

                @Override
                public void onOpen()
                {
                    Log.d(TAG, "Status: Connected to " + uri);

                    webSocketConnection.sendTextMessage(otherUserName);

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                webSocketConnection.sendTextMessage(
                                        Integer.toString(getSharedPreferences("lastNetwork", 0).getInt("session", 0)));
                            }
                        },
                    1000);

                }

                @Override
                public void onTextMessage(String payload)
                {
                    Log.d(TAG, "MESSAGE: " + payload);

                    if (!authenticated)
                    {
                        if (payload.startsWith("Authenticated"))
                            this.authenticated = true;
                    }
                    else
                    {
                        // display the message etc.
                        chatMessages.add(0,
                                new Message(
                                        payload,
                                        "",
                                        otherUserName,
                                        getSharedPreferences("lastNetwork", 0).getString("username", "none")));

                        messageListAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onClose(int code, String reason)
                {
                    Log.d(TAG, "Connection lost.");
                }
            });
        }
        catch (Exception e)
        {
            Log.e("WebSocket", "e", e);
        }
    }

    @Override
    public void onBackPressed()
    {
        this.webSocketConnection.disconnect();
        this.webSocketConnection = null;
        finish();
    }
}