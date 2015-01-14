package com.vanzstuff.readdit.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vanzstuff.readdit.Logger;
import com.vanzstuff.readdit.Utils;
import com.vanzstuff.readdit.data.ReadditContract;
import com.vanzstuff.redditapp.R;

/**
 * Dialog where the user inserts the post's tags
 */
public class InputTagFragment extends DialogFragment implements View.OnClickListener {

    private static final String ARG_POST = "post";

    private Button mCancelButton;
    private Button mOkButton;
    private EditText mEditTags;


    public static InputTagFragment newInstance(long post){
        Bundle args = new Bundle(1);
        args.putLong(ARG_POST, post);
        InputTagFragment instance = new InputTagFragment();
        instance.setArguments(args);
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_input_labels, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCancelButton = (Button) view.findViewById(R.id.cancel_input_label_dialog);
        mCancelButton.setOnClickListener(this);
        mOkButton = (Button) view.findViewById(R.id.ok_input_label_dialog);
        mOkButton.setOnClickListener(this);
        mEditTags = (EditText) view.findViewById(R.id.edit_tags);
    }

    @Override
    public void onClick(View v) {
        if( v.getId() == R.id.ok_input_label_dialog) {
            //add the tag to the post
            String tagString = mEditTags.getText().toString();
            String[] tags = tagString.split(" ");
            long post = getArguments().getLong(ARG_POST);
            if( post <= 0 ){
                //post id invalid.Dismiss!
                Logger.e("Post id " + post + " invalid. The id must be greater the 0");
                dismiss();
                return;
            }
            for ( String tag : tags){
                if ( Utils.isAlphaNumeric(tag)) {
                    //everything ok. Let's store in the database
                    getActivity().getContentResolver().insert(ReadditContract.Post.buildAddTagUri(post, tag), null);
                } else {
                    //notify the user about the invalid tag and ignore it
                    Toast.makeText(getActivity(), "Tag " + tag + " invalid. The tag allows only letters and numbers", Toast.LENGTH_LONG ).show();
                    Logger.w("Tag " + tag + " invalid. The tag allows only letters and numbers");
                }
            }
        }
        dismiss();
    }
}
