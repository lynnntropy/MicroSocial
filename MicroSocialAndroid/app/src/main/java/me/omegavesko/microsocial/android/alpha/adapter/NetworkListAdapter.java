package me.omegavesko.microsocial.android.alpha.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import me.omegavesko.microsocial.android.alpha.NetworkInfo;
import me.omegavesko.microsocial.android.alpha.R;
import me.omegavesko.microsocial.android.alpha.activity.MainActivity;

/**
 * Created by Veselin on 1/8/2015.
 */
public class NetworkListAdapter extends ArrayAdapter<NetworkInfo>
{
    private boolean dummyData;
    private Context context;

    private List<NetworkInfo> data;

    public NetworkListAdapter(Context context, List<NetworkInfo> objects)
    {
        super(context, R.layout.list_item_network, objects);
        this.context = context;
        this.data = objects;
    }

    @Override
    public int getCount()
    {
        return super.getCount();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
//        return super.getView(position, convertView, parent);

        View view = convertView;

        if (view == null)
        {
            // inflate a new view from a layout

            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.list_item_network, parent, false);
        }

        // populate the elements of the view normally
        // ...
        TextView networkName = (TextView) view.findViewById(R.id.networkName);
        TextView networkHost = (TextView) view.findViewById(R.id.networkHost);

        networkName.setText(this.data.get(position).networkName);
        networkHost.setText(this.data.get(position).networkIP);

        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SharedPreferences settings =  context.getSharedPreferences("currentNetworkInfo", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("networkName", data.get(position).networkName);
                editor.putString("networkLocation", data.get(position).networkIP);
                editor.commit();

                Intent mainActivityIntent = new Intent(context, MainActivity.class);
                context.startActivity(mainActivityIntent);
            }
        });

        return view;
    }


}
