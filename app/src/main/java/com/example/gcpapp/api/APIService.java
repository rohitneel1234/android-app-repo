package com.example.gcpapp.api;

import com.example.gcpapp.models.Result;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Rohit Neel on 30-08-2020.
 */

public interface APIService {

    String BASE_URL = "https://vast-service-281617.uc.r.appspot.com";

    @FormUrlEncoded
    @POST("/updateProfile.php")
    Call<Result> updateUser(
            @Field("txtName") String txtName,
            @Field("txtEmail") String txtEmail,
            @Field("txtMobile") String txtMobile
            );

}
