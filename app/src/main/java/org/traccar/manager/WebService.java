/*
 * Copyright 2015 Anton Tananaev (anton.tananaev@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.manager;

import org.json.JSONArray;
import org.json.JSONObject;
import org.traccar.manager.model.Command;
import org.traccar.manager.model.CommandType;
import org.traccar.manager.model.Device;
import org.traccar.manager.model.DeviceEvent;
import org.traccar.manager.model.DeviceGeofence;
import org.traccar.manager.model.User;
import org.traccar.manager.model.Geofence;
import org.traccar.manager.model.DeviceEvent;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WebService {

    @FormUrlEncoded
    @POST("/api/session")
    Call<User> addSession(@Field("email") String email, @Field("password") String password);

    @GET("/api/devices")
    Call<List<Device>> getDevices();

    @GET("/api/commandtypes")
    Call<List<CommandType>> getCommandTypes(@Query("deviceId") long deviceId);

    @POST("/api/commands")
    Call<Command> sendCommand(@Body Command command);

    @GET("/api/geofences")
    Call<List<Geofence>> getGeofences(@Query("deviceId") long deviceId);

    @POST("/api/geofences")
    Call<Geofence> saveGeofence(@Body Geofence geofence);

    @POST("/api/devices/geofences")
    Call<DeviceGeofence> saveDeviceGeofence(@Body DeviceGeofence devicegeofence);

    @DELETE("/api/geofences/{id}")
    Call<ResponseBody> deleteGeofences(@Path("id") long geofenceId);

    @GET("/api/reports/events")
    Call<List<DeviceEvent>> getDeviceEvents(@Query("deviceId") long deviceId, @Query("type") String type, @Query("from") String from_date, @Query("to") String to_date);

    @GET("/maps/api/geocode/json?sensor=false")
    Call<JSONObject> getAddress(@Query("address") String address);

    }
