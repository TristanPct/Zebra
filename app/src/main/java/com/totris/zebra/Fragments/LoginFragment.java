package com.totris.zebra.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.totris.zebra.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements WithErrorView {

    @BindView(R.id.mailInput)
    EditText mailInput;

    @BindView(R.id.passwordInput)
    EditText passwordInput;

    @BindView(R.id.errorText)
    TextView errorText;

    private LoginListener listener;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (LoginListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement LoginListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    public void setError(String error) {
        errorText.setText(error);
    }

    protected boolean validate() {
        String mail = mailInput.getText().toString();
        boolean mailIsValid = !mail.matches("");

        String password = passwordInput.getText().toString();
        boolean passwordIsValid = !password.matches("");

        if (!mailIsValid) {
            mailInput.setError(getString(R.string.error_required));
        }

        if (!passwordIsValid) {
            passwordInput.setError(getString(R.string.error_required));
        }

        return mailIsValid && passwordIsValid;
    }

    @OnClick(R.id.loginButton)
    public void onLoginButtonClick() {
        setError("");

        if (listener == null || !validate()) return;

        String mail = mailInput.getText().toString();
        String password = passwordInput.getText().toString();

        listener.onLogin(mail, password);
    }

    @OnClick(R.id.gotoRegisterButton)
    public void onGotoRegisterButtonClick() {
        if (listener == null) return;

        listener.onGotoRegister();
    }

    public interface LoginListener {
        void onLogin(String mail, String password);
        void onGotoRegister();
    }
}
