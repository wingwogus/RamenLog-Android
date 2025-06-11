package kkj.mjc.ramenlog.service;

import java.util.List;

import kkj.mjc.ramenlog.dto.ApiResponse;
import kkj.mjc.ramenlog.dto.Restaurant;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Path;

// Retrofit을 이용해 서버의 API와 통신하기 위한 인터페이스
public interface RestaurantService {

    // 검색어(keyword)를 이용한 맛집 검색 API
    // 예: /api/restaurant/search?keyword=라멘
    @GET("/api/restaurant/search")
    Call<ApiResponse<List<Restaurant>>> search(@Query("keyword") String keyword);

    // 전체 맛집 리스트를 불러오는 API
    // 예: /api/restaurant/all
    @GET("api/restaurant/all")
    Call<ApiResponse<List<Restaurant>>> getAllRestaurants();

    // 특정 ID로 맛집 정보를 불러오는 API
    // 예: /api/restaurant/3
    @GET("api/restaurant/{id}")
    Call<Restaurant> getRestaurantById(@Path("id") long id);
}