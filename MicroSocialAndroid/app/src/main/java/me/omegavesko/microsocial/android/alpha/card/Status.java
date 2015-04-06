package me.omegavesko.microsocial.android.alpha.card;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import fr.tkeunebr.gravatar.Gravatar;
import it.gmariotti.cardslib.library.internal.Card;
import me.omegavesko.microsocial.android.alpha.R;
import me.omegavesko.microsocial.android.alpha.Utils;

public class Status extends Card
{
    TextView userFullName;
    TextView statusText;
    ImageView userAvatar;
    TextView statusTime;

    private me.omegavesko.microsocial.android.alpha.schema.Status status;

    public Status(Context context, me.omegavesko.microsocial.android.alpha.schema.Status status)
    {
        this(context, R.layout.card_status);
        this.status = status;
    }

    public Status(Context context, int innerLayout)
    {
        super(context, innerLayout);
        init();
    }

    private void init()
    {
        setOnClickListener(new OnCardClickListener()
        {
            @Override
            public void onClick(Card card, View view)
            {
//                Toast.makeText(getContext(), "Click Listener card=", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view)
    {
        this.userFullName = (TextView) parent.findViewById(R.id.userFullName);
        this.statusText = (TextView) parent.findViewById(R.id.statusText);
        this.userAvatar = (ImageView) parent.findViewById(R.id.userAvatar);
        this.statusTime = (TextView) parent.findViewById(R.id.statusTime);

        this.userFullName.setText(this.status.poster.fullName);
        this.statusText.setText(this.status.statusContent);
        this.statusTime.setText(Utils.getRelativeTimeString(Utils.jsonDateTimeToDate(this.status.time)));

        String gravatarUrl = Gravatar.init().with(this.status.poster.email).size(100).build();

        Picasso.with(getContext())
                .load(gravatarUrl)
                .into(userAvatar);
    }
}
