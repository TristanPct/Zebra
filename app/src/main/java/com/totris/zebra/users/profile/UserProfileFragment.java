package com.totris.zebra.users.profile;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.totris.zebra.users.User;
import com.totris.zebra.R;

import org.jdeferred.DoneCallback;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfileFragment extends Fragment {
    private static final String TAG = "UserProfileFragment";

    private String username;
    private String mail;

    @BindView(R.id.usernameView)
    TextView usernameView;

    @BindView(R.id.userEmailView)
    TextView mailView;

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

        ButterKnife.bind(this, view);

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

            User user = User.getByUid(userId);
            if (user != null) {
                username = user.getUsername();
                mail = user.getMail();
            }
        } else {
            username = savedInstanceState.getString("username");
            mail = savedInstanceState.getString("mail");
        }

        refreshHeader();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("username", username);
        outState.putString("mail", mail);
        super.onSaveInstanceState(outState);
    }

    private void refreshHeader() {
        usernameView.setText(username);
        mailView.setText(mail);
    }

}
