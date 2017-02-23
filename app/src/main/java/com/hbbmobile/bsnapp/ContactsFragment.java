package com.hbbmobile.bsnapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hbbmobile.bsnapp.friend_list.view.FriendListFragment;
import com.hbbmobile.bsnapp.request.view.RequestsFragment;

/**
 * Created by buivu on 19/01/2017.
 */
public class ContactsFragment extends Fragment {
    private FragmentTabHost mTabHost;

    public ContactsFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null){
            View view = inflater.inflate(R.layout.activity_contacts_requests, null);
            mTabHost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
            mTabHost = new FragmentTabHost(getActivity());
            mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);

            mTabHost.addTab(mTabHost.newTabSpec(getActivity().getResources().getString(R.string.contacts)).setIndicator(getActivity().getResources().getString(R.string.contacts)),
                    FriendListFragment.class, null);
            mTabHost.addTab(mTabHost.newTabSpec(getActivity().getResources().getString(R.string.requests)).setIndicator(getActivity().getResources().getString(R.string.requests)),
                    RequestsFragment.class, null);
            return mTabHost;
        }else{
            ViewGroup parent = (ViewGroup) container.getParent();
            parent.removeView(view);
            return view;
        }
    }

    private void changeTextColor() {
        for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
            View v = mTabHost.getTabWidget().getChildAt(i);
            //set text color for tab host
            TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(Color.parseColor("#FFFFFF"));
            //change background color
            mTabHost.getTabWidget().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorBSN));
            v.setBackgroundResource(R.drawable.tab_indicator_ab_example);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        changeTextColor();
    }
}
