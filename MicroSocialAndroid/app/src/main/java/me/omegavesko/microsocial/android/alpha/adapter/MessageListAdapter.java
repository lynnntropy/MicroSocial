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

/**
 * Created by Veselin on 1/13/2015.
 */
public class MessageListAdapter extends ArrayAdapter<ChatMessage>
{
    public MessageListAdapter(Context context, List<ChatMessage> objects)
    {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final String userName = new AuthTokenManager(getContext()).getClientToken().username;
        final ChatMessage chatMessage = getItem(position);

        // don't reuse views because it causes issues with right/left alignment
        convertView =
                LayoutInflater.from(getContext()).inflate(
                        chatMessage.sender.equals(userName) ? R.layout.message_right : R.layout.message_left, parent, false);

        TextView usernameLabel = (TextView) convertView.findViewById(R.id.username);
        TextView messageText = (TextView) convertView.findViewById(R.id.messageText);

        usernameLabel.setText(chatMessage.sender);
        messageText.setText(chatMessage.messageText);

        return convertView;
    }


}
