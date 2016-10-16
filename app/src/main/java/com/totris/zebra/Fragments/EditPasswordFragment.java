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
public class EditPasswordFragment extends Fragment implements EditProfileItemFragment {

    @BindView(R.id.passwordEditText)
    EditText passwordEditText;

    @BindView(R.id.passwordConfirmationEditText)
    EditText passwordConfirmationEditText;

    public EditPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_password, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    private boolean validate() {
        String password = passwordEditText.getText().toString();
        boolean passwordIsValid = password.matches(getString(R.string.pattern_password));

        String passwordConfirmation = passwordConfirmationEditText.getText().toString();
        boolean passwordConfirmationIsValid = passwordConfirmation.matches(password);

        if (!passwordIsValid) {
            passwordEditText.setError(getString(R.string.error_invalid_password));
        }

        if (!passwordConfirmationIsValid) {
            passwordConfirmationEditText.setError(getString(R.string.error_invalid_password_confirmation));
        }

        return passwordIsValid && passwordConfirmationIsValid;
    }

    @Override
    public String getValue() {
        if (!validate()) return null;

        return passwordEditText.getText().toString();
    }

    @Override
    public void setError(String error) {

    }
}
