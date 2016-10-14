package com.totris.zebra.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.totris.zebra.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfileFragment extends Fragment {

    OnClickListener onClickListener;

    @BindView(R.id.editUsername)
    Layout editUsername;

    @BindView(R.id.editEmail)
    Layout editEmail;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            onClickListener = (OnClickListener) context;
        } catch (ClassCastException exception) {
            throw new ClassCastException(context.toString() + " must implement OnClickListener"); // Try catch pour afficher un message d'erreur custom pour mieux travailler en groupe
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        ButterKnife.bind(this, view);

        return view;
    }



    public interface OnClickListener {
        void onEditUsernameClick();
        void onEditEmailClick();
    }

}
