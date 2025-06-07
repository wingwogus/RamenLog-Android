package kkj.mjc.ramenlog;

import android.content.Intent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.KakaoMapSdk;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;

import java.util.Arrays;
import java.util.List;


public class FindMapActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findmap);
        TextView search_bar = findViewById(R.id.search_bar);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        View bottomSheet = findViewById(R.id.bottom_sheet);
        RecyclerView storeList = findViewById(R.id.store_list);

        MapView mapView = findViewById(R.id.map_view);

        mapView.start(new MapLifeCycleCallback() {
            @Override
            public void onMapDestroy() {
                // 지도 API 가 정상적으로 종료될 때 호출됨
                Log.d("KakaoMap", "onMapDestroy");
            }

            @Override
            public void onMapError(Exception error) {
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출됨
                Log.d("KakaoMap" , "onMapError");
            }
        }, new KakaoMapReadyCallback() {
            @Override
            public void onMapReady(KakaoMap kakaoMap) {
                // 인증 후 API 가 정상적으로 실행될 때 호출됨
                kakaoMap = kakaoMap ;
            }
        });


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

        List<String> dummyData = Arrays.asList("라멘집 A", "라멘집 B", "라멘집 C");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dummyData);

        RecyclerView.Adapter recyclerAdapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                TextView view = new TextView(parent.getContext());
                view.setPadding(16, 16, 16, 16);
                return new RecyclerView.ViewHolder(view) {};
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ((TextView) holder.itemView).setText(dummyData.get(position));
            }

            @Override
            public int getItemCount() {
                return dummyData.size();
            }
        };

        storeList.setAdapter(recyclerAdapter);

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
