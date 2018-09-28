package com.example.achraf.testandroid.network;

import com.example.achraf.testandroid.entities.AccessToken;
import com.example.achraf.testandroid.entities.PostResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @POST("register") // http://domain.com/api/
    @FormUrlEncoded
    Call<AccessToken> register(@Field("first_name") String first_name, @Field("last_name") String last_name, @Field("email") String email, @Field("password") String password);

    @POST("login")
    @FormUrlEncoded
    Call<AccessToken> login(@Field("username") String username, @Field("password") String password);

    @POST("refresh")
    @FormUrlEncoded
    Call<AccessToken> refresh(@Field("refresh_token") String refreshToken);


    @GET("posts")
    Call<PostResponse> posts();


}
