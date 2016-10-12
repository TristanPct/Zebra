package com.totris.zebra.Fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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

    @BindView(R.id.login_progress)
    View progressView;

    @BindView(R.id.login_form)
    View loginForm;

    @BindView(R.id.mail_input)
    EditText mailInput;

    @BindView(R.id.password_input)
    EditText passwordInput;

    @BindView(R.id.error_text)
    TextView errorText;

    @BindView(R.id.login_button)
    Button loginButton;

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
        toggleProgress(false);

        switch (error) {
            case "":
                errorText.setText("");
                errorText.setVisibility(View.GONE);
                break;
            case "ERROR_USER_NOT_FOUND":
            case "ERROR_INVALID_EMAIL":
                mailInput.setError(getString(R.string.error_wrong_email));
                break;
            case "ERROR_WRONG_PASSWORD":
                passwordInput.setError(getString(R.string.error_wrong_login));
                break;
            default:
                errorText.setText(getString(R.string.error_login));
                errorText.setVisibility(View.VISIBLE);
        }
    }

    private void toggleProgress(final boolean show) {
        if (progressView.getVisibility() == View.VISIBLE && show) return;

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
        loginForm.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
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

    @OnClick(R.id.login_button)
    public void onLoginButtonClick() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        setError("");

        if (listener == null || !validate()) return;

        toggleProgress(true);

        String mail = mailInput.getText().toString();
        String password = passwordInput.getText().toString();

        listener.onLogin(mail, password);
    }

    @OnClick(R.id.goto_register_button)
    public void onGotoRegisterButtonClick() {
        if (listener == null) return;

        listener.onGotoRegister();
    }

    public interface LoginListener {
        void onLogin(String mail, String password);
        void onGotoRegister();
    }
}
