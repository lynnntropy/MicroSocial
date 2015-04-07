package me.omegavesko.microsocial.android.alpha.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.omegavesko.microsocial.android.alpha.R;
import me.omegavesko.microsocial.android.alpha.adapter.StatusListAdapter;
import me.omegavesko.microsocial.android.alpha.network.RESTManager;
import me.omegavesko.microsocial.android.alpha.schema.OutboundStatus;
import me.omegavesko.microsocial.android.alpha.schema.Status;
import me.omegavesko.microsocial.android.alpha.schema.User;
import retrofit.client.Response;

public class TimelineFragment extends Fragment
{
    @InjectView(R.id.statusListView)
    ListView statusListView;

    StatusListAdapter adapter;
    @InjectView(R.id.fab)
    FloatingActionButton fab;

    List<Status> statusList = new ArrayList<Status>();

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    class GetFeedTask extends AsyncTask<Void, Void, List<Status>>
    {
        int first, last;
        boolean addToTop;

        GetFeedTask(int first, int last, boolean addToTop)
        {
            this.first = first;
            this.last = last;
            this.addToTop = addToTop;
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
            if (addToTop)
            {
                TimelineFragment.this.statusList.addAll(0, statuses);
                TimelineFragment.this.adapter.notifyDataSetChanged();
            }
            else
            {
                TimelineFragment.this.statusList.addAll(statuses);
                TimelineFragment.this.adapter.notifyDataSetChanged();
            }

        }
    }

    class SendPostTask extends AsyncTask<Void, Void, Response>
    {
        String post;

        SendPostTask(String post)
        {
            this.post = post;
        }

        @Override
        protected Response doInBackground(Void... params)
        {
            RESTManager restManager = RESTManager.getManager();
            return restManager.restInterface.sendPost(
                    new OutboundStatus(
                            Integer.toString(getActivity().getSharedPreferences("lastNetwork", 0).getInt("session", 0)),
                            post
                    )
            );
        }

        @Override
        protected void onPostExecute(Response response)
        {
            if (response.getStatus() == 200)
            {
                new GetFeedTask(0, 0, true).execute();
            }
            else
            {
                // failed to post
            }
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

        this.fab.attachToListView(this.statusListView);

        this.fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openNewPostDialog();
            }
        });

        TimelineFragment.this.adapter = new StatusListAdapter(getActivity(), statusList);
        TimelineFragment.this.statusListView.setAdapter(this.adapter);

        View loadMoreView = ((LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.loadmore_feed, null, false);
        loadMoreView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new GetFeedTask(statusList.size(), statusList.size() + 20, false).execute();
            }
        });
        this.statusListView.addFooterView(loadMoreView);

        new GetFeedTask(0, 30, false).execute();

        return rootView;
    }

    void openNewPostDialog()
    {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title("New Post")
                .customView(R.layout.dialog_new_post, false)
                .positiveText("Post")
                .negativeText("Cancel")
                .theme(Theme.LIGHT)
                .titleColor(Color.BLACK)
                .callback(new MaterialDialog.ButtonCallback()
                {
                    @Override
                    public void onPositive(MaterialDialog dialog)
                    {
                        sendPost(
                                ((EditText)dialog.getCustomView().findViewById(R.id.postEditor))
                                        .getText().toString());
                    }
                })
                .show();

//        View view = dialog.getCustomView();
    }

    void sendPost(String post)
    {
        new SendPostTask(post).execute();
    }
}

