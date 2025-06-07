package kkj.mjc.ramenlog;

import java.util.List;

import kkj.mjc.ramenlog.dto.ApiResponse;
import kkj.mjc.ramenlog.dto.Restaurant;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestaurantService {
    @GET("/api/restaurant/search")
    Call<ApiResponse<List<Restaurant>>> search (@Query("keyword") String keyword);

    @GET("api/restaurant/all")
    Call<ApiResponse<List<Restaurant>>> getAll();
}
