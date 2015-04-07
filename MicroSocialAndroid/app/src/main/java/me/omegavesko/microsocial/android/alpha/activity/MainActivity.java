package me.omegavesko.microsocial.android.alpha.activity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.tkeunebr.gravatar.Gravatar;
import me.omegavesko.microsocial.android.alpha.R;
import me.omegavesko.microsocial.android.alpha.adapter.ViewPagerAdapter;
import me.omegavesko.microsocial.android.alpha.network.RESTManager;
import me.omegavesko.microsocial.android.alpha.schema.CheckSession;
import me.omegavesko.microsocial.android.alpha.schema.User;


public class MainActivity extends ActionBarActivity
{
    @InjectView(R.id.main_toolbar)
    Toolbar mainToolbar;
    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @InjectView(R.id.main_pager)
    ViewPager mainPager;
    @InjectView(R.id.user_avatar)
    ImageView userAvatar;
    @InjectView(R.id.userName)
    TextView userName;
    @InjectView(R.id.userEmail)
    TextView userEmail;

    class GetUserTask extends AsyncTask<Void, Void, User>
    {
        @Override
        protected User doInBackground(Void... params)
        {
            RESTManager restManager = RESTManager.getManager();

            return restManager.restInterface.getUserFromSession(
                    new CheckSession(
                            getSharedPreferences("lastNetwork", 0).getInt("session", 0)
                    )
            );
        }

        @Override
        protected void onPostExecute(User user)
        {
            SharedPreferences storedNetworkSettings = getSharedPreferences("lastNetwork", 0);
            SharedPreferences.Editor editor = storedNetworkSettings.edit();
            editor.putString("fullName", user.fullName);
            editor.putString("email", user.email);
            editor.apply();

            userEmail.setText(user.email);
            userName.setText(user.fullName);

            String gravatarUrl = Gravatar.init().with(user.email).size(100).build();

            Picasso.with(MainActivity.this)
                    .load(gravatarUrl)
                    .into(userAvatar);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        ViewPager pager = (ViewPager) findViewById(R.id.main_pager);
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pager.setOffscreenPageLimit(3);
        pager.setAdapter(pagerAdapter);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setIndicatorColor(getResources().getColor(R.color.white));
        tabs.setShouldExpand(true);
        tabs.setViewPager(pager);

        new GetUserTask().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
