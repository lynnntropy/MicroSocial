package me.omegavesko.microsocial.android.alpha.fragment;

import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import me.omegavesko.microsocial.android.alpha.R;
import me.omegavesko.microsocial.android.alpha.StatusListAdapter;
import me.omegavesko.microsocial.android.alpha.UserStatus;

public class TimelineFragment extends Fragment
{
    ListView statusList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_timeline, container, false);

        this.statusList = (ListView) rootView.findViewById(R.id.statusListView);
        StatusListAdapter adapter = new StatusListAdapter(getActivity().getApplicationContext(), new ArrayList<UserStatus>(), true);
        this.statusList.setAdapter(adapter);

        return rootView;
    }
}

