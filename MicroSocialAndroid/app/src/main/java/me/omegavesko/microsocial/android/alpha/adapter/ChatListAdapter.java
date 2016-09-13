package me.omegavesko.microsocial.android.alpha.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.omegavesko.microsocial.android.alpha.AuthTokenManager;
import me.omegavesko.microsocial.android.alpha.ChatMessage;
import me.omegavesko.microsocial.android.alpha.R;
import me.omegavesko.microsocial.android.alpha.activity.MessageActivity;
import me.omegavesko.microsocial.android.alpha.schema.Message;

public class ChatListAdapter extends ArrayAdapter<Message>
{
    private boolean dummyData;
    private Context context;

    private List<Message> data;

    public ChatListAdapter(Context context, List<Message> objects, boolean dummyData)
    {
        super(context, R.layout.list_item_user, objects);
        this.dummyData = dummyData;
        this.context = context;

        if (!dummyData)
        {
            this.data = objects;
        }
        else
        {
            // full a list with dummy users

//            List<Message> dummyMessages = new ArrayList<Message>();
//
//            for (int i = 1; i <= 50; i++)
//                dummyMessages.add(new ChatMessage("Sender " + i, "Recipient", "Hello!", ChatMessage.MessageType.TEXT_MESSAGE));
//
//            this.data = dummyMessages;
        }
    }

    @Override
    public int getCount()
    {
        if (dummyData)
        {
            return 50;
        }
        else
        {
            return super.getCount();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
//        return super.getView(position, convertView, parent);
        final String userName = this.context.getSharedPreferences("lastNetwork", 0).getString("username", "none");

        View view = convertView;

        if (view == null)
        {
            // inflate a new view from a layout

            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.list_item_message, parent, false);
        }

        // populate the elements of the view normally
        // ...
        final Message message = this.data.get(position);

        TextView senderName = (TextView) view.findViewById(R.id.senderName);
        TextView messageText = (TextView) view.findViewById(R.id.messageText);

        if (message.recipientName.equals("all"))
        {
            // set name to "Everyone" and make it bold if it's the all channel
            senderName.setText("Everyone");
            senderName.setTypeface(null, Typeface.BOLD);
        }
//        else if (message.recipientName.equals(userName))
//        {
//            // set name to the sender if the message was sent to us
//            senderName.setText(message.senderName);
//        }
//        else
//        {
//            // otherwise, just set it to the recipient
//            senderName.setText(message.recipientName);
//        }
        else
        {
            senderName.setText(message.displayName);
        }

        messageText.setText('"' + message.messageBody + '"');

        final String userEmail = getContext().getSharedPreferences("lastNetwork", 0).getString("email", "none");

        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openMessageActivity(
                        message.senderName.equals(userName) ? message.recipientName : message.senderName,
                        message.displayName,
                        message.senderEmail.equals(userEmail)? userEmail : message.senderEmail);
            }
        });

        return view;
    }

    void openMessageActivity(String username, String fullname, String email)
    {
        Intent intent = new Intent(getContext(), MessageActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Pack the user's information into the intent so we have something to show
        intent.putExtra("USERNAME", username);
        intent.putExtra("FULLNAME", fullname);
        intent.putExtra("EMAIL", email);

        // start the activity
        getContext().startActivity(intent);
    }
}
