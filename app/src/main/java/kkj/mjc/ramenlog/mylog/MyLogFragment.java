package kkj.mjc.ramenlog.mylog;

import android.graphics.Typeface;
import android.icu.util.LocaleData;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import kkj.mjc.ramenlog.R;
import kkj.mjc.ramenlog.request.ReviewListRequest;
@RequiresApi(api = Build.VERSION_CODES.O)
public class MyLogFragment extends Fragment {
    // 내 리뷰 목록을 리스트 형태로 보여주는 뷰
    private RecyclerView recycler;

    private TextView tvSortByDate, tvSortByRating;

    // RecyclerView와 데이터를 연결하는 역할
    private MyLogAdapter adapter;

    // 서버에서 받아온 내 리뷰 목록 데이터를 저장
    private List<ReviewItem> reviewItemList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // fragment_mylog.xml 레이아웃을 inflate 해서 View 객체 생성
        View view = inflater.inflate(R.layout.fragment_mylog, container, false);

        recycler = view.findViewById(R.id.recycler_mylog);
        // RecyclerView의 레이아웃 매니저 설정 (세로 방향 리스트 구성)
        tvSortByDate = view.findViewById(R.id.tvSortByDate);
        tvSortByRating = view.findViewById(R.id.tvSortByRating);
        // 1) 레이아웃 매니저
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        // 어댑터 연결

        // NestedScrollView 안에 있을 경우 부드럽게 스크롤 되도록 설정
        recycler.setNestedScrollingEnabled(false);

        // 어댑터 생성 및 RecyclerView 에 연결
        adapter = new MyLogAdapter(reviewItemList);
        recycler.setAdapter(adapter);

        tvSortByDate.setOnClickListener(v -> {
            // Sort by date (latest first)
            reviewItemList = reviewItemList.stream()
                    .sorted((a, b) -> {
                        java.time.LocalDateTime dateA = java.time.LocalDateTime.parse(a.getCreatedAt());
                        java.time.LocalDateTime dateB = java.time.LocalDateTime.parse(b.getCreatedAt());
                        return dateB.compareTo(dateA);
                    })
                    .collect(java.util.stream.Collectors.toList());
            adapter.notifyDataSetChanged();

            tvSortByDate.setTypeface(null, Typeface.BOLD);
            tvSortByRating.setTypeface(null, Typeface.NORMAL);
        });

        tvSortByRating.setOnClickListener(v -> {
            reviewItemList = reviewItemList.stream()
                    .sorted((a, b) -> Double.compare(b.getRating(), a.getRating()))
                    .collect(java.util.stream.Collectors.toList());

            tvSortByDate.setTypeface(null, Typeface.BOLD);
            tvSortByRating.setTypeface(null, Typeface.NORMAL);

            adapter.notifyDataSetChanged();
        });


        // 내 리뷰 목록 불러오기
        loadMyReviews();

        return view;
    }

    // 내 리뷰 리스트를 서버에서 불러오는 메소드
    private void loadMyReviews() {
        // 토큰 가져오기
        Bundle args = getArguments();
        if (args != null) {
            String token = args.getString("token");
            if(token != null) {
                // 리뷰 리스트를 요청하는 ReviewListRequest
                JsonObjectRequest req = new ReviewListRequest(
                        token,
                        response -> {
                            try {
                                JSONArray data = response.getJSONArray("data");
                                // 기존 리스트 초기화
                                reviewItemList.clear();

                                // 각 리뷰 아이템 파싱
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject obj = data.getJSONObject(i);
                                    String name = obj.getString("restaurantName");
                                    double rating = obj.getDouble("rating");
                                    String content = obj.getString("content");
                                    String createdAt = obj.getString("createdAt");

                                    // images 배열이 있으면 최대 3개까지
                                    String url1 = null, url2 = null, url3 = null;
                                    if (obj.has("images")) {
                                        JSONArray imgs = obj.getJSONArray("images");
                                        if (imgs.length() > 0) url1 = imgs.getString(0);
                                        if (imgs.length() > 1) url2 = imgs.getString(1);
                                        if (imgs.length() > 2) url3 = imgs.getString(2);
                                    }

                                    // ReviewItem 객체로 생성해서 리스트에 추가
                                    reviewItemList.add(new ReviewItem(
                                            name, rating,
                                            url1, url2, url3,
                                            content, createdAt
                                    ));
                                }
                                reviewItemList = reviewItemList.stream()
                                        .sorted((a, b) -> {
                                            LocalDateTime dateA = LocalDateTime.parse(a.getCreatedAt());
                                            LocalDateTime dateB = LocalDateTime.parse(b.getCreatedAt());
                                            return dateB.compareTo(dateA); // 최신순
                                        })
                                        .collect(Collectors.toList());

                                // RecyclerView 갱신
                                adapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        },
                        error -> {
                            error.printStackTrace();
                        }
                );
                // 요청 큐에 추가
                Volley.newRequestQueue(requireContext())
                        .add(req);
            }
        }
    }
}