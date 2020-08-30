package com.guidoperre.youarrive.retrofit;

import com.guidoperre.youarrive.models.AutoSuggestResponse;
import com.guidoperre.youarrive.models.GeoCodeResponse;
import com.guidoperre.youarrive.models.RouteResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface HTTPRequest {

    @GET("suggest.json")
    Call<AutoSuggestResponse> suggestAPI(@Query("app_id") String AppID, @Query("app_code") String AppCode, @Query("query") String query, @Query("country") String ctryCode);

    @GET("geocode.json")

    Call<GeoCodeResponse> geocodeAPI(@Query("app_id") String AppID, @Query("app_code") String AppCode, @Query("locationid") String locationID);

    @GET("calculateroute.json")
    Call<RouteResponse> routesAPI(@Query("apiKey") String ApiKey, @Query("waypoint0") String WayPoint0, @Query("waypoint1") String WayPoint1, @Query("mode") String Mode, @Query("avoidtransporttypes") String AvoidTransportTypes, @Query("metricSystem") String MetricSystem , @Query("lineAttributes") String LineAttributes, @Query("routeAttributes") String RouteAttributes, @Query("combineChange") String Change, @Query("departure") String Departure, @Query("alternatives") String Alternatives);

}
