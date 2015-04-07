package me.omegavesko.microsocial.android.alpha.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.tkeunebr.gravatar.Gravatar;
import me.omegavesko.microsocial.android.alpha.AuthTokenManager;
import me.omegavesko.microsocial.android.alpha.R;
import me.omegavesko.microsocial.android.alpha.schema.Message;

public class MessageListAdapter extends ArrayAdapter<Message>
{
    View loadMoreView;

    public MessageListAdapter(Context context, List<Message> objects, View loadMoreView)
    {
        super(context, 0, objects);
        this.loadMoreView = loadMoreView;
    }

    @Override
    public int getCount()
    {
//        return super.getCount();
        return super.getCount() + 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (position == 0)
        {
            // return load more view
//            View loadMoreView =
//                    LayoutInflater.from(getContext()).inflate(
//                            R.layout.chat_loadmore, parent, false);
            return this.loadMoreView;
        }
        else
        {
            final String userName = new AuthTokenManager(getContext()).getClientToken().username;
            final Message chatMessage = getItem(super.getCount() - 1 - (position - 1)); // reverse list order

            Message previousMessage;
            if (position > 1) previousMessage = getItem(super.getCount() - 1 - ((position - 1) - 1));
            else previousMessage = new Message("", "", "", "", "");

            // don't reuse views because it causes issues with right/left alignment
            convertView =
                    LayoutInflater.from(getContext()).inflate(
                            chatMessage.senderName.equals(userName) ? R.layout.message_right : R.layout.message_left, parent, false);

            TextView messageText = (TextView) convertView.findViewById(R.id.messageText);
            CircleImageView profileImage = (CircleImageView) convertView.findViewById(R.id.profile_image);

            if (!chatMessage.senderName.equals(previousMessage.senderName))
            {
                String gravatarUrl = "anonymous@gmail.com";
                if (chatMessage.senderEmail != null)
                     gravatarUrl = Gravatar.init().with(chatMessage.senderEmail).size(50).build();

                Picasso.with(getContext())
                        .load(gravatarUrl)
                        .noFade()
                        .into(profileImage);
            }
            else
            {
                profileImage.setVisibility(View.INVISIBLE);
            }

            messageText.setText(chatMessage.messageBody);

            return convertView;
        }
    }


}
