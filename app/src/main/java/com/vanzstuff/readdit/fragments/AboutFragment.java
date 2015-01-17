package com.vanzstuff.readdit.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.vanzstuff.redditapp.R;

/**
 * DialogFragment shows the information about the app
 */
public class AboutFragment extends DialogFragment implements View.OnClickListener {

    private Button mOkButton;

    public AboutFragment(){
        this.setStyle(STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.about_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mOkButton = (Button) view.findViewById(R.id.about_ok);
        mOkButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        this.dismiss();
    }
}
