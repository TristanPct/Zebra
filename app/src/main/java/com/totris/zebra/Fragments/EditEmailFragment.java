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
public class EditEmailFragment extends Fragment {

    OnSubmitListener onSubmitListener;

    @BindView(R.id.emailEditText)
    EditText emailEditText;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            onSubmitListener = (OnSubmitListener) getActivity();
        } catch (ClassCastException exception) {
            throw new ClassCastException(context.toString() + " must implement OnClickListener"); // Try catch pour afficher un message d'erreur custom pour mieux travailler en groupe
        }
    }

    public EditEmailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_email, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.emailEditSubmit)
    public void submitEmail() {
        onSubmitListener.onEmailSubmit(emailEditText.getText().toString());
    }

    public interface OnSubmitListener {
        void onEmailSubmit(String email);
    }

}
