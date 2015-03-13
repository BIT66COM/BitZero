package com.jooik.tabbeddialog.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.thezero.bitzero.R;

/**
 * A simple counterpart for tab1 layout...
 */
public class Fragment_Tab_1 extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_tab_1, container, false);
        TextView content = (TextView)view.findViewById(R.id.dialog);
        content.setText(getString(R.string.about_content));
        return view;
    }
}