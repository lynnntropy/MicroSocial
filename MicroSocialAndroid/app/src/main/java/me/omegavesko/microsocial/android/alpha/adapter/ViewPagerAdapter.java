package me.omegavesko.microsocial.android.alpha.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import me.omegavesko.microsocial.android.alpha.fragment.ExampleFragment;
import me.omegavesko.microsocial.android.alpha.fragment.HomeFragment;
import me.omegavesko.microsocial.android.alpha.fragment.MessagesFragment;
import me.omegavesko.microsocial.android.alpha.fragment.TimelineFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter
{
    private HomeFragment homeFragment;
    private MessagesFragment messagesFragment;
    private TimelineFragment timelineFragment;

    public ViewPagerAdapter(FragmentManager fm)
    {
        super(fm);

        this.homeFragment = new HomeFragment();
        this.messagesFragment = new MessagesFragment();
        this.timelineFragment = new TimelineFragment();
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0:
                // home fragment
                return this.homeFragment;

            case 1:
                // messages fragment
                return this.messagesFragment;

            case 2:
                // timeline fragment
                return this.timelineFragment;

            default:
                return new ExampleFragment();
        }
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0:
                return "Home";

            case 1:
                return "Messages";

            case 2:
                return "Timeline";

            default:
                return "Unknown";
        }
    }

    @Override
    public int getCount()
    {
        // we have three pages..
        return 3;
    }
}