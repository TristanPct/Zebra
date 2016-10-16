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
public class EditProfileListFragment extends Fragment {

    OnClickListener onClickListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            onClickListener = (OnClickListener) getActivity();
        } catch (ClassCastException exception) {
            throw new ClassCastException(context.toString() + " must implement OnClickListener"); // Try catch pour afficher un message d'erreur custom pour mieux travailler en groupe
        }
    }

    public EditProfileListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile_list, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.editUsername)
    public void editUsernameClick() {
        if(onClickListener != null) onClickListener.onEditUsernameClick();
    }

    @OnClick(R.id.editEmail)
    public void editEmailClick() {
        if(onClickListener != null) onClickListener.onEditEmailClick();
    }

    @OnClick(R.id.editLogout)
    public void editLogoutClick() {
        if (onClickListener != null) onClickListener.onEditLogoutClick();
    }

    public interface OnClickListener {
        void onEditUsernameClick();
        void onEditEmailClick();
        void onEditLogoutClick();
    }

}
