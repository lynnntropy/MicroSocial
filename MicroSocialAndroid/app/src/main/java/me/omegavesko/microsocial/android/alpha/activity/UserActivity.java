package me.omegavesko.microsocial.android.alpha.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;

import me.omegavesko.microsocial.android.alpha.R;

public class UserActivity extends ActionBarActivity
{
    private TextView userName;
    private TextView fullName;
    private TextView phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

//        ViewPager pager = (ViewPager) findViewById(R.id.main_pager);
//        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
//        pager.setAdapter(pagerAdapter);
//
//        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
//        tabs.setIndicatorColor(getResources().getColor(R.color.white));
//        tabs.setShouldExpand(true);
//        tabs.setViewPager(pager);

        Bundle bundle = getIntent().getExtras();

        String userName = "None";
        String fullname = "None";
        String phone = "None";

        if (bundle != null)
        {
            userName = (String) bundle.get("USERNAME");
            fullname = (String) bundle.get("FULLNAME");
            phone = (String) bundle.get("PHONE");
        }

        this.userName = (TextView) findViewById(R.id.userName);
        this.userName.setText(userName);

        this.fullName = (TextView) findViewById(R.id.fullName);
        this.fullName.setText(fullname);

        this.phone = (TextView) findViewById(R.id.phoneNumber);
        this.phone.setText(phone.equals("none") ? "None" : phone);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
