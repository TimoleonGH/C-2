package com.lamti.calculatorcurrencyconverter.retrofit_fixer;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetDataService {

    @GET("/{date}?access_key=d89cbef2638138bffb28b823034f2da4")
    Call<FixerObject> getRates(@Path("date") String date);

    @GET("/latest")
    Call<FixerLatestObject> getLatest();

    @GET("/symbols")
    Call<FixerSymbolsObject> getCurrenciesList();

    @GET("/convert")
    Call<FixerConvertObject> getConvertedValue( @Query("from") String baseC, @Query("to") String targetC,
                                                @Query("amount") double amount );
}
