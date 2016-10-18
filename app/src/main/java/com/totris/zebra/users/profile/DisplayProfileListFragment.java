package com.totris.zebra.users.profile;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.totris.zebra.R;

import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class DisplayProfileListFragment extends Fragment {

    private OnClickListener listener;

    public DisplayProfileListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (OnClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implements DisplayProfileListListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display_profile_list, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.contactSendMessage)
    public void onContactSendMessageClick() {
        listener.onContactSendMessageClick();
    }

    public interface OnClickListener {
        void onContactSendMessageClick();
    }
}
