package org.traccar.manager;

import com.google.android.gms.maps.model.LatLng;


import org.json.JSONObject;
import org.traccar.manager.model.Geocodeobj;
import org.traccar.manager.model.TranslateAddress;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;

import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;


/**
 * Created by cf4 on 11-10-2016.
 */
public interface GeocodeService  {


    @GET("/maps/api/geocode/json?sensor=false")
    Call<String> getLatLong(@Query("address") String address);


    @GET("/maps/api/geocode/json")
    Call<Geocodeobj> getAddress(@Query("latlng") String latlong, @Query("language") String lang);


    @GET("/language/translate/v2?key=AIzaSyD-kFDe0nTEDhp2qpwc0s_ftXud4mGIRVw")
    Call<TranslateAddress> getTranslatedAddress(@Query("target") String target, @Query("source") String source, @Query("q") ArrayList<String> address);



}
