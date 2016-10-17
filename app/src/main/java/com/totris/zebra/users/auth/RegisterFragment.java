package com.totris.zebra.users.auth;


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
import com.totris.zebra.utils.WithErrorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements WithErrorView {

    @BindView(R.id.register_progress)
    View progressView;

    @BindView(R.id.register_form)
    View registerForm;

    @BindView(R.id.username_input)
    EditText usernameInput;

    @BindView(R.id.mail_input)
    EditText mailInput;

    @BindView(R.id.password_input)
    EditText passwordInput;

    @BindView(R.id.password_confirmation_input)
    EditText passwordConfirmationInput;

    @BindView(R.id.error_text)
    TextView errorText;

    @BindView(R.id.register_button)
    Button registerButton;

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
            throw new ClassCastException(context.toString() + " must implement RegisterListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

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
            case "ERROR_EMAIL_ALREADY_IN_USE":
                mailInput.setError(getString(R.string.error_taken_email));
                break;
            default:
                errorText.setText(getString(R.string.error_registration));
                errorText.setVisibility(View.VISIBLE);
        }
    }

    private void toggleProgress(final boolean show) {
        if (progressView.getVisibility() == View.VISIBLE && show) return;

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        registerForm.setVisibility(show ? View.GONE : View.VISIBLE);
        registerForm.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                registerForm.setVisibility(show ? View.GONE : View.VISIBLE);
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
        String username = usernameInput.getText().toString();
        boolean usernameIsValid = !username.matches("");

        String mail = mailInput.getText().toString();
        boolean mailIsValid = mail.matches(getString(R.string.pattern_email));

        String password = passwordInput.getText().toString();
        boolean passwordIsValid = password.matches(getString(R.string.pattern_password));

        String passwordConfirmation = passwordConfirmationInput.getText().toString();
        boolean passwordConfirmationIsValid = passwordConfirmation.matches(password);

        if (!usernameIsValid) {
            usernameInput.setError(getString(R.string.error_required));
        }

        if (!mailIsValid) {
            mailInput.setError(getString(R.string.error_invalid_email));
        }

        if (!passwordIsValid) {
            passwordInput.setError(getString(R.string.error_invalid_password));
        }

        if (!passwordConfirmationIsValid) {
            passwordConfirmationInput.setError(getString(R.string.error_invalid_password_confirmation));
        }

        return usernameIsValid && mailIsValid && passwordIsValid && passwordConfirmationIsValid;
    }

    @OnClick(R.id.register_button)
    public void onRegisterButtonClick() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        setError("");

        if (listener == null || !validate()) return;

        toggleProgress(true);

        String username = usernameInput.getText().toString();
        String mail = mailInput.getText().toString();
        String password = passwordInput.getText().toString();

        listener.onRegister(username, mail, password);
    }

    @OnClick(R.id.goto_login_button)
    public void onGotoLoginButtonClick() {
        if (listener == null) return;

        listener.onGotoLogin();
    }

    public interface RegisterListener {
        void onRegister(String username, String mail, String password);
        void onGotoLogin();
    }

}
