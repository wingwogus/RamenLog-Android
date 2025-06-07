package kkj.mjc.ramenlog;

import static com.android.volley.toolbox.Volley.newRequestQueue;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import kkj.mjc.ramenlog.like.LikeItem;
import kkj.mjc.ramenlog.rank.RankAdapter;
import kkj.mjc.ramenlog.rank.RankItem;
import kkj.mjc.ramenlog.request.LikeListRequest;
import kkj.mjc.ramenlog.request.RankListRequest;

public class RankActivity extends AppCompatActivity{
    private RecyclerView rvRankList;
    private RankAdapter adapter;
    private final List<RankItem> rankList = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank); // 👉 여기에 연결할 레이아웃

        // 툴바
        setSupportActionBar(findViewById(R.id.toolbar_rank));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // RecyclerView 세팅
        rvRankList = findViewById(R.id.rv_rank_list);
        rvRankList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RankAdapter(rankList);
        rvRankList.setAdapter(adapter);
        TextView[] nameList = {findViewById(R.id.tvRank1), findViewById(R.id.tvRank2), findViewById(R.id.tvRank3)};
        TextView[] ratingList = {findViewById(R.id.tvRating1), findViewById(R.id.tvRating2), findViewById(R.id.tvRating3)};
        TextView[] addressList = {findViewById(R.id.tvAddress1),findViewById(R.id.tvAddress2),findViewById(R.id.tvAddress3)};

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("accessToken", null);

        RankListRequest request = new RankListRequest(
                token,
                response -> {
                    try {
                        JSONArray data = response.getJSONArray("data");
                        rankList.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject item = data.getJSONObject(i);
                            JSONObject address = item.getJSONObject("address");
                            String fullAddress = address.getString("fullAddress").substring(0,7);
                            String name = item.getString("name");
                            double avgRating = item.getDouble("avgRating");

                            rankList.add(new RankItem(i + 1, name, avgRating));

                            // 3위까지
                            if (i < 3) {
                                nameList[i].setText(name);
                                ratingList[i].setText(String.valueOf(avgRating));
                                addressList[i].setText(fullAddress);
                            }

                        }
                        adapter.notifyDataSetChanged();
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
                }
        );
        RequestQueue queue = newRequestQueue(this);
        queue.add(request);

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
