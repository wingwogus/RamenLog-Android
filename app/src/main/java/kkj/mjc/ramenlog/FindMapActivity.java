package kkj.mjc.ramenlog;

import static kkj.mjc.ramenlog.DistanceUtils.calculateDistance;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;


import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findmap);

        TextView search_bar = findViewById(R.id.search_bar);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        View bottomSheet = findViewById(R.id.bottom_sheet);
        RecyclerView storeList = findViewById(R.id.store_list);


        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MODE_PRIVATE);

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
                return true;
            } else if (id == R.id.nav_search) {
                startActivity(new Intent(this, SearchActivity.class));
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
        bottomNav.setSelectedItemId(R.id.nav_home);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        mMap.setMyLocationEnabled(true);

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
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
                                                .title(r.getName()));
                                    }

                                    // RecyclerView에 데이터 연결
                                    StoreListAdapter adapter = new StoreListAdapter(restaurantList);
                                    RecyclerView storeList = findViewById(R.id.store_list);
                                    storeList.setAdapter(adapter);
                                }
                            }

                            @Override
                            public void onFailure(Call<ApiResponse<List<Restaurant>>> call, Throwable t) {
                                Log.e("Map", "API 실패", t);
                            }
                        });
                    } else {
                        // 위치 못받으면 기본 위치로 이동
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.568256, 126.897240), 15));
                    }
                });
    }

}
