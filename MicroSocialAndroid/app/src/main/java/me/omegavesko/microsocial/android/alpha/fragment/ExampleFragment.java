package me.omegavesko.microsocial.android.alpha.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.omegavesko.microsocial.android.alpha.R;

/**
 * Created by Veselin on 1/4/2015.
 */
public class ExampleFragment extends Fragment
{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.example_layout, container, false);

        return rootView;
    }
}
