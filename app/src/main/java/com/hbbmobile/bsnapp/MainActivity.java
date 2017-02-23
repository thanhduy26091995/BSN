package com.hbbmobile.bsnapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hbbmobile.bsnapp.base.view.BaseActivity;
import com.hbbmobile.bsnapp.base.view.DeviceToken;
import com.hbbmobile.bsnapp.event.view.EventFragment;
import com.hbbmobile.bsnapp.home_page.view.AddFriendActivity;
import com.hbbmobile.bsnapp.home_page.view.HomeFragment;
import com.hbbmobile.bsnapp.list_group.view.ListGroupFragment;
import com.hbbmobile.bsnapp.login.model.GoogleAuthController;
import com.hbbmobile.bsnapp.login.view.LoginActivity;
import com.hbbmobile.bsnapp.member_around_here.view.MemberAroundHereFragment;
import com.hbbmobile.bsnapp.member_list.view.MessageListFragment;
import com.hbbmobile.bsnapp.news.BrowserActivity;
import com.hbbmobile.bsnapp.profile_user.view.MyProfileActivity;
import com.hbbmobile.bsnapp.utils.Constants;
import com.hbbmobile.bsnapp.utils.DialogCallback;
import com.hbbmobile.bsnapp.utils.SessionManager;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_ADD_FRIEND = "addFriend";
    private static final String TAG_CHAT_ROOM = "chatRoom";
    private static final String TAG_MEMBER_AROUND = "memberAround";
    private static final String TAG_DISCOUNT = "discount";
    private static final String TAG_NEWS = "news";
    private static final String TAG_EVENTS = "events";
    private static final String TAG_SETTING = "setting";
    private static final String TAG_MESSAGE = "messageList";
    private static final String TAG_BSTYLE = "bstyle";
    private static final String TAG_PCDN = "pcdn";
    private static final String TAG_CONTACTS_REQUESTS = "contactsRequests";


    public static String CURRENT_TAG = TAG_HOME;
    public static int navItemIndex = 0;
    private Handler mHandler;
    private String[] activityTitles;
    private TextView txtTitle;
    private CircleImageView circleImageView;
    private com.hbbmobile.bsnapp.model.User user;
    private TextView txtUsername;
    public static Activity instanceMainActivity;
    private SessionManager sessionManager;
    private String currentID = "";
    private DialogCallback dialogCallback;
    private boolean isShow = false;
    private boolean isClickNews = true;
    private ImageView imgAddFriend;
    private DatabaseReference mDatabase;
    private LinearLayout linearAddFriend;
    private boolean isClickLogout = false;
    private Fragment[] fragments;
    private String[] fragmentTAGS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instanceMainActivity = this;
        mHandler = new Handler();
        //init components
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        imgAddFriend = (ImageView) toolbar.findViewById(R.id.img_add_friend);
        linearAddFriend = (LinearLayout) toolbar.findViewById(R.id.linear_add_friend);
        //end init
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        View headerView = navigationView.getHeaderView(0);
        sessionManager = new SessionManager(this);
        //user = (com.hbbmobile.bsnapp.model.User) getIntent().getSerializableExtra("User");
        circleImageView = (CircleImageView) headerView.findViewById(R.id.nav_header_avatar);
        txtUsername = (TextView) headerView.findViewById(R.id.txt_user_name);
        currentID = getIntent().getStringExtra(Constants.CURRENT_ID);
        dialogCallback = new DialogCallback(this);

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadProfile();
            }


        });
        //
        setUpNavigationView();
        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
        //event click
        linearAddFriend.setOnClickListener(this);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            String token = FirebaseInstanceId.getInstance().getToken();
            DeviceToken.getInstance().addDeviceToken(mDatabase, getUid(), token);
            //show button logout
            showItems();
        } else {
            hideItems();
        }
    }

    private void cachingFragment() {
        //TODO
        HomeFragment homeFragment = new HomeFragment();
        ContactsFragment contactsFragment = new ContactsFragment();
        MessageListFragment messageListFragment = new MessageListFragment();
        ListGroupFragment listGroupFragment = new ListGroupFragment();
        MemberAroundHereFragment memberAroundHereFragment = new MemberAroundHereFragment();
        EventFragment eventFragment = new EventFragment();
        SettingFragment settingFragment = new SettingFragment();
        fragments = new Fragment[]{homeFragment, contactsFragment, messageListFragment, listGroupFragment, memberAroundHereFragment,
                eventFragment, settingFragment};
        fragmentTAGS = new String[]{
                TAG_HOME, TAG_CONTACTS_REQUESTS, TAG_MESSAGE, TAG_CHAT_ROOM, TAG_MEMBER_AROUND, TAG_EVENTS, TAG_SETTING
        };
    }
//    Function sử dụng để collapse item

    private void hideItems() {
        //set boolean
        isShow = false;
        Menu nav_menu = navigationView.getMenu();
        nav_menu.findItem(R.id.nav_logout).setVisible(false);
    }

    //
    private void showItems() {
        isShow = true;
        Menu nav_menu = navigationView.getMenu();
        nav_menu.findItem(R.id.nav_logout).setVisible(true);
    }

    private void initInfo() {
        HashMap<String, String> user = sessionManager.getUserData();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            txtUsername.setText(user.get(SessionManager.KEY_FULLNAME));
            Glide.with(MainActivity.this)
                    .load(user.get(SessionManager.KEY_AVATAR))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .centerCrop()
                    .error(R.drawable.avatar)
                    .into(circleImageView);

            Log.d("Main", user.get(SessionManager.KEY_AVATAR));
        } else {
            txtUsername.setText(getResources().getString(R.string.unknowName));
            circleImageView.setImageResource(R.drawable.avatar);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAuthController.install(this, this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void loadProfile() {
        //Intent it = new Intent(MainActivity.this, MyProfileActivity.class);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            //Toast.makeText(this, "Chưa có thông tin", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
//            if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
            Intent it = new Intent(MainActivity.this, MyProfileActivity.class);
            startActivity(it);
            overridePendingTransition(R.anim.comming_in_right, R.anim.comming_out_right);
//            } else {
//                dialogCallback.showAlert(getResources().getString(R.string.verifyFirst), MainActivity.this);
//                if (dialogCallback.getResult()) {
//                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
            //ShowAlertDialog.showAlert(getResources().getString(R.string.verifyFirst), MainActivity.this);

        }
    }

    private void setToolbarTitle() {
        txtTitle.setText(activityTitles[navItemIndex]);
    }

    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();
        //set toolbar title
        setToolbarTitle();
        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            // show or hide the fab button

            return;
        }
        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                overridePendingTransition(R.anim.comming_in_right, R.anim.comming_out_right);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };
        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    //TODO
    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                //home
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                ContactsFragment contactsFragment = new ContactsFragment();
                return contactsFragment;
            case 2:
                //chat
                MessageListFragment messageListFragment = new MessageListFragment();
                return messageListFragment;
            case 3:
                ListGroupFragment listGroupFragment = new ListGroupFragment();
                return listGroupFragment;
            case 4:
                MemberAroundHereFragment memberAroundHereFragment = new MemberAroundHereFragment();
                return memberAroundHereFragment;
            case 5:
//                FriendListFragment friendListFragment = new FriendListFragment();
//                return friendListFragment;
                DiscountFragment discountFragment = new DiscountFragment();
                return discountFragment;
//            case 5:
//                NewsFragment newsFragment = new NewsFragment();
//                return newsFragment;
            case 7:
                EventFragment eventFragment = new EventFragment();
                return eventFragment;
            case 8:
                SettingFragment settingFragment = new SettingFragment();
                return settingFragment;

            default:
                return new HomeFragment();
        }
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home_page:
                        isClickLogout = false;
                        linearAddFriend.setVisibility(View.GONE);
                        CURRENT_TAG = TAG_HOME;
                        navItemIndex = 0;
                        break;
                    case R.id.nav_contacts:
                        isClickLogout = false;
                        linearAddFriend.setVisibility(View.VISIBLE);
                        CURRENT_TAG = TAG_CONTACTS_REQUESTS;
                        navItemIndex = 1;
                        break;
                    case R.id.nav_messages:
                        isClickLogout = false;
                        linearAddFriend.setVisibility(View.GONE);
                        CURRENT_TAG = TAG_MESSAGE;
                        navItemIndex = 2;
                        break;
                    case R.id.nav_room:
                        isClickLogout = false;
                        linearAddFriend.setVisibility(View.GONE);
                        CURRENT_TAG = TAG_CHAT_ROOM;
                        navItemIndex = 3;
                        break;
                    case R.id.nav_member_around:
                        isClickLogout = false;
                        linearAddFriend.setVisibility(View.GONE);
                        CURRENT_TAG = TAG_MEMBER_AROUND;
                        navItemIndex = 4;
                        break;
//                    case R.id.nav_discount:
//                        isClickLogout = false;
//                        linearAddFriend.setVisibility(View.GONE);
//                        CURRENT_TAG = TAG_DISCOUNT;
//                        navItemIndex = 5;
//                        break;
                    case R.id.nav_news:
                        isClickLogout = false;
                        linearAddFriend.setVisibility(View.GONE);
                        startActivity(new Intent(MainActivity.this, BrowserActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_events:
                        isClickLogout = false;
                        linearAddFriend.setVisibility(View.GONE);
                        CURRENT_TAG = TAG_EVENTS;
                        navItemIndex = 7;
                        break;
                    case R.id.nav_settings:
                        isClickLogout = false;
                        linearAddFriend.setVisibility(View.GONE);
                        CURRENT_TAG = TAG_SETTING;
                        navItemIndex = 8;
                        break;
                    case R.id.nav_logout:
                        isClickLogout = true;
                        //show alert xác nhận
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage(R.string.confirmLogOut)
                                .setCancelable(false)
                                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        logOut();
                                        //initInfo();
                                    }
                                })
                                .setNegativeButton(R.string.huy, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });
                        builder.create().show();
                        break;
                    default:
                        isClickLogout = false;
                        navItemIndex = 0;
                }
                //checking if the item is in checked or not, if not make it in checked state
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                item.setChecked(true);
                if (!isClickLogout) {
                    loadHomeFragment();
                }


                return true;
            }

        });
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this, AddFriendActivity.class);
        startActivity(intent);
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        sessionManager.logoutUser();

        Intent refresh = new Intent(MainActivity.this, MainActivity.class);
        startActivity(refresh);
        finish();
        //update UI
        // hideItems();
    }
}
