package me.omegavesko.microsocial.android.alpha.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.omegavesko.microsocial.android.alpha.R;
import me.omegavesko.microsocial.android.alpha.adapter.StatusListAdapter;
import me.omegavesko.microsocial.android.alpha.network.RESTManager;
import me.omegavesko.microsocial.android.alpha.schema.Status;

public class TimelineFragment extends Fragment
{
    @InjectView(R.id.statusListView)
    ListView statusListView;

    StatusListAdapter adapter;

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    class GetFeedTask extends AsyncTask<Void, Void, List<Status>>
    {
        int first, last;

        GetFeedTask(int first, int last)
        {
            this.first = first;
            this.last = last;
        }

        @Override
        protected List<me.omegavesko.microsocial.android.alpha.schema.Status> doInBackground(Void... params)
        {
            RESTManager restManager = RESTManager.getManager();
            return restManager.restInterface.getFeed(this.first, this.last).feed;
        }

        @Override
        protected void onPostExecute(List<me.omegavesko.microsocial.android.alpha.schema.Status> statuses)
        {
            TimelineFragment.this.adapter = new StatusListAdapter(getActivity(), statuses);
            TimelineFragment.this.adapter.notifyDataSetChanged();
            TimelineFragment.this.statusListView.setAdapter(adapter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_timeline, container, false);

        ButterKnife.inject(this, rootView);

//        this.adapter = new StatusListAdapter(getActivity().getApplicationContext(), new ArrayList<Status>(), true);
//        this.statusListView.setAdapter(adapter);

        new GetFeedTask(0, 30).execute();

        return rootView;
    }
}

