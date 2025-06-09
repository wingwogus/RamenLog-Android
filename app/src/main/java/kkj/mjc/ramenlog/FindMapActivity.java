package kkj.mjc.ramenlog;

import static kkj.mjc.ramenlog.DistanceUtils.calculateDistance;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
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
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;


import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kkj.mjc.ramenlog.dto.ApiResponse;
import kkj.mjc.ramenlog.dto.Restaurant;
import kkj.mjc.ramenlog.service.RestaurantService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class FindMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private com.google.android.gms.location.LocationCallback locationCallback;
    private boolean isCameraMoved = false; // 🔸 최초 한 번만 카메라 이동


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findmap);

        TextView search_bar = findViewById(R.id.search_bar);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        View bottomSheet = findViewById(R.id.bottom_sheet);
        RecyclerView storeList = findViewById(R.id.store_list);


        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setDraggable(true);
        behavior.setHideable(false); // STATE_HIDDEN은 사용하지 않음
        behavior.setFitToContents(false); // HALF 상태 구분 가능
        behavior.setHalfExpandedRatio(0.5f); // 50%까지 내려감
        behavior.setPeekHeight(300); // 최소 내려갈 높이 설정 (px 단위)
        behavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED); // 시작은 반쯤 열림

        // ✅ 여기에 위치 권한 요청 넣기 (if문으로)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    100);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }



        search_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FindMapActivity.this, SearchActivity.class));
            }
        });

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

        storeList.setLayoutManager(new LinearLayoutManager(this));


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
        bottomNav.setSelectedItemId(R.id.nav_search);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        mMap.setMyLocationEnabled(true);

        mMap.setOnMapClickListener(latLng -> {
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED); // 아래로 내리기
        });



        // ⭐ 실시간 위치 업데이트 반영 추가 시작
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        com.google.android.gms.location.LocationRequest locationRequest =
                com.google.android.gms.location.LocationRequest.create()
                        .setPriority(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY)
                        .setInterval(5000) // 5초마다 위치 갱신
                        .setFastestInterval(2000); // 최소 간격

        locationCallback = new com.google.android.gms.location.LocationCallback(){
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

        //맵 이동시 위치 업데이트 요청보내는거 수정해야댐

        // 위치 업데이트 요청
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));

                        // Retrofit 설정
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("http://10.0.2.2:8080/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        RestaurantService rs = retrofit.create(RestaurantService.class);

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

                                    // 마커 추가
                                    for (Restaurant r : restaurantList) {
                                        LatLng position = new LatLng(r.getLatitude(), r.getLongitude());

                                        mMap.addMarker(new MarkerOptions()
                                                .position(position)
                                                .title(r.getName()))
                                                .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ramen));
                                    }

                                    // RecyclerView에 데이터 연결
                                    StoreListAdapter adapter = new StoreListAdapter(restaurantList);
                                    RecyclerView storeList = findViewById(R.id.store_list);
                                    storeList.setAdapter(adapter);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

}
