package com.example.ototocarsrentingapp.auth.API;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CarApi {
    //call יהיה האובייקט שייצג את הבקשת רשת לשרת החיצוני
    @GET("v2/ymm/{year}/{make}/{model}")
    Call<CarPriceResponse> getCarPriceByYMM(
            @Header("x-authkey") String apiKey,
            @Path("year") int year,
            @Path("make") String make,
            @Path("model") String model
    );
}
