package kkj.mjc.ramenlog;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TableLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

import kkj.mjc.ramenlog.detail.DetailHomeFragment;
import kkj.mjc.ramenlog.detail.DetailMenuFragment;
import kkj.mjc.ramenlog.detail.DetailReviewFragment;
import kkj.mjc.ramenlog.like.LikeFragment;
import kkj.mjc.ramenlog.mylog.MyLogFragment;
import kkj.mjc.ramenlog.request.DetailRequest;
import kkj.mjc.ramenlog.request.ProfileRequest;

public class DetailActivity extends AppCompatActivity {
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);

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
    }
}
