package me.omegavesko.microsocial.android.alpha.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.view.CardView;
import me.omegavesko.microsocial.android.alpha.R;
import me.omegavesko.microsocial.android.alpha.schema.Status;

public class StatusListAdapter extends ArrayAdapter<Status>
{
    private boolean dummyData;
    private Context context;

    private List<Status> data;

    public StatusListAdapter(Context context, List<Status> objects)
    {
        super(context, R.layout.list_item_status, objects);
        this.context = context;

        this.data = objects;
    }

    @Override
    public int getCount()
    {
//        if (dummyData)
//        {
//            return 50;
//        }
//        else
//        {
//            return super.getCount();
//        }

//        return super.getCount();
        return this.data.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
//        return super.getView(position, convertView, parent);

        View view = convertView;

        // inflate a new view from a layout

        LayoutInflater inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.list_item_status, parent, false);

        // populate the elements of the view normally
        // ...
//        TextView userName = (TextView) view.findViewById(R.id.userFullName);
//        TextView statusText = (TextView) view.findViewById(R.id.statusText);
//
//        userName.setText(this.data.get(position).poster.fullName);
//        statusText.setText(this.data.get(position).statusContent);

        me.omegavesko.microsocial.android.alpha.card.Status card
                = new me.omegavesko.microsocial.android.alpha.card.Status(context, this.data.get(position));

        CardView cardView = (CardView) view.findViewById(R.id.statusCard);
        cardView.setCard(card);

        return view;
    }
}
