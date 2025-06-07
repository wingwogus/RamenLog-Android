package kkj.mjc.ramenlog;

import android.os.Bundle;
import android.widget.TableLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

import kkj.mjc.ramenlog.detail.DetailHomeFragment;
import kkj.mjc.ramenlog.detail.DetailMenuFragment;
import kkj.mjc.ramenlog.detail.DetailReviewFragment;

public class DetailActivity extends AppCompatActivity {
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);

        // 1️⃣ TabLayout 찾기
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        // 2️⃣ 탭 추가
        tabLayout.addTab(tabLayout.newTab().setText("홈"));
        tabLayout.addTab(tabLayout.newTab().setText("메뉴"));
        tabLayout.addTab(tabLayout.newTab().setText("리뷰"));

        // 3️⃣ 초기 Fragment 설정 (홈 Fragment 먼저 보여주기)
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.tab_fragment_container, new DetailHomeFragment())
                .commit();

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
