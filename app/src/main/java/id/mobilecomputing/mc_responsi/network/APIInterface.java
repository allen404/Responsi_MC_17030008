package id.mobilecomputing.mc_responsi.network;

import id.mobilecomputing.mc_responsi.models.CityWeather;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIInterface {
    @GET("forecast/daily")
    Call<CityWeather> getWeatherCity (@Query("q") String city, @Query("APPID")String key, @Query("units") String units , @Query("cnt") int days);

    @GET("forecast/daily")
    Call<CityWeather> getWeatherGPS(@Query("lat") String latitude,
                                    @Query("lon") String longitude,
                                    @Query("APPID") String key,
                                    @Query("cnt") int days,
                                    @Query("units") String units);



}
