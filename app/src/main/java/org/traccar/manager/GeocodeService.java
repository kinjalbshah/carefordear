package org.traccar.manager;

import com.google.android.gms.maps.model.LatLng;


import org.json.JSONObject;
import org.traccar.manager.model.Geocodeobj;

import okhttp3.OkHttpClient;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;


/**
 * Created by cf4 on 11-10-2016.
 */
public interface GeocodeService  {


    @GET("/maps/api/geocode/json?sensor=false")
    Call<String> getLatLong(@Query("address") String address);


    @GET("/maps/api/geocode/json")
    Call<Geocodeobj> getAddress(@Query("latlng") String latlong);



}
