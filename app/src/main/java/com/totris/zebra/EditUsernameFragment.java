package com.totris.zebra;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditUsernameFragment extends Fragment {

    OnSubmitListener onSubmitListener;

    @BindView(R.id.usernameEditText)
    EditText usernameEditText;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            onSubmitListener = (OnSubmitListener) getActivity();
        } catch (ClassCastException exception) {
            throw new ClassCastException(context.toString() + " must implement OnClickListener"); // Try catch pour afficher un message d'erreur custom pour mieux travailler en groupe
        }
    }

    public EditUsernameFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_username, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.usernameEditSubmit)
    public void submitUsername() {
        onSubmitListener.onUsernameSubmit(usernameEditText.getText().toString());
    }

    public interface OnSubmitListener {
        void onUsernameSubmit(String username);
    }

}
