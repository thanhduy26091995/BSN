package com.hbbmobile.bsnapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hbbmobile.bsnapp.utils.SessionManager;

import java.util.Locale;

public class SettingFragment extends Fragment implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView title;
    private TextView txtFeedback, txtAboutUs, txtLanguage;
    private LinearLayout linearLanguage;
    private String[] listLanguage;
    private Locale locale;
    private SessionManager sessionManager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_setting, container, false);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        //init views
        txtFeedback = (TextView) rootView.findViewById(R.id.txtFeedback);
        txtAboutUs = (TextView) rootView.findViewById(R.id.txtAboutUs);
        linearLanguage = (LinearLayout) rootView.findViewById(R.id.linear_language);
        txtLanguage = (TextView) rootView.findViewById(R.id.txt_language);
        //event click
        txtFeedback.setOnClickListener(this);
        txtAboutUs.setOnClickListener(this);
        linearLanguage.setOnClickListener(this);
        //init
        sessionManager = new SessionManager(getActivity());
        txtLanguage.setText(sessionManager.getLanguage());
        return rootView;
    }

    @Override
    public void onClick(View view) {
        if (view == txtFeedback) {
            startActivity(new Intent(getActivity(), SendFeedbackActivity.class));
        } else if (view == txtAboutUs) {
            startActivity(new Intent(getActivity(), AboutUsActivity.class));
        } else if (view == linearLanguage) {
            openPopupLanguage();
        }
    }

    private void openPopupLanguage() {
        listLanguage = getResources().getStringArray(R.array.arr_language);
        createPopUp(listLanguage, getResources().getString(R.string.chooseLanguage), txtLanguage);
    }

    private void createPopUp(final String[] values, String title, final TextView textToShow) {

        Dialog d = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT)
                .setTitle(title)
                .setNegativeButton(getActivity().getResources().getString(R.string.cancel), null)
                .setItems(values, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dlg, int position) {
                        String language;
                        if (values[position].equals("Vietnamese") || values[position].equals("Tiếng Việt")) {
                            setLocale("vi");
                            language = "Tiếng Việt";


                        } else {
                            setLocale("en");
                            language = "English";
                        }
                        sessionManager.createLanguageSession(language);
                    }
                })
                .create();
        d.show();
    }

    private void setLocale(String lang) {
        Log.d("SETTING", lang);
        locale = new Locale(lang);
        Resources res = getActivity().getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        Configuration configuration = res.getConfiguration();
        configuration.locale = locale;
        res.updateConfiguration(configuration, displayMetrics);
        Intent refresh = new Intent(getActivity(), getActivity().getClass());
        startActivity(refresh);
        getActivity().finish();
    }
}
