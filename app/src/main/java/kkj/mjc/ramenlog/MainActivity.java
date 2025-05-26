package kkj.mjc.ramenlog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvTypeItems;
    private TypeAdapter       adapter;
    private LinearLayout btnRamen, btnToping, btnNoodle, btnSource;
    private LinearLayout selectedButton; // 현재 선택된 버튼 추적


    // 3) 각 카테고리별 데이터 리스트
    private final List<TypeItem> ramenList = Arrays.asList(
            new TypeItem(R.drawable.ramen_sou, "소유 라멘", "간장 베이스"),
            new TypeItem(R.drawable.ramen_don,   "돈코츠 라멘", "돼지 뼈를 우린 국물"),
            new TypeItem(R.drawable.ramen_miso, "미소 라멘",   "된장 베이스"),
            new TypeItem(R.drawable.ramen_sio, "시오 라멘",   "소금 베이스"),
            new TypeItem(R.drawable.ramen_giro, "지로 라멘",   "폭력적인 양")
    );
    private final List<TypeItem> toppingList = Arrays.asList(
            new TypeItem(R.drawable.toping_egg,         "아지타마고", "양념 밴 반숙 계란"),
            new TypeItem(R.drawable.toping_chashu,       "차슈",     "돼지고기에 양념 발라 구운 것"),
            new TypeItem(R.drawable.toping_menma, "멘마",       "죽순쪄서 소금에 절혀 발효"),
            new TypeItem(R.drawable.toping_nori, "노리",       "소금 양념이 되어있지 않은 김"),
            new TypeItem(R.drawable.toping_negi, "네기",       "슬라이스 되어있는 대파")
    );
    private final List<TypeItem> noodleList = Arrays.asList(
            new TypeItem(R.drawable.noodle_thin,  "호소멘",  "가느다란 면"),
            new TypeItem(R.drawable.noodle_curve, "치지레멘",  "곡선 면"),
            new TypeItem(R.drawable.noodle_thick,  "후토멘",   "굵은 면"),
            new TypeItem(R.drawable.noodle_aldente,  "카타멘",   "덜 익은 면(꼬들면)")
    );
    private final List<TypeItem> seasoningList = Arrays.asList(
            new TypeItem(R.drawable.sauce_ggae,   "깨",     "고소한 맛을 더함"),
            new TypeItem(R.drawable.sauce_ekhime, "고춧가루(이치미)","약간 매콤해지고 느끼함을 잡아줌"),
            new TypeItem(R.drawable.sauce_garlic,      "다진 마늘",     "느끼함을 잡아줌"),
            new TypeItem(R.drawable.sauce_pepper,      "후추(고쇼)",     "느끼함을 잡아줌")
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 뷰 바인딩
        rvTypeItems = findViewById(R.id.rv_type_items);
        btnRamen    = findViewById(R.id.btn_Ramen);
        btnToping   = findViewById(R.id.btn_Toping);
        btnNoodle   = findViewById(R.id.btn_Noodle);
        btnSource   = findViewById(R.id.btn_Source);

        // RecyclerView 세팅 (가로 레이아웃 매니저)
        rvTypeItems.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        adapter = new TypeAdapter();
        rvTypeItems.setAdapter(adapter);

        // 초기 카테고리: 라멘
        adapter.setItems(ramenList);
        selectedButton = btnRamen;
        btnRamen.setBackgroundResource(R.drawable.bg_rounded_selected);

        // 클릭 이벤트 및 선택 상태 처리
        btnRamen.setOnClickListener(v -> updateCategory(btnRamen, ramenList));
        btnToping.setOnClickListener(v -> updateCategory(btnToping, toppingList));
        btnNoodle.setOnClickListener(v -> updateCategory(btnNoodle, noodleList));
        btnSource.setOnClickListener(v -> updateCategory(btnSource, seasoningList));

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

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

    private void updateCategory(LinearLayout newButton, List<TypeItem> newList) {
        if (selectedButton != null) {
            selectedButton.setBackgroundResource(R.drawable.bg_rounded); // 기본 배경
        }
        newButton.setBackgroundResource(R.drawable.bg_rounded_selected); // 강조 배경
        selectedButton = newButton;

        adapter.setItems(newList); // 목록 변경
    }
}