package me.omegavesko.microsocial.android.alpha.activity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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

import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.List;

import me.omegavesko.microsocial.android.alpha.AuthTokenManager;
import me.omegavesko.microsocial.android.alpha.ChatMessage;
import me.omegavesko.microsocial.android.alpha.adapter.MessageListAdapter;
import me.omegavesko.microsocial.android.alpha.ObjectSocket;
import me.omegavesko.microsocial.android.alpha.R;
import me.omegavesko.microsocial.android.alpha.RequestCode;
import me.omegavesko.microsocial.android.alpha.ResponseCode;
import me.omegavesko.microsocial.android.alpha.ServerConnector;


public class MessageActivity extends ActionBarActivity
{
    private class GetMessagesTask extends AsyncTask<int[], Void, List<ChatMessage>>
    {
        private String userName;

        private GetMessagesTask(String userName)
        {
            this.userName = userName;
        }

        @Override
        protected List<ChatMessage> doInBackground(int[]... params)
        {
            try
            {
                SharedPreferences settings = getSharedPreferences("currentNetworkInfo", 0);
                String serverLocation = settings.getString("networkLocation", "none");

                int startMessage = params[0][0];
                int endMessage = params[0][1];

                ObjectSocket objectSocket = ServerConnector.connect
                        (serverLocation, 9000, new RequestCode(
                                RequestCode.Code.GET_MESSAGES_FROM_USER,
                                new AuthTokenManager(MessageActivity.this).getClientToken(),
                                new String[]{userName, Integer.toString(startMessage), Integer.toString(endMessage)}));

                List<ChatMessage> returnedMessages = (List<ChatMessage>) objectSocket.inputStream.readObject();

                return returnedMessages;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(List<ChatMessage> chatMessages)
        {
            super.onPostExecute(chatMessages);

            MessageActivity.this.chatMessages = chatMessages;
            messageListAdapter = new MessageListAdapter(MessageActivity.this, MessageActivity.this.chatMessages);

            messageListView.setAdapter(messageListAdapter);

            // fade the wheel out
            progressWheel.animate().alpha(0f).setDuration(500).setListener(null);

            // fade in the ListView
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

    private class SendMessageTask extends AsyncTask <ChatMessage, Void, ResponseCode>
    {
        private ChatMessage sentMessage;

        @Override
        protected ResponseCode doInBackground(ChatMessage... params)
        {
            try
            {
                SharedPreferences settings = getSharedPreferences("currentNetworkInfo", 0);
                String serverLocation = settings.getString("networkLocation", "none");

                ObjectSocket objectSocket = ServerConnector.connect
                        (serverLocation, 9000, new RequestCode(
                                RequestCode.Code.SEND_MESSAGE,
                                new AuthTokenManager(MessageActivity.this).getClientToken()));

                objectSocket.outputStream.writeObject(params[0]);

                ResponseCode response = (ResponseCode) objectSocket.inputStream.readObject();
                Log.i("SendMessage", "Response: " + response.toString());

                this.sentMessage = params[0];

                return response;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return new ResponseCode(ResponseCode.Code.FAILURE);
        }

        @Override
        protected void onPostExecute(ResponseCode response)
        {
            super.onPostExecute(response);

            if (response.code.equals(ResponseCode.Code.SUCCESS))
            {
                // add it to the list
                chatMessages.add(sentMessage);
                messageListAdapter.notifyDataSetChanged();
            }
        }
    }

    private GetMessagesTask getMessagesTask;
    private String otherUserName;

    private MessageListAdapter messageListAdapter;
    private List<ChatMessage> chatMessages;

    private Toolbar toolbar;
    private ListView messageListView;
    private ProgressWheel progressWheel;
    private EditText messageTextField;
    private Button sendButton;

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

        if (bundle != null)
        {
            userName = (String) bundle.get("USERNAME");
        }

        this.otherUserName = userName;
        setTitle(otherUserName);

        this.getMessagesTask = new GetMessagesTask(this.otherUserName);
        this.getMessagesTask.execute(new int[] {1, 20});
    }

    void sendMessage()
    {
        String message = this.messageTextField.getText().toString().trim();
        this.messageTextField.setText("");

        // make sure the message isn't empty or whitespace
        if (!message.trim().equals(""))
        {
            new SendMessageTask().execute(
                    new ChatMessage(
                            new AuthTokenManager(this).getClientToken().username,
                            otherUserName,
                            message,
                            ChatMessage.MessageType.TEXT_MESSAGE));
        }

    }

}