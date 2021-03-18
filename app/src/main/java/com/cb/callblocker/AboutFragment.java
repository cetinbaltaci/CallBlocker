package com.cb.callblocker;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView txtEMail = (TextView) view.findViewById(R.id.txtEmail);
        txtEMail.setText(HtmlCompat.fromHtml("<a href=\"mailto:cetinbaltaci@gmail.com\">cetinbaltaci@gmail.com</a>",
                HtmlCompat.FROM_HTML_MODE_LEGACY));
        txtEMail.setMovementMethod(LinkMovementMethod.getInstance());
        TextView txtGithub = (TextView) view.findViewById(R.id.txtGithub);
        txtGithub.setText(HtmlCompat.fromHtml("<a href=\"https://github.com/cetinbaltaci/BinderExample\">CallBlocker</a>",
                HtmlCompat.FROM_HTML_MODE_LEGACY));
        txtGithub.setMovementMethod(LinkMovementMethod.getInstance());
    }
}