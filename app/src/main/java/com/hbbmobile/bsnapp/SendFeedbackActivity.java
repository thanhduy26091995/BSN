package com.hbbmobile.bsnapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hbbmobile.bsnapp.utils.SessionManager;

import java.util.HashMap;

/**
 * Created by buivu on 15/12/2016.
 */
public class SendFeedbackActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView title;
    private ImageView imgback;
    private SessionManager sessionManager;
    private RelativeLayout linearBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_feedback);
        //init session
        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserData();
        //Log.d("SendFeedback", user.get(SessionManager.KEY_ID) + "/" + user.get(SessionManager.KEY_NAME));
        toolbar = (Toolbar) findViewById(R.id.toolbarback);
        title = (TextView) toolbar.findViewById(R.id.titletoolbar);
        linearBack = (RelativeLayout) toolbar.findViewById(R.id.linear_img_back);
        linearBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.comming_in_left, R.anim.comming_out_left);
            }
        });
        title.setText(getResources().getString(R.string.sendFeedback));
    }
}
