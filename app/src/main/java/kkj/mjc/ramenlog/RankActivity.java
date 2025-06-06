package kkj.mjc.ramenlog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import kkj.mjc.ramenlog.rank.RankAdapter;
import kkj.mjc.ramenlog.rank.RankItem;

public class RankActivity extends AppCompatActivity{

    private RecyclerView rvRankList;
    private RankAdapter adapter;
    private final List<RankItem> rankItems = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank); // 👉 여기에 연결할 레이아웃

        // 툴바
        setSupportActionBar(findViewById(R.id.toolbar_rank));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // RecyclerView 세팅
        rvRankList = findViewById(R.id.rv_rank_list);
        rvRankList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RankAdapter(rankItems);
        rvRankList.setAdapter(adapter);

        // 예제 데이터 추가 (여기서 Volley/Retrofit 으로 서버 호출 후 채우시면 됩니다)
        for (int i = 1; i <= 10; i++) {
            rankItems.add(new RankItem(i,
                    "라멘집 " + i, // 서버에서 실제 이름
                    5.0 - i*0.1   // 예제 평점 (
            ));
        }
        adapter.notifyDataSetChanged();

        // 바텀 내비게이션
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (id == R.id.nav_search) {
                startActivity(new Intent(this, SearchActivity.class));
                return true;
            } else if (id == R.id.nav_rank) {
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
        bottomNav.setSelectedItemId(R.id.nav_rank);
    }
}
