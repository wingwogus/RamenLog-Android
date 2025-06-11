package kkj.mjc.ramenlog;

import static kkj.mjc.ramenlog.DistanceUtils.calculateDistance;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.Collections;
import java.util.List;

import kkj.mjc.ramenlog.dto.ApiResponse;
import kkj.mjc.ramenlog.dto.Restaurant;
import kkj.mjc.ramenlog.service.RestaurantService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// 지도 및 맛집 목록을 표시하는 액티비티
public class FindMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    // 구글 맵 객체
    GoogleMap mMap;

    // 위치 추적 클라이언트 및 콜백
    private FusedLocationProviderClient fusedLocationClient;
    private com.google.android.gms.location.LocationCallback locationCallback;

    // 카메라가 한 번만 사용자 위치로 이동되었는지 여부
    private boolean isCameraMoved = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findmap);

        // UI 요소 초기화
        TextView search_bar = findViewById(R.id.search_bar);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        View bottomSheet = findViewById(R.id.bottom_sheet);
        RecyclerView storeList = findViewById(R.id.store_list);

        // BottomSheet 설정 (반쯤 열린 상태로 시작)
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setDraggable(true);
        behavior.setHideable(false);
        behavior.setFitToContents(false);
        behavior.setHalfExpandedRatio(0.5f);
        behavior.setPeekHeight(300);
        behavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);

        // 위치 권한 체크 및 요청
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    100);
        }

        // Map Fragment 설정 및 비동기 초기화
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // 검색바 클릭 시 검색 화면으로 이동
        search_bar.setOnClickListener(v -> {
            startActivity(new Intent(FindMapActivity.this, SearchActivity.class));
        });

        // 바텀시트 상태 변화 로깅
        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                Log.d("바텀시트", "State changed to: " + newState);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.d("바텀시트", "Slide offset: " + slideOffset);
            }
        });

        // 가게 리스트 RecyclerView 설정
        storeList.setLayoutManager(new LinearLayoutManager(this));

        // 하단 네비게이션 바 클릭 처리
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (id == R.id.nav_search) {
                return true;
            } else if (id == R.id.nav_rank) {
                startActivity(new Intent(this, RankActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });

        // 현재 페이지를 선택된 상태로 표시
        bottomNav.setSelectedItemId(R.id.nav_search);
    }

    // GoogleMap 객체가 준비되었을 때 실행되는 콜백
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // 위치 권한 재확인
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        // 현재 위치 버튼 및 파란 점 표시 활성화
        mMap.setMyLocationEnabled(true);

        // 지도 클릭 시 바텀시트를 아래로 내림
        mMap.setOnMapClickListener(latLng -> {
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });

        // 위치 업데이트 관련 설정
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        com.google.android.gms.location.LocationRequest locationRequest =
                com.google.android.gms.location.LocationRequest.create()
                        .setPriority(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY)
                        .setInterval(5000)
                        .setFastestInterval(2000);

        // 위치 콜백 정의
        locationCallback = new com.google.android.gms.location.LocationCallback() {
            @Override
            public void onLocationResult(com.google.android.gms.location.LocationResult locationResult) {
                if (locationResult == null) return;

                for (android.location.Location location : locationResult.getLocations()) {
                    LatLng updatedLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(updatedLocation));
                    isCameraMoved = true;
                }
            }
        };

        // 위치 업데이트 요청 시작
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

        // 마지막 위치를 받아 지도 초기 위치 설정 및 맛집 API 호출
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));

                        // Retrofit 인스턴스 생성
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("http://10.0.2.2:8080/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        RestaurantService rs = retrofit.create(RestaurantService.class);

                        // 맛집 리스트 API 호출
                        rs.getAllRestaurants().enqueue(new Callback<ApiResponse<List<Restaurant>>>() {
                            @Override
                            public void onResponse(Call<ApiResponse<List<Restaurant>>> call, Response<ApiResponse<List<Restaurant>>> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    List<Restaurant> restaurantList = response.body().getData();

                                    // 거리순 정렬
                                    Collections.sort(restaurantList, (r1, r2) -> {
                                        double dist1 = calculateDistance(myLocation.latitude, myLocation.longitude, r1.getLatitude(), r1.getLongitude());
                                        double dist2 = calculateDistance(myLocation.latitude, myLocation.longitude, r2.getLatitude(), r2.getLongitude());
                                        return Double.compare(dist1, dist2);
                                    });

                                    // 지도에 마커 추가
                                    for (Restaurant r : restaurantList) {
                                        LatLng position = new LatLng(r.getLatitude(), r.getLongitude());
                                        mMap.addMarker(new MarkerOptions()
                                                        .position(position)
                                                        .title(r.getName()))
                                                .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ramen));
                                    }

                                    // 리스트에 데이터 연결
                                    StoreListAdapter adapter = new StoreListAdapter(restaurantList);
                                    RecyclerView storeList = findViewById(R.id.store_list);
                                    storeList.setAdapter(adapter);

                                    // 가게 아이템 클릭 시 상세 페이지 이동
                                    adapter.setOnItemClickListener(item -> {
                                        Intent intent = new Intent(FindMapActivity.this, DetailActivity.class);
                                        intent.putExtra("restaurantId", item.getId());
                                        startActivity(intent);
                                    });
                                }
                            }

                            @Override
                            public void onFailure(Call<ApiResponse<List<Restaurant>>> call, Throwable t) {
                                Log.e("Map", "API 실패", t);
                            }
                        });
                    }
                });
    }

    // 액티비티 종료 시 위치 업데이트 중단
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}