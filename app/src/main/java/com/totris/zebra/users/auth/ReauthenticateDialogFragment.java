package com.totris.zebra.users.auth;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.totris.zebra.R;
import com.totris.zebra.utils.Authentication;
import com.totris.zebra.utils.ViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReauthenticateDialogFragment extends DialogFragment {

    private static final String TAG = "ReauthenticateDialog";
    private ReauthenticateDialogListener listener;

    @BindView(R.id.progress)
    View progressView;

    @BindView(R.id.password_input_container)
    View passwordView;

    @BindView(R.id.password_input)
    EditText passwordEditText;

    public ReauthenticateDialogFragment() {
        // Required empty public constructor
    }

    public void setListener(ReauthenticateDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (ReauthenticateDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ReauthenticateDialogListener");
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        if (listener == null) {
//            throw new NullPointerException("No listener set. Use ReauthenticateDialogFragment#setListener before show.");
//        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_reauthenticate, null);

        ButterKnife.bind(this, view);

        builder.setView(view)
                .setTitle(getString(R.string.reauthenticate))
                .setPositiveButton(R.string.confirm, null)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: NegativeButton");
                        ReauthenticateDialogFragment.this.getDialog().cancel();
                    }
                });

        final AlertDialog dialog = builder.create();
        // Needed to prevent automatic close on positive button click
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: PositiveButton  validation:" + validate());
                        if (!validate()) return;

                        ViewUtils.closeKeyboard(getActivity());

                        toggleProgress(true);
                        String password = passwordEditText.getText().toString();

                        Authentication.getInstance().reauthenticate(password, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                toggleProgress(false);
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "onComplete: success");
                                    ReauthenticateDialogFragment.this.getDialog().dismiss();
                                    listener.onReauthenticationSuccess();
                                } else {
                                    Log.d(TAG, "onComplete: error");
                                    passwordEditText.setError(getString(R.string.error_wrong_password));
                                }
                            }
                        });
                    }
                });
            }
        });

        return dialog;
    }

    private void toggleProgress(boolean show) {
        ViewUtils.toggleProgress(progressView, passwordView, show);
    }

    protected boolean validate() {
        String password = passwordEditText.getText().toString();
        boolean passwordIsValid = !password.matches("");

        if (!passwordIsValid) {
            passwordEditText.setError(getString(R.string.error_required));
        }

        return passwordIsValid;
    }

    public interface ReauthenticateDialogListener {
        void onReauthenticationSuccess();
    }
}
