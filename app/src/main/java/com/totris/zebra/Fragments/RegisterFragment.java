package com.totris.zebra.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.totris.zebra.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    @BindView(R.id.nameInput)
    EditText nameInput;

    @BindView(R.id.mailInput)
    EditText mailInput;

    @BindView(R.id.passwordInput)
    EditText passwordInput;

    @BindView(R.id.passwordConfirmationInput)
    EditText passwordConfirmationInput;

    private RegisterListener listener;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (RegisterListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnRegisterListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    protected boolean validate() {
        String name = nameInput.getText().toString();
        boolean nameIsValid = !name.matches("");

        String mail = mailInput.getText().toString();
        boolean mailIsValid = mail.matches(getString(R.string.pattern_email));

        String password = passwordInput.getText().toString();
        boolean passwordIsValid = !password.matches(getString(R.string.pattern_password));

        String passwordConfirmation = passwordConfirmationInput.getText().toString();
        boolean passwordConfirmationIsValid = passwordConfirmation.matches(password);

        if (!nameIsValid) {
            nameInput.setError(getString(R.string.error_required));
        }

        if (!mailIsValid) {
            mailInput.setError(getString(R.string.error_email));
        }

        if (!passwordIsValid) {
            passwordInput.setError(getString(R.string.error_password));
        }

        if (!passwordConfirmationIsValid) {
            passwordConfirmationInput.setError(getString(R.string.error_password_confirmation));
        }

        return nameIsValid && mailIsValid && passwordIsValid && passwordConfirmationIsValid;
    }

    @OnClick(R.id.registerButton)
    public void onRegisterButtonClick() {
        if (listener == null || !validate()) return;

        String name = nameInput.getText().toString();
        String mail = mailInput.getText().toString();
        String password = passwordInput.getText().toString();

        listener.onRegisterClick(name, mail, password);
    }

    @OnClick(R.id.gotoLoginButton)
    public void onGotoLoginButtonClick() {
        if (listener == null) return;

        listener.onGotoLoginClick();
    }

    public interface RegisterListener {
        void onRegisterClick(String name, String mail, String password);
        void onGotoLoginClick();
    }

}
