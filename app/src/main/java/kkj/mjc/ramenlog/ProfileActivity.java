package kkj.mjc.ramenlog;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import kkj.mjc.ramenlog.like.LikeFragment;

import kkj.mjc.ramenlog.like.LikeItem;
import kkj.mjc.ramenlog.mylog.MyLogFragment;
import kkj.mjc.ramenlog.request.ProfileRequest;

public class ProfileActivity extends AppCompatActivity{

    TextView name, grade, nextGrade, startReviewCount, endReviewCount, remainingReviewCount;
    ImageView userImg;
    TabLayout tabLayout;
    ProgressBar progressBar;
    Button btnfixReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // 👉 여기에 연결할 레이아웃

        name = findViewById(R.id.user_name);
        grade = findViewById(R.id.user_grade);
        userImg = findViewById(R.id.user_img);
        tabLayout = findViewById(R.id.tab_layout);
        nextGrade = findViewById(R.id.next_grade);
        startReviewCount = findViewById(R.id.start_count);
        endReviewCount = findViewById(R.id.end_count);
        remainingReviewCount = findViewById(R.id.remaining_count);
        progressBar = findViewById(R.id.grade_progressbar);

        // 토큰 꺼내오기
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("accessToken", null);

        // 기본 프래그먼트
        MyLogFragment myLofFragment = new MyLogFragment();
        LikeFragment likeFragment = new LikeFragment();

        Bundle args = new Bundle();
        args.putString("token", token);

        // 토큰 번들에 저장
        myLofFragment.setArguments(args);
        likeFragment.setArguments(args);


        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.tab_fragment_container, myLofFragment)
                .commit();

        tabLayout.addTab(tabLayout.newTab().setText("마이 로그"));
        tabLayout.addTab(tabLayout.newTab().setText("찜 그릇"));

        Volley.newRequestQueue(this).add(new ProfileRequest(token,
                response -> {
                    try {
                        JSONObject data = response.getJSONObject("data");

                        int start = data.getInt("startReviewCount");
                        int end = data.getInt("endReviewCount");
                        int reviewCount = data.getInt("reviewCount");

                        name.setText(data.getString("nickname"));
                        grade.setText(" 🍜 " + data.getString("grade") + " ");
                        nextGrade.setText(data.getString("nextGrade"));
                        startReviewCount.setText(String.valueOf(start));
                        endReviewCount.setText(String.valueOf(end));
                        remainingReviewCount.setText(data.getInt("remainingReviewCount") + "그릇");
                        // 서버에서 받아온 리뷰 수(reviewCount)를 이용해 '마이 로그' 탭 텍스트를 "마이 로그 {n}" 형식으로 설정
                        tabLayout.getTabAt(0).setText("마이 로그 " + reviewCount);
                        // 서버에서 받아온 좋아요 수(likeCount)를 이용해 '찜 그릇' 탭 텍스트를 "찜 그릇 {n}" 형식으로 설정
                        tabLayout.getTabAt(1).setText("찜 그릇 " + data.getInt("likeCount"));

                        // 다음 등급까지 남은 진행도 프로그레스 바에 표시
                        int progress = 0;
                        if (end > start) {
                            progress = (int) (((double)(reviewCount - start) / (end - start)) * 100);
                            progress = Math.min(Math.max(progress, 0), 100);
                        }

                        progressBar.setProgress(progress);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
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
                }));

        // 탭 클릭 시 프래그먼트 변경
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selected = null;
                switch (tab.getPosition()) {
                    case 0:
                        selected = myLofFragment;
                        break;
                    case 1:
                        selected = likeFragment;
                        break;
                }
                if (selected != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.tab_fragment_container, selected)
                            .commit();
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (id == R.id.nav_search) {
                startActivity(new Intent(this, FindMapActivity.class));
                return true;
            } else if (id == R.id.nav_rank) {
                startActivity(new Intent(this, RankActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                return true;
            }
            return false;
        });
        bottomNav.setSelectedItemId(R.id.nav_profile);
    }
}
