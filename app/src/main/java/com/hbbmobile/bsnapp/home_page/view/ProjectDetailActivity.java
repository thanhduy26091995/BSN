package com.hbbmobile.bsnapp.home_page.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hbbmobile.bsnapp.R;

/**
 * Created by buivu on 11/11/2016.
 */
public class ProjectDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnAddAuthor;
    private TextView txtBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);
        //init views
        initViews();
    }

    private void initViews() {
        btnAddAuthor = (Button) findViewById(R.id.btn_add_author);
        txtBack = (TextView) findViewById(R.id.txtBack);
        //set on click
        btnAddAuthor.setOnClickListener(this);
        txtBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == btnAddAuthor) {
            moveToAddFriend();
        } else if (view == txtBack) {
            finish();
            overridePendingTransition(R.anim.comming_in_left,R.anim.comming_out_left);

        }
    }

    private void moveToAddFriend() {
        Intent addFriendIntent = new Intent(ProjectDetailActivity.this, AddFriendActivity.class);
        startActivity(addFriendIntent);
        overridePendingTransition(R.anim.comming_in_right,R.anim.comming_out_right);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.comming_in_left,R.anim.comming_out_left);

    }
}
