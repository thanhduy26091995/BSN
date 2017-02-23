package com.hbbmobile.bsnapp.rest;

import com.hbbmobile.bsnapp.event.model.EventResponse;
import com.hbbmobile.bsnapp.model.GPSResponse;
import com.hbbmobile.bsnapp.model.RegisterResponse;
import com.hbbmobile.bsnapp.model.UserResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by buivu on 16/12/2016.
 */
public interface ApiInterface {


    @GET("/getinfoevents.php")
    Call<EventResponse> getAllEvent();

    @FormUrlEncoded
    @POST("/login.php/")
    Call<UserResponse> checkLogin(@Field("username") String tenDangNhap, @Field("password") String matKhau);

    @FormUrlEncoded
    @POST("/createuser.php/")
    Call<RegisterResponse> register(@Field("idfb") String uid, @Field("username") String name,
                                    @Field("phone") String soDienThoai, @Field("email") String email, @Field("encoded_string") String encoded_string);

    @GET("/searchuser.php")
    Call<UserResponse> searchUser(@Query("phone") String phoneNumber);

    @GET("/locationuser.php")
    Call<GPSResponse> getMemberAround(@Query("idfb") String id, @Query("lat") String lat, @Query("lng") String lng);

    @FormUrlEncoded
    @POST("/updateinfouser.php")
    Call<UserResponse> updateUserInfo(@Field("fullname") String fullName, @Field("nickname") String nickName, @Field("phone") String phone,
                                      @Field("birth") String birth, @Field("gender") String gender, @Field("companyname") String companyName,
                                      @Field("companyaddress") String companyAddress, @Field("website") String website,
                                      @Field("title") String title, @Field("aboutyou") String aboutYou,
                                      @Field("encoded_string") String encodedString, @Field("idfb") String idFb);

    @FormUrlEncoded
    @POST("/updateinfo.php")
    Call<UserResponse> updateUserWithoutAvatar(@Field("fullname") String fullName, @Field("nickname") String nickName, @Field("phone") String phone,
                                               @Field("birth") String birth, @Field("gender") String gender, @Field("companyname") String companyName,
                                               @Field("companyaddress") String companyAddress, @Field("website") String website,
                                               @Field("title") String title, @Field("aboutyou") String aboutYou,
                                               @Field("idfb") String idFb);

    @GET("/getuser.php")
    Call<UserResponse> getUser(@Query("idfb") String idFb);

    @GET("/detailevents.php")
    Call<EventResponse> getEventDetail(@Query("idevent") String idEvent);
}
