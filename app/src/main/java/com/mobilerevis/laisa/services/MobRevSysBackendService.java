package com.mobilerevis.laisa.services;

import com.mobilerevis.laisa.entidades.SystematicReview;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MobRevSysBackendService {
    @DELETE("api/systematicreview/{id}")
    Call<ResponseBody> deleteSystematicReview(@Path("id") long id, @Header("Authorization") String auth);

    @POST("api/systematicreview/update")
    Call<ResponseBody> updateSystematicReview(@Body SystematicReview sr, @Header("Authorization") String auth);
}
