package com.hbbmobile.bsnapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by buivu on 15/12/2016.
 */
public class AboutUsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView title;
    private ImageView imgback;
    private RelativeLayout relaBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        toolbar = (Toolbar) findViewById(R.id.toolbarback);
        title = (TextView) toolbar.findViewById(R.id.titletoolbar);
        relaBack = (RelativeLayout) toolbar.findViewById(R.id.linear_img_back);
        relaBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.comming_in_left, R.anim.comming_out_left);
            }
        });
        title.setText(getResources().getString(R.string.aboutUs));
    }
}
