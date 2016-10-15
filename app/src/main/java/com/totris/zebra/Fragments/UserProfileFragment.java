package com.totris.zebra.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.totris.zebra.Models.User;
import com.totris.zebra.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfileFragment extends Fragment {

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        String userId = this.getArguments().getString("userId");

        if (savedInstanceState == null) {
            if (User.getCurrent().getUid().equals(userId)) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.userProfileContent, new EditProfileListFragment())
                        .commit();
            } else {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.userProfileContent, new DisplayProfileListFragment())
                        .commit();
            }
        }

        return view;
    }

}
