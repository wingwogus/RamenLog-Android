package kkj.mjc.ramenlog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import kkj.mjc.ramenlog.detail.DetailHomeFragment;
import kkj.mjc.ramenlog.detail.DetailMenuFragment;
import kkj.mjc.ramenlog.detail.DetailReviewFragment;
import kkj.mjc.ramenlog.rank.RankItem;
import kkj.mjc.ramenlog.request.DetailRequest;
import kkj.mjc.ramenlog.request.LikeRequest;

public class DetailActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ImageView ivBack, ivLike;
    TextView tvName, tvAddress, tvRating, tvRank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);

        ivBack = findViewById(R.id.toolbar_detail);
        tvName = findViewById(R.id.detail_name);
        tvAddress = findViewById(R.id.detail_address);
        tvRating = findViewById(R.id.tv_rating);
        tvRank = findViewById(R.id.tv_rank);
        ivLike = findViewById(R.id.iv_like);

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("accessToken", null);

        // ⭐⭐⭐ restaurantId 받아오기 ⭐⭐⭐
        Long restaurantId = getIntent().getLongExtra("restaurantId", -1L);

        // 기본 프래그먼트
        DetailHomeFragment homeFragment = new DetailHomeFragment();
        DetailMenuFragment menuFragment = new DetailMenuFragment();
        DetailReviewFragment reviewFragment = new DetailReviewFragment();

        Bundle args = new Bundle();
        args.putString("token", token);
        args.putLong("restaurantId", restaurantId);  // ⭐⭐⭐ 추가 ⭐⭐⭐

        DetailRequest detailRequest = new DetailRequest(token, String.valueOf(restaurantId),
                response -> {
                    try {
                        JSONObject data = response.getJSONObject("data");
                        JSONObject address = data.getJSONObject("address");
                        tvAddress.setText(address.getString("fullAddress"));
                        String name = data.getString("name");
                        tvName.setText(name);
                        args.putString("restaurantName", name);
                        tvRating.setText(data.getDouble("avgRating") + "");

                        String imageUrl = data.getString("imageUrl");
                        if (!imageUrl.equals("null")) {
                            Picasso.get()
                                    .load(imageUrl)
                                    .into(ivBack);
                        }

                        if(data.getBoolean("liked")) {
                            ivLike.setImageResource(R.drawable.ic_like);
                        } else {
                            ivLike.setImageResource(R.drawable.ic_unlike);
                        }
                        args.putDouble("longitude" , data.getDouble("longitude"));
                        args.putDouble("latitude" , data.getDouble("latitude"));
                        args.putString("fullAddress" , address.getString("fullAddress"));
                        args.putString("restaurantName" , data.getString("name"));

                        homeFragment.setArguments(args);

                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.tab_fragment_container, homeFragment)
                                .commit();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }, this::errorHandler);

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(detailRequest);

        ivLike.setOnClickListener(v -> {
            LikeRequest likeRequest = new LikeRequest(token, String.valueOf(restaurantId),
                    response -> {
                        queue.add(detailRequest);
                    },
                    this::errorHandler);

            queue.add(likeRequest);
        });

        homeFragment.setArguments(args);
        menuFragment.setArguments(args);
        reviewFragment.setArguments(args);

        // TabLayout 찾기
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        // 탭 추가
        tabLayout.addTab(tabLayout.newTab().setText("홈"));
        tabLayout.addTab(tabLayout.newTab().setText("메뉴"));
        tabLayout.addTab(tabLayout.newTab().setText("리뷰"));

        // 4️⃣ 탭 선택 리스너 등록
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selectedFragment = null;
                switch (tab.getPosition()) {
                    case 0:
                        selectedFragment = new DetailHomeFragment();
                        break;
                    case 1:
                        selectedFragment = new DetailMenuFragment();
                        break;
                    case 2:
                        selectedFragment = new DetailReviewFragment();
                        break;
                }

                // Fragment 교체
                if (selectedFragment != null) {
                    selectedFragment.setArguments(args);  // ⭐⭐ 꼭 필요 ⭐⭐
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.tab_fragment_container, selectedFragment)
                            .commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

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

    public void errorHandler(VolleyError error) {
        if (error.networkResponse != null) {
            int status = error.networkResponse.statusCode;
            String body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
            // 서버가 ErrorResponse 구조로 보냈다면 body에서 message 파싱 가능
            try {
                JSONObject errJson = new JSONObject(body);
                String serverMsg = errJson.optString("message");
                Toast.makeText(this, serverMsg, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "서버 오류: " + status, Toast.LENGTH_SHORT).show();
            }
        } else {
            // 네트워크 자체 문제
            Toast.makeText(this, "네트워크 연결을 확인하세요.", Toast.LENGTH_SHORT).show();
        }
    }
}
