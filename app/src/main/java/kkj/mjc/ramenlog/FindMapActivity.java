package kkj.mjc.ramenlog;

import static kkj.mjc.ramenlog.DistanceUtils.calculateDistance;

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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.RoadViewRequest;
import com.kakao.vectormap.camera.CameraUpdateFactory;


import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import kkj.mjc.ramenlog.dto.ApiResponse;
import kkj.mjc.ramenlog.dto.Restaurant;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class FindMapActivity extends AppCompatActivity {

    private KakaoMap kakaoMap;
    private Double currentLat = null;
    private Double currentLng = null;

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

}
