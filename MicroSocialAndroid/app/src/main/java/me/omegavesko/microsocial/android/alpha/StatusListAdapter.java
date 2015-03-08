package me.omegavesko.microsocial.android.alpha;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class StatusListAdapter extends ArrayAdapter<UserStatus>
{
    private boolean dummyData;
    private Context context;

    private List<UserStatus> data;

    public StatusListAdapter(Context context, List<UserStatus> objects, boolean dummyData)
    {
        super(context, R.layout.list_item_status, objects);
        this.dummyData = dummyData;
        this.context = context;

        if (!dummyData)
        {
            this.data = objects;
        }
        else
        {
            // full a list with dummy data

            List<UserStatus> dummyStatuses = new ArrayList<UserStatus>();

            for (int i = 1; i <= 50; i++)
                dummyStatuses.add(new UserStatus("username", "Hello there!"));

            this.data = dummyStatuses;
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

        View view = convertView;

        if (view == null)
        {
            // inflate a new view from a layout

            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.list_item_status, parent, false);
        }

        // populate the elements of the view normally
        // ...
        TextView userName = (TextView) view.findViewById(R.id.userFullName);
        TextView statusText = (TextView) view.findViewById(R.id.statusText);

        userName.setText(this.data.get(position).ownerUserName);
        statusText.setText(this.data.get(position).statusText);

        return view;
    }
}
