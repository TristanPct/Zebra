package com.totris.zebra.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.totris.zebra.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditUsernameFragment extends Fragment implements EditProfileItemFragment {

    @BindView(R.id.usernameEditText)
    EditText usernameEditText;

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

    private boolean validate() {
        boolean isValid = !usernameEditText.getText().toString().matches("");

        if (!isValid) {
            usernameEditText.setError(getString(R.string.error_required));
        }

        return isValid;
    }

    @Override
    public String getValue() {
        if (!validate()) return null;

        return usernameEditText.getText().toString();
    }

    @Override
    public void setError(String error) {

    }
}
