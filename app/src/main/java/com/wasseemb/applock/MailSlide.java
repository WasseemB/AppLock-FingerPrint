package com.wasseemb.applock;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.heinrichreimersoftware.materialintro.app.SlideFragment;

import java.util.regex.Pattern;

public class MailSlide extends SlideFragment {


    private EditText saveMail;

    private Button saveButton;
    private Helper mHelper;




    public MailSlide() {
        // Required empty public constructor
    }

    public static MailSlide newInstance() {
        return new MailSlide();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.login_fragment, container, false);

        saveMail = (EditText) root.findViewById(R.id.saveMail);
        saveButton = (Button) root.findViewById(R.id.saveButton);



        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHelper = new Helper(getContext());
                if(mHelper.isEmailValid(saveMail.getText().toString()))
                     mHelper.editSharedPref(SettingsKeys.MAIL_PREFERENCE_KEY,saveMail.getText().toString());
                else
                    Toast.makeText(getContext(),"Email address is not valid",Toast.LENGTH_SHORT).show();


            }
        });

        return root;
    }



}