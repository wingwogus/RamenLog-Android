package kkj.mjc.ramenlog.detail;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kkj.mjc.ramenlog.R;
import kkj.mjc.ramenlog.ReviewWriteActivity;
import kkj.mjc.ramenlog.mylog.MyLogAdapter;
import kkj.mjc.ramenlog.mylog.ReviewItem;
import kkj.mjc.ramenlog.request.DetailRequest;
import kkj.mjc.ramenlog.request.DetailReviewRequest;
import kkj.mjc.ramenlog.request.ReviewListRequest;

public class DetailReviewFragment extends Fragment {
    TextView btnWriteReview;
    private RecyclerView recycler;
    private DetailReviewAdapter adapter;
    private List<DetailReviewItem> reviewItemList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // fragment_detail_review레이아웃 inflate
        View view = inflater.inflate(R.layout.fragment_detail_review, container, false);

        recycler = view.findViewById(R.id.recycler_detail_review);

        // RecyclerView레이아웃 매니저 설정 (세로 방향 리스트 구성)
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        // NestedScrollView 안에 있을 경우 부드럽게 스크롤 되도록 설정
        recycler.setNestedScrollingEnabled(false);

        // 어댑터 생성 및 RecyclerView 에 연결
        adapter = new DetailReviewAdapter(reviewItemList);
        recycler.setAdapter(adapter);

        // 매장 리뷰 목록 불러오기
        loadStoreReviews();

        // 리뷰 쓰기 버튼 클릭 시 이동
        btnWriteReview = view.findViewById(R.id.btn_write_review);
        btnWriteReview.setOnClickListener(v -> {
            // 현재 화면에 전달된 arguments 에서 restaurantId, restaurantName 가져오기
            Long currentRestaurantId = -1L;
            String currentRestaurantName = null;
            if (getArguments() != null) {
                Bundle arguments = getArguments();
                currentRestaurantId = arguments.getLong("restaurantId", -1L);
                currentRestaurantName = arguments.getString("restaurantName", "없음");
            }
            // 리뷰 작성 화면으로 이동 (restaurantId, restaurantName 전달)
            Intent intent = new Intent(getActivity(), ReviewWriteActivity.class);
            intent.putExtra("restaurantId", currentRestaurantId);// 현재 화면의 restaurantId를 넘김
            intent.putExtra("restaurantName", currentRestaurantName);
            startActivity(intent);
        });
        return view;
    }

    // 매장 리뷰 목록 데이터 불러오는 메소드
    private void loadStoreReviews() {
        // arguments 에서 token 가져오기
        Bundle args = getArguments();
        if (args != null) {
            String token = args.getString("token");
            if(token != null) {
                // 매장 상세 리뷰 리스트를 요청하는 DetailReviewRequest
                JsonObjectRequest req = new DetailReviewRequest(
                        token,
                        response -> {
                            try {
                                JSONArray data = response.getJSONArray("data");

                                // 기존 리뷰 리스트 초기화
                                reviewItemList.clear();

                                // 각 리뷰 아이템 파싱
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject obj = data.getJSONObject(i);
                                    String name = obj.getString("nickname");
                                    double rating = obj.getDouble("rating");
                                    String content = obj.getString("content");

                                    // images 배열이 있으면 최대 3개까지
                                    String url1 = null, url2 = null, url3 = null;
                                    if (obj.has("images")) {
                                        JSONArray imgs = obj.getJSONArray("images");
                                        if (imgs.length() > 0) url1 = imgs.getString(0);
                                        if (imgs.length() > 1) url2 = imgs.getString(1);
                                        if (imgs.length() > 2) url3 = imgs.getString(2);
                                    }

                                    // DetailReviewItem 객체 생성 후 리스트에 추가
                                    reviewItemList.add(new DetailReviewItem(
                                            name, rating,
                                            url1, url2, url3,
                                            content
                                    ));
                                }

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
