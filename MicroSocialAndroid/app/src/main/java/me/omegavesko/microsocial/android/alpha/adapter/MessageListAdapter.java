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
import me.omegavesko.microsocial.android.alpha.R;
import me.omegavesko.microsocial.android.alpha.schema.Message;

public class MessageListAdapter extends ArrayAdapter<Message>
{
    public MessageListAdapter(Context context, List<Message> objects)
    {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final String userName = new AuthTokenManager(getContext()).getClientToken().username;
        final Message chatMessage = getItem(this.getCount() - 1 - position); // reverse list order

        // don't reuse views because it causes issues with right/left alignment
        convertView =
                LayoutInflater.from(getContext()).inflate(
                        chatMessage.senderName.equals(userName) ? R.layout.message_right : R.layout.message_left, parent, false);

        TextView usernameLabel = (TextView) convertView.findViewById(R.id.username);
        TextView messageText = (TextView) convertView.findViewById(R.id.messageText);

        usernameLabel.setText(chatMessage.senderName);
        messageText.setText(chatMessage.messageBody);

        return convertView;
    }


}
