package kkj.mjc.ramenlog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kkj.mjc.ramenlog.hometype.TypeAdapter;
import kkj.mjc.ramenlog.hometype.TypeItem;
import kkj.mjc.ramenlog.request.HomeRequest;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvTypeItems;
    private TypeAdapter adapter;
    private LinearLayout btnRamen, btnToping, btnNoodle, btnSource;
    private LinearLayout selectedButton; // 현재 선택된 버튼 추적
    private ImageView ivImage;
    private TextView tvName, tvAvgRating, tvAddress;

    private ConstraintLayout cardView;

    // 각 카테고리별 데이터 리스트
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
        ivImage = findViewById(R.id.iv_recommend_image);
        tvName = findViewById(R.id.tv_recommend_name);
        tvAvgRating = findViewById(R.id.tv_recommend_avgRating);
        tvAddress = findViewById(R.id.tv_recommend_address);
        cardView = findViewById(R.id.card_recommend);

        // 랜덤 라멘 추천 픽 가져오기
        loadRandomRestaurant();

        // RecyclerView 레이아웃 매니저 → 가로 스크롤 사용 (HORIZONTAL)
        rvTypeItems.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        // TypeAdapter 객체 생성 후 RecyclerView에 연결
        adapter = new TypeAdapter();
        rvTypeItems.setAdapter(adapter);

        // 초기 카테고리: 라멘
        adapter.setItems(ramenList);
        // 선택된 버튼 초기화
        selectedButton = btnRamen;
        btnRamen.setBackgroundResource(R.drawable.bg_rounded_selected); // 선택된 버튼 배경 변경

        // 클릭 이벤트 및 선택 상태 처리
        btnRamen.setOnClickListener(v -> updateCategory(btnRamen, ramenList));
        btnToping.setOnClickListener(v -> updateCategory(btnToping, toppingList));
        btnNoodle.setOnClickListener(v -> updateCategory(btnNoodle, noodleList));
        btnSource.setOnClickListener(v -> updateCategory(btnSource, seasoningList));

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

            // 하단 네비게이션 메뉴 클릭 시 화면 이동 처리
            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    return true;
                } else if (id == R.id.nav_search) {
                    startActivity(new Intent(this, FindMapActivity.class));
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
        // 현재 선택된 메뉴는 홈 아이콘으로 설정
        bottomNav.setSelectedItemId(R.id.nav_home);
    }
    // 아이콘 클릭 시 배경 변경으로 버튼 효과 추가
    private void updateCategory(LinearLayout newButton, List<TypeItem> newList) {
        if (selectedButton != null) {
            selectedButton.setBackgroundResource(R.drawable.bg_rounded); // 기본 배경
        }
        newButton.setBackgroundResource(R.drawable.bg_rounded_selected); // 강조 배경
        selectedButton = newButton;

        adapter.setItems(newList); // 목록 변경
    }
    // 랜덤 라멘 추천 API 호출
    private void loadRandomRestaurant() {
        // 토큰 가져오기
        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        String token = pref.getString("accessToken", null);
        if (token == null) {
            return; // 토큰이 없으면 API 호출 X
        }
        RequestQueue queue = Volley.newRequestQueue(this);
        // 랜덤 식당 요청하는 HomeRequest
        HomeRequest request = new HomeRequest(
                token,
                response -> {
                    try {
                        JSONObject data = response.getJSONObject("data");

                        String name = data.getString("name");
                        double avgRating = data.getDouble("avgRating");
                        JSONObject addrObj = data.getJSONObject("address");
                        String fullAddress = addrObj.getString("fullAddress");

                        // UI 업데이트 (추천 카드에 표시)
                        tvName.setText(name);
                        tvAvgRating.setText(String.valueOf(avgRating));
                        tvAddress.setText(fullAddress);

                        String imageUrl = data.getString("imageUrl");
                        if (!imageUrl.equals("null")) {
                            // imageUrl이 null이 아니면 Picasso 라이브러리로 이미지 로드
                            Picasso.get()
                                    .load(imageUrl)
                                    .into(ivImage);
                        }
                        // 카드 클릭 시 상세화면 이동 설정
                        cardView.setOnClickListener(v -> {
                            Intent intent = new Intent(this, DetailActivity.class);
                            try {
                                // restaurantId를 넘겨서 상세화면에서 해당 식당 정보 표시
                                intent.putExtra("restaurantId", data.getLong("id"));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            startActivity(intent);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                }
        );
        // 요청 큐에 추가
        queue.add(request);
    }
}