package com.hbbmobile.bsnapp.login.presenter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.hbbmobile.bsnapp.login.view.LoginActivity;
import com.hbbmobile.bsnapp.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by buivu on 28/11/2016.
 */
public class LoginFacebookPresenter {
    private LoginActivity view;

    public LoginFacebookPresenter(LoginActivity view) {
        this.view = view;
    }

    public void setUpFacebook(Context context, CallbackManager callbackManager) {
        FacebookSdk.sdkInitialize(context);

        final AccessToken accessToken = AccessToken.getCurrentAccessToken();
        LoginManager loginManager;
        loginManager = LoginManager.getInstance();
        loginManager.logInWithReadPermissions(view, Arrays.asList(Constants.FACEBOOK_PERMISSION));
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken.setCurrentAccessToken(loginResult.getAccessToken());
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                // Log.d(view.TAG, object + "\n" + response);
                                try {
                                    String name = response.getJSONObject().get("name").toString();
                                    String email = response.getJSONObject().get("email").toString();
                                    JSONObject picture = response.getJSONObject().getJSONObject("picture");
                                    JSONObject data = picture.getJSONObject("data");
                                    String url = data.getString("url");
                                    Log.d(view.TAG, email + "/" + name + "/" + url);
                                    /*Intent myIntent = new Intent(view, TempActivity.class);
                                    myIntent.putExtra(TempActivity.DISPLAY_NAME, name);
                                    myIntent.putExtra(TempActivity.EMAIL, email);
                                    myIntent.putExtra(TempActivity.PHOTO_URL, url);
                                    view.startActivity(myIntent);*/

//                                    Intent intent = new Intent(view, MainActivity.class);
//                                    com.hbbmobile.bsnapp.model.User user = new com.hbbmobile.bsnapp.model.User("", name,
//                                            "", "", "", "", url);
//                                    //User user=new User(name,email,url);
//                                    intent.putExtra("User", user);
//                                    view.startActivity(intent);
//                                    view.finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email, picture");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }
}
